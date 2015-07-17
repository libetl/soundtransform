package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class PralongAndCarlileSoundTransform extends SimpleFrequencySoundTransform<Complex []> {

    private static final double []        RANGES        = { 0, 10, 100, 125, 130, 150, 250, 500, 1000, 1237.384651, 1531.120775, 1894.585346, 2002.467159, 2344.330828, 2721.273584, 3001.403462, 3589.453635, 4001.342781, 4441.534834, 5004.212211, 5495.887031, 5997.423738, 6800.526258, 6946.931144,
        7995.508928, 8414.866811, 9008.422743, 20000, 48000 };
    private static final double []        AMPLIFICATION = { 0.08, 0.1, 0.81, 1, 1, 1, 1, 1, 0.994850557, 0.994850557, 0.994850557, 1.114513162, 1.235743262, 1.867671314, 2.822751493, 2.180544843, 1.442755787, 1.173563859, 1.37016005, 1.599690164, 1.37016005, 1.114513162, 0.648125625, 0.631609176,
        0.276505667, 0.084335217, 0.084335217, 0, 0 };
    private final EqualizerSoundTransform decoratedSoundTransform;

    public PralongAndCarlileSoundTransform () {
        this.decoratedSoundTransform = new EqualizerSoundTransform (PralongAndCarlileSoundTransform.RANGES, PralongAndCarlileSoundTransform.AMPLIFICATION);
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset, final int powOf2NearestLength, final int length) {
        return this.decoratedSoundTransform.transformFrequencies (fs, offset, powOf2NearestLength, length);
    }
}
