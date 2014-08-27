package org.toilelibre.soundtransform.note;

import org.toilelibre.soundtransform.objects.Sound;
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
    public Sound[] getAttack(int frequency) {
        return this.transformSubsound(this.attack, frequency);
    }

    @Override
    public Sound[] getDecay(int frequency) {
        return this.transformSubsound(this.decay, frequency);
    }

    @Override
    public Sound[] getSustain(int frequency) {
        return this.transformSubsound(this.sustain, frequency);
    }

    @Override
    public Sound[] getRelease(int frequency) {
        return this.transformSubsound(this.release, frequency);
    }

    private Sound[] transformSubsound(Sound[] subSound, int frequency) {
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
