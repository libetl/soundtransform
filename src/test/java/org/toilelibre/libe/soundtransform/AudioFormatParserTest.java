package org.toilelibre.libe.soundtransform;

import javax.sound.sampled.AudioFormat;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AudioFormatParserTest {

	@Test
	public void testCDFormat () {
		InputStreamInfo isi = new InputStreamInfo (2, 0, 2, 44100.0, false, true);
		AudioFormat audioFormat = (AudioFormat) new AudioFormatParser ().audioFormatfromInputStreamInfo (isi);
		InputStreamInfo isi2 = (InputStreamInfo) new AudioFormatParser ().fromAudioFormat (audioFormat, 0);
		assert isi.toString ().equals (audioFormat.toString ());
		assert isi2.toString ().equals (audioFormat.toString ());
	}

	@Test
	public void testIsiToAudioFormat () {
		InputStreamInfo isi = new InputStreamInfo (2, 0, 2, 44100.0, false, true);
		AudioFormat audioFormat = (AudioFormat) new AudioFormatParser ().audioFormatfromInputStreamInfo (isi);
		assert isi.toString ().equals (audioFormat.toString ());
	}

	@Test
	public void testAudioFormatToIsi () {
		AudioFormat audioFormat = new AudioFormat (44100.0f, 16, 2, true, false);
		InputStreamInfo isi2 = (InputStreamInfo) new AudioFormatParser ().fromAudioFormat (audioFormat, 0);
		assert isi2.toString ().equals (audioFormat.toString ());
	}
}
