package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioFormatParserTest {

    @Test
    public void testAudioFormatToIsi () {
        final InputStreamInfo audioFormat = new InputStreamInfo (2, 0, 2, 44100.0f, true, false);
        final Object isi2 = $.select (AudioFormatParser.class).audioFormatfromInputStreamInfo (audioFormat);
        assert isi2 != null;
    }

    @Test
    public void testCDFormat () {
        final InputStreamInfo isi = new InputStreamInfo (2, 0, 2, 44100.0, false, true);
        final Object audioFormat = $.select (AudioFormatParser.class).audioFormatfromInputStreamInfo (isi);
        final InputStreamInfo isi2 = $.select (AudioFormatParser.class).fromAudioFormat (audioFormat, 0);
        assert isi.toString ().equals (audioFormat.toString ());
        assert isi2.toString ().equals (audioFormat.toString ());
    }

    @Test
    public void testIsiToAudioFormat () {
        final InputStreamInfo isi = new InputStreamInfo (2, 0, 2, 44100.0, false, true);
        final Object audioFormat = $.select (AudioFormatParser.class).audioFormatfromInputStreamInfo (isi);
        assert isi.toString ().equals (audioFormat.toString ());
    }
}
