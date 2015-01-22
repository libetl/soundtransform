package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.InputStream;


import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.actions.transform.ExportSoundToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class AndroidPlaySoundProcessor implements PlaySoundProcessor<Complex []> {

    public AndroidPlaySoundProcessor () {

    }

    @Override
    public Object play (final InputStream ais) throws PlaySoundException {
        return null;
    }

    @Override
    public Object play (final Sound [] channels) throws SoundTransformException {

        if (channels.length == 0) {
            return new Object ();
        }

        final InputStream ais = new ExportSoundToInputStream ().toStream (channels, new InputStreamInfo (channels.length, channels [0].getSamples ().length, channels [0].getNbBytesPerSample () * 8, channels [0].getSampleRate (), true, false));
        return this.play (ais);
    }

    @Override
    public Object play (final Spectrum<Complex []> spectrum) throws SoundTransformException {
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
        final Complex [] complexArray = fastFourierTransformer.transform (spectrum.getState (), TransformType.INVERSE);
        final long [] sampleArray = new long [complexArray.length];
        int i = 0;
        for (final Complex c : complexArray) {
            sampleArray [i++] = (long) c.getReal ();
        }
        return this.play (new Sound [] { new Sound (sampleArray, spectrum.getNbBytes (), spectrum.getSampleRate (), 0) });
    }

}
