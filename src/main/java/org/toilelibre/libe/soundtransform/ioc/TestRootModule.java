package org.toilelibre.libe.soundtransform.ioc;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.JavazoomAudioFileHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.WavAudioFormatParser;
import org.toilelibre.libe.soundtransform.infrastructure.service.play.javax.LineListenerPlaySoundProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class TestRootModule extends RootModule {

    @Override
    protected void declare () {
        super.declare ();
        super.bind (PlaySoundProcessor.class).to (new LineListenerPlaySoundProcessor ());
        super.bind (AudioFileHelper.class).to (new JavazoomAudioFileHelper ());
        super.bind (AudioFormatParser.class).to (new WavAudioFormatParser ());
    }

}
