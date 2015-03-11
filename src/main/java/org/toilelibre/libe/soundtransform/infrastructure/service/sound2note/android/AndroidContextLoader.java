package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.android;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

import android.content.Context;

public class AndroidContextLoader implements ContextLoader {

    public enum AndroidContextReaderErrorCode implements ErrorCode {
        WRONG_CONTEXT_CLASS ("Expected an Android context"), COULD_NOT_READ_ID ("Could not read id : %1s");

        private final String messageFormat;

        AndroidContextReaderErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }
    
    @Override
    public InputStream read (Object context, Class<Object> rClass, String idName) throws SoundTransformException {
        if (!(context instanceof Context)){
            throw new SoundTransformException (AndroidContextReaderErrorCode.WRONG_CONTEXT_CLASS, new ClassCastException ());
        }
        int id;
        try {
            id = rClass.getDeclaredField (idName).getInt (null);
        } catch (IllegalArgumentException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        } catch (IllegalAccessException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        } catch (NoSuchFieldException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        } catch (SecurityException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        }
        return ((Context)context).getResources ().openRawResource (id);
    }

}
