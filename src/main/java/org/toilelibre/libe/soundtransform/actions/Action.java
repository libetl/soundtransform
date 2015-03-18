package org.toilelibre.libe.soundtransform.actions;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public abstract class Action {

    protected final CallTransformService      callTransform;
    protected final LoudestFreqsService       loudestFreqs;
    protected final ModifySoundService        modifySound;
    protected final PlaySoundService<?>       playSound;
    protected final ImportPackService         importPack;
    protected final InputStreamToSoundService is2Sound;
    protected final SoundToInputStreamService sound2is;
    protected final Library                   library;
    protected final AudioFileService          audioFile;

    public Action (final Observer... observers) {
        this.callTransform = $.create (CallTransformService.class, new Object [] { observers });
        this.audioFile = $.create (AudioFileService.class, new Object [] { observers });
        this.playSound = $.create (PlaySoundService.class);
        this.importPack = $.create (ImportPackService.class, new Object [] { observers });
        this.is2Sound = $.create (InputStreamToSoundService.class, new Object [] { observers });
        this.loudestFreqs = $.create (LoudestFreqsService.class);
        this.modifySound = $.create (ModifySoundService.class);
        this.sound2is = $.create(SoundToInputStreamService.class, new Object [] { observers });
        this.library = $.select (Library.class);
    }
}
