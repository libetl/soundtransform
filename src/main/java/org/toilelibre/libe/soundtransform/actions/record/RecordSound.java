package org.toilelibre.libe.soundtransform.actions.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.record.AmplitudeObserver;

public class RecordSound extends Action {

    public InputStream recordRawInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return this.recordSound.recordRawInputStream (streamInfo, stop);
    }

    public InputStream recordLimitedTimeRawInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        return this.recordSound.recordLimitedTimeRawInputStream (streamInfo);
    }

    public Sound startRecordingASound (final StreamInfo streamInfo, final AmplitudeObserver amplitudeObserver, Object stop) throws SoundTransformException {
        return this.recordSound.startRecordingASound (streamInfo, amplitudeObserver, stop);
    }
}
