package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

public class NoOpWindowSoundTransform extends AbstractWindowSoundTransform {

    @Override
    protected double applyFunction (final double progress) {
        return 1;
    }

}
