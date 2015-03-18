package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class InputStreamToSoundService extends AbstractLogAware<InputStreamToSoundService> {

    public enum TransformInputStreamServiceEventCode implements EventCode {
        CONVERT_INTO_JAVA_OBJECT (LogLevel.INFO, "Converting input into java object"), CONVERT_DONE (LogLevel.INFO, "Done converting the input stream");

        private String   messageFormat;
        private LogLevel logLevel;

        TransformInputStreamServiceEventCode (final LogLevel ll, final String mF) {
            this.logLevel = ll;
            this.messageFormat = mF;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final FrameProcessor<?> frameProcessor;
    private final AudioFormatParser audioFormatParser;

    public InputStreamToSoundService (final FrameProcessor<?> processor1, final AudioFormatParser parser1) {
        this (processor1, parser1, new Observer [0]);
    }

    public InputStreamToSoundService (final FrameProcessor<?> processor1, final AudioFormatParser parser1, final Observer... observers) {
        this.frameProcessor = (FrameProcessor<?>) processor1.setObservers (observers);
        this.audioFormatParser = parser1;
        this.setObservers (observers);
    }

    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.fromInputStream (ais, this.audioFormatParser.getStreamInfo (ais));
    }

    public Sound [] fromInputStream (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        this.log (new LogEvent (TransformInputStreamServiceEventCode.CONVERT_INTO_JAVA_OBJECT));
        final Sound [] result = this.frameProcessor.fromInputStream (ais, isInfo);
        this.log (new LogEvent (TransformInputStreamServiceEventCode.CONVERT_DONE));
        return result;
    }

    public StreamInfo getStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.audioFormatParser.getStreamInfo (ais);
    }

    @Override
    public InputStreamToSoundService setObservers (final Observer... observers1) {
        super.setObservers (observers1);
        this.frameProcessor.setObservers (observers1);
        return this;
    }
}