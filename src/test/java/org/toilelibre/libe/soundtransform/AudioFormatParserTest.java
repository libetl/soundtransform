package org.toilelibre.libe.soundtransform;

import javax.sound.sampled.AudioFormat;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax.WavAudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioFormatParserTest {

    @Test
    public void testAudioFormatToIsi () {
        final AudioFormat audioFormat = new AudioFormat (44100.0f, 16, 2, true, false);
        final InputStreamInfo isi2 = new WavAudioFormatParser ().fromAudioFormat (audioFormat, 0);
        assert isi2.toString ().equals (audioFormat.toString ());
    }

    @Test
    public void testCDFormat () {
        final InputStreamInfo isi = new InputStreamInfo (2, 0, 2, 44100.0, false, true);
        final AudioFormat audioFormat = (AudioFormat) new WavAudioFormatParser ().audioFormatfromInputStreamInfo (isi);
        final InputStreamInfo isi2 = new WavAudioFormatParser ().fromAudioFormat (audioFormat, 0);
        assert isi.toString ().equals (audioFormat.toString ());
        assert isi2.toString ().equals (audioFormat.toString ());
    }

    @Test
    public void testIsiToAudioFormat () {
        final InputStreamInfo isi = new InputStreamInfo (2, 0, 2, 44100.0, false, true);
        final AudioFormat audioFormat = (AudioFormat) new WavAudioFormatParser ().audioFormatfromInputStreamInfo (isi);
        assert isi.toString ().equals (audioFormat.toString ());
    }
}
