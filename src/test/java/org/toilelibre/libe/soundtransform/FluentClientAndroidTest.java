package org.toilelibre.libe.soundtransform;

import java.io.InputStream;
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

import android.content.Context;
import android.content.res.Resources;

public class FluentClientAndroidTest extends SoundTransformAndroidTest {

    private final Answer<InputStream> findAmongRFields = new Answer<InputStream> () {

                                                           @Override
                                                           public InputStream answer (final InvocationOnMock invocation) throws Throwable {
                                                               final int id = invocation.getArgumentAt (0, int.class);
                                                               Class<?> clazz = TestR.raw.class;
                                                               for (final Field f : clazz.getDeclaredFields ()) {

                                                                   try {
                                                                       f.setAccessible (true);
                                                                       if (f.getType () == int.class && f.getInt (null) == id) {
                                                                           InputStream result = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (f.getName ());
                                                                           if (result == null) {
                                                                               result = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (f.getName () + ".wav");
                                                                           }
                                                                           if (result == null) {
                                                                               result = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (f.getName () + ".json");
                                                                           }
                                                                           return result;
                                                                       }
                                                                   } catch (final IllegalArgumentException e) {
                                                                       new Slf4jObserver (LogLevel.INFO).notify ("openRawResource : " + e);
                                                                   } catch (final IllegalAccessException e) {
                                                                       new Slf4jObserver (LogLevel.INFO).notify ("openRawResource : " + e);
                                                                   }
                                                               }
                                                               throw Mockito.mock (Resources.NotFoundException.class);
                                                           }

                                                       };

    @Test (expected = SoundTransformException.class)
    public void loadPackWithMissingFile () throws SoundTransformException, ClassNotFoundException {
        final Context context = this.given ();
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        try {
            FluentClient.start ().withAPack ("default", context, TestR.raw.class, -1).stopWithAPack ("default");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals ("COULD_NOT_READ_ID", ste.getErrorCode ().name ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void loadPackOk () throws SoundTransformException, ClassNotFoundException {
        final Context context = this.given ();
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        try {
            FluentClient.start ().withAPack ("default", context, TestR.raw.class, TestR.raw.badidpack).stopWithAPack ("default");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals ("EMPTY_INPUT_STREAM", ste.getErrorCode ().name ());
            throw ste;
        }
    }

    @Test
    public void loadPack () throws SoundTransformException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        try {
            Class.forName ("org.toilelibre.libe.soundtransform.R.raw");
        } catch (ClassNotFoundException cnfe) {
            //this test cannot be launched on non android platform
            return;
        }
        
        final Context context = this.given ();

        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        final Pack pack = FluentClient.start ().withAPack ("default", context, TestR.raw.class, 
                (Integer)Class.forName ("org.toilelibre.libe.soundtransform.R.raw").getDeclaredField ("defaultpack").get (null)).stopWithAPack ("default");
        pack.toString ();
        Assert.assertNotNull (pack);
        Assert.assertNotEquals (pack.size (), 0);
    }

    private Context given () {
        final Context context = Mockito.mock (Context.class);
        final Resources resources = Mockito.mock (Resources.class);
        Mockito.when (context.getResources ()).thenReturn (resources);
        Mockito.when (resources.openRawResource (Matchers.any (int.class))).then (this.findAmongRFields);
        return context;
    }

    @Test
    public void readOgg () throws SoundTransformException {
        FluentClient.start ().withClasspathResource ("raw/short.ogg").convertIntoSound ().exportToClasspathResource ("short.wav");
    }

    @Test
    public void readMp3 () throws SoundTransformException {
        FluentClient.start ().withClasspathResource ("mp3test.mp3").convertIntoSound ().exportToClasspathResource ("mp3test.wav");
    }
}
