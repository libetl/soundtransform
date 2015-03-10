package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class ShapeTest extends SoundTransformTest {

    @Test
    public void testAppendSoundsWithDifferentNbBytes () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input1 = new File (classLoader.getResource ("piano2d.wav").getFile ());
        final File input2 = new File (classLoader.getResource ("gpiano3.wav").getFile ());
        final Sound [] s1 = $.create (TransformSoundService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (input1));
        final Sound [] s2 = $.create (TransformSoundService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (input2));
        new ConvertedSoundAppender ().append (s2 [0], 1000, s1 [0]);
    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNote () throws SoundTransformException {

        new Slf4jObserver ().notify ("Loading packs");
        $.create (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN)).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("piano5g.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final InputStream outputStream = $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformAudioStream ($.create (ConvertAudioFileService.class).callConverter (input), new ShapeSoundTransformation ("default", "chord_piano"));

        $.create (ConvertAudioFileService.class).writeInputStream (outputStream, output);

        final float frequency = $.create (Sound2NoteService.class).convert ("output chord_note", $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (output))).getFrequency ();
        new Slf4jObserver ().notify ("Output chord note should be around 387Hz, but is " + frequency + "Hz");

    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNoteSameFrequency () throws SoundTransformException {

        new Slf4jObserver ().notify ("Loading packs");
        $.create (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN)).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpack.json"));
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("piano3e.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final InputStream outputStream = $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).transformAudioStream ($.create (ConvertAudioFileService.class).callConverter (input), new ShapeSoundTransformation ("default", "chord_piano"));

        $.create (ConvertAudioFileService.class).writeInputStream (outputStream, output);

        final float frequency = $.create (Sound2NoteService.class).convert ("output chord_note", $.create (TransformSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (output))).getFrequency ();
        new Slf4jObserver ().notify ("Output chord note should be around 332Hz, but is " + frequency + "Hz");

    }
}
