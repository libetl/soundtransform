package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface RecordSoundService<T> extends LogAware<T> {

    public abstract InputStream recordRawInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException;

    public abstract InputStream recordLimitedTimeRawInputStream (StreamInfo streamInfo) throws SoundTransformException;

    public abstract Sound startRecordingASound (StreamInfo streamInfo, Object stop) throws SoundTransformException;

}