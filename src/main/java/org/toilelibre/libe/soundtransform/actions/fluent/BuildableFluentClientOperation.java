package org.toilelibre.libe.soundtransform.actions.fluent;


public interface BuildableFluentClientOperation extends FluentClientCommon {

    BuildableFluentClientOperationReady andAfterStart ();
    
    FluentClientOperation build ();

}
