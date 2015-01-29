package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformInputStreamService extends AbstractLogAware<TransformInputStreamService> {

    public enum TransformInputStreamServiceErrorCode implements ErrorCode {
        COULD_NOT_READ_STREAM ("Could not read stream");

        private String messageFormat;

        TransformInputStreamServiceErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final FrameProcessor    frameProcessor;
    private final AudioFormatParser audioFormatParser;

    public TransformInputStreamService (final FrameProcessor processor1, final AudioFormatParser parser1) {
        this (processor1, parser1, new Observer [0]);
    }

    public TransformInputStreamService (final FrameProcessor processor1, final AudioFormatParser parser1, final Observer... observers) {
        this.setObservers (observers);
        this.frameProcessor = processor1;
        this.audioFormatParser = parser1;
    }

    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.fromInputStream (ais, this.audioFormatParser.getInputStreamInfo (ais));
    }

    public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
        this.log (new LogEvent (LogLevel.INFO, "Converting input into java object"));
        return this.frameProcessor.fromInputStream (ais, isInfo);
    }

    public InputStreamInfo getInputStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.audioFormatParser.getInputStreamInfo (ais);
    }

    public byte [] soundToByteArray (final Sound [] channels, final InputStreamInfo inputStreamInfo) {
        return this.frameProcessor.framesToByteArray (channels, inputStreamInfo.getSampleSize (), inputStreamInfo.isBigEndian (), inputStreamInfo.isPcmSigned ());
    }
}