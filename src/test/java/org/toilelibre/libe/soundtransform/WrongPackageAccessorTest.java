package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.AppenderAccessor;
import org.toilelibre.libe.soundtransform.ioc.RootModuleWithoutAccessor;

import se.jbee.inject.DIRuntimeException;

public class WrongPackageAccessorTest {

    static class TestAccessor extends RootModuleWithoutAccessor {

        @Override
        protected void declare () {
        }
    }

    @Test (expected = DIRuntimeException.class)
    public void testOverride () {
        new AppenderAccessor () {

            @Override
            protected void declare () {

            }
        };
    }

    @Test (expected = DIRuntimeException.class)
    public void testWrongPackage () {
        new TestAccessor ();
    }
}
