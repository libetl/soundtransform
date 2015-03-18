package org.toilelibre.libe.soundtransform.ioc;

import org.junit.BeforeClass;
import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;

import se.jbee.inject.bootstrap.Bootstrap;

public class SoundTransformAndroidTest {

    @BeforeClass
    public static void setUp () {
        ApplicationInjector.injector = Bootstrap.injector (AndroidRootModule.class);
    }
}
