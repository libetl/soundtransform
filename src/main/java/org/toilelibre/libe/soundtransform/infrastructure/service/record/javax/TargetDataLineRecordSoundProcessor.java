package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

class TargetDataLineRecordSoundProcessor implements RecordSoundProcessor {
    
    public enum TargetDataLineRecordSoundProcessorErrorCode implements ErrorCode {

        AUDIO_FORMAT_EXPECTED ("An audio format was expected");

        private final String messageFormat;

        TargetDataLineRecordSoundProcessorErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }
    
    public TargetDataLineRecordSoundProcessor () {

    }

    @Override
    public InputStream recordRawInputStream(Object audioFormat, Object stop) throws SoundTransformException {
        if (!(audioFormat instanceof AudioFormat)){
            throw new SoundTransformException(TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED, new IllegalArgumentException());
        }

        return null;
    }


    
}
