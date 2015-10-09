package org.toilelibre.libe.soundtransform.model.library.pack;

import java.util.HashMap;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;

public class Pack extends HashMap<String, Range> {

    /**
     *
     */
    private static final long serialVersionUID = -7834749756288316057L;

    @Override
    public String toString () {
        return $.select (PackToStringHelper.class).toString (this);
    }
}
