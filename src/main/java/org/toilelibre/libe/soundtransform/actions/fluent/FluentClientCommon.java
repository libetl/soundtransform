package org.toilelibre.libe.soundtransform.actions.fluent;

public interface FluentClientCommon {

    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    FluentClientReady andAfterStart ();

}
