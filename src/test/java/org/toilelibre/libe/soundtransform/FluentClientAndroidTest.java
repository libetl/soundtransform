package org.toilelibre.libe.soundtransform;

import java.io.InputStream;
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class FluentClientAndroidTest extends SoundTransformAndroidTest {

    public static class Context {

        public Resources getResources () {
            return new Resources ();
        }

    }

    public static class Resources {
        public InputStream openRawResource (final int id) throws RuntimeException {
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
    }

    @Test
    public void testLoadPack () throws SoundTransformException {
        final Context context = new Context ();
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        final Pack pack = FluentClient.start ().withAPack ("default", context, R.raw.class, R.raw.defaultpack).stopWithAPack ("default");
        pack.toString ();
        Assert.assertNotNull (pack);
        Assert.assertNotEquals (pack.size (), 0);
    }
}
