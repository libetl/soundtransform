package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor.FrameProcessorErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class ByteArrayFrameProcessorTest {

    @Test (expected = SoundTransformException.class)
    public void simulateAvailableFailure () throws IOException, SoundTransformException {
        final InputStream ais = Mockito.mock (InputStream.class);
        Mockito.when (ais.available ()).thenThrow (new IOException ("Could not open stream"));
        try {
            new ByteArrayFrameProcessor ().fromInputStream (ais, new StreamInfo (2, -1, 2, 441000.0f, false, true, null));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (FrameProcessorErrorCode.COULD_NOT_FIND_LENGTH, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void simulateCloseFailure () throws IOException, SoundTransformException {
        final InputStream ais = Mockito.mock (InputStream.class);
        Mockito.doThrow (new IOException ("Could not close stream")).when (ais).close ();
        try {
            new ByteArrayFrameProcessor ().fromInputStream (ais, new StreamInfo (2, 200000, 2, 441000.0f, false, true, null));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (FrameProcessorErrorCode.COULD_NOT_CLOSE_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void simulateReadFailure () throws IOException, SoundTransformException {
        final InputStream ais = Mockito.mock (InputStream.class);
        Mockito.when (ais.read (Matchers.any (byte [].class))).thenThrow (new IOException ("Could not read stream"));
        try {
            new ByteArrayFrameProcessor ().fromInputStream (ais, new StreamInfo (2, 200000, 2, 441000.0f, false, true, null));
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (FrameProcessorErrorCode.COULD_NOT_READ_STREAM, ste.getErrorCode ());
            throw ste;
        }
    }
}
