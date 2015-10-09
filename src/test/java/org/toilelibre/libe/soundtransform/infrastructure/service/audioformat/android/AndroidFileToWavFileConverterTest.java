package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class AndroidFileToWavFileConverterTest {

    @Test
    public void oggConvertToWav () throws SoundTransformException {
        new AndroidAudioFileHelper ().getUnknownInputStreamFromFile (new File (Thread.currentThread ().getContextClassLoader ().getResource ("raw/short.ogg").getFile ()));
    }
}
