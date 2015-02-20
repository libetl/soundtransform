package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoService;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SimpleNote implements Note {

    private final Sound [] attack;
    private final Sound [] decay;
    private final Sound [] sustain;
    private final Sound [] release;
    private final float    frequency;
    private final String   fileName;

    public SimpleNote (final String fileName, final Sound [] channels, final float frequency, final int attack, final int decay, final int sustain, final int release) {
        this.frequency = frequency;
        this.attack = new Sound [channels.length];
        this.decay = new Sound [channels.length];
        this.sustain = new Sound [channels.length];
        this.release = new Sound [channels.length];
        this.fileName = fileName;
        for (int i = 0 ; i < channels.length ; i++) {
            this.attack [i] = this.soundToSubSound (channels [i], attack, decay);
            this.decay [i] = this.soundToSubSound (channels [i], decay, sustain);
            this.sustain [i] = this.soundToSubSound (channels [i], sustain, release);
            this.release [i] = this.soundToSubSound (channels [i], release, channels [i].getSamples ().length - 1);
        }
    }

    private Sound get (final Sound [] adsr, final int channelnum) {
        if (adsr.length == 0) {
            return new Sound (new long [0], 0, 0, 0);
        }
        if (adsr.length <= channelnum) {
            return adsr [adsr.length - 1];
        }
        return adsr [channelnum];
    }

    @Override
    public Sound getAttack (final int frequency, final int channelnum, final float length) throws SoundTransformException {
        return $.create (SoundPitchAndTempoService.class).callTransform (this.get (this.attack, channelnum), this.getPercent (frequency), this.getRatio (this.attack) * length);
    }

    @Override
    public Sound getDecay (final int frequency, final int channelnum, final float length) throws SoundTransformException {
        return $.create (SoundPitchAndTempoService.class).callTransform (this.get (this.decay, channelnum), this.getPercent (frequency), this.getRatio (this.decay) * length);
    }

    @Override
    public float getFrequency () {
        return this.frequency;
    }

    @Override
    public String getName () {
        return this.fileName;
    }

    private float getPercent (final int frequency2) {
        return (float) (frequency2 * 100.0 / this.frequency);
    }

    private float getRatio (final Sound [] subsound) {
        final float lengthOfSubsound = 1.0f * subsound [0].getSamples ().length / subsound [0].getSampleRate ();
        final float lengthOfSound = 1.0f * this.attack [0].getSamples ().length / this.attack [0].getSampleRate () + 1.0f * this.decay [0].getSamples ().length / this.decay [0].getSampleRate () + 1.0f * this.sustain [0].getSamples ().length / this.sustain [0].getSampleRate () + 1.0f
                * this.release [0].getSamples ().length / this.release [0].getSampleRate ();
        return lengthOfSubsound * 1.0f / lengthOfSound;
    }

    @Override
    public Sound getRelease (final int frequency, final int channelnum, final float length) throws SoundTransformException {
        return $.create (SoundPitchAndTempoService.class).callTransform (this.get (this.release, channelnum), this.getPercent (frequency), this.getRatio (this.release) * length);
    }

    @Override
    public Sound getSustain (final int frequency, final int channelnum, final float length) throws SoundTransformException {
        return $.create (SoundPitchAndTempoService.class).callTransform (this.get (this.sustain, channelnum), this.getPercent (frequency), this.getRatio (this.sustain) * length);
    }

    private Sound soundToSubSound (final Sound input, final int beginning, final int end) {
        long [] newsamples = new long [0];
        if (beginning < end) {
            newsamples = new long [end - beginning];
            System.arraycopy (input.getSamples (), beginning, newsamples, 0, end - beginning);
        }
        return new Sound (newsamples, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
    }

}
