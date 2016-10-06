package org.toilelibre.libe.soundtransform.infrastructure.service.pack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.PackToStringHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;
import org.toilelibre.libe.soundtransform.model.library.pack.note.Note;
import org.toilelibre.libe.soundtransform.model.library.pack.note.SimpleNote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Processor
final class GsonPackToStringHelper implements PackToStringHelper {

    private static final Gson SMALL_GSON_OBJECT = new Gson ();
    private static final Gson GSON_OBJECT = new GsonBuilder ().setPrettyPrinting ().create ();

    private JsonElement toJsonElement (final Note note) {
        if (note instanceof SimpleNote) {
            return SMALL_GSON_OBJECT.toJsonTree ( ((SimpleNote) note).getNoteInfo ());
        }
        return SMALL_GSON_OBJECT.toJsonTree (Collections.emptyList ());
    }

    private JsonElement toJsonElement (final Pack pack) {
        final Map<String, JsonElement> map = new HashMap<String, JsonElement> ();
        for (final Entry<String, Range> entry : pack.entrySet ()) {
            map.put (entry.getKey (), this.toJsonElement (entry.getValue ()));
        }
        return SMALL_GSON_OBJECT.toJsonTree (map);
    }

    private JsonElement toJsonElement (final Range range) {
        if (range.size () == 1 && ! (range.values ().iterator ().next () instanceof SimpleNote)) {
            return new JsonArray ();
        }
        final JsonElement [] noteJsons = new JsonElement [range.size ()];
        int i = 0;
        for (final Note note : range.values ()) {
            noteJsons [i++] = SMALL_GSON_OBJECT.toJsonTree (this.toJsonElement (note));
        }
        return SMALL_GSON_OBJECT.toJsonTree (noteJsons);
    }

    @Override
    public String toString (final Note note) {
        return GSON_OBJECT.toJson (this.toJsonElement (note));
    }

    @Override
    public String toString (final Pack pack) {
        return GSON_OBJECT.toJson (this.toJsonElement (pack));
    }

    @Override
    public String toString (final Range range) {
        return GSON_OBJECT.toJson (this.toJsonElement (range));
    }

}
