package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 * Uses a list of spectrums to convert them into a sound
 */
public class SpectrumsToSoundSoundTransform implements SoundTransform<Spectrum<Serializable> [], Sound> {

    private final FourierTransformHelper<?>       fourierHelper;
    private final SoundAppender                   appender;

    /**
     * Default constructors
     */
    public SpectrumsToSoundSoundTransform () {
        this.fourierHelper = $.select (FourierTransformHelper.class);
        this.appender = $.select (SoundAppender.class);
    }

    @Override
    public Sound transform (final Spectrum<Serializable> [] spectrumChannel) throws SoundTransformException {
        int roundedSampleRate = 2;
        if (spectrumChannel == null || spectrumChannel.length == 0) {
            return null;
        }
        while (roundedSampleRate < spectrumChannel [0].getSampleRate ()) {
            roundedSampleRate *= 2;
        }
        final Sound result = new Sound (new long [roundedSampleRate * spectrumChannel.length], spectrumChannel [0].getFormatInfo (), 0);
        int length = 0;
        for (final Spectrum<?> spectrum : spectrumChannel) {
            @SuppressWarnings ("unchecked")
            final Sound tmpSound = ((FourierTransformHelper<Serializable>) this.fourierHelper).reverse ((Spectrum<Serializable>) spectrum);
            this.appender.append (result, length, tmpSound);
            length += tmpSound.getSamplesLength ();
        }
        return result;
    }

}
