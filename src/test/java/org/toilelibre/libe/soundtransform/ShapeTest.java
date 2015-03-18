package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class ShapeTest extends SoundTransformTest {

    @Test
    public void testAppendSoundsWithDifferentNbBytes () throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input1 = new File (classLoader.getResource ("piano2d.wav").getFile ());
        final File input2 = new File (classLoader.getResource ("gpiano3.wav").getFile ());
        final Sound [] s1 = $.create (InputStreamToSoundService.class).fromInputStream ($.create (AudioFileService.class).streamFromFile (input1));
        final Sound [] s2 = $.create (InputStreamToSoundService.class).fromInputStream ($.create (AudioFileService.class).streamFromFile (input2));
        $.select (SoundAppender.class).append (s2 [0], 1000, s1 [0]);
    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNote () throws SoundTransformException {

        new Slf4jObserver ().notify ("Loading packs");
        $.create (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN)).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("piano5g.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound [] sounds = $.create(CallTransformService.class).apply($.create (InputStreamToSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream ($.create (AudioFileService.class).streamFromFile (input)), new ShapeSoundTransformation ("default", "chord_piano"));

        final InputStream finalInputStream = $.create(SoundToInputStreamService.class).toStream(sounds, StreamInfo.from(sounds [0].getFormatInfo(), sounds));
        $.create (AudioFileService.class).fileFromStream (finalInputStream, output);

        final float frequency = $.create (Sound2NoteService.class).convert (new SimpleNoteInfo ("output chord_note"), $.create (InputStreamToSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream ($.create (AudioFileService.class).streamFromFile (output))).getFrequency ();
        new Slf4jObserver ().notify ("Output chord note should be around 387Hz, but is " + frequency + "Hz");

    }

    @Test
    public void testShapeASimplePianoNoteAsAChordNoteSameFrequency () throws SoundTransformException {

        new Slf4jObserver ().notify ("Loading packs");
        $.create (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN)).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input = new File (classLoader.getResource ("piano3e.wav").getFile ());
        final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
        final Sound [] sounds = $.create(CallTransformService.class).apply($.create (InputStreamToSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream ($.create (AudioFileService.class).streamFromFile (input)), new ShapeSoundTransformation ("default", "chord_piano"));

        final InputStream finalInputStream = $.create(SoundToInputStreamService.class).toStream(sounds, StreamInfo.from(sounds [0].getFormatInfo(), sounds));
        $.create (AudioFileService.class).fileFromStream (finalInputStream, output);

        final float frequency = $.create (Sound2NoteService.class).convert (new SimpleNoteInfo ("output chord_note"), $.create (InputStreamToSoundService.class, new Slf4jObserver (LogLevel.WARN)).fromInputStream ($.create (AudioFileService.class).streamFromFile (output))).getFrequency ();
        new Slf4jObserver ().notify ("Output chord note should be around 332Hz, but is " + frequency + "Hz");

    }
}
