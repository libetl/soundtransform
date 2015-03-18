package org.toilelibre.libe.soundtransform.ioc;

import org.junit.Test;

public class CorrectPackageAccessorTest {

    class TestAccessor extends RootModuleWithoutAccessor {

        @Override
        protected void declare () {
        }
    }

    @Test
    public void testCorrectPackage () {
        new TestAccessor ();
    }
}
