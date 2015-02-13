package org.toilelibre.libe.soundtransform.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

import se.jbee.inject.Array;
import se.jbee.inject.DIRuntimeException.NoSuchResourceException;
import se.jbee.inject.Dependency;
import se.jbee.inject.Injector;
import se.jbee.inject.bootstrap.Bootstrap;

public class ApplicationInjector {
    public static class $ {

        public static <T> T create (final Class<T> type, final Object... additionalParameters) {
            return ApplicationInjector.instantiate (type, additionalParameters);
        }

        public static <T> T select (final Class<T> type) {
            return ApplicationInjector.getBean (type);
        }

        private $ () {

        }
    }

    public enum ApplicationInjectorErrorCode implements ErrorCode {

        INSTANTIATION_FAILED ("Instantiation failed (seen warnings list : %1s)");

        private final String messageFormat;

        ApplicationInjectorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public static <T> T getBean (final Class<T> type) {
        return ApplicationInjector.injector.resolve (Dependency.<T> dependency (type));
    }

    @SuppressWarnings ("unchecked")
    public static <T> T instantiate (final Class<T> type, final Object... additionalParameters) {
        final List<String> warnings = new LinkedList<String> ();
        for (final Constructor<?> constructor : type.getDeclaredConstructors ()) {
            final Class<?> [] ptypes = constructor.getParameterTypes ();
            final Object [] newInstanceParams = new Object [ptypes.length];
            for (int i = 0 ; i < ptypes.length ; i++) {
                try {
                    newInstanceParams [i] = ApplicationInjector.getBean (ptypes [i]);
                } catch (final NoSuchResourceException nsre) {
                    warnings.add ("Could not find a bean named " + ptypes [i] == null ? null : ptypes.getClass () + " (" + nsre.getMessage () + ")");
                }
            }
            int additionalParamCounter = 0;
            for (int i = 0 ; i < newInstanceParams.length ; i++) {
                if (newInstanceParams [i] == null) {
                    if (additionalParamCounter < additionalParameters.length) {
                        if (ptypes [i].isArray () && !additionalParameters [additionalParamCounter].getClass ().isArray ()) {
                            newInstanceParams [i] = Array.fill (additionalParameters [additionalParamCounter], 1);
                        } else {
                            newInstanceParams [i] = additionalParameters [additionalParamCounter];
                        }
                    }
                    additionalParamCounter++;
                }
            }
            final String warningPrefix = "Constructor " + constructor;
            if (additionalParamCounter != additionalParameters.length) {
                warnings.add (warningPrefix + " did not match");
                continue;
            }
            try {
                return (T) constructor.newInstance (newInstanceParams);
            } catch (final InstantiationException e) {
                warnings.add (warningPrefix + " could not instantiate");
            } catch (final IllegalAccessException e) {
                warnings.add (warningPrefix + " is not accessible");
            } catch (final IllegalArgumentException e) {
                warnings.add (warningPrefix + " had an illegal argument");
            } catch (final InvocationTargetException e) {
                if (e.getCause () instanceof SoundTransformException) {
                    warnings.add (warningPrefix + " threw an ErrorCode : " + ((SoundTransformException) e.getCause ()).getErrorCode ().name ());
                }
                warnings.add (warningPrefix + " could not call a method");
            }
        }
        throw new SoundTransformRuntimeException (new SoundTransformException (ApplicationInjectorErrorCode.INSTANTIATION_FAILED, new NullPointerException (), warnings.toString ()));
    }

    static Injector injector = Bootstrap.injector (AndroidRootModule.class);

    private ApplicationInjector () {

    }

}
