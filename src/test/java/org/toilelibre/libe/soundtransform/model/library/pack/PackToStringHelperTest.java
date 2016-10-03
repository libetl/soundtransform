package org.toilelibre.libe.soundtransform.model.library.pack;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.note.SimpleNoteInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PackToStringHelperTest extends SoundTransformTest {

    private PackToStringHelper packToStringHelper = $.select (PackToStringHelper.class);
    
    @Test
    public void toStringOfASingleNote () throws SoundTransformException {
        final Range range = new Range ();
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("gpiano3.wav"));
        JsonElement result = new Gson ().fromJson (packToStringHelper.toString (range.values ().iterator ().next ()), JsonElement.class);
        Assert.assertTrue ("gpiano3.wav".equals(((JsonObject)result).get ("name").getAsString ()));
    }
    
    @Test
    public void toStringOfARange () throws SoundTransformException {
        final Range range = new Range ();
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("gpiano3.wav"));
        JsonArray result = new Gson ().fromJson (packToStringHelper.toString (range), JsonArray.class);
        Assert.assertTrue ("gpiano3.wav".equals(((JsonObject)((JsonArray)result).get (0)).get ("name").getAsString ()));
    }
    
    @Test
    public void toStringOfAPack () throws SoundTransformException {
        final Pack pack = new Pack ();
        final Range range = new Range ();
        $.select (AddNoteService.class).addNote (range, new SimpleNoteInfo ("gpiano3.wav"));
        pack.put ("piano", range);
        JsonObject result = new Gson ().fromJson (packToStringHelper.toString (pack), JsonObject.class);
        Assert.assertTrue ("gpiano3.wav".equals(((JsonObject)((JsonArray)((JsonObject)result).get ("piano")).get (0)).get ("name").getAsString ()));
    }
}
