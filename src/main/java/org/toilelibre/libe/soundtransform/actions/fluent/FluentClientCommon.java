package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public interface FluentClientCommon {

    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    FluentClientReady andAfterStart();

    /**
     * Stops the client pipeline and returns the pack whose title is in
     * parameter
     *
     * @param title
     *            the title of the pack
     * @return a pack object
     */
    Pack stopWithAPack(String title);

    /**
     * Stops the client pipeline and returns the currently subscribed observers
     * 
     * @return the observers
     */
    Observer[] stopWithObservers();
}
