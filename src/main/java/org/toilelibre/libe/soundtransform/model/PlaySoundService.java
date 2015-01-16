package org.toilelibre.libe.soundtransform.model;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class PlaySoundService<T> {

    private final PlaySoundProcessor<T> processor;

    public PlaySoundService (final PlaySoundProcessor<T> processor) {
        this.processor = processor;
    }

    public void play (final Sound [] channels) throws SoundTransformException {
        this.processor.play (channels);
    }

}
