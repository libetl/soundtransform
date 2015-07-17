package org.toilelibre.libe.soundtransform;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.LevelSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.PralongAndCarlileSoundTransform;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CepstrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CompositeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.HarmonicProductSpectrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MaximumLikelihoodSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class PeakFindClassesScoreTest {

    @Test
    public void peakFindTest () throws SoundTransformException {

        final String [] classpathResources = { "piano1c.wav", "piano2d.wav", "piano3e.wav", "piano4f.wav", "piano5g.wav", "piano6a.wav", "piano7b.wav", "piano8c.wav" };
        final FormatInfo [] testedFormats = { new FormatInfo (2, 11025), new FormatInfo (2, 22050), new FormatInfo (2, 44100) };
        final int [] expectedValues = { 260, 293, 329, 349, 391, 440, 493, 523 };
        final String [] notes = { "C3", "D3", "E3", "F3", "G3", "A4", "B4", "C4" };
        final int nbChannels = 1;
        final int allowedDelta = 15;
        final Map<String, Integer> peakFindMethodsScore = new HashMap<String, Integer> ();
        final Map<String, SoundTransform<Channel, float []>> peakFindMethods = new HashMap<String, SoundTransform<Channel, float []>> ();

        this.addKnownMethods (peakFindMethods, peakFindMethodsScore);

        final float total = this.loopAndReturnTotal (classpathResources, testedFormats, expectedValues, notes, nbChannels, allowedDelta, peakFindMethodsScore, peakFindMethods);

        this.answerScores (peakFindMethodsScore, total);
    }

    private void addKnownMethods (final Map<String, SoundTransform<Channel, float []>> peakFindMethods, final Map<String, Integer> peakFindMethodsScore) {
        this.addMethod (peakFindMethods, peakFindMethodsScore, "cepstrum", new CompositeSoundTransform<Channel, Channel, float []> (new LevelSoundTransform (300), new CepstrumSoundTransform<Serializable> (1, true)));
        this.addMethod (peakFindMethods, peakFindMethodsScore, "hps", new HarmonicProductSpectrumSoundTransform<Serializable> (true, false));
        this.addMethod (peakFindMethods, peakFindMethodsScore, "hpsWithRawSpectrums", new HarmonicProductSpectrumSoundTransform<Serializable> (true, true));
        this.addMethod (peakFindMethods, peakFindMethodsScore, "maxlikelihood", new CompositeSoundTransform<Channel, Channel, float []> (new PralongAndCarlileSoundTransform (), new MaximumLikelihoodSoundTransform (8000, 8000, 100, 800)));
    }

    private void answerScores (final Map<String, Integer> peakFindMethodsScore, final float total) {
        final StringBuffer scoresStringBuffer = new StringBuffer ("Scores : ");
        boolean first = true;
        for (final Entry<String, Integer> scoreEntry : peakFindMethodsScore.entrySet ()) {
            scoresStringBuffer.append ((first ? "" : ", ") + scoreEntry.getKey () + " -> " + String.format ("%2.2f", scoreEntry.getValue () / total * 100) + "%");
            first = false;
        }

        new Slf4jObserver (LogLevel.INFO).notify (scoresStringBuffer.toString ());
    }

    private float loopAndReturnTotal (final String [] classpathResources, final FormatInfo [] testedFormats, final int [] expectedValues, final String [] notes, final int nbChannels, final int allowedDelta, final Map<String, Integer> peakFindMethodsScore,
            final Map<String, SoundTransform<Channel, float []>> peakFindMethods) throws SoundTransformException {
        float total = 0;
        int expectedValuesIndex = 0;
        for (final String classpathResource : classpathResources) {
            new Slf4jObserver (LogLevel.INFO).notify ("Peak find with the file " + classpathResource + " ( " + notes [expectedValuesIndex] + " = " + expectedValues [expectedValuesIndex] + "Hz ) : ");
            for (final FormatInfo formatInfo : testedFormats) {
                for (int channel = 0 ; channel < nbChannels ; channel++) {
                    final StringBuffer channelStringBuffer = new StringBuffer ("                        channel " + channel + "   : ");
                    boolean first = true;
                    for (final Entry<String, SoundTransform<Channel, float []>> methodEntry : peakFindMethods.entrySet ()) {
                        final float [][] freqs = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withClasspathResource (classpathResource).convertIntoSound ().changeFormat (formatInfo).mergeChannels ().applyAndStop (methodEntry.getValue ());
                        channelStringBuffer.append ((first ? "" : ", ") + methodEntry.getKey () + "(" + formatInfo.getSampleSize () + ", " + formatInfo.getSampleRate () + ") -> " + freqs [channel] [0]);
                        first = false;
                        if (Math.abs (freqs [channel] [0] - expectedValues [expectedValuesIndex]) < allowedDelta) {
                            peakFindMethodsScore.put (methodEntry.getKey (), peakFindMethodsScore.get (methodEntry.getKey ()) + 1);
                        }
                    }
                    total++;
                    new Slf4jObserver (LogLevel.INFO).notify (channelStringBuffer.toString ());
                }
            }
            expectedValuesIndex++;
        }
        return total;
    }

    private void addMethod (final Map<String, SoundTransform<Channel, float []>> peakFindMethods, final Map<String, Integer> peakFindMethodsScore, final String key, final SoundTransform<Channel, float []> soundTransform) {
        peakFindMethods.put (key, soundTransform);
        peakFindMethodsScore.put (key, 0);
    }
}
