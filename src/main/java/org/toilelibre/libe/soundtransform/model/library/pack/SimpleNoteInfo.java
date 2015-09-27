package org.toilelibre.libe.soundtransform.model.library.pack;

import java.util.Map;

public class SimpleNoteInfo {

    public static final int    DEFAULT_VALUE  = -1;
    public static final String ATTACK_KEY     = "attack";
    public static final String DECAY_KEY      = "decay";
    public static final String SUSTAIN_KEY    = "sustain";
    public static final String RELEASE_KEY    = "release";
    public static final String NAME_KEY       = "name";
    public static final String FREQUENCY_KEY  = "frequency";
    public static final String ADSR_READY_KEY = "adsrReady";

    private final float        frequency;
    private final String       name;
    private final int          attack;
    private final int          decay;
    private final int          sustain;
    private final int          release;
    private boolean            adsrReady;

    public SimpleNoteInfo (final Map<String, Object> noteElement) {
        this.frequency = this.safeParse (noteElement.get (SimpleNoteInfo.FREQUENCY_KEY));
        this.adsrReady = true;
        this.name = noteElement.get (SimpleNoteInfo.NAME_KEY).toString ();
        this.attack = (int) this.safeParse (noteElement.get (SimpleNoteInfo.ATTACK_KEY));
        this.decay = (int) this.safeParse (noteElement.get (SimpleNoteInfo.DECAY_KEY));
        this.sustain = (int) this.safeParse (noteElement.get (SimpleNoteInfo.SUSTAIN_KEY));
        this.release = (int) this.safeParse (noteElement.get (SimpleNoteInfo.RELEASE_KEY));
        this.adsrReady |= Boolean.TRUE.equals (noteElement.get (SimpleNoteInfo.ADSR_READY_KEY));
    }

    public SimpleNoteInfo (final String fileName) {
        this.adsrReady = false;
        this.frequency = SimpleNoteInfo.DEFAULT_VALUE;
        this.name = fileName;
        this.attack = SimpleNoteInfo.DEFAULT_VALUE;
        this.decay = SimpleNoteInfo.DEFAULT_VALUE;
        this.sustain = SimpleNoteInfo.DEFAULT_VALUE;
        this.release = SimpleNoteInfo.DEFAULT_VALUE;
    }

    /**
     * @return the attack
     */
    public int getAttack () {
        return this.attack;
    }

    /**
     * @return the decay
     */
    public int getDecay () {
        return this.decay;
    }

    /**
     * @return the frequency
     */
    public float getFrequency () {
        return this.frequency;
    }

    /**
     * @return the name
     */
    public String getName () {
        return this.name;
    }

    /**
     * @return the release
     */
    public int getRelease () {
        return this.release;
    }

    /**
     * @return the sustain
     */
    public int getSustain () {
        return this.sustain;
    }

    /**
     * @return the adsrReady
     */
    public boolean isAdsrReady () {
        return this.adsrReady;
    }

    /**
     * @return the frequency
     */
    public boolean hasFrequency () {
        return this.frequency != SimpleNoteInfo.DEFAULT_VALUE;
    }

    private float safeParse (final Object object) {
        try {
            final float result = Float.parseFloat (object == null ? "-1" : object.toString ());
            if (result == -1) {
                this.adsrReady = false;
            }
            return result;
        } catch (final NumberFormatException nfe) {
            this.adsrReady = false;
            return -1;
        }
    }

}
