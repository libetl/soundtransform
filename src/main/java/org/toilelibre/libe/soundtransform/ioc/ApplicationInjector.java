package org.toilelibre.libe.soundtransform.ioc;

import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;

import se.jbee.inject.Dependency;
import se.jbee.inject.Injector;
import se.jbee.inject.bootstrap.Bootstrap;

public class ApplicationInjector {

    static Injector injector = Bootstrap.injector (AndroidRootModule.class);

    private ApplicationInjector () {

    }

    public static class $ {

        private $ () {

        }

        public static <T> T select (final Class<T> type) {
            return ApplicationInjector.getBean (type);
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


}
