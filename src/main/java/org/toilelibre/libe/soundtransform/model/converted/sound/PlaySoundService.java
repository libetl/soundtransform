package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class PlaySoundService<T> {

    private final PlaySoundProcessor processor;

    public PlaySoundService (final PlaySoundProcessor processor) {
        this.processor = processor;
    }

    public Object play (final InputStream is) throws SoundTransformException {
        return this.processor.play (is);
    }

    public Object play (final Sound [] channels) throws SoundTransformException {

        if (channels.length == 0) {
            return new Object ();
        }

        final InputStream ais = $.create (TransformSoundService.class).toStream (channels, InputStreamInfo.of (channels));
        return this.processor.play (ais);
    }

    public Object play (final Spectrum<T> spectrum) throws SoundTransformException {
        @SuppressWarnings ("unchecked")
        final FourierTransformHelper<T> fourierTransformHelper = $.select (FourierTransformHelper.class);
        return this.play (new Sound [] { fourierTransformHelper.reverse (spectrum) });
    }
}
