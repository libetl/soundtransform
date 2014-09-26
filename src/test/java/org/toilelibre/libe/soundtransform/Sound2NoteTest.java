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
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.pda.Sound2Note;

public class Sound2NoteTest {

    @Test
    public void run() throws UnsupportedAudioFileException, IOException {
        @SuppressWarnings("serial")
        Map<String, Integer> frequenciesPerSound = new HashMap<String, Integer>() {
            {
                this.put("Piano1-C.wav", 260);// OK
                this.put("Piano2-D.wav", 293);// OK
                this.put("Piano3-E.wav", 332);// OK
                this.put("Piano4-F.wav", 344);// OK
                this.put("Piano5-G.wav", 376);// OK
                this.put("Piano6-A.wav", 451);// OK
                this.put("Piano7-B.wav", 499);// OK
                this.put("Piano8-C.wav", 524);// OK
            }
        };
        System.out.println("Loading Packs");
        Pack pack = PacksList.getInstance().defaultPack;
        for (String instrument : pack.keySet()) {
            for (Integer noteKey : pack.get(instrument).keySet()) {
                Note n = pack.get(instrument).get(noteKey);
                if (frequenciesPerSound.get(n.getName()) != null) {
                    org.junit.Assert.assertEquals(
                            frequenciesPerSound.get(n.getName()).intValue(),
                            n.getFrequency());
                    System.out.println("f0 (" + n.getName() + ") = "
                            + n.getFrequency());
                } else {
                    System.out.println("Did not find " + n.getName());
                }
            }
        }
    }

    @Test
    public void shouldNotBeTwiceTheF0ValuePiano1C()
            throws UnsupportedAudioFileException, IOException {
        ClassLoader classLoader = Sound2NoteTest.class.getClassLoader();
        URL fileURL = classLoader.getResource("notes/Piano1-C.wav");
        File input = new File(fileURL.getFile());

        AudioInputStream ais = AudioFileHelper.getAudioInputStream(input);
        TransformSound ts = new TransformSound();

        Note n = Sound2Note.convert("Piano1-C.wav", ts.fromInputStream(ais));
        System.out.println("c' 1-line octave : " + n.getFrequency() + "Hz, should be around 261Hz");
        org.junit.Assert.assertTrue(n.getFrequency() > 261 - 10
                && n.getFrequency() < 261 + 10);
    }
    

    @Test
    public void shouldNotBeTwiceTheF0ValuePiano4F()
            throws UnsupportedAudioFileException, IOException {
        ClassLoader classLoader = Sound2NoteTest.class.getClassLoader();
        URL fileURL = classLoader.getResource("notes/Piano4-F.wav");
        File input = new File(fileURL.getFile());

        AudioInputStream ais = AudioFileHelper.getAudioInputStream(input);
        TransformSound ts = new TransformSound();

        Note n = Sound2Note.convert("Piano4-F.wav", ts.fromInputStream(ais));
        System.out.println("f' 4 : " + n.getFrequency() + "Hz, should be around 349Hz");
        org.junit.Assert.assertTrue(n.getFrequency() > 349 - 10
                && n.getFrequency() < 349 + 10);
    }
    
    @Test
    public void shouldRecognize440HzNote(){
    	long [] signal = new long [4410];
    	for (int i = 0 ; i < 4410 ; i++){
    		signal [i] = (long) (Math.sin(i / 4) * 256 * 128.0);
    	}
    	Sound s = new Sound (signal, 2, 44100, 1);
    	Note n = Sound2Note.convert("Sample 440Hz Sound", new Sound []{s});
        System.out.println("Sample 440Hz Sound : " + n.getFrequency() + "Hz, should be around 440Hz");
    	org.junit.Assert.assertTrue(n.getFrequency() > 440 - 10
                && n.getFrequency() < 440 + 10);
    }
    
    @Test
    public void shouldRecognize529HzNote(){
    	long [] signal = new long [4410];
    	for (int i = 0 ; i < 4410 ; i++){
    		signal [i] = (long) (Math.sin(i / 3.318181) * 256 * 128.0);
    	}
    	Sound s = new Sound (signal, 2, 44100, 1);
    	Note n = Sound2Note.convert("Sample 529Hz Sound", new Sound []{s});
        System.out.println("Sample 529Hz Sound : " + n.getFrequency() + "Hz, should be around 529Hz");
    	org.junit.Assert.assertTrue(n.getFrequency() > 529 - 10
                && n.getFrequency() < 529 + 10);
    }
}
