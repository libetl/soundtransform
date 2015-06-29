package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public final class HammingWindowSoundTransform extends AbstractWindowSoundTransform {

    @Override
    protected double applyFunction (final double progress) {
        return 0.54 - 0.46 * Math.cos (2 * Math.PI * progress);
    }

}
