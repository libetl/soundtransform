package org.toilelibre.libe.soundtransform.actions;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;

public abstract class Action {

    protected org.toilelibre.libe.soundtransform.model.TransformSoundService transformSound;

    public Action (
            final org.toilelibre.libe.soundtransform.model.observer.Observer... observers) {
        this.transformSound = new org.toilelibre.libe.soundtransform.model.TransformSoundService (
                $.select (FrameProcessor.class),
                $.select (AudioFileHelper.class),
                $.select (AudioFormatParser.class), observers);
    }
}
