package org.toilelibre.libe.soundtransform.ioc;

import se.jbee.inject.Dependency;
import se.jbee.inject.Injector;
import se.jbee.inject.bootstrap.Bootstrap;

public class ApplicationInjector {
    
    private static final String RUNTIME_SYSTEM_PROPERTY = "java.vm.vendor";

    static Injector injector = Bootstrap.injector (ImplChooser.getCorrectImplModule (System.getProperty (ApplicationInjector.RUNTIME_SYSTEM_PROPERTY)));

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
