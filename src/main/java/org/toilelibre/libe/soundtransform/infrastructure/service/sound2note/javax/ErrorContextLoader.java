package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.javax;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public class ErrorContextLoader implements ContextLoader {

    public enum ErrorContextReaderErrorCode implements ErrorCode {
        STUB_IMPLEMENTATION ("Stub implementation of the context reader with javax. This json pack cannot be imported in the javax impl.");

        private final String messageFormat;

        ErrorContextReaderErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }
    
    @Override
    public InputStream read (Object context, Class<Object> rClass, String idName) throws SoundTransformException {
        throw new SoundTransformException (ErrorContextReaderErrorCode.STUB_IMPLEMENTATION, new UnsupportedOperationException ());
    }

}
