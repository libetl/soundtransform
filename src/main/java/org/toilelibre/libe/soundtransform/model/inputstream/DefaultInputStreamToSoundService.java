package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

class DefaultInputStreamToSoundService extends AbstractLogAware<DefaultInputStreamToSoundService> implements InputStreamToSoundService<AbstractLogAware<DefaultInputStreamToSoundService>> {


    private final FrameProcessor<?> frameProcessor;
    private final AudioFormatParser audioFormatParser;

    public DefaultInputStreamToSoundService (final FrameProcessor<?> processor1, final AudioFormatParser parser1) {
        this.frameProcessor = (FrameProcessor<?>) processor1.setObservers (observers);
        this.audioFormatParser = parser1;
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService#fromInputStream(java.io.InputStream)
     */
    @Override
    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.fromInputStream (ais, this.audioFormatParser.getStreamInfo (ais));
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService#fromInputStream(java.io.InputStream, org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo)
     */
    @Override
    public Sound [] fromInputStream (final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        this.log (new LogEvent (TransformInputStreamServiceEventCode.CONVERT_INTO_JAVA_OBJECT));
        final Sound [] result = this.frameProcessor.fromInputStream (ais, isInfo);
        this.log (new LogEvent (TransformInputStreamServiceEventCode.CONVERT_DONE));
        return result;
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService#getStreamInfo(java.io.InputStream)
     */
    @Override
    public StreamInfo getStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.audioFormatParser.getStreamInfo (ais);
    }

    @Override
    public DefaultInputStreamToSoundService setObservers (final Observer... observers1) {
        super.setObservers (observers1);
        this.frameProcessor.setObservers (observers1);
        return this;
    }
}