package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class PlaySoundService<T extends Serializable> {

    private final PlaySoundProcessor processor;
    private final TransformSoundService transformSoundService;
    private final FourierTransformHelper<T> fourierTransformHelper;

    public PlaySoundService (final PlaySoundProcessor processor1, final TransformSoundService
            transformSoundService1, FourierTransformHelper<T> fourierTransformHelper1) {
        this.processor = processor1;
        this.transformSoundService = transformSoundService1;
        this.fourierTransformHelper = fourierTransformHelper1;
        
    }

    public Object play (final InputStream is) throws SoundTransformException {
        return this.processor.play (is);
    }

    public Object play (final Sound [] channels) throws SoundTransformException {

        if (channels.length == 0) {
            return new Object ();
        }

        final InputStream ais = this.transformSoundService.toStream (channels, 
                StreamInfo.from (channels [0].getFormatInfo (), channels));
        return this.processor.play (ais);
    }

    public Object play (final Spectrum<T> spectrum) throws SoundTransformException {
        return this.play (new Sound [] { this.fourierTransformHelper.reverse (spectrum) });
    }
}
