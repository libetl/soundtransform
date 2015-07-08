package org.toilelibre.libe.soundtransform.actions.fluent;

public interface BuildableFluentClientOperation extends FluentClientCommon {

    @Override
    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    BuildableFluentClientOperationReady  andAfterStart ();

    /**
     * Builds the operation object, makes it ready to be used in the inParallel
     * method
     *
     * @return the client operation
     */
    FluentClientOperation build ();

}
