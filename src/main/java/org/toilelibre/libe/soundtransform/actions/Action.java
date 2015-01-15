package org.toilelibre.libe.soundtransform.actions;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;

public abstract class Action {

    protected org.toilelibre.libe.soundtransform.model.TransformSoundService transformSound;

    public Action (final org.toilelibre.libe.soundtransform.model.observer.Observer... observers) {
        this.transformSound = $.create (TransformSoundService.class, (Object []) observers);
    }
}
