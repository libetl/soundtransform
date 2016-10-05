package org.toilelibre.libe.soundtransform;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.toilelibre.libe.soundtransform.actions.notes.ImportAPackIntoTheLibrary;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;

import android.content.Context;
import android.content.res.Resources;

public class BlackBoxAndroidTest extends SoundTransformAndroidTest {

    static final class FakeR {
        public static final class attr {
        }

        public static final class raw {
            public static int gpiano6 = 0x7f020003;
        }
    }
    
    @Test
    public void importAndroidPack () throws SoundTransformException {
        //given
        Context fakeContext = Mockito.mock (Context.class);
        Resources fakeResources = Mockito.mock (Resources.class);
        Mockito.when (fakeContext.getResources ()).thenReturn (fakeResources);
        Mockito.when (fakeResources.openRawResource(1)).thenReturn (
                Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("smallpack.json"));
        Mockito.when (fakeResources.openRawResource (FakeR.raw.gpiano6)).thenReturn (
                Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("gpiano6.wav"));
        ImportAPackIntoTheLibrary importAPackAction = new ImportAPackIntoTheLibrary ();

        //when
        importAPackAction.importAPack ("pack1", fakeContext, FakeR.raw.class, 1);
        
        //then
        Pack pack1 = $.select (Library.class).getPack ("pack1");
        Assert.assertTrue (pack1.size () == 1);
    }
}
