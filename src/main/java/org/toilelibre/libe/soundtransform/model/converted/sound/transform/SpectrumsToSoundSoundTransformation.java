package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SpectrumsToSoundSoundTransformation implements SoundTransformation {

    private final List<Spectrum<?> []>      spectrums;
    private final FourierTransformHelper<?> fourierHelper;
    private final SoundAppender             appender;

    public SpectrumsToSoundSoundTransformation (final List<Spectrum<?> []> spectrums1) {
        this.fourierHelper = $.select (FourierTransformHelper.class);
        this.appender = $.select (SoundAppender.class);
        this.spectrums = spectrums1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        if (this.spectrums == null || this.spectrums.size () == 0) {
            return null;
        }
        final Spectrum<?> [] spectrumChannel = this.spectrums.get (input.getChannelNum ());
        int roundedSampleRate = 2;
        while (roundedSampleRate < this.spectrums.get (input.getChannelNum ()) [0].getSampleRate ()) {
            roundedSampleRate *= 2;
        }
        final Sound result = new Sound (new long [roundedSampleRate * spectrumChannel.length], spectrumChannel [0].getNbBytes (), spectrumChannel [0].getSampleRate (), 0);
        int length = 0;
        for (final Spectrum<?> spectrum : spectrumChannel) {
            @SuppressWarnings ("unchecked")
            final Sound tmpSound = ((FourierTransformHelper<Object>) this.fourierHelper).reverse ((Spectrum<Object>) spectrum);
            this.appender.append (result, length, tmpSound);
            length += tmpSound.getSamples ().length;
        }
        return result;
    }

}
