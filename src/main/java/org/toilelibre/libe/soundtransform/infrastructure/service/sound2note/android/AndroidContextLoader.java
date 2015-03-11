package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.android;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public class AndroidContextLoader implements ContextLoader {

    public enum AndroidContextReaderErrorCode implements ErrorCode {
        WRONG_CONTEXT_CLASS ("Expected an Android context"), COULD_NOT_READ_ID ("Could not read id : %1s"), 
        COULD_NOT_FIND_ID ("Could not find id : %1s"),
        COULD_NOT_USE_CONTEXT ("Could not use context : %1s");

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
    public InputStream read (Object context, Class<?> rClass, String idName) throws SoundTransformException {
        int id = this.getIdFromIdName (rClass, idName);
       
        return this.openRawResource (this.getResources (context), id);
    }

    private Object getResources (Object context) throws SoundTransformException {
        try {
            return context.getClass ().getMethod ("getResources").invoke (context);
        } catch (IllegalArgumentException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_USE_CONTEXT, e, context);
        } catch (SecurityException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_USE_CONTEXT, e, context);
        } catch (IllegalAccessException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_USE_CONTEXT, e, context);
        } catch (InvocationTargetException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_USE_CONTEXT, e, context);
        } catch (NoSuchMethodException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.WRONG_CONTEXT_CLASS, e);
        }
    }

    private InputStream openRawResource (Object resources, int id) throws SoundTransformException {
        try {
            return (InputStream) resources.getClass ().getDeclaredMethod ("openRawResource", int.class).invoke (resources, id);
        } catch (IllegalArgumentException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_FIND_ID, e, id);
        } catch (SecurityException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_FIND_ID, e, id);
        } catch (IllegalAccessException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_FIND_ID, e, id);
        } catch (InvocationTargetException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_FIND_ID, e, id);
        } catch (NoSuchMethodException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_FIND_ID, e, id);
        }
    }

    private int getIdFromIdName (Class<?> rClass, String idName) throws SoundTransformException {
        try {
            return rClass.getDeclaredField (idName).getInt (null);
        } catch (IllegalArgumentException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        } catch (IllegalAccessException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        } catch (NoSuchFieldException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        } catch (SecurityException e) {
            throw new SoundTransformException (AndroidContextReaderErrorCode.COULD_NOT_READ_ID, e, idName);
        }
    }

}
