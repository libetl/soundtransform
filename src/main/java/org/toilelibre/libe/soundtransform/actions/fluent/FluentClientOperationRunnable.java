package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation.Step;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class FluentClientOperationRunnable implements Runnable {

    private final FluentClientOperation operation;
    private final FluentClientInterface clientInterface;
    private final int                   clientId;

    public FluentClientOperationRunnable (final FluentClientOperation operation1, final FluentClientInterface clientInterface1, final int clientId1) {
        this.operation = operation1;
        this.clientInterface = clientInterface1;
        this.clientId = clientId1;
    }

    @Override
    public void run () {
        for (final Step step : this.operation.getSteps ()) {
            try {
                step.run (this.clientInterface, this.clientId);
            } catch (final SoundTransformException ste) {
                throw new SoundTransformRuntimeException (ste);
            }
        }
    }

}