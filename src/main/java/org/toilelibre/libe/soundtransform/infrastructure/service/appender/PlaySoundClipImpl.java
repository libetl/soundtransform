package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.actions.transform.ExportSoundToInputStream;
import org.toilelibre.libe.soundtransform.model.PlaySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class PlaySoundClipImpl implements PlaySoundService {

    @Override
    public Object play (final InputStream ais) throws PlaySoundException {
        try {
            if (! (ais instanceof AudioInputStream)) {
                throw new PlaySoundException (new IllegalArgumentException ("" + ais));
            }
            final Line.Info linfo = new Line.Info (Clip.class);
            final Line line = AudioSystem.getLine (linfo);
            final Clip clip = (Clip) line;
            clip.addLineListener (new LineListener () {

                @Override
                public void update (final LineEvent event) {
                    final LineEvent.Type type = event.getType ();
                    if (type == LineEvent.Type.OPEN) {
                    } else if (type == LineEvent.Type.CLOSE) {
                    } else if (type == LineEvent.Type.START) {
                    } else if (type == LineEvent.Type.STOP) {
                        synchronized (clip) {
                            clip.close ();
                            clip.notify ();
                        }
                    }

                }

            });
            clip.open ((AudioInputStream) ais);
            clip.start ();
            synchronized (clip) {
                clip.wait ();
            }
            return clip;
        } catch (final LineUnavailableException lineUnavailableException) {
            throw new PlaySoundException (lineUnavailableException);
        } catch (final IOException e) {
            throw new PlaySoundException (e);
        } catch (final InterruptedException e) {
            throw new PlaySoundException (e);
        }
    }

    @Override
    public Object play (final Sound [] channels) throws SoundTransformException {
        final InputStream ais = new ExportSoundToInputStream ().toStream (channels, new InputStreamInfo (channels.length, channels [0].getSamples ().length, channels [0].getNbBytesPerSample () * 8,
                channels [0].getSampleRate (), true, false));

        return this.play (ais);
    }

    @Override
    public Object play (final Spectrum spectrum) throws SoundTransformException {
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
