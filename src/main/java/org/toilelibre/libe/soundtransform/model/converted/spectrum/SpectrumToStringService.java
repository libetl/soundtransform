package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.Service;

@Service
interface SpectrumToStringService<T> {

    String convert (Spectrum<T> spectrum);

}