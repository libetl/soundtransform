package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import org.toilelibre.libe.soundtransform.ioc.AllServicesAccessors;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;

public abstract class AppenderAccessor extends AllServicesAccessors {

    protected SoundAppender provideSoundAppender () {
        return new ConvertedSoundAppender ();
    }

    protected SoundPitchAndTempoHelper provideSoundPitchAndTempoHelper () {
        return new ConvertedSoundPitchAndTempoHelper ();
    }
}
