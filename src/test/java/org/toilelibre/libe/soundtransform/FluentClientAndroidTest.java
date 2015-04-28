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
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

import android.content.Context;
import android.content.res.Resources;

public class FluentClientAndroidTest extends SoundTransformAndroidTest {

    private final Answer<InputStream> findAmongRFields = new Answer<InputStream> () {

                                                           @Override
                                                           public InputStream answer (final InvocationOnMock invocation) throws Throwable {
                                                               final int id = invocation.getArgumentAt (0, int.class);
                                                               for (final Field f : org.toilelibre.libe.soundtransform.R.raw.class.getDeclaredFields ()) {

                                                                   try {
                                                                       if (f.getInt (null) == id) {
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
                                                               throw new RuntimeException ("" + id);
                                                           }

                                                       };

    @Test (expected = SoundTransformException.class)
    public void loadPackWithMissingFile () throws SoundTransformException {
        final Context context = this.given ();
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        try {
            FluentClient.start ().withAPack ("default", context, TestR.raw.class, TestR.raw.badidpack).stopWithAPack ("default");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals ("COULD_NOT_READ_ID", ste.getErrorCode ().name ());
            throw ste;
        }
    }

    @Test
    public void loadPack () throws SoundTransformException {
        final Context context = this.given ();

        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        final Pack pack = FluentClient.start ().withAPack ("default", context, R.raw.class, R.raw.defaultpack).stopWithAPack ("default");
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
}
