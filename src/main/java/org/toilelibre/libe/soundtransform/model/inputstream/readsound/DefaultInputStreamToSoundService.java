package org.toilelibre.libe.soundtransform.model.inputstream.readsound;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatService;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

final class DefaultInputStreamToSoundService extends AbstractLogAware<DefaultInputStreamToSoundService> implements InputStreamToSoundService<AbstractLogAware<DefaultInputStreamToSoundService>> {

    private final FrameProcessor<?>  frameProcessor;
    private final AudioFormatService audioFormatService;

    public DefaultInputStreamToSoundService (final FrameProcessor<?> processor1, final AudioFormatService service1) {
        this.frameProcessor = (FrameProcessor<?>) processor1.setObservers (this.observers);
        this.audioFormatService = service1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.model.inputstream.
     * InputStreamToSoundService#fromInputStream(java.io.InputStream)
     */
    @Override
    public Sound fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.fromInputStream (ais, this.audioFormatService.getStreamInfo (ais));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.model.inputstream.
     * InputStreamToSoundService#fromInputStream(java.io.InputStream,
     * org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo)
     */
    @Override
    public Sound fromInputStream (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        this.log (new LogEvent (TransformInputStreamServiceEventCode.CONVERT_INTO_JAVA_OBJECT));
        final Channel [] result = this.frameProcessor.fromInputStream (ais, isInfo);
        this.log (new LogEvent (TransformInputStreamServiceEventCode.CONVERT_DONE));
        return new Sound (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.model.inputstream.
     * InputStreamToSoundService#getStreamInfo(java.io.InputStream)
     */
    @Override
    public StreamInfo getStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.audioFormatService.getStreamInfo (ais);
    }

    @Override
    public DefaultInputStreamToSoundService setObservers (final Observer... observers1) {
        super.setObservers (observers1);
        this.frameProcessor.setObservers (observers1);
        return this;
    }
}