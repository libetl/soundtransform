package org.toilelibre.libe.soundtransform;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TestDetectNotes {

    @Test
    public void test1 () throws SoundTransformException {
        int value = 200;
        int tenpercent = value * 10 / 100 - 1;
        final List<String> messages = new LinkedList<String> ();
        int [] t = new int [2000];
        for (int i = 200 ; i < 600 ; i++) {
            t [i] = (int) (value + Math.random () * tenpercent - tenpercent / 2);
        }
        for (int i = 800 ; i < 1000 ; i++) {
            t [i] = (int) (value + Math.random () * tenpercent - tenpercent / 2);
        }
        for (int i = 1100 ; i < 1600 ; i++) {
            t [i] = (int) (value + Math.random () * tenpercent - tenpercent / 2);
        }

        new ShapeSoundTransformation (Library.defaultPack, "simple_piano", t).setObservers (new PrintlnTransformObserver (), new Observer () {

            @Override
            public void notify (LogEvent logEvent) {
                messages.add (logEvent.toString ());
            }
        }).transform (200000, 100, 2, 44100, 1);
        Assert.assertTrue (messages.get (0).endsWith (" between 200/2000 and 602/2000"));
        Assert.assertTrue (messages.get (1).endsWith (" between 800/2000 and 1002/2000"));
        Assert.assertTrue (messages.get (2).endsWith (" between 1100/2000 and 1602/2000"));
    }
}
