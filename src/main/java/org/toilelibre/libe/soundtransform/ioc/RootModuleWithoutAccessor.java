package org.toilelibre.libe.soundtransform.ioc;

import se.jbee.inject.bind.BinderModule;

public abstract class RootModuleWithoutAccessor extends BinderModule {

    public RootModuleWithoutAccessor () {
        if (!this.getClass ().getPackage ().getName ().startsWith (RootModuleWithoutAccessor.class.getPackage ().getName ())) {
            throw new se.jbee.inject.DIRuntimeException ("Not allowed to override this lib accessors");
        }
    }
}
