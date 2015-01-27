package org.toilelibre.libe.soundtransform.ioc;

import org.junit.BeforeClass;

import se.jbee.inject.bootstrap.Bootstrap;

public class SoundTransformTest {

    @BeforeClass
    public static void setUp () {
        ApplicationInjector.injector = Bootstrap.injector (JavaXRootModule.class);
    }
}
