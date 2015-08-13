package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

/**
 *
 * Proxy transform to pass a Window Transform and apply on a whole Channel
 *
 */
public class UseWindowFunctionSoundTransform implements SoundTransform<Channel, Channel> {

    private final AbstractWindowSoundTransform windowFunction;

    /**
     *
     * @param windowFunction1
     *            nested window transform
     */
    public UseWindowFunctionSoundTransform (final AbstractWindowSoundTransform windowFunction1) {
        this.windowFunction = windowFunction1;
    }

    @Override
    public Channel transform (final Channel input) throws SoundTransformException {
        return this.windowFunction.transformWholeChannel (input);
    }

}
