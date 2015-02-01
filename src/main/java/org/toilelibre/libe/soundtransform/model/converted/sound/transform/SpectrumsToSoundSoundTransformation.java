package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SpectrumsToSoundSoundTransformation implements SoundTransformation {

    private final Spectrum<Object> []            spectrums;
    private final FourierTransformHelper<Object> fourierHelper;
    private final SoundAppender                  appender;

    @SuppressWarnings ("unchecked")
    public SpectrumsToSoundSoundTransformation (final Spectrum<?> [] spectrums1) {
        this.fourierHelper = $.select (FourierTransformHelper.class);
        this.appender = $.select (SoundAppender.class);
        this.spectrums = (Spectrum<Object> []) spectrums1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        if ((this.spectrums == null) || (this.spectrums.length == 0)) {
            return null;
        }
        int roundedSampleRate = 2;
        while (roundedSampleRate < this.spectrums [0].getSampleRate ()) {
            roundedSampleRate *= 2;
        }
        final Sound result = new Sound (new long [roundedSampleRate * this.spectrums.length], this.spectrums [0].getNbBytes (), this.spectrums [0].getSampleRate (), 0);
        int length = 0;
        for (final Spectrum<Object> spectrum : this.spectrums) {
            final Sound tmpSound = this.fourierHelper.reverse (spectrum);
            this.appender.append (result, length, tmpSound);
            length += tmpSound.getSamples ().length;
        }
        return result;
    }

}
