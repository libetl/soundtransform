package org.toilelibre.libe.soundtransform.infrastructure.service.pack;

import java.util.Map;

import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;

import com.google.gson.Gson;

public class GsonPackConfigParser implements PackConfigParser {

    @SuppressWarnings ("unchecked")
    @Override
    public Map<String, Map<String, String>> parse (final String input) {
        final Gson gson = new Gson ();
        return gson.fromJson (input, Map.class);
    }

}
