package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import org.toilelibre.libe.soundtransform.ioc.RootModuleWithoutAccessor;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;

public abstract class AppenderAccessor extends RootModuleWithoutAccessor {

    protected SoundAppender provideSoundAppender () {
        return new ConvertedSoundAppender ();
    }

    protected SoundPitchAndTempoHelper provideSoundPitchAndTempoHelper () {
        return new ConvertedSoundPitchAndTempoHelper ();
    }
}
