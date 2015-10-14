package org.toilelibre.libe.soundtransform.model.inputstream.fromsound;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;

final class DefaultSoundToInputStreamService extends AbstractLogAware<DefaultSoundToInputStreamService> implements SoundToInputStreamService<AbstractLogAware<DefaultSoundToInputStreamService>> {

    private final FrameProcessor<?>  frameProcessor;
    private final AudioFileHelper    audioFileHelper;
    private final AudioFormatService audioFormatService;

    public DefaultSoundToInputStreamService (final FrameProcessor<?> processor1, final AudioFileHelper audioFileHelper1, final AudioFormatService audioFormatService1) {
        this.frameProcessor = (FrameProcessor<?>) processor1.setObservers (this.observers);
        this.audioFileHelper = audioFileHelper1;
        this.audioFormatService = audioFormatService1;
        this.setObservers (this.observers);
    }

    private byte [] soundToByteArray (final Sound sound, final StreamInfo streamInfo) {
        return this.frameProcessor.framesToByteArray (sound.getChannels (), streamInfo);
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
    public InputStream toStream (final Sound sound, final StreamInfo streamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream (this.soundToByteArray (sound, streamInfo), this.audioFormatService.audioFormatfromStreamInfo (streamInfo));
    }
}
