package org.toilelibre.libe.soundtransform.ioc;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.ioc.ImplChooser.ImplChooserErrorCode;
import org.toilelibre.libe.soundtransform.ioc.android.AndroidRootModule;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

@PrepareForTest ({ System.class })
public class ImplChooserTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test
    public void testWrongRuntime () {
        this.rule.hashCode ();
        PowerMockito.mockStatic (System.class, new Answer<Object> () {

            @Override
            public Object answer (final InvocationOnMock invocation) throws Throwable {
                return !invocation.getMethod ().getName ().equals ("getProperty") || !invocation.getArgumentAt (0, String.class).equals (ApplicationInjector.RUNTIME_SYSTEM_PROPERTY) ? invocation.callRealMethod () : "Not a real runtime";
            }

        });
        try {
            ImplChooser.getCorrectImplModule (System.getProperty (ApplicationInjector.RUNTIME_SYSTEM_PROPERTY));
            Assert.fail ("Should have failed because the Java Runtime is not correct");
        } catch (final SoundTransformRuntimeException stre) {
            Assert.assertEquals (stre.getErrorCode (), ImplChooserErrorCode.INVALID_RUNTIME);
        }
    }

    @Test
    public void testAndroid () {
        PowerMockito.mockStatic (System.class, new Answer<Object> () {

            @Override
            public Object answer (final InvocationOnMock invocation) throws Throwable {
                return !invocation.getMethod ().getName ().equals ("getProperty") || !invocation.getArgumentAt (0, String.class).equals (ApplicationInjector.RUNTIME_SYSTEM_PROPERTY) ? invocation.callRealMethod () : "The Android Project";
            }

        });
        Assert.assertSame (AndroidRootModule.class, ImplChooser.getCorrectImplModule (System.getProperty (ApplicationInjector.RUNTIME_SYSTEM_PROPERTY)));

    }
}
