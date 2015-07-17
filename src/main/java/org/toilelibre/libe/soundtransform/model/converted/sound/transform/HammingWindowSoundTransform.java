package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public final class HammingWindowSoundTransform extends AbstractWindowSoundTransform {

    private static final int    TWO    = 2;
    private static final double COEFF2 = 0.46;
    private static final double COEFF1 = 0.54;

    @Override
    protected double applyFunction (final double progress) {
        return HammingWindowSoundTransform.COEFF1 - HammingWindowSoundTransform.COEFF2 * Math.cos (HammingWindowSoundTransform.TWO * Math.PI * progress);
    }

}
