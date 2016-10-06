package org.toilelibre.libe.soundtransform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertToInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetStreamInfo;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundToSpectrumsSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectException;

public class BlackBoxTest extends SoundTransformTest {
    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());
    private final File        gPiano3     = new File (this.classLoader.getResource ("gpiano3.wav").getFile ());
    private final File        output      = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

    @Test
    public void callPlaySoundFromOutside () throws SoundTransformException {
        new PlaySound ().play (new Sound (null), null, 0);

        final InputStream is = new ConvertToInputStream ().toStream (this.input);
        Sound sound = new ConvertFromInputStream ().fromInputStream (is);
        final SoundToSpectrumsSoundTransform sound2Spectrums = new SoundToSpectrumsSoundTransform ();
        final Spectrum<Serializable> [][] spectrums = new ApplySoundTransform ().apply (sound.getChannels (), sound2Spectrums);
        try {
            new PlaySound ().play (spectrums [0] [0], new Object (), 0);
        } catch (PlayObjectException poe) {
            
        }
    }

    @Test
    public void callTransformFromOutside () throws SoundTransformException {
        final InputStream is = new ConvertToInputStream ().toStream (this.input);
        final StreamInfo streamInfo = new GetStreamInfo ().getStreamInfo (new ConvertToInputStream ().toStream (this.input));
        Sound sound = new ConvertFromInputStream ().fromInputStream (is);
        sound = new Sound (new ApplySoundTransform ().apply (sound.getChannels (), new EightBitsSoundTransform (25)));
        final InputStream isOut = new ConvertToInputStream ().toStream (sound, streamInfo);
        new ExportAFile ().writeFile (isOut, this.output);
    }

    @Test
    public void getStreamInfo () throws SoundTransformException, IOException {
        new Slf4jObserver ().notify (new GetStreamInfo ().getStreamInfo (new BufferedInputStream (new FileInputStream (this.gPiano3))).toString ());
    }
}
