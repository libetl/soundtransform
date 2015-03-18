package org.toilelibre.libe.soundtransform.infrastructure.service.pack;

import java.util.List;
import java.util.Map;

import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;

import com.google.gson.Gson;

class GsonPackConfigParser implements PackConfigParser {

    @Override
    public Map<String, List<Map<String, Object>>> parse (final String input) {
        final Gson gson = new Gson ();
        return gson.<Map<String, List<Map<String, Object>>>> fromJson (input, Map.class);
    }

}
