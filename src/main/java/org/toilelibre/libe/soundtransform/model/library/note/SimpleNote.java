package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoService;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;

public class SimpleNote implements Note {

    private final Channel []                attack;
    private final Channel []                decay;
    private final Channel []                sustain;
    private final Channel []                release;
    private final SimpleNoteInfo            noteInfo;
    private final SoundPitchAndTempoService soundPitchAndTempoService;

    public SimpleNote (final SimpleNoteInfo noteInfo1, final Channel [] channels) {
        this.soundPitchAndTempoService = $.select (SoundPitchAndTempoService.class);
        this.attack = new Channel [channels.length];
        this.decay = new Channel [channels.length];
        this.sustain = new Channel [channels.length];
        this.release = new Channel [channels.length];
        this.noteInfo = noteInfo1;
        for (int i = 0 ; i < channels.length ; i++) {
            this.attack [i] = this.soundToSubSound (channels [i], this.noteInfo.getAttack (), this.noteInfo.getDecay ());
            this.decay [i] = this.soundToSubSound (channels [i], this.noteInfo.getDecay (), this.noteInfo.getSustain ());
            this.sustain [i] = this.soundToSubSound (channels [i], this.noteInfo.getSustain (), this.noteInfo.getRelease ());
            this.release [i] = this.soundToSubSound (channels [i], this.noteInfo.getRelease (), channels [i].getSamplesLength () - 1);
        }
    }

    private Channel get (final Channel [] adsr, final int channelnum) {
        if (adsr.length == 0) {
            return new Channel (new long [0], new FormatInfo (0, 0), 0);
        }
        if (adsr.length <= channelnum) {
            return adsr [adsr.length - 1];
        }
        return adsr [channelnum];
    }

    @Override
    public Channel getAttack (final float frequency, final int channelnum, final float length) throws SoundTransformException {
        return this.soundPitchAndTempoService.callTransform (this.get (this.attack, channelnum), this.getPercent (frequency), this.getRatio (this.attack) * length);
    }

    @Override
    public Channel getDecay (final float frequency, final int channelnum, final float length) throws SoundTransformException {
        return this.soundPitchAndTempoService.callTransform (this.get (this.decay, channelnum), this.getPercent (frequency), this.getRatio (this.decay) * length);
    }

    @Override
    public float getFrequency () {
        return this.noteInfo.getFrequency ();
    }

    /**
     * @return the noteInfo
     */
    public SimpleNoteInfo getNoteInfo () {
        return this.noteInfo;
    }

    @Override
    public String getName () {
        return this.noteInfo.getName ();
    }

    private float getPercent (final float frequency2) {
        return (float) (frequency2 * 100.0 / this.getFrequency ());
    }

    private float getRatio (final Channel [] subsound) {
        final float lengthOfSubsound = 1.0f * subsound [0].getSamplesLength () / subsound [0].getSampleRate ();
        final float lengthOfSound = 1.0f * this.attack [0].getSamplesLength () / this.attack [0].getSampleRate () + 1.0f * this.decay [0].getSamplesLength () / this.decay [0].getSampleRate () + 1.0f * this.sustain [0].getSamplesLength () / this.sustain [0].getSampleRate () + 1.0f
                * this.release [0].getSamplesLength () / this.release [0].getSampleRate ();
        return lengthOfSubsound * 1.0f / lengthOfSound;
    }

    @Override
    public Channel getRelease (final float frequency, final int channelnum, final float length) throws SoundTransformException {
        return this.soundPitchAndTempoService.callTransform (this.get (this.release, channelnum), this.getPercent (frequency), this.getRatio (this.release) * length);
    }

    @Override
    public Channel getSustain (final float frequency, final int channelnum, final float length) throws SoundTransformException {
        return this.soundPitchAndTempoService.callTransform (this.get (this.sustain, channelnum), this.getPercent (frequency), this.getRatio (this.sustain) * length);
    }

    private Channel soundToSubSound (final Channel input, final int beginning, final int end) {
        long [] newsamples = new long [0];
        if (beginning < end) {
            newsamples = new long [end - beginning];
            System.arraycopy (input.getSamples (), beginning, newsamples, 0, end - beginning);
        }
        return new Channel (newsamples, input.getFormatInfo (), input.getChannelNum ());
    }

    @Override
    public String toString () {
        return $.select (PackToStringHelper.class).toString (this);
    }
}
