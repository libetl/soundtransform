package org.toilelibre.libe.soundtransform;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TestDetectNotes extends SoundTransformTest {

    @Test
    public void test1 () throws SoundTransformException {
        final int value = 200;
        final int twopercents = (int) (value * 5.0 / 100 - 1);
        final List<String> messages = new LinkedList<String> ();
        final float [] t = new float [2000];
        for (int i = 200 ; i < 600 ; i++) {
            t [i] = (float) (value + Math.random () * twopercents - twopercents / 2);
        }
        for (int i = 800 ; i < 1000 ; i++) {
            t [i] = (float) (value + Math.random () * twopercents - twopercents / 2);
        }
        for (int i = 1100 ; i < 1600 ; i++) {
            t [i] = (float) (value + Math.random () * twopercents - twopercents / 2);
        }
        ((ImportPackService<?>) $.select (ImportPackService.class).setObservers (new Slf4jObserver (LogLevel.WARN))).importPack ($.select (Library.class), "default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));
        new ShapeSoundTransform ("default", "simple_piano", t, new FormatInfo (2, 44100.0f)).setObservers (new Slf4jObserver (), new Observer () {

            @Override
            public void notify (final LogEvent logEvent) {
                messages.add (logEvent.toString ());
            }
        }).transform (100, 1, 200000);
        Assert.assertTrue (messages.get (0).endsWith (" between 200/2000 and  600/ 2000"));
        Assert.assertTrue (messages.get (1).endsWith (" between 800/2000 and 1000/ 2000"));
        Assert.assertTrue (messages.get (2).endsWith (" between 1100/2000 and 1600/ 2000"));
    }
}
