package org.toilelibre.libe.soundtransform.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

import se.jbee.inject.Array;
import se.jbee.inject.DIRuntimeException.NoSuchResourceException;
import se.jbee.inject.Dependency;
import se.jbee.inject.Injector;
import se.jbee.inject.bootstrap.Bootstrap;

public class ApplicationInjector {
    public enum ApplicationInjectorErrorCode implements ErrorCode {

        INSTANTIATION_FAILED ("Instantiation failed");

        private final String messageFormat;

        ApplicationInjectorErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static Injector injector = Bootstrap.injector (RootModule.class);

    public static <T> T getBean (Class<T> type) {
        return ApplicationInjector.injector.resolve (Dependency
                .<T> dependency (type));
    }

    @SuppressWarnings ("unchecked")
    public static <T> T instantiate (Class<T> type,
            Object... additionalParameters) {
        List<String> warnings = new LinkedList<String> ();
        for (Constructor<?> constructor : type.getDeclaredConstructors ()) {
            Class<?> [] ptypes = constructor.getParameterTypes ();
            Object [] newInstanceParams = new Object [ptypes.length];
            for (int i = 0 ; i < ptypes.length ; i++) {
                try {
                    newInstanceParams [i] = ApplicationInjector
                            .getBean (ptypes [i]);
                } catch (NoSuchResourceException nsre) {
                    warnings.add ("Could not find a bean named " + ptypes [i] == null ? null
                            : ptypes.getClass () + " (" + nsre.getMessage ()
                                    + ")");
                }
            }
            int additionalParamCounter = 0;
            for (int i = 0 ; i < newInstanceParams.length ; i++) {
                if (newInstanceParams [i] == null) {
                    if (additionalParamCounter < additionalParameters.length) {
                        if (ptypes [i].isArray ()
                                && !additionalParameters [additionalParamCounter]
                                        .getClass ().isArray ()) {
                            newInstanceParams [i] = Array
                                    .fill (additionalParameters [additionalParamCounter],
                                            1);
                        } else {
                            newInstanceParams [i] = additionalParameters [additionalParamCounter];
                        }
                    }
                    additionalParamCounter++;
                }
            }
            if (additionalParamCounter != additionalParameters.length) {
                warnings.add ("Constructor " + constructor + " did not match");
                continue;
            }
            try {
                return (T) constructor.newInstance (newInstanceParams);
            } catch (InstantiationException e) {
                warnings.add ("Constructor " + constructor
                        + " could not instantiate");
            } catch (IllegalAccessException e) {
                warnings.add ("Constructor " + constructor
                        + " is not accessible");
            } catch (IllegalArgumentException e) {
                warnings.add ("Constructor " + constructor
                        + " had an illegal argument");
            } catch (InvocationTargetException e) {
                warnings.add ("Constructor " + constructor
                        + " could not call a method");
            }
        }
        throw new SoundTransformRuntimeException (new SoundTransformException (
                ApplicationInjectorErrorCode.INSTANTIATION_FAILED,
                new NullPointerException (warnings.toString ())));
    }

    public static class $ {
        public static <T> T select (Class<T> type) {
            return ApplicationInjector.getBean (type);
        }

        public static <T> T create (Class<T> type,
                Object... additionalParameters) {
            return ApplicationInjector.instantiate (type, additionalParameters);
        }
    }
}
