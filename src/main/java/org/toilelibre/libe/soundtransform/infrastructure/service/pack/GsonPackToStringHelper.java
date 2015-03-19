package org.toilelibre.libe.soundtransform.infrastructure.service.pack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.PackToStringHelper;
import org.toilelibre.libe.soundtransform.model.library.note.SimpleNote;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

class GsonPackToStringHelper implements PackToStringHelper {

    private JsonElement toJsonElement (final Note note) {
        if (note instanceof SimpleNote) {
            return new Gson ().toJsonTree (((SimpleNote) note).getNoteInfo ());
        }
        return new Gson ().toJsonTree (Collections.emptyList ());
    }

    private JsonElement toJsonElement (final Pack pack) {
        final Map<String, JsonElement> map = new HashMap<String, JsonElement> ();
        for (final Entry<String, Range> entry : pack.entrySet ()) {
            map.put (entry.getKey (), this.toJsonElement (entry.getValue ()));
        }
        return new Gson ().toJsonTree (map);
    }

    private JsonElement toJsonElement (final Range range) {
        if (range.size () == 1 && !(range.get (0) instanceof SimpleNote)) {
            return new JsonArray ();
        }
        final JsonElement [] noteJsons = new JsonElement [range.size ()];
        int i = 0;
        for (final Note note : range.values ()) {
            noteJsons [i++] = new Gson ().toJsonTree (this.toJsonElement (note));
        }
        return new Gson ().toJsonTree (noteJsons);
    }

    @Override
    public String toString (final Note note) {
        return new GsonBuilder ().setPrettyPrinting ().create ().toJson (this.toJsonElement (note));
    }

    @Override
    public String toString (final Pack pack) {
        return new GsonBuilder ().setPrettyPrinting ().create ().toJson (this.toJsonElement (pack));
    }

    @Override
    public String toString (final Range range) {
        return new GsonBuilder ().setPrettyPrinting ().create ().toJson (this.toJsonElement (range));
    }

}
