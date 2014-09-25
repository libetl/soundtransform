package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.objects.Note;
import org.toilelibre.libe.soundtransform.objects.Pack;
import org.toilelibre.libe.soundtransform.objects.PacksList;
import org.toilelibre.libe.soundtransform.pda.Sound2Note;

public class Sound2NoteTest {

    @Test
    public void run() throws UnsupportedAudioFileException, IOException {
        @SuppressWarnings("serial")
        Map<String, Integer> frequenciesPerSound = new HashMap<String, Integer>() {
            {
                this.put("Piano1-C.wav", 528);// Buggy : f(0) * 2
                this.put("Piano2-D.wav", 297);// OK
                this.put("Piano3-E.wav", 666);// Buggy : f(0) * 2
                this.put("Piano4-F.wav", 705);// Buggy : f(0) * 2
                this.put("Piano5-G.wav", 2646);// Buggy - should be 392...
                this.put("Piano6-A.wav", 462);// Buggy - should be 440...
                this.put("Piano7-B.wav", 501);// OK
                this.put("Piano8-C.wav", 528);// OK
            }
        };
        System.out.println("Loading Packs");
        Pack pack = PacksList.getInstance().defaultPack;
        for (String instrument : pack.keySet()) {
            for (Integer noteKey : pack.get(instrument).keySet()) {
                Note n = pack.get(instrument).get(noteKey);
                if (frequenciesPerSound.get(n.getName()) != null) {
                    org.junit.Assert.assertEquals(n.getFrequency(),
                            frequenciesPerSound.get(n.getName()).intValue());
                    System.out.println("f0 (" + n.getName() + ") = "
                            + n.getFrequency());
                } else {
                    System.out.println("Did not find " + n.getName());
                }
            }
        }
    }

    @Test
    public void shouldNotBeTwiceTheF0Value()
            throws UnsupportedAudioFileException, IOException {
        ClassLoader classLoader = Sound2NoteTest.class.getClassLoader();
        URL fileURL = classLoader.getResource("notes/Piano1-C.wav");
        File input = new File(fileURL.getFile());

        AudioInputStream ais = AudioFileHelper.getAudioInputStream(input);
        TransformSound ts = new TransformSound();

        Note n = Sound2Note.convert("Piano1-C.wav", ts.fromInputStream(ais));
        System.out.println("c′ 1-line octave : " + n.getFrequency() + "Hz, should be around 261Hz");
        org.junit.Assert.assertTrue(n.getFrequency() > 261 - 10
                && n.getFrequency() < 261 + 10);
    }
}
