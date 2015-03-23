package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.ByteArrayInputStream;

import org.junit.Assert;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class WriteInputStreamToByteArrayTest {

    @Test
    public void errorWhileReadingStream () throws SoundTransformException {
        try {
        WriteInputStreamToByteArray writer = new WriteInputStreamToByteArray ();
        writer.convertToByteArray (new ByteArrayInputStream (new byte [1]));
        }catch (SoundTransformException ste){
            Assert.assertEquals (ste.getErrorCode (), WriteInputStreamToByteArray.WriteInputStreamToByteArrayErrorCode.ERROR_WHILE_READING_STREAM);
        }
    }
}
