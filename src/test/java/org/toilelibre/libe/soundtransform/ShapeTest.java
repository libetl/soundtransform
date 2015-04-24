package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.SoundToNoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class ShapeTest extends SoundTransformTest {

    @Test
    public void testAppendSoundsWithDifferentNbBytes () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input1 = new File (classLoader.getResource ("piano2d.wav").getFile ());
        final File input2 = new File (classLoader.getResource ("gpiano3.wav").getFile ());
        final Sound [] s1 = $.select (InputStreamToSoundService.class).fromInputStream ($.select (AudioFileService.class).streamFromFile (input1));
        final Sound [] s2 = $.select (InputStreamToSoundService.class).fromInputStream ($.select (AudioFileService.class).streamFromFile (input2));
        $.select (SoundAppender.class).append (s2 [0], 1000, s1 [0]);
    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNote () throws SoundTransformException {

        Sound [] sound = FluentClient.start ().withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json")).withClasspathResource ("piano5g.wav").convertIntoSound ().findLoudestFrequencies ().shapeIntoSound ("default", "chord_piano", new FormatInfo (2, 44100)).stopWithSound ();
        ((ImportPackService<?>) $.select (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN))).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));

        final float frequency = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("output chord_note"), sound).getFrequency ();
        FluentClient.start ().withSound (sound).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
        new Slf4jObserver ().notify ("Output chord note should be around 387Hz, but is " + frequency + "Hz");

    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNoteSameFrequency () throws SoundTransformException {

        Sound [] sound = FluentClient.start ().withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json")).withClasspathResource ("piano3e.wav").convertIntoSound ().findLoudestFrequencies ().shapeIntoSound ("default", "chord_piano", new FormatInfo (2, 44100)).stopWithSound ();
        ((ImportPackService<?>) $.select (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN))).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));

        final float frequency = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("output chord_note"), sound).getFrequency ();
        FluentClient.start ().withSound (sound).exportToClasspathResourceWithSiblingResource ("after.wav", "before.wav");
        new Slf4jObserver ().notify ("Output chord note should be around 332Hz, but is " + frequency + "Hz");
    }
}
