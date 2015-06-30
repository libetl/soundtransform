package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.SoundToNoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.AddNoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;

public class SoundToNoteTest extends SoundTransformTest {

    @Test
    public void fileNotFound () throws SoundTransformException {
        final Range range = new Range ();
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("notAFile.wav"));
        org.junit.Assert.assertTrue (range.size () == 0);
    }

    @Test
    public void readNotes () throws SoundTransformException {
        final Range range = new Range ();
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("gpiano3.wav"));
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("gpiano4.wav"));
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
                this.put ("piano1c.wav", 260);// OK
                this.put ("piano2d.wav", 293);// OK
                this.put ("piano3e.wav", 332);// OK
                this.put ("piano4f.wav", 344);// OK
                this.put ("piano5g.wav", 387);// OK
                this.put ("piano6a.wav", 451);// OK
                this.put ("piano7b.wav", 499);// OK
                this.put ("piano8c.wav", 524);// OK
            }
        };
        new Slf4jObserver ().notify ("Loading Packs");
        FluentClient.start().withAPack("default",  Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        final Pack pack = $.select (Library.class).getPack ("default");
        for (final Entry<String, Range> packEntry : pack.entrySet ()) {
            for (final Entry<Float, Note> noteEntry : packEntry.getValue ().entrySet ()) {
                final Note n = noteEntry.getValue ();
                if (frequenciesPerSound.get (n.getName ()) != null) {
                    org.junit.Assert.assertEquals (frequenciesPerSound.get (n.getName ()).intValue (), n.getFrequency (), 1);
                    new Slf4jObserver ().notify ("f0 (" + n.getName () + ") = " + n.getFrequency ());
                } else {
                    new Slf4jObserver ().notify ("Did not find " + n.getName ());
                }
            }
        }
    }

    @Test
    public void shouldBeTwiceTheF0ValuePiano4F () throws SoundTransformException {
        final ClassLoader classLoader = SoundToNoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano3e.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.select (AudioFileService.class).streamFromFile (input);

        final Sound f4 = $.select (InputStreamToSoundService.class).fromInputStream (ais);
        final PitchSoundTransform pitcher = new PitchSoundTransform (200);
        final Channel f51 = pitcher.transform (f4.getChannels () [0]);
        final Channel f52 = pitcher.transform (f4.getChannels () [1]);

        final Note n = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("piano4e.wav"), new Sound (new Channel [] { f51, f52 }));
        new Slf4jObserver ().notify ("e' 4 : " + n.getFrequency () + "Hz, should be around 664Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 664 - 10 && n.getFrequency () < 664 + 10);
    }

    @Test
    public void shouldNotBeTwiceTheF0ValuePiano1C () throws SoundTransformException {
        final ClassLoader classLoader = SoundToNoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano1c.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.select (AudioFileService.class).streamFromFile (input);

        final Note n = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("piano1c.wav"), $.select (InputStreamToSoundService.class).fromInputStream (ais));
        new Slf4jObserver ().notify ("c' 1-line octave : " + n.getFrequency () + "Hz, should be around 261Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 261 - 10 && n.getFrequency () < 261 + 10);
    }

    @Test
    public void shouldNotBeTwiceTheF0ValuePiano4F () throws SoundTransformException {
        final ClassLoader classLoader = SoundToNoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano4f.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.select (AudioFileService.class).streamFromFile (input);

        final Note n = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("piano4f.wav"), $.select (InputStreamToSoundService.class).fromInputStream (ais));
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
        final Channel s = new Channel (signal, new FormatInfo (2, samplerate), 1);
        final Note n = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("Sample A4 (440 Hz) Sound"), new Sound (new Channel [] { s }));

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
            final Channel s = new Channel (signal, new FormatInfo (2, samplerate), 1);
            final Note n = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("Sample " + notesTitle [i] + "(" + notes [i] + "Hz) Sound"), new Sound (new Channel [] { s }));

            new Slf4jObserver ().notify ("Sample " + notesTitle [i] + "(" + notes [i] + "Hz) Sound, but frequency found was " + n.getFrequency () + "Hz");
            org.junit.Assert.assertTrue (n.getFrequency () > notes [i] - 10 && n.getFrequency () < notes [i] + 10);
            new Slf4jObserver ().notify ("...acceptable");
        }
    }

    @Test
    public void strangeFileName () throws SoundTransformException {
        final Range range = new Range ();
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("sftp://mywebsite.fr"));
        org.junit.Assert.assertTrue (range.size () == 0);
    }
}
