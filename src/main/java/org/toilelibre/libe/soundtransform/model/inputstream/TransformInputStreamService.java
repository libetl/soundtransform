package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformInputStreamService implements LogAware<TransformInputStreamService> {

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

    private Observer []             observers = new Observer [0];
    private final FrameProcessor    frameProcessor;
    private final AudioFormatParser audioFormatParser;

    public TransformInputStreamService (FrameProcessor processor1, AudioFormatParser parser1) {
        this (processor1, parser1, new Observer [0]);
    }

    public TransformInputStreamService (FrameProcessor processor1, AudioFormatParser parser1, final Observer... observers) {
        this.setObservers (observers);
        this.frameProcessor = processor1;
        this.audioFormatParser = parser1;
    }

    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.fromInputStream (ais, this.audioFormatParser.getInputStreamInfo (ais));
    }

    public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
        this.notifyAll ("Converting input into java object");
        final Sound [] ret = this.frameProcessor.fromInputStream (ais, isInfo);
        return ret;
    }

    @Override
    public void log (final LogEvent event) {
        for (final Observer to : this.observers) {
            to.notify (event);
        }

    }

    private void notifyAll (final String s) {
        this.log (new LogEvent (LogLevel.INFO, s));
    }

    @Override
    public TransformInputStreamService setObservers (final Observer... observers2) {
        this.observers = observers2;
        for (final Observer observer : observers2) {
            this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
        }
        return this;
    }

    public byte [] soundToByteArray (final Sound [] channels, final InputStreamInfo inputStreamInfo) {
        return this.frameProcessor.framesToByteArray (channels, inputStreamInfo.getSampleSize (), inputStreamInfo.isBigEndian (), inputStreamInfo.isPcmSigned ());
    }
}