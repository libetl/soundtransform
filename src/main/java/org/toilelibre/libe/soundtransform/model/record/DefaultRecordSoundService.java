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

    private static class StreamReaderThread extends Thread {
        private final List<Sound>                  results;
        private final StreamInfo                   streamInfo;
        private final ByteBuffer                   targetByteBuffer;
        private final AudioFileService<?>          audioFileService1;
        private final InputStreamToSoundService<?> isToSoundService1;
        private boolean                            waiting;

        private StreamReaderThread (final List<Sound> results, final StreamInfo streamInfo, final ByteBuffer targetByteBuffer, final AudioFileService<?> audioFileService1, final InputStreamToSoundService<?> isToSoundService1) {
            this.results = results;
            this.streamInfo = streamInfo;
            this.targetByteBuffer = targetByteBuffer;
            this.audioFileService1 = audioFileService1;
            this.isToSoundService1 = isToSoundService1;
            this.waiting = true;
            this.setName (this.getClass ().getSimpleName ());
        }

        private void waitForNewBytes (final ByteBuffer targetByteBuffer) throws SoundTransformException {
            boolean waited = false;
            synchronized (targetByteBuffer) {
                try {
                    while (!waited) {
                        targetByteBuffer.wait ();
                        waited = true;
                    }
                } catch (final InterruptedException e) {
                    waited = true;
                }
            }
        }

        private void stopWaiting () {
            this.waiting = false;
        }

        @Override
        public void run () {
            while (this.waiting) {
                try {
                    this.waitForNewBytes (this.targetByteBuffer);
                    final InputStream inputStream = this.audioFileService1.streamFromRawStream (new ByteArrayInputStream (this.targetByteBuffer.array ()), this.streamInfo);
                    if (inputStream.available () > 0) {
                        this.results.add (this.isToSoundService1.fromInputStream (inputStream, this.streamInfo));
                    }
                } catch (final IOException e) {
                    throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING, e));
                } catch (final SoundTransformException e) {
                    throw new SoundTransformRuntimeException (e);
                }
            }
        }
    }

    private static final class StopDetectorThread extends Thread {
        private final ByteBuffer         targetByteBuffer;
        private final Object             stop;
        private final StreamReaderThread streamReader;

        private StopDetectorThread (final ByteBuffer targetByteBuffer, final Object stop, final StreamReaderThread streamReader) {
            this.targetByteBuffer = targetByteBuffer;
            this.stop = stop;
            this.streamReader = streamReader;
            this.setName (this.getClass ().getSimpleName ());
        }

        @Override
        public void run () {
            synchronized (this.stop) {
                boolean waited = false;
                try {
                    while (!waited) {
                        waited = true;
                        this.stop.wait ();
                    }
                    this.notifyTargetByteBuffer (waited);
                } catch (final InterruptedException e) {
                    this.notifyTargetByteBuffer (waited);
                }
            }
            this.streamReader.stopWaiting ();
        }

        private void notifyTargetByteBuffer (final boolean waited) {
            synchronized (this.targetByteBuffer) {
                if (waited) {
                    this.targetByteBuffer.notifyAll ();
                }
            }
        }
    }

    private static class SleepThread extends Thread {
        private final Object stop;
        private final long   millis;

        private SleepThread (final Object stop, final long millis) {
            this.stop = stop;
            this.millis = millis;
            this.setName (this.getClass ().getSimpleName ());
        }

        @Override
        public void run () {
            try {
                Thread.sleep (this.millis);
            } catch (final InterruptedException e) {
                throw new SoundTransformRuntimeException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e, e.getMessage ());
            }
            synchronized (this.stop) {
                this.stop.notify ();
            }
        }
    }

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


    private static final float                 MS_PER_SECOND                                               = 1000.0f;
    private static final long                  ARBITRARY_SLEEP_TIME_TO_ENSURE_THE_STREAMING_IS_INITIALIZED = 1000;
    private final RecordSoundProcessor         processor;
    private final AudioFormatParser            audioFormatParser;
    private final AudioFileService<?>          audioFileService;
    private final InputStreamToSoundService<?> isToSoundService;

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
        new SleepThread (stop, millis).start ();
        return this.recordRawInputStream (streamInfo, stop);
    }

    private ByteBuffer startRecordingAndReturnByteBuffer (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return this.processor.startRecordingAndReturnByteBuffer (this.audioFormatParser.audioFormatfromStreamInfo (streamInfo), stop);
    }

    private List<Sound> recordInBackgroundTask (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        final ByteBuffer targetByteBuffer = this.startRecordingAndReturnByteBuffer (streamInfo, stop);
        final List<Sound> results = new ArrayList<Sound> ();

        final StreamReaderThread streamReader = this.getStreamReader (streamInfo, targetByteBuffer, results);
        streamReader.start ();
        try {
            Thread.sleep (DefaultRecordSoundService.ARBITRARY_SLEEP_TIME_TO_ENSURE_THE_STREAMING_IS_INITIALIZED);
        } catch (final InterruptedException e) {
            throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e, e.getMessage ()));
        }

        this.stopDetector (stop, streamReader, targetByteBuffer).start ();

        return results;
    }

    private Thread stopDetector (final Object stop, final StreamReaderThread streamReader, final ByteBuffer targetByteBuffer) {
        return new StopDetectorThread (targetByteBuffer, stop, streamReader);
    }

    private StreamReaderThread getStreamReader (final StreamInfo streamInfo, final ByteBuffer targetByteBuffer, final List<Sound> results) {
        final AudioFileService<?> audioFileService1 = this.audioFileService;
        final InputStreamToSoundService<?> isToSoundService1 = this.isToSoundService;
        return new StreamReaderThread (results, streamInfo, targetByteBuffer, audioFileService1, isToSoundService1);
    }

    @Override
    public Sound startRecordingASound (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return new SegmentedSound (streamInfo, this.recordInBackgroundTask (streamInfo, stop));
    }
}
