package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import org.toilelibre.libe.soundtransform.infrastructure.service.pack.PackAccessor;
import org.toilelibre.libe.soundtransform.model.library.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;

public abstract class Sound2NoteAccessor extends PackAccessor {

    protected FrequencyHelper provideFrequencyHelper () {
        return new CallHPSFrequencyHelper ();
    }

    protected ADSRHelper provideAdsrHelper () {
        return new MagnitudeADSRHelper ();
    }
}
