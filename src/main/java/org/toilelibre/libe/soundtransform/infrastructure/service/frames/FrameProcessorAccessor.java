package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import org.toilelibre.libe.soundtransform.infrastructure.service.fourier.FourierAccessor;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

public abstract class FrameProcessorAccessor extends FourierAccessor {

    protected FrameProcessor<AbstractLogAware<ByteArrayFrameProcessor>> provideFrameProcessor () {
        return new ByteArrayFrameProcessor ();
    }
}
