package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.library.note.ComputedChordNote;
import org.toilelibre.libe.soundtransform.model.library.note.ComputedOrganNote;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.PureNote;

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
        final Sound s = new Sound (signal, sampleInBytes, samplerate, 1);

        final InputStream ais = $.create (TransformSoundService.class).toStream (new Sound [] { s }, new InputStreamInfo (1, s.getSamplesLength (), sampleInBytes, samplerate, false, true));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.create (ConvertAudioFileService.class).writeInputStream (ais, fDest);
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
        final Sound s = new Sound (signal, sampleInBytes, samplerate, 1);
        final SoundTransformation st = new SimpleFrequencySoundTransformation<Complex []> ();
        st.transform (s);

    }

    @Test
    public void testWithChordNote () throws SoundTransformException {
        final Note pureNote = new ComputedChordNote ();
        final SoundAppender soundAppender = $.select (SoundAppender.class);
        Sound s = pureNote.getAttack (270, 1, 2);
        s = soundAppender.append (s, pureNote.getDecay (270, 1, 2));
        s = soundAppender.append (s, pureNote.getSustain (270, 1, 2));
        s = soundAppender.append (s, pureNote.getRelease (270, 1, 2));
        final InputStream ais = $.create (TransformSoundService.class).toStream (new Sound [] { s }, new InputStreamInfo (1, s.getSamplesLength (), s.getNbBytesPerSample (), s.getSampleRate (), false, true));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.create (ConvertAudioFileService.class).writeInputStream (ais, fDest);
    }

    @Test
    public void testWithComputedOrganNote () throws SoundTransformException {
        final Note pureNote = new ComputedOrganNote ();
        final SoundAppender soundAppender = $.select (SoundAppender.class);
        Sound s = pureNote.getAttack (150, 1, 1);
        s = soundAppender.append (s, pureNote.getDecay (150, 1, 1));
        s = soundAppender.append (s, pureNote.getSustain (150, 1, 1));
        s = soundAppender.append (s, pureNote.getRelease (150, 1, 1));
        final InputStream ais = $.create (TransformSoundService.class).toStream (new Sound [] { s }, new InputStreamInfo (1, s.getSamplesLength (), s.getNbBytesPerSample (), s.getSampleRate (), false, true));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.create (ConvertAudioFileService.class).writeInputStream (ais, fDest);
    }

    @Test
    public void testWithPureNote () throws SoundTransformException {
        final Note pureNote = new PureNote ();
        final SoundAppender soundAppender = $.select (SoundAppender.class);
        Sound s = pureNote.getAttack (440, 1, 1);
        s = soundAppender.append (s, pureNote.getDecay (440, 1, 1));
        s = soundAppender.append (s, pureNote.getSustain (440, 1, 1));
        s = soundAppender.append (s, pureNote.getRelease (440, 1, 1));
        final InputStream ais = $.create (TransformSoundService.class).toStream (new Sound [] { s }, new InputStreamInfo (1, s.getSamplesLength (), s.getNbBytesPerSample (), s.getSampleRate (), false, true));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.create (ConvertAudioFileService.class).writeInputStream (ais, fDest);
    }
}
