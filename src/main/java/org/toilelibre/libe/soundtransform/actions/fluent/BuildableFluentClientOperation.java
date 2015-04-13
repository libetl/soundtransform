package org.toilelibre.libe.soundtransform.actions.fluent;

public interface BuildableFluentClientOperation extends FluentClientCommon {

    @Override
    BuildableFluentClientOperationReady andAfterStart ();

    FluentClientOperation build ();

}
