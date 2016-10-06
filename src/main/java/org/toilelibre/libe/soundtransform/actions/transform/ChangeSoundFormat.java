package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

@Action
public class ChangeSoundFormat {

    private final ModifySoundService modifySound;

    public ChangeSoundFormat () {
        this.modifySound = ApplicationInjector.$.select (ModifySoundService.class);
    }

    public Sound changeFormat (final Sound input, final FormatInfo formatInfo) {
        return this.modifySound.changeFormat (input, formatInfo);
    }
}
