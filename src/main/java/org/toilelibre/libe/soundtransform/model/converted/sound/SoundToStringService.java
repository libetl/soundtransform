package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.Service;

@Service
interface SoundToStringService {

    String convert (Channel input);

}