package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.javax;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

@Processor
final class ErrorContextLoader implements ContextLoader {

    private enum ErrorContextReaderErrorCode implements ErrorCode {
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
    public InputStream read (final Object context, final int id) throws SoundTransformException {
        throw new SoundTransformException (ErrorContextReaderErrorCode.STUB_IMPLEMENTATION, new UnsupportedOperationException ());
    }

    @Override
    public InputStream read (final Object context, final Class<?> rClass, final String idName) throws SoundTransformException {
        return this.read (context, 0);
    }

}
