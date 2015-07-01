package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public final class BlackmanHarrisWindowSoundTransform extends AbstractWindowSoundTransform {

    private static final double COEFF1 = 0.35875;
    private static final double COEFF2 = 0.48829;
    private static final double COEFF3 = 0.14128;
    private static final double COEFF4 = 0.01168;
    private static final double TWO = 2;
    private static final double FOUR = 4;
    private static final double SIX = 6;

    @Override
    protected double applyFunction (final double progress) {
        return BlackmanHarrisWindowSoundTransform.COEFF1 - BlackmanHarrisWindowSoundTransform.COEFF2 * Math.cos (BlackmanHarrisWindowSoundTransform.TWO * Math.PI * progress) + BlackmanHarrisWindowSoundTransform.COEFF3 * Math.cos (BlackmanHarrisWindowSoundTransform.FOUR * Math.PI * progress) - BlackmanHarrisWindowSoundTransform.COEFF4 * Math.cos (BlackmanHarrisWindowSoundTransform.SIX * Math.PI * progress);
    }

}
