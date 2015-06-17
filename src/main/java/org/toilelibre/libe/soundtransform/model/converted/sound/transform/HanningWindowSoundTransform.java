package org.toilelibre.libe.soundtransform.model.converted.sound.transform;


public class HanningWindowSoundTransform extends AbstractWindowSoundTransform {


    protected double applyFunction (int iteration, int length) {
        return 0.5 * ( 1 - Math.cos ((2 * Math.PI * iteration) / (length - 1)));
    }

}
