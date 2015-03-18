package org.toilelibre.libe.soundtransform.infrastructure.service.fourier;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.AppenderAccessor;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;

public abstract class FourierAccessor extends AppenderAccessor {

    protected FourierTransformHelper<Complex []> provideFourierTransformHelper () {
        return new CommonsMath3FourierTransformHelper ();
    }
}
