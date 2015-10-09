package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.SoundToInputStreamService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.library.pack.note.Note;
import org.toilelibre.libe.soundtransform.model.library.pack.note.SimpleNoteInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.note.SoundToNoteService;

public class PitchAndSpeedHelperTest extends SoundTransformTest {

    @Test
    public void shouldBeTwiceTheF0ValuePiano3e () throws SoundTransformException {
        final ClassLoader classLoader = PitchAndSpeedHelperTest.class.getClassLoader ();
        final URL fileURL = classLoader.getResource ("piano3e.wav");
        final File input = new File (fileURL.getFile ());

        final InputStream ais = $.select (AudioFileService.class).streamFromFile (input);

        final InputStreamToSoundService<?> is2Sound = $.select (InputStreamToSoundService.class);
        final SoundToInputStreamService<?> sound2Is = $.select (SoundToInputStreamService.class);

        final Sound e3 = is2Sound.fromInputStream (ais);
        final SoundPitchAndTempoHelper helper = $.select (SoundPitchAndTempoHelper.class);
        final Channel [] e4 = new Channel [2];
        e4 [0] = helper.pitchAndSetLength (e3.getChannels () [0], 200, 1);
        e4 [1] = helper.pitchAndSetLength (e3.getChannels () [1], 200, 1);

        final InputStream ais2 = sound2Is.toStream (new Sound (e4), $.select (AudioFormatParser.class).getStreamInfo (ais));
        final File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

        $.select (AudioFileService.class).fileFromStream (ais2, fDest);
        final Note n = $.select (SoundToNoteService.class).convert (new SimpleNoteInfo ("e4"), new Sound (e4));
        new Slf4jObserver ().notify ("e' 4 : " + n.getFrequency () + "Hz, should be around 658Hz");
        org.junit.Assert.assertTrue (n.getFrequency () > 658 - 10 && n.getFrequency () < 658 + 10);
    }
}
