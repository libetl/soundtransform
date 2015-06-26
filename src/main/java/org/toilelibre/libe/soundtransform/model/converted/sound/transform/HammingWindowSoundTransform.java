package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public final class HammingWindowSoundTransform extends AbstractWindowSoundTransform {

    @Override
    protected double applyFunction (final int iteration, final int length) {
        return 0.54 - 0.46 * Math.cos (2 * Math.PI * iteration / (length - 1));
    }

}
