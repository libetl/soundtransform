package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

@Service
final class DefaultModifySoundService implements ModifySoundService {

    private final SoundAppender soundAppender;

    public DefaultModifySoundService (final SoundAppender soundAppender1) {
        this.soundAppender = soundAppender1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService
     * #append(org.toilelibre.libe.soundtransform.model.converted.sound.Sound[],
     * org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    public Sound append (final Sound sound1, final Sound sound2) throws SoundTransformException {
        if (sound1.getNumberOfChannels () != sound2.getNumberOfChannels ()) {
            throw new SoundTransformException (ModifySoundServiceErrorCode.DIFFERENT_NUMBER_OF_CHANNELS, new IllegalArgumentException (), sound1.getNumberOfChannels (), sound2.getNumberOfChannels ());
        }
        final Channel [] result = new Channel [sound1.getNumberOfChannels ()];

        for (int i = 0 ; i < sound1.getNumberOfChannels () ; i++) {
            result [i] = this.soundAppender.append (sound1.getChannels () [i], sound2.getChannels () [i]);
        }
        return new Sound (result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService
     * #
     * changeFormat(org.toilelibre.libe.soundtransform.model.converted.sound.Sound
     * [], org.toilelibre.libe.soundtransform.model.converted.FormatInfo)
     */
    @Override
    public Sound changeFormat (final Sound sound, final FormatInfo formatInfo) {
        return this.changeFormat (sound, formatInfo.getSampleSize (), (int) formatInfo.getSampleRate ());
    }

    private Sound changeFormat (final Sound sound, final int sampleSize, final int sampleRate) {
        final Channel [] result = new Channel [sound.getNumberOfChannels ()];
        for (int i = 0 ; i < sound.getNumberOfChannels () ; i++) {
            result [i] = sound.getChannels () [i];
            result [i] = this.soundAppender.changeNbBytesPerSample (result [i], sampleSize);
            result [i] = this.soundAppender.resizeToSampleRate (result [i], sampleRate);
        }
        return new Sound (result);
    }

}
