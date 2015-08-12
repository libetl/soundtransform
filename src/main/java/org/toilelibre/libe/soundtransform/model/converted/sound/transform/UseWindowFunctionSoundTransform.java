package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class UseWindowFunctionSoundTransform implements SoundTransform<Channel, Channel> {

    private final AbstractWindowSoundTransform windowFunction;

    public UseWindowFunctionSoundTransform (final AbstractWindowSoundTransform windowFunction1) {
        this.windowFunction = windowFunction1;
    }
    
    @Override
    public Channel transform (final Channel input) throws SoundTransformException {
        return this.windowFunction.transformWholeChannel (input);
    }

}
