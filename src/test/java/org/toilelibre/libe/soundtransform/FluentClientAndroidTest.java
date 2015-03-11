package org.toilelibre.libe.soundtransform;

import java.io.InputStream;
import java.lang.reflect.Field;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class FluentClientAndroidTest extends SoundTransformAndroidTest {

    public static class Resources {
        public InputStream openRawResource (int id) throws RuntimeException {
            for (Field f : org.toilelibre.libe.soundtransform.R.raw.class.getDeclaredFields ()){

                try {
                if (f.getInt (null) == id){
                    InputStream result = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (f.getName ());
                    if (result == null){
                        result = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (f.getName () + ".wav");
                    }
                    if (result == null){
                        result = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (f.getName () + ".json");
                    }
                    return result;
                }
                } catch (IllegalArgumentException e) {
                    new Slf4jObserver (LogLevel.INFO).notify ("openRawResource : " + e);
                } catch (IllegalAccessException e) {
                    new Slf4jObserver (LogLevel.INFO).notify ("openRawResource : " + e);
                }
            }
            throw new RuntimeException ("" + id);
        }
    }
    public static class Context {

        public Resources getResources () {
            return new Resources ();
        }
        
    }
    
    @Test
    public void testLoadPack () throws SoundTransformException{
        Context context = new Context ();
        FluentClient.setDefaultObservers (new Slf4jObserver (LogLevel.WARN));
        FluentClient.start ().withAPack ("default", context, org.toilelibre.libe.soundtransform.R.raw.class, 
                context.getResources ().openRawResource (R.raw.defaultpack));
    }
}
