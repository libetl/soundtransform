package org.toilelibre.libe.soundtransform.actions;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;

public abstract class Action {

    protected final TransformSoundService transformSound;
    protected final LoudestFreqsService   loudestFreqsService;
    protected final PlaySoundService<?>   playSound;
    protected final ImportPackService     importPackService;
    protected final Library               library;

    public Action (final org.toilelibre.libe.soundtransform.model.observer.Observer... observers) {
        this.transformSound = $.create (TransformSoundService.class, new Object [] { observers });
        this.playSound = $.create (PlaySoundService.class);
        this.importPackService = $.create (ImportPackService.class, new Object [] { observers });
        this.loudestFreqsService = $.create (LoudestFreqsService.class);
        this.library = $.select (Library.class);
    }
}
