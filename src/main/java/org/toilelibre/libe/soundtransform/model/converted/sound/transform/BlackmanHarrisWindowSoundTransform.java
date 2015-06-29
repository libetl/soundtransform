package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public final class BlackmanHarrisWindowSoundTransform extends AbstractWindowSoundTransform {

    @Override
    protected double applyFunction (final double progress) {
        return 0.35875 - 0.48829 * Math.cos (2 * Math.PI * progress) + 0.14128 * Math.cos (4 * Math.PI * progress) - 0.01168 * Math.cos (6 * Math.PI * progress);
    }

}
