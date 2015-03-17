package org.toilelibre.libe.soundtransform.infrastructure.service.frames;

import org.toilelibre.libe.soundtransform.infrastructure.service.fourier.FourierAccessor;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;

public abstract class FrameProcessorAccessor extends FourierAccessor {

    protected FrameProcessor<?> provideFrameProcessor() {
        return new ByteArrayFrameProcessor();
    }
}
