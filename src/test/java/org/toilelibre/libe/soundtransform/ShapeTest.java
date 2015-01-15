package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;

public class ShapeTest {

    @Test
    public void testAppendSoundsWithDifferentNbBytes () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input1 = new File (classLoader.getResource ("notes/Piano2-D.wav").getFile ());
        final File input2 = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
        final Sound [] s1 = $.create (TransformSoundService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (input1));
        final Sound [] s2 = $.create (TransformSoundService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (input2));
        new ConvertedSoundAppender ().append (s2 [0], 1000, s1 [0]);
    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNote () throws SoundTransformException {

        try {
            System.out.println ("Loading packs");
            @SuppressWarnings ("unused")
            final Library packsList = Library.getInstance ();
            final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
            final File input = new File (classLoader.getResource ("notes/Piano5-G.wav").getFile ());
            final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
            final InputStream outputStream = $.create (TransformSoundService.class, new PrintlnTransformObserver ()).transformAudioStream ($.create (ConvertAudioFileService.class).callConverter (input), new ShapeSoundTransformation (Library.defaultPack, "chord_piano"));

            AudioSystem.write ((AudioInputStream) outputStream, AudioFileFormat.Type.WAVE, output);

            final int frequency = $.create (Sound2NoteService.class).convert ("output chord_note", $.create (TransformSoundService.class, new PrintlnTransformObserver ()).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (output))).getFrequency ();
            System.out.println ("Output chord note should be around 387Hz, but is " + frequency + "Hz");
        } catch (final IOException e) {
            e.printStackTrace ();
        }
    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNoteSameFrequency () throws SoundTransformException {

        try {
            System.out.println ("Loading packs");
            @SuppressWarnings ("unused")
            final Library packsList = Library.getInstance ();
            final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
            final File input = new File (classLoader.getResource ("notes/Piano3-E.wav").getFile ());
            final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
            final InputStream outputStream = $.create (TransformSoundService.class, new PrintlnTransformObserver ()).transformAudioStream ($.create (ConvertAudioFileService.class).callConverter (input), new ShapeSoundTransformation (Library.defaultPack, "chord_piano"));

            AudioSystem.write ((AudioInputStream) outputStream, AudioFileFormat.Type.WAVE, output);

            final int frequency = $.create (Sound2NoteService.class).convert ("output chord_note", $.create (TransformSoundService.class, new PrintlnTransformObserver ()).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (output))).getFrequency ();
            System.out.println ("Output chord note should be around 332Hz, but is " + frequency + "Hz");
        } catch (final IOException e) {
            e.printStackTrace ();
        }
    }
}
