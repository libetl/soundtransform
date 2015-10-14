package org.toilelibre.libe.soundtransform.model.library.pack.note;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ComputeMagnitudeSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

final class DefaultSoundToNoteService implements SoundToNoteService {

    private final ADSRHelper      adsrHelper;

    private final FrequencyHelper frequencyHelper;

    private static final int      ACCURATE_STEP = 100;

    public DefaultSoundToNoteService (final ADSRHelper helper1, final FrequencyHelper helper2) {
        this.adsrHelper = helper1;
        this.frequencyHelper = helper2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService
     * #convert
     * (org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo,
     * org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    public Note convert (final SimpleNoteInfo noteInfo, final Sound sound) throws SoundTransformException {
        final Channel channel1 = sound.getChannels () [0];

        final Map<String, Object> noteInfoValues = new HashMap<String, Object> ();

        if (noteInfo.isAdsrReady ()) {
            return this.newNoteFromExistingInfo (noteInfo, sound, noteInfoValues);
        }

        return this.newNote (noteInfo, sound, channel1, noteInfoValues);

    }

    /**
     * @param noteInfo
     * @param sound
     * @param channel1
     * @param noteInfoValues
     * @return
     * @throws SoundTransformException
     */
    private Note newNote (final SimpleNoteInfo noteInfo, final Sound sound, final Channel channel1, final Map<String, Object> noteInfoValues) throws SoundTransformException {
        final ComputeMagnitudeSoundTransform soundTransform = new ComputeMagnitudeSoundTransform (DefaultSoundToNoteService.ACCURATE_STEP);
        final double [] magnitudeArray = soundTransform.transform (channel1);
        noteInfoValues.put (SimpleNoteInfo.ATTACK_KEY, 0);
        noteInfoValues.put (SimpleNoteInfo.DECAY_KEY, this.adsrHelper.findDecay (magnitudeArray, ((Integer) noteInfoValues.get (SimpleNoteInfo.ATTACK_KEY)).intValue () / DefaultSoundToNoteService.ACCURATE_STEP) * DefaultSoundToNoteService.ACCURATE_STEP);
        noteInfoValues.put (SimpleNoteInfo.SUSTAIN_KEY, this.adsrHelper.findSustain (magnitudeArray, ((Integer) noteInfoValues.get (SimpleNoteInfo.DECAY_KEY)).intValue () / DefaultSoundToNoteService.ACCURATE_STEP) * DefaultSoundToNoteService.ACCURATE_STEP);
        noteInfoValues.put (SimpleNoteInfo.RELEASE_KEY, this.adsrHelper.findRelease (magnitudeArray) * DefaultSoundToNoteService.ACCURATE_STEP);
        noteInfoValues.put (SimpleNoteInfo.FREQUENCY_KEY, noteInfo.hasFrequency () ? noteInfo.getFrequency () : this.frequencyHelper.findFrequency (sound));
        noteInfoValues.put (SimpleNoteInfo.NAME_KEY, noteInfo.getName ());
        return new SimpleNote (new SimpleNoteInfo (noteInfoValues), sound.getChannels ());
    }

    /**
     * @param noteInfo
     * @param sound
     * @param noteInfoValues
     * @throws SoundTransformException
     */
    private Note newNoteFromExistingInfo (final SimpleNoteInfo noteInfo, final Sound sound, final Map<String, Object> noteInfoValues) throws SoundTransformException {
        noteInfoValues.put (SimpleNoteInfo.ATTACK_KEY, noteInfo.getAttack ());
        noteInfoValues.put (SimpleNoteInfo.DECAY_KEY, noteInfo.getDecay ());
        noteInfoValues.put (SimpleNoteInfo.SUSTAIN_KEY, noteInfo.getSustain ());
        noteInfoValues.put (SimpleNoteInfo.RELEASE_KEY, noteInfo.getRelease ());
        noteInfoValues.put (SimpleNoteInfo.FREQUENCY_KEY, noteInfo.hasFrequency () ? noteInfo.getFrequency () : this.frequencyHelper.findFrequency (sound));
        noteInfoValues.put (SimpleNoteInfo.NAME_KEY, noteInfo.getName ());
        return new SimpleNote (new SimpleNoteInfo (noteInfoValues), sound.getChannels ());

    }
}
