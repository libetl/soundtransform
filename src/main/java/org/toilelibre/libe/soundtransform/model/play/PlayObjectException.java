package org.toilelibre.libe.soundtransform.model.play;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class PlayObjectException extends SoundTransformException {

    public enum PlaySoundErrorCode implements ErrorCode {
        COULD_NOT_PLAY_SOUND ("Could not play a sound");
        private final String messageFormat;

        PlaySoundErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -4904836048288493711L;

    public PlayObjectException (final Exception e) {
        super (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e);
    }

}
