package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.exporter.BytesExportAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.note.FrequencyHelper;

public abstract class SoundToNoteAccessor extends BytesExportAccessor {

    protected FrequencyHelper provideFrequencyHelper () {
        return new CallHPSFrequencyHelper ();
    }

    protected ADSRHelper provideAdsrHelper () {
        return new MagnitudeADSRHelper ();
    }
}
