package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

final class DefaultSoundToInputStreamService extends AbstractLogAware<DefaultSoundToInputStreamService> implements SoundToInputStreamService<AbstractLogAware<DefaultSoundToInputStreamService>> {

    private final FrameProcessor<?> frameProcessor;
    private final AudioFileHelper   audioFileHelper;
    private final AudioFormatParser audioFormatParser;

    public DefaultSoundToInputStreamService (final FrameProcessor<?> processor1, final AudioFileHelper audioFileHelper1, final AudioFormatParser audioFormatParser1) {
        this.frameProcessor = (FrameProcessor<?>) processor1.setObservers (this.observers);
        this.audioFileHelper = audioFileHelper1;
        this.audioFormatParser = audioFormatParser1;
        this.setObservers (this.observers);
    }

    private byte [] soundToByteArray (final Sound [] channels, final StreamInfo streamInfo) {
        return this.frameProcessor.framesToByteArray (channels, streamInfo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.model.inputstream.
     * SoundToInputStreamService
     * #toStream(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound[],
     * org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo)
     */
    @Override
    public InputStream toStream (final Sound [] channels, final StreamInfo streamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream (this.soundToByteArray (channels, streamInfo), this.audioFormatParser.audioFormatfromStreamInfo (streamInfo));
    }
}
