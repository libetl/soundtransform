package org.toilelibre.libe.soundtransform.model.record;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

final class DefaultRecordSoundService extends AbstractLogAware<DefaultRecordSoundService> implements RecordSoundService<AbstractLogAware<DefaultRecordSoundService>> {

    enum DefaultRecordSoundServiceErrorCode implements ErrorCode {

        NOT_ABLE ("Not able to wait for a recording (%1s)"),
        PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING ("Problem while reading the buffer in a continuous recording");

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

    public DefaultRecordSoundService (final RecordSoundProcessor processor1, final AudioFormatParser audioFormatParser1) {
        this.processor = processor1;
        this.audioFormatParser = audioFormatParser1;

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
    public <O> List<O> recordAndProcess (final StreamInfo streamInfo, final Object stop, final FluentClientOperation operation, final Class<O> returnType) throws SoundTransformException {
        final ByteBuffer targetByteBuffer = this.startRecordingAndReturnByteBuffer (streamInfo, stop);
        final List<InputStream> streamsFromBuffer = new ArrayList<InputStream> ();
        final List<O> results = new ArrayList<O> ();
        new Thread () {
            public void run (){
                synchronized (stop) {
                    boolean stopped = false;
                    while (!stopped){
                        try {
                            InputStream inputStream = FluentClient.start ().withByteBuffer (targetByteBuffer, streamInfo).readBuffer ().stopWithInputStream ();
                            if (inputStream.available () > 0){
                                streamsFromBuffer.add (inputStream);
                                FluentClient targetFluentClient = (FluentClient) FluentClient.start ().withRawInputStream (inputStream, streamInfo);
                                new FluentClientOperation.FluentClientOperationRunnable (operation, targetFluentClient, 1).run ();
                                results.add (targetFluentClient.getResult (returnType));
                            }
                            stop.wait (1);
                        } catch (InterruptedException e) {
                            stopped = true;
                        } catch (IOException e) {
                            throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING, e));
                        } catch (SoundTransformException e) {
                            throw new SoundTransformRuntimeException (e);
                        }
                    }
                }
                
            }
        }.start ();
        
        return results;
    }
}
