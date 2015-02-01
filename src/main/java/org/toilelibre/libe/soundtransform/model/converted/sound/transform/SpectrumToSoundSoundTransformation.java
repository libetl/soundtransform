package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SpectrumToSoundSoundTransformation implements SoundTransformation {

    private final Spectrum<Object>               spectrum;
    private final FourierTransformHelper<Object> fourierHelper;

    @SuppressWarnings ("unchecked")
    public SpectrumToSoundSoundTransformation (final Spectrum<?> spectrum) {
        this.fourierHelper = $.select (FourierTransformHelper.class);
        this.spectrum = (Spectrum<Object>) spectrum;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        return this.fourierHelper.reverse (this.spectrum);
    }

}
