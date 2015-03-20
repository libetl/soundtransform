package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

class DefaultRecordSoundService<T extends Serializable> extends AbstractLogAware<DefaultRecordSoundService<T>> implements RecordSoundService<T> {

    public enum DefaultRecordSoundServiceEventCode implements EventCode {

        NOT_ABLE (LogLevel.ERROR, "Not able to wait for a recording (%1s)");

        private final String messageFormat;
        private final LogLevel logLevel;

        DefaultRecordSoundServiceEventCode(final LogLevel ll, final String mF) {
            this.logLevel = ll;
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }

        @Override
        public LogLevel getLevel() {
            return this.logLevel;
        }
    }
    private static final float MS_PER_SECOND = 1000.0f;
    private final RecordSoundProcessor processor;
    private final AudioFormatParser audioFormatParser;

    public DefaultRecordSoundService (final RecordSoundProcessor processor1, final AudioFormatParser audioFormatParser1) {
        this.processor = processor1;
        this.audioFormatParser = audioFormatParser1;

    }

    @Override
    public InputStream recordRawInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException {
        return this.processor.recordRawInputStream (this.audioFormatParser.audioFormatfromStreamInfo (streamInfo), stop);
    }

    @Override
    public InputStream recordLimitedTimeRawInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        final long millis = (long) (streamInfo.getFrameLength() / streamInfo.getSampleRate() * MS_PER_SECOND);
        final Object stop = new Object ();
        new Thread () {
            public void run (){
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    DefaultRecordSoundService.this.log(new LogEvent (DefaultRecordSoundServiceEventCode.NOT_ABLE, e));
                }
                synchronized (stop){
                    stop.notify();
                }
            }
        }.start();
        return this.processor.recordRawInputStream (this.audioFormatParser.audioFormatfromStreamInfo (streamInfo), stop);
    }

}
