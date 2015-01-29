package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.HasInputStreamInfo;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException.PlaySoundErrorCode;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AndroidPlaySoundProcessor implements PlaySoundProcessor<Complex []> {

    public AndroidPlaySoundProcessor () {

    }

    @Override
    public Object play (final InputStream ais) throws PlaySoundException {
        if (!(ais instanceof HasInputStreamInfo)) {
            throw new PlaySoundException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, new ClassCastException ("Could not cas InputStream as a HasInputStreamInfo class")));

        }
        final HasInputStreamInfo is = (HasInputStreamInfo) ais;
        final AudioTrack audioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, (int) is.getInfo ().getSampleRate (), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, (int) is.getInfo ().getFrameLength (), AudioTrack.MODE_STATIC);
        final byte [] baSoundByteArray = new byte [(int) is.getInfo ().getFrameLength () * is.getInfo ().getSampleSize ()];
        try {
            ais.read (baSoundByteArray);
        } catch (final IOException e1) {
            throw new PlaySoundException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e1));
        }
        audioTrack.write (baSoundByteArray, 0, baSoundByteArray.length);
        audioTrack.flush ();
        audioTrack.play ();

        final Thread thread = new Thread () {
            @Override
            public void run () {
                int lastFrame = -1;
                while (lastFrame != audioTrack.getPlaybackHeadPosition ()) {
                    lastFrame = audioTrack.getPlaybackHeadPosition ();
                    try {
                        Thread.sleep (1000);
                    } catch (final InterruptedException e) {
                        throw new SoundTransformRuntimeException(
                                new PlaySoundException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e)));
                    }
                }
                audioTrack.stop ();
                audioTrack.release ();
            }
        };
        thread.start ();
        return thread;
    }

    @Override
    public Object play (final Sound [] channels) throws SoundTransformException {

        if (channels.length == 0) {
            return new Object ();
        }

        final InputStream ais = $.create (TransformSoundService.class).toStream (channels, new InputStreamInfo (channels.length, channels [0].getSamples ().length, channels [0].getNbBytesPerSample () * 8, channels [0].getSampleRate (), true, false));
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
