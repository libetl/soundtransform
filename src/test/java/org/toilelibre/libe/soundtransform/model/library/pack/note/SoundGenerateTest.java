package org.toilelibre.libe.soundtransform.model.library.pack.note;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;

public class SoundGenerateTest extends SoundTransformTest {

    @Test
    public void generateA440HzSound () throws SoundTransformException {
        final int length = 4000;
        final int soundfreq = 440;
        final int sampleInBytes = 2;

        final int samplerate = 44100;
        final long [] signal = new long [length];
        for (int j = 0 ; j < length ; j++) {
            signal [j] = (long) (Math.sin (j * soundfreq * 2 * Math.PI / samplerate) * 32768.0);
        }
        final Channel s = new Channel (signal, new FormatInfo (sampleInBytes, samplerate), 1);

        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (new Sound (new Channel [] { s }), new StreamInfo (1, s.getSamplesLength (), sampleInBytes, samplerate, false, true, null));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.select (AudioFileService.class).fileFromStream (ais, fDest);
    }

    @Test
    public void seeHps () throws SoundTransformException {
        final int length = 10000;
        final int soundfreq = 440;
        final int sampleInBytes = 2;

        final int samplerate = 44100;
        final long [] signal = new long [length];
        for (int j = 0 ; j < length ; j++) {
            signal [j] = (long) (Math.sin (j * soundfreq * 2 * Math.PI / samplerate) * 32768.0);
        }
        final Channel s = new Channel (signal, new FormatInfo (sampleInBytes, samplerate), 1);
        final SoundTransform<Channel, Channel> st = new SimpleFrequencySoundTransform<Complex []> ();
        st.transform (s);

    }

    @Test
    public void testWithChordNote () throws SoundTransformException {
        final Note pureNote = new ComputedChordNote ();
        final SoundAppender soundAppender = $.select (SoundAppender.class);
        Channel s = pureNote.getAttack (270, 1, 2);
        s = soundAppender.append (s, pureNote.getDecay (270, 1, 2));
        s = soundAppender.append (s, pureNote.getSustain (270, 1, 2));
        s = soundAppender.append (s, pureNote.getRelease (270, 1, 2));
        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (new Sound (new Channel [] { s }), new StreamInfo (1, s.getSamplesLength (), s.getSampleSize (), s.getSampleRate (), false, true, null));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.select (AudioFileService.class).fileFromStream (ais, fDest);
    }

    @Test
    public void testWithComputedOrganNote () throws SoundTransformException {
        final Note pureNote = new ComputedOrganNote ();
        final SoundAppender soundAppender = $.select (SoundAppender.class);
        Channel s = pureNote.getAttack (150, 1, 1);
        s = soundAppender.append (s, pureNote.getDecay (150, 1, 1));
        s = soundAppender.append (s, pureNote.getSustain (150, 1, 1));
        s = soundAppender.append (s, pureNote.getRelease (150, 1, 1));
        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (new Sound (new Channel [] { s }), new StreamInfo (1, s.getSamplesLength (), s.getSampleSize (), s.getSampleRate (), false, true, null));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.select (AudioFileService.class).fileFromStream (ais, fDest);
    }

    @Test
    public void testWithPureNote () throws SoundTransformException {
        final Note pureNote = new PureNote ();
        final SoundAppender soundAppender = $.select (SoundAppender.class);
        Channel s = pureNote.getAttack (440, 1, 1);
        s = soundAppender.append (s, pureNote.getDecay (440, 1, 1));
        s = soundAppender.append (s, pureNote.getSustain (440, 1, 1));
        s = soundAppender.append (s, pureNote.getRelease (440, 1, 1));
        final InputStream ais = $.select (SoundToInputStreamService.class).toStream (new Sound (new Channel [] { s }), new StreamInfo (1, s.getSamplesLength (), s.getSampleSize (), s.getSampleRate (), false, true, null));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.select (AudioFileService.class).fileFromStream (ais, fDest);
    }
}
