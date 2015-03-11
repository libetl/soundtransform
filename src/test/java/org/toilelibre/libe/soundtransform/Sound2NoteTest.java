package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.AddNoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class Sound2NoteTest extends SoundTransformTest {

    @Test
    public void fileNotFound () throws SoundTransformException {
        final Range range = new Range ();
        $.create (AddNoteService.class).addNote (range, "notAFile.wav");
        org.junit.Assert.assertTrue (range.size () == 0);
    }

    @Test
    public void readNotes () throws SoundTransformException {
        final Range range = new Range ();
        $.create (AddNoteService.class).addNotes (range, "gpiano3.wav", "gpiano4.wav");
        org.junit.Assert.assertTrue (range.size () == 2);
    }

    @Test
    public void run () throws SoundTransformException {

        final Map<String, Integer> frequenciesPerSound = new HashMap<String, Integer> () {
            /**
             *
             */
            private static final long serialVersionUID = -7749603459667098370L;

            {
                this.put ("Piano1-C.wav", 260);// OK
                this.put ("Piano2-D.wav", 293);// OK
                this.put ("Piano3-E.wav", 332);// OK
                this.put ("Piano4-F.wav", 344);// OK
                this.put ("Piano5-G.wav", 387);// OK
                this.put ("Piano6-A.wav", 451);// OK
                this.put ("Piano7-B.wav", 499);// OK
                this.put ("Piano8-C.wav", 524);// OK
            }
        };
        new Slf4jObserver ().notify ("Loading Packs");
        $.create (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN)).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        final Pack pack = $.select (Library.class).getPack ("default");
        for (final Entry<String, Range> packEntry : pack.entrySet ()) {
            for (final Entry<Float, Note> noteEntry : packEntry.getValue ().entrySet ()) {
                final Note n = noteEntry.getValue ();
                if (frequenciesPerSound.get (n.getName ()) != null) {
                    org.junit.Assert.assertEquals (frequenciesPerSound.get (n.getName ()).intValue (), n.getFrequency (), 0);
                    new Slf4jObserver ().notify ("f0 (" + n.getName () + ") = " + n.getFrequency ());
                } else {
                    new Slf4jObserver ().notify ("Did not find " + n.getName ());
                }
            }
        }
    }

    @Test
    public void shouldBeTwiceTheF0ValuePiano4F () throws SoundTransformException {
        final ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano3e.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.create (ConvertAudioFileService.class).callConverter (input);
        final TransformSoundService ts = $.create (TransformSoundService.class);

        final Sound [] f4 = ts.fromInputStream (ais);
        final PitchSoundTransformation pitcher = new PitchSoundTransformation (200);
        final Sound f51 = pitcher.transform (f4 [0]);
        final Sound f52 = pitcher.transform (f4 [1]);

        final Note n = $.create (Sound2NoteService.class).convert ("piano4e.wav", new Sound [] { f51, f52 });
        new Slf4jObserver ().notify ("e' 4 : " + n.getFrequency () + "Hz, should be around 664Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 664 - 10 && n.getFrequency () < 664 + 10);
    }

    @Test
    public void shouldNotBeTwiceTheF0ValuePiano1C () throws SoundTransformException {
        final ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano1c.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.create (ConvertAudioFileService.class).callConverter (input);
        final TransformSoundService ts = $.create (TransformSoundService.class);

        final Note n = $.create (Sound2NoteService.class).convert ("piano1c.wav", ts.fromInputStream (ais));
        new Slf4jObserver ().notify ("c' 1-line octave : " + n.getFrequency () + "Hz, should be around 261Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 261 - 10 && n.getFrequency () < 261 + 10);
    }

    @Test
    public void shouldNotBeTwiceTheF0ValuePiano4F () throws SoundTransformException {
        final ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano4f.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.create (ConvertAudioFileService.class).callConverter (input);
        final TransformSoundService ts = $.create (TransformSoundService.class);

        final Note n = $.create (Sound2NoteService.class).convert ("piano4f.wav", ts.fromInputStream (ais));
        new Slf4jObserver ().notify ("f' 4 : " + n.getFrequency () + "Hz, should be around 349Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 349 - 10 && n.getFrequency () < 349 + 10);
    }

    @Test
    public void shouldRecognizeAPure440Note () throws SoundTransformException {
        final int length = 10000;

        final int samplerate = 44100;
        final long [] signal = new long [length];
        for (int j = 0 ; j < length ; j++) {
            signal [j] = (long) (Math.sin (j * 440 * 2 * Math.PI / samplerate) * 32768.0);
        }
        final Sound s = new Sound (signal, new FormatInfo (2, samplerate), 1);
        final Note n = $.create (Sound2NoteService.class).convert ("Sample A4 (440 Hz) Sound", new Sound [] { s });

        new Slf4jObserver ().notify ("Sample A4 (440Hz) Sound, but frequency found was " + n.getFrequency () + "Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 440 - 10 && n.getFrequency () < 440 + 10);
        new Slf4jObserver ().notify ("...acceptable");
    }

    @Test
    public void shouldRecognizeSimpleNotes () throws SoundTransformException {
        final int length = 2000;
        final int [] notes = new int [] { 261, 293, 329, 349, 392, 440, 493 };
        final String [] notesTitle = new String [] { "C4", "D4", "E4", "F4", "G4", "A4", "B4" };

        for (int i = 0 ; i < notes.length ; i++) {
            final int samplerate = 11025;
            final long [] signal = new long [length];
            for (int j = 0 ; j < length ; j++) {
                signal [j] = (long) (Math.sin (j * notes [i] * 2 * Math.PI / samplerate) * 32768.0);
            }
            final Sound s = new Sound (signal, new FormatInfo (2, samplerate), 1);
            final Note n = $.create (Sound2NoteService.class).convert ("Sample " + notesTitle [i] + "(" + notes [i] + "Hz) Sound", new Sound [] { s });

            new Slf4jObserver ().notify ("Sample " + notesTitle [i] + "(" + notes [i] + "Hz) Sound, but frequency found was " + n.getFrequency () + "Hz");
            org.junit.Assert.assertTrue (n.getFrequency () > notes [i] - 10 && n.getFrequency () < notes [i] + 10);
            new Slf4jObserver ().notify ("...acceptable");
        }
    }

    @Test
    public void strangeFileName () throws SoundTransformException {
        final Range range = new Range ();
        $.create (AddNoteService.class).addNote (range, "sftp://mywebsite.fr");
        org.junit.Assert.assertTrue (range.size () == 0);
    }
}
