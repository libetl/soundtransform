package org.toilelibre.libe.soundtransform.ioc;

import java.util.HashMap;
import java.util.Map;

import se.jbee.inject.bind.BinderModule;

public abstract class RootModuleWithoutAccessor extends BinderModule {

    protected Map<Class<? extends Object>, Class<? extends Object>> usedImpls = new HashMap<Class<? extends Object>, Class<? extends Object>> ();
    
    public RootModuleWithoutAccessor () {
        if (!this.getClass ().getPackage ().getName ().startsWith (RootModuleWithoutAccessor.class.getPackage ().getName ())) {
            throw new se.jbee.inject.DIRuntimeException ("Not allowed to override this lib accessors");
        }
    }
}
