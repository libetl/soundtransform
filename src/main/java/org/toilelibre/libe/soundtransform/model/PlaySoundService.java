package org.toilelibre.libe.soundtransform.model;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class PlaySoundService<T> {

    private PlaySoundProcessor<T> processor;

    public PlaySoundService (PlaySoundProcessor<T> processor) {
        this.processor = processor;
    }

    public void play (Sound [] channels) throws SoundTransformException {
        this.processor.play (channels);
    }

}
