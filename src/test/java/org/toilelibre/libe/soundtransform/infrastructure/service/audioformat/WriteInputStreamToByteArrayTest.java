package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class WriteInputStreamToByteArrayTest {

    @Test (expected = SoundTransformException.class)
    public void errorWhileReadingStream () throws SoundTransformException {
        try {
            final WriteInputStreamToByteArray writer = new WriteInputStreamToByteArray ();
            InputStream failingInputStream = Mockito.mock (InputStream.class);
            try {
                Mockito.when (failingInputStream.read (Mockito.any (byte [].class), Mockito.any (int.class), Mockito.any (int.class))).thenThrow (
                        new IOException ("Forced failure"));
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
            writer.convertToByteArray (failingInputStream);
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), WriteInputStreamToByteArray.WriteInputStreamToByteArrayErrorCode.ERROR_WHILE_READING_STREAM);
            throw ste;
        }
    }
}
