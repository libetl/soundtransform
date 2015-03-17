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

    static Injector injector = Bootstrap.injector(AndroidRootModule.class);

    private ApplicationInjector() {

    }

    public static class $ {

        private $() {

        }

        public static <T> T create(final Class<T> type, final Object... additionalParameters) {
            return ApplicationInjector.instantiate(type, additionalParameters);
        }

        public static <T> T select(final Class<T> type) {
            return ApplicationInjector.getBean(type);
        }

    }

    public enum ApplicationInjectorErrorCode implements ErrorCode {

        INSTANTIATION_FAILED("Instantiation failed (seen warnings list : %1s)");

        private final String messageFormat;

        ApplicationInjectorErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private static int findNewInstanceParams(final Object[] newInstanceParams, final Constructor<?> constructor, final Object[] additionalParameters, final List<String> warnings) {
        final Class<?>[] ptypes = constructor.getParameterTypes();
        for (int i = 0; i < ptypes.length; i++) {
            newInstanceParams[i] = ApplicationInjector.tryToFindABeanForClass(ptypes[i], warnings);
        }
        int additionalParamCounter = 0;
        for (int i = 0; i < newInstanceParams.length; i++) {
            if (newInstanceParams[i] == null) {
                ApplicationInjector.setNewInstanceParamsValue(newInstanceParams, i, ptypes[i], additionalParameters, additionalParamCounter);
                additionalParamCounter++;
            }
        }
        return additionalParamCounter;
    }

    private static void setNewInstanceParamsValue(Object[] newInstanceParams, int i, Class<?> parameterType, final Object[] additionalParameters, int additionalParamCounter) {
        if (additionalParamCounter < additionalParameters.length) {
            if (parameterType.isArray() && !additionalParameters[additionalParamCounter].getClass().isArray()) {
                newInstanceParams[i] = Array.fill(additionalParameters[additionalParamCounter], 1);
            } else {
                newInstanceParams[i] = additionalParameters[additionalParamCounter];
            }
        }
    }

    public static <T> T getBean(final Class<T> type) {
        return ApplicationInjector.injector.resolve(Dependency.<T> dependency(type));
    }

    public static <T> T instantiate(final Class<T> type, final Object... additionalParameters) {
        final List<String> warnings = new LinkedList<String>();
        for (final Constructor<?> constructor : type.getDeclaredConstructors()) {
            @SuppressWarnings("unchecked")
            final T result = (T) ApplicationInjector.tryToInstantiateWithThisConstructor(constructor, additionalParameters, warnings);
            if (result != null) {
                return result;
            }
        }
        throw new SoundTransformRuntimeException(new SoundTransformException(ApplicationInjectorErrorCode.INSTANTIATION_FAILED, new NullPointerException(), warnings.toString()));
    }

    private static <T> T newInstance(final Constructor<T> constructor, final Object[] newInstanceParams, final List<String> warnings) {
        final String warningPrefix = "Constructor " + constructor;
        try {
            return constructor.newInstance(newInstanceParams);
        } catch (final InstantiationException e) {
            warnings.add(warningPrefix + " could not instantiate (" + e + ")");
        } catch (final IllegalAccessException e) {
            warnings.add(warningPrefix + " is not accessible (" + e + ")");
        } catch (final IllegalArgumentException e) {
            warnings.add(warningPrefix + " had an illegal argument (" + e + ")");
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof SoundTransformException) {
                warnings.add(warningPrefix + " threw an ErrorCode : " + ((SoundTransformException) e.getCause()).getErrorCode().name());
            }
            warnings.add(warningPrefix + " could not call a method (" + e + ")");
        }
        return null;
    }

    private static Object tryToFindABeanForClass(final Class<?> class1, final List<String> warnings) {
        try {
            return ApplicationInjector.instantiate(class1);
        } catch (final SoundTransformRuntimeException stre) {
            warnings.add(stre.getMessage() + "  (" + stre + ")");
        }
        try {
            return ApplicationInjector.getBean(class1);
        } catch (final NoSuchResourceException nsre) {
            warnings.add("Could not find a bean named " + class1 + " (" + nsre + ")");
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T tryToInstantiateWithThisConstructor(final Constructor<?> constructor, final Object[] additionalParameters, final List<String> warnings) {
        constructor.setAccessible (true);
        final Object[] newInstanceParams = new Object[constructor.getParameterTypes().length];
        final int additionalParamCounter = ApplicationInjector.findNewInstanceParams(newInstanceParams, constructor, additionalParameters, warnings);
        if (additionalParamCounter != additionalParameters.length) {
            warnings.add("Argument number in constructor did not match");
            return null;
        }
        return (T) ApplicationInjector.newInstance(constructor, newInstanceParams, warnings);
    }

}
