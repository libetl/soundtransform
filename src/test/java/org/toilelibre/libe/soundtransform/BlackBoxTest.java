package org.toilelibre.libe.soundtransform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetStreamInfo;
import org.toilelibre.libe.soundtransform.actions.transform.ToInputStream;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class BlackBoxTest extends SoundTransformTest {
    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    private final File        gPiano3     = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());
    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void callPlaySoundFromOutside () throws SoundTransformException {
        new PlaySound ().play (new Sound (null));
    }

    @Test
    public void callTransformFromOutside () throws SoundTransformException {
        final InputStream is = new ToInputStream ().toStream (this.input);
        final StreamInfo streamInfo = new GetStreamInfo ().getStreamInfo (new ToInputStream ().toStream (this.input));
        Sound sound = new ConvertFromInputStream ().fromInputStream (is);
        sound = new Sound (new ApplySoundTransform ().apply (sound.getChannels(), new EightBitsSoundTransform (25)));
        final InputStream isOut = new ToInputStream ().toStream (sound, streamInfo);
        new ExportAFile ().writeFile (isOut, this.output);
    }

    @Test
    public void getStreamInfo () throws SoundTransformException, IOException {
        new Slf4jObserver ().notify (new GetStreamInfo ().getStreamInfo (new BufferedInputStream (new FileInputStream (this.gPiano3))).toString ());
    }
}
