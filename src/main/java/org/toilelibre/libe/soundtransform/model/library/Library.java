package org.toilelibre.libe.soundtransform.model.library;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.soundtransform.model.library.pack.Pack;

public class Library {

    private final Map<String, Pack> packs;

    public Library() {
        this.packs = new HashMap<String, Pack>();
    }

    public void addPack(final String name, final Pack p) {
        this.packs.put(name, p);
    }

    public Pack getPack(final String name) {
        return this.packs.get(name);
    }

}
