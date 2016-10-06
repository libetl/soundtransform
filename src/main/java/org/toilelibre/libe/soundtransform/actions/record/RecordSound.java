package org.toilelibre.libe.soundtransform.actions.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.LoudestFreqsService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.logging.Observer;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectService;
import org.toilelibre.libe.soundtransform.model.record.AmplitudeObserver;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundService;

@Action
public class RecordSound {

    private final RecordSoundService recordSound;

    public RecordSound () {
        this.recordSound = ApplicationInjector.$.select (RecordSoundService.class);
    }

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
