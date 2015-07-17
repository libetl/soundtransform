package org.toilelibre.libe.soundtransform.model.record;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.SegmentedSound;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

final class DefaultRecordSoundService extends AbstractLogAware<DefaultRecordSoundService> implements RecordSoundService<AbstractLogAware<DefaultRecordSoundService>> {

    enum DefaultRecordSoundServiceErrorCode implements ErrorCode {

        NOT_ABLE ("Not able to wait for a recording (%1s)"), PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING ("Problem while reading the buffer in a continuous recording");

        private final String messageFormat;

        DefaultRecordSoundServiceErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static final float         MS_PER_SECOND = 1000.0f;
    private final RecordSoundProcessor processor;
    private final AudioFormatParser    audioFormatParser;
    private final AudioFileService<?>  audioFileService;
    private InputStreamToSoundService<?> isToSoundService;

    public DefaultRecordSoundService (final RecordSoundProcessor processor1, final AudioFormatParser audioFormatParser1, final AudioFileService<?> audioFileService1, final InputStreamToSoundService<?> isToSoundService1) {
        this.processor = processor1;
        this.audioFormatParser = audioFormatParser1;
        this.audioFileService = audioFileService1;
        this.isToSoundService = isToSoundService1;
    }

    @Override
    public InputStream recordRawInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return this.processor.recordRawInputStream (this.audioFormatParser.audioFormatfromStreamInfo (streamInfo), stop);
    }

    @Override
    public InputStream recordLimitedTimeRawInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        final long millis = (long) (streamInfo.getFrameLength () / streamInfo.getSampleRate () * DefaultRecordSoundService.MS_PER_SECOND);
        final Object stop = new Object ();
        new Thread () {
            @Override
            public void run () {
                try {
                    Thread.sleep (millis);
                } catch (final InterruptedException e) {
                    throw new SoundTransformRuntimeException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e);
                }
                synchronized (stop) {
                    stop.notify ();
                }
            }
        }.start ();
        return this.recordRawInputStream (streamInfo, stop);
    }

    private ByteBuffer startRecordingAndReturnByteBuffer (StreamInfo streamInfo, Object stop) throws SoundTransformException {
        return this.processor.startRecordingAndReturnByteBuffer (this.audioFormatParser.audioFormatfromStreamInfo (streamInfo), stop);
    }

    @Override
    public <O> List<O> recordAndProcess (final StreamInfo streamInfo, final Object stop, final RunnableWithInputStream runnable, final Class<O> returnType) throws SoundTransformException {
        final ByteBuffer targetByteBuffer = this.startRecordingAndReturnByteBuffer (streamInfo, stop);
        final List<InputStream> streamsFromBuffer = new ArrayList<InputStream> ();
        final List<O> results = new ArrayList<O> ();

        final Thread streamReader = this.getStreamReader (streamInfo, runnable, returnType, targetByteBuffer, streamsFromBuffer, results);
        streamReader.start ();
        try {
            Thread.sleep (100);
        } catch (InterruptedException e) {
            throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e));
        }

        this.stopDetector (stop, streamReader).start ();

        return results;
    }

    private Thread stopDetector (final Object stop, final Thread streamReader) {
        return new Thread () {
            public void run () {
                boolean stopped = false;
                while (!stopped) {
                    stopped = true;
                    synchronized (stop) {
                        try {
                            stop.wait ();
                            streamReader.interrupt ();
                        } catch (InterruptedException e) {
                            streamReader.interrupt ();
                        }
                    }
                    streamReader.interrupt ();
                }
            }
        };
    }

    private <O> Thread getStreamReader (final StreamInfo streamInfo, final RunnableWithInputStream runnable, final Class<O> returnType, final ByteBuffer targetByteBuffer, final List<InputStream> streamsFromBuffer, final List<O> results) {
        return new Thread () {
            public void run () {
                try {
                    DefaultRecordSoundService.this.waitForNewBytes (targetByteBuffer);
                    InputStream inputStream = DefaultRecordSoundService.this.audioFileService.streamFromRawStream (new ByteArrayInputStream (targetByteBuffer.array ()), streamInfo);
                    if (inputStream.available () > 0) {
                        streamsFromBuffer.add (inputStream);
                        results.add (runnable.runWithInputStreamAndGetResult (inputStream, streamInfo, returnType));
                    }
                } catch (IOException e) {
                    throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING, e));
                } catch (SoundTransformException e) {
                    throw new SoundTransformRuntimeException (e);
                }
            }
        };
    }

    private void waitForNewBytes (final ByteBuffer targetByteBuffer) throws SoundTransformException {
        boolean waited = false;
        synchronized (targetByteBuffer) {
            try {
                while (!waited){
                    targetByteBuffer.wait ();
                    waited = true;
                }
            } catch (InterruptedException e) {
                throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e));
            }
        }
    }

    @Override
    public Sound startRecordingASound (StreamInfo streamInfo, Object stop) throws SoundTransformException {
        final RunnableWithInputStream convertIntoSound = new RunnableWithInputStream () {
            
            @Override
            public void run () {
                
            }
            
            @SuppressWarnings ("unchecked")
            @Override
            public <T> T runWithInputStreamAndGetResult (InputStream inputStream, StreamInfo streamInfo, Class<T> returnType) throws SoundTransformException {
                return (T) DefaultRecordSoundService.this.isToSoundService.fromInputStream (inputStream, streamInfo);
            }
        };
        return new SegmentedSound (streamInfo, this.recordAndProcess (streamInfo, stop, convertIntoSound, Sound.class));
    }
}
