package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

@Service
public interface ModifySoundService {

    enum ModifySoundServiceErrorCode implements ErrorCode {
        DIFFERENT_NUMBER_OF_CHANNELS ("Could not append two sounds : Different number of channels (%1d and %2d)");

        private final String messageFormat;

        ModifySoundServiceErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    Sound append (Sound sounds1, Sound sounds2) throws SoundTransformException;

    Sound changeFormat (Sound input, FormatInfo formatInfo);

}