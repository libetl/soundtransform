package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.library.pack.Pack;

public interface FluentClientCommon {

    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    FluentClientReady andAfterStart ();

    /**
     * Stops the client pipeline and returns the pack whose title is in parameter
     *
     * @param title the title of the pack
     * @return a pack object
     */
    Pack stopWithAPack (String title);
}
