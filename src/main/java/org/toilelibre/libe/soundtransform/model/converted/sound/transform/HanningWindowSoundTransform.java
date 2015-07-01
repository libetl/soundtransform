package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public class HanningWindowSoundTransform extends AbstractWindowSoundTransform {

    private static final int TWO = 2;
    private static final int ONE = 1;
    private static final double COEFF1 = 0.5;

    @Override
    protected double applyFunction (final double progress) {
        return HanningWindowSoundTransform.COEFF1 * (HanningWindowSoundTransform.ONE - Math.cos (HanningWindowSoundTransform.TWO * Math.PI * progress));
    }

}
