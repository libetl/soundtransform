package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;


public class AndroidFileToWavFileConverterTest {
    
    @Test
    public void oggConvertToWav () throws SoundTransformException {
        new AndroidFileToWavFileConverter.OGGConverter ().convert (new File ("/mnt/data/lionel/Musique/Dan Gna - Les Go.ogg"));
    }
}
