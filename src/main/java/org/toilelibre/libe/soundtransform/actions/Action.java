package org.toilelibre.libe.soundtransform.actions;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.observer.Observer;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundService;

public abstract class Action {

    protected final CallTransformService<?>      callTransform;
    protected final LoudestFreqsService          loudestFreqs;
    protected final ModifySoundService           modifySound;
    protected final RecordSoundService<?>        recordSound;
    protected final PlaySoundService<?>          playSound;
    protected final ImportPackService<?>         importPack;
    protected final InputStreamToSoundService<?> is2Sound;
    protected final SoundToInputStreamService<?> sound2is;
    protected final Library                      library;
    protected final AudioFileService<?>          audioFile;

    public Action (final Observer... observers) {
        this.callTransform = (CallTransformService<?>) $.select (CallTransformService.class).setObservers (observers);
        this.audioFile = (AudioFileService<?>) $.select (AudioFileService.class).setObservers (observers);
        this.playSound = $.select (PlaySoundService.class);
        this.recordSound = $.select (RecordSoundService.class);
        this.importPack = (ImportPackService<?>) $.select (ImportPackService.class).setObservers (observers);
        this.is2Sound = (InputStreamToSoundService<?>) $.select (InputStreamToSoundService.class).setObservers (observers);
        this.loudestFreqs = $.select (LoudestFreqsService.class);
        this.modifySound = $.select (ModifySoundService.class);
        this.sound2is = (SoundToInputStreamService<?>) $.select (SoundToInputStreamService.class).setObservers (observers);
        this.library = $.select (Library.class);
    }
}
