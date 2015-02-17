package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class ChangeSoundFormat extends Action {

    public ChangeSoundFormat (final Observer... observers) {
        super (observers);
    }

    public Sound [] changeFormat (final Sound [] input, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
        return this.transformSound.changeSoundFormat (input, inputStreamInfo);
    }
}
