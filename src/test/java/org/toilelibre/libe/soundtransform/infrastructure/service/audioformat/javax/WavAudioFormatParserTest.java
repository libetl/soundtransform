package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;

public class WavAudioFormatParserTest {

    @Test
    public void wrongTypeTest () throws SoundTransformException {
        try {
            new WavAudioFormatParser ().fromAudioFormat (new Object (), 0);
            Assert.fail ("Should have failed with read error");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), AudioFormatParser.AudioFormatParserErrorCode.READ_ERROR);
        }
    }

    @Test
    public void notASoundFileTest () throws SoundTransformException {
        try {
            new WavAudioFormatParser ().getStreamInfo (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
            Assert.fail ("Should have failed with read error");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), AudioFormatParser.AudioFormatParserErrorCode.WRONG_TYPE);
        }
    }
}
