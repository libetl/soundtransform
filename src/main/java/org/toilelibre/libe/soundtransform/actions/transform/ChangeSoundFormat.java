package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

public class ChangeSoundFormat extends Action {

    public ChangeSoundFormat (final Observer... observers) {
        super (observers);
    }

    public Sound changeFormat (final Sound input, final FormatInfo formatInfo) {
        return this.modifySound.changeFormat (input, formatInfo);
    }
}
