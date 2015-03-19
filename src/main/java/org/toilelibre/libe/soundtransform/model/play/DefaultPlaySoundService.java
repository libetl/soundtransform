package org.toilelibre.libe.soundtransform.model.play;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

class DefaultPlaySoundService<T extends Serializable> implements PlaySoundService<T> {

    private final PlaySoundProcessor        processor;
    private final SoundToInputStreamService sound2IsService;
    private final FourierTransformHelper<T> fourierTransformHelper;

    public DefaultPlaySoundService (final PlaySoundProcessor processor1, final SoundToInputStreamService sound2IsService1, final FourierTransformHelper<T> fourierTransformHelper1) {
        this.processor = processor1;
        this.sound2IsService = sound2IsService1;
        this.fourierTransformHelper = fourierTransformHelper1;

    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.play.PlaySoundService#play(java.io.InputStream)
     */
    @Override
    public Object play (final InputStream is) throws SoundTransformException {
        return this.processor.play (is);
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.play.PlaySoundService#play(org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    public Object play (final Sound [] channels) throws SoundTransformException {

        if (channels.length == 0) {
            return new Object ();
        }

        final InputStream ais = this.sound2IsService.toStream (channels, StreamInfo.from (channels [0].getFormatInfo (), channels));
        return this.processor.play (ais);
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.play.PlaySoundService#play(org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum)
     */
    @Override
    public Object play (final Spectrum<T> spectrum) throws SoundTransformException {
        return this.play (new Sound [] { this.fourierTransformHelper.reverse (spectrum) });
    }
}
