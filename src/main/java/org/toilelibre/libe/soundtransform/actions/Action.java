package org.toilelibre.libe.soundtransform.actions;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;

public abstract class Action {

    protected TransformSoundService transformSound;
    protected PlaySoundService<?>   playSound;
    protected ImportPackService     importPackService;

    public Action (final org.toilelibre.libe.soundtransform.model.observer.Observer... observers) {
        this.transformSound = $.create (TransformSoundService.class, new Object [] { observers });
        this.playSound = $.create (PlaySoundService.class);
        this.importPackService = $.create (ImportPackService.class);
    }
}
