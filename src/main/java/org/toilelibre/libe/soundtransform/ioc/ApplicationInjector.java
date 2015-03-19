package org.toilelibre.libe.soundtransform.ioc;

import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;

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


    private static <T> T getBean (final Class<T> type) {
        return ApplicationInjector.injector.resolve (Dependency.<T> dependency (type));
    }


}
