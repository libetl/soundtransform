package org.toilelibre.soundtransform.objects;

import org.toilelibre.soundtransform.transforms.PitchSoundTransformation;

public class SimpleNote implements Note {

    private Sound[] attack;
    private Sound[] decay;
    private Sound[] sustain;
    private Sound[] release;
    private int frequency;

    public SimpleNote(Sound[] channels, int frequency, int attack, int decay,
            int sustain, int release) {
        this.frequency = frequency;
        this.attack = new Sound[channels.length];
        this.decay = new Sound[channels.length];
        this.sustain = new Sound[channels.length];
        this.release = new Sound[channels.length];
        for (int i = 0; i < channels.length; i++) {
            this.attack[i] = channels[i].toSubSound(attack, decay);
            this.decay[i] = channels[i].toSubSound(decay, sustain);
            this.sustain[i] = channels[i].toSubSound(sustain, release);
            this.release[i] = channels[i].toSubSound(release,
                    channels[i].getSamples().length - 1);
        }
    }

    @Override
    public Sound[] getAttack(int frequency, int length) {
        return this.transformSubsound(this.attack, frequency, (int) 0.1 * length);
    }

    @Override
    public Sound[] getDecay(int frequency, int length) {
        return this.transformSubsound(this.decay, frequency, (int) 0.2 * length);
    }

    @Override
    public Sound[] getSustain(int frequency, int length) {
        return this.transformSubsound(this.sustain, frequency, (int) 0.5 * length);
    }

    @Override
    public Sound[] getRelease(int frequency, int length) {
        return this.transformSubsound(this.release, frequency, (int) 0.2 * length);
    }

    private Sound[] transformSubsound(Sound[] subSound, int frequency, int length) {
        if (frequency == this.frequency) {
            return subSound;
        }
        int percent = (int) (frequency * 100.0 / this.frequency);
        Sound[] result = new Sound[subSound.length];

        PitchSoundTransformation pitcher = new PitchSoundTransformation(percent);
        for (int i = 0; i < result.length; i++) {
            result[i] = pitcher.transform(subSound[i]);
        }
        return result;
    }

}
