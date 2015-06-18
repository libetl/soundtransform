package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.Serializable;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LevelSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.MaximumLikelihoodSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.PralongAndCarlileSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.BlackmanHarrisWindowSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CepstrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CompositeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.HarmonicProductSpectrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class PeakFindClassesScoreTest {

    @Test
    public void peakFindTest () throws SoundTransformException {
        ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File [] files = { new File (classLoader.getResource ("piano1c.wav").getFile ()), new File (classLoader.getResource ("piano2d.wav").getFile ()), new File (classLoader.getResource ("piano3e.wav").getFile ()),
                new File (classLoader.getResource ("piano4f.wav").getFile ()), new File (classLoader.getResource ("piano5g.wav").getFile ()), new File (classLoader.getResource ("piano6a.wav").getFile ()), new File (classLoader.getResource ("piano7b.wav").getFile ()),
                new File (classLoader.getResource ("piano8c.wav").getFile ()) };
        

        final int [] expectedValues = {260, 293, 329, 349, 391, 440, 493, 523};
        int expectedValuesIndex = 0;
        int cepstrumScore = 0;
        int hpsScore = 0;
        int maxLikelihoodScore = 0;
        for (final File file : files) {
            final SoundTransform<Channel, float []> cepstrum = new CompositeSoundTransform<Channel, Channel, float []> (new LevelSoundTransform (4000), new CepstrumSoundTransform<Serializable> (300, true));
            final SoundTransform<Channel, float []> hps = new HarmonicProductSpectrumSoundTransform<Serializable> (true);
            final SoundTransform<Channel, float []> maxlikelihood = new CompositeSoundTransform<Channel, Channel, float []> (new BlackmanHarrisWindowSoundTransform (), new CompositeSoundTransform<Channel, Channel, float []> (new PralongAndCarlileSoundTransform (), new MaximumLikelihoodSoundTransform (24000, 4000, 100, 800)));
            final float [][] freqscepstrum11025 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().applyAndStop (cepstrum);
            final float [][] freqshps11025 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().applyAndStop (hps);
            final float [][] freqsmaxlikelihood11025 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().applyAndStop (maxlikelihood);
            final float [][] freqscepstrum22050 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().changeFormat (new FormatInfo (2, 22050)).applyAndStop (cepstrum);
            final float [][] freqshps22050 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().changeFormat (new FormatInfo (2, 22050)).applyAndStop (hps);
            final float [][] freqsmaxlikelihood22050 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().changeFormat (new FormatInfo (2, 22050)).applyAndStop (maxlikelihood);
            final float [][] freqscepstrum44100 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().changeFormat (new FormatInfo (2, 44100)).applyAndStop (cepstrum);
            final float [][] freqshps44100 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().changeFormat (new FormatInfo (2, 44100)).applyAndStop (hps);
            final float [][] freqsmaxlikelihood44100 = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ().changeFormat (new FormatInfo (2, 44100)).applyAndStop (maxlikelihood);
            new Slf4jObserver (LogLevel.INFO).notify ("Peak find with the file " + file.getName () + " : ");
            for (int i = 0 ; i < freqscepstrum11025.length ; i++) {
                new Slf4jObserver (LogLevel.INFO).notify ("                        channel " + i + "   : cepstrum(11025) -> " + freqscepstrum11025 [i] [0] + ", hps(11025) -> " + freqshps11025 [i] [0] + ", maxlikelihood(11025) -> " + freqsmaxlikelihood11025 [i] [0]);
                if (Math.abs (freqscepstrum11025 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    cepstrumScore++;
                }
                if (Math.abs (freqshps11025 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    hpsScore++;
                }
                if (Math.abs (freqsmaxlikelihood11025 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    maxLikelihoodScore++;
                }
            }
            for (int i = 0 ; i < freqscepstrum22050.length ; i++) {
                new Slf4jObserver (LogLevel.INFO).notify ("                        channel " + i + "   : cepstrum(22050) -> " + freqscepstrum22050 [i] [0] + ", hps(22050) -> " + freqshps22050 [i] [0] + ", maxlikelihood(22050) -> " + freqsmaxlikelihood22050 [i] [0]);
                if (Math.abs (freqscepstrum22050 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    cepstrumScore++;
                }
                if (Math.abs (freqshps22050 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    hpsScore++;
                }
                if (Math.abs (freqsmaxlikelihood22050 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    maxLikelihoodScore++;
                }
            }
            for (int i = 0 ; i < freqscepstrum44100.length ; i++) {
                new Slf4jObserver (LogLevel.INFO).notify ("                        channel " + i + "   : cepstrum(44100) -> " + freqscepstrum44100 [i] [0] + ", hps(44100) -> " + freqshps44100 [i] [0] + ", maxlikelihood(44100) -> " + freqsmaxlikelihood44100 [i] [0]);
                if (Math.abs (freqscepstrum44100 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    cepstrumScore++;
                }
                if (Math.abs (freqshps44100 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    hpsScore++;
                }
                if (Math.abs (freqsmaxlikelihood44100 [i] [0] - expectedValues [expectedValuesIndex]) < 15){
                    maxLikelihoodScore++;
                }
            }
            expectedValuesIndex++;
        }
        new Slf4jObserver (LogLevel.INFO).notify ("Scores : cepstrum -> " + String.format ("%2.2f", cepstrumScore / 48.0 * 100) + 
                "%, hps -> " + String.format ("%2.2f", hpsScore / 48.0 * 100) + "%, maxLikelihood -> " + String.format ("%2.2f", maxLikelihoodScore / 48.0 * 100) + "%");       
    }
}
