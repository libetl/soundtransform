package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

@Action
public final class ApplySoundTransform {

    private final CallTransformService<?> callTransform;

    public ApplySoundTransform (final Observer... observers) {
        this.callTransform = (CallTransformService<?>) ApplicationInjector.$.select (CallTransformService.class).setObservers (observers);
    }

    public <T, U> U [] apply (final T [] input, final SoundTransform<T, U> transform) throws SoundTransformException {
        return this.callTransform.apply (input, transform);
    }
}
