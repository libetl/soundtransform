package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.WavAudioFormatParser;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;

public class PitchAndSpeedHelperTest {

    @Test
    public void shouldBeTwiceTheF0ValuePiano3e () throws SoundTransformException {
        final ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("notes/Piano3-E.wav");
        final File input = new File (fileURL.getFile ());

        final AudioInputStream ais = (AudioInputStream) $.create (ConvertAudioFileService.class).callConverter (input);
        final TransformSoundService ts = $.create (TransformSoundService.class);

        final Sound [] e3 = ts.fromInputStream (ais);
        final SoundPitchAndTempoHelper helper = new ConvertedSoundPitchAndTempoHelper ();
        final Sound [] e4 = new Sound [2];
        e4 [0] = helper.pitchAndSetLength (e3 [0], 200, 1);
        e4 [1] = helper.pitchAndSetLength (e3 [1], 200, 1);

        final InputStream ais2 = ts.toStream (e4, new WavAudioFormatParser ().fromAudioFormat (ais.getFormat (), (int) ais.getFormat ().getSampleRate ()));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        try {
            AudioSystem.write ((AudioInputStream) ais2, AudioFileFormat.Type.WAVE, fDest);
        } catch (final IOException e) {
        }
        final Note n = $.create (Sound2NoteService.class).convert ("e4", e4);
        new Slf4jObserver ().notify ("e' 4 : " + n.getFrequency () + "Hz, should be around 658Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 658 - 10 && n.getFrequency () < 658 + 10);
    }
}
