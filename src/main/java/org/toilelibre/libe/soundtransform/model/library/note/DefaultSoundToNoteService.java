package org.toilelibre.libe.soundtransform.model.library.note;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.ComputeMagnitudeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;

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

        final ComputeMagnitudeSoundTransform soundTransform = new ComputeMagnitudeSoundTransform (DefaultSoundToNoteService.ACCURATE_STEP);
        final double [] magnitudeArray = soundTransform.transform (channel1);

        noteInfoValues.put (SimpleNoteInfo.ATTACK_KEY, noteInfo.hasAttack () ? noteInfo.getAttack () : 0);
        noteInfoValues.put (SimpleNoteInfo.DECAY_KEY, noteInfo.hasDecay () ? noteInfo.getDecay () : this.adsrHelper.findDecay (magnitudeArray, ((Integer) noteInfoValues.get (SimpleNoteInfo.ATTACK_KEY)).intValue ()) * DefaultSoundToNoteService.ACCURATE_STEP);
        noteInfoValues.put (SimpleNoteInfo.SUSTAIN_KEY, noteInfo.hasSustain () ? noteInfo.getSustain () : this.adsrHelper.findSustain (magnitudeArray, ((Integer) noteInfoValues.get (SimpleNoteInfo.DECAY_KEY)).intValue () / DefaultSoundToNoteService.ACCURATE_STEP) * DefaultSoundToNoteService.ACCURATE_STEP);
        noteInfoValues.put (SimpleNoteInfo.RELEASE_KEY, noteInfo.hasRelease () ? noteInfo.getRelease () : this.adsrHelper.findRelease (magnitudeArray) * DefaultSoundToNoteService.ACCURATE_STEP);
        noteInfoValues.put (SimpleNoteInfo.FREQUENCY_KEY, noteInfo.hasFrequency () ? noteInfo.getFrequency () : this.frequencyHelper.findFrequency (sound));
        noteInfoValues.put (SimpleNoteInfo.NAME_KEY, noteInfo.getName ());

        return new SimpleNote (new SimpleNoteInfo (noteInfoValues), sound.getChannels ());

    }
}
