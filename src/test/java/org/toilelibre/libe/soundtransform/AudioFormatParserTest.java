package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class AudioFormatParserTest extends SoundTransformTest {

    @Test
    public void testAudioFormatToIsi () {
        final StreamInfo audioFormat = new StreamInfo (2, 0, 2, 44100.0f, false, true, null);
        final Object isi2 = $.select (AudioFormatParser.class).audioFormatfromStreamInfo (audioFormat);
        org.junit.Assert.assertNotSame (null, isi2);
    }

    @Test
    public void testCDFormat () throws SoundTransformException {
        final StreamInfo isi = new StreamInfo (2, 0, 2, 44100.0f, false, true, null);
        final Object audioFormat = $.select (AudioFormatParser.class).audioFormatfromStreamInfo (isi);
        final FormatInfo fi = $.select (AudioFormatParser.class).fromAudioFormat (audioFormat, 0);
        org.junit.Assert.assertEquals (audioFormat.toString (), isi.toString ());
        org.junit.Assert.assertEquals (audioFormat.toString (), fi.toString ());
    }

    @Test
    public void testIsiToAudioFormat () {
        final StreamInfo isi = new StreamInfo (2, 0, 2, 44100.0f, false, true, null);
        final Object audioFormat = $.select (AudioFormatParser.class).audioFormatfromStreamInfo (isi);
        org.junit.Assert.assertEquals (audioFormat.toString (), isi.toString ());
    }
}
