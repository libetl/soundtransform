package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class SoundToInputStreamService extends AbstractLogAware<SoundToInputStreamService>  {

    private final FrameProcessor<?> frameProcessor;
    private final AudioFileHelper   audioFileHelper;
    private final AudioFormatParser audioFormatParser;

    public SoundToInputStreamService(final FrameProcessor<?> processor1, final AudioFileHelper audioFileHelper1, final AudioFormatParser audioFormatParser1) {
        this(processor1, audioFileHelper1, audioFormatParser1, new Observer[0]);
    }

    public SoundToInputStreamService(final FrameProcessor<?> processor1, final AudioFileHelper audioFileHelper1, final AudioFormatParser audioFormatParser1, final Observer... observers) {
        this.frameProcessor = (FrameProcessor<?>) processor1.setObservers(observers);
        this.audioFileHelper = audioFileHelper1;
        this.audioFormatParser = audioFormatParser1;
        this.setObservers(observers);
    }

    private byte [] soundToByteArray (final Sound [] channels, final StreamInfo streamInfo) {
        return this.frameProcessor.framesToByteArray (channels, streamInfo);
    }

    public InputStream toStream(final Sound [] channels, final StreamInfo streamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream(this.soundToByteArray(channels, streamInfo), this.audioFormatParser.audioFormatfromStreamInfo(streamInfo));
    }
}
