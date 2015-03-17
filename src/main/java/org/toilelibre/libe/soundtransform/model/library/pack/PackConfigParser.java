package org.toilelibre.libe.soundtransform.model.library.pack;

import java.util.List;
import java.util.Map;

public interface PackConfigParser {

    Map<String, List<Map<String, Object>>> parse(String input);
}
