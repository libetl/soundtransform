package org.toilelibre.libe.soundtransform.model.library.pack;

import java.util.Map;

public interface PackConfigParser {

    Map<String, Map<String, String>> parse (String input);
}
