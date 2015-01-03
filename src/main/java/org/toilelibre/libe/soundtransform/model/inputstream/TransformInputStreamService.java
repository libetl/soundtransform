package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformInputStreamService implements LogAware {

    Observer []    observers = new Observer [0];
    FrameProcessor frameProcessor;

    public TransformInputStreamService (final Observer... observers) {
        this.setObservers (observers);
        this.frameProcessor = new org.toilelibre.libe.soundtransform.infrastructure.service.frames.ByteArrayFrameProcessor ();
    }

    public Sound [] byteArrayToFrames (final byte [] byteArray, final int channels, final long frameLength, final int sampleSize, final double sampleRate,
            final boolean bigEndian, final boolean pcmSigned) throws IOException {
        this.notifyAll ("[Test] byteArray -> ByteArrayInputStream");

        final ByteArrayInputStream bais = new ByteArrayInputStream (byteArray);
        return this.fromInputStream (bais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
    }

    public Sound [] fromInputStream (final AudioInputStream ais) throws IOException {
        final int channels = ais.getFormat ().getChannels ();
        final long frameLength = ais.getFrameLength ();
        final int sampleSize = ais.getFormat ().getFrameSize () / channels;
        final double sampleRate = ais.getFormat ().getSampleRate ();
        final boolean bigEndian = ais.getFormat ().isBigEndian ();
        final boolean pcmSigned = ais.getFormat ().getEncoding () == Encoding.PCM_SIGNED;

        return this.fromInputStream (ais, channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
    }

    public Sound [] fromInputStream (final InputStream ais, final int channels, final long frameLength, final int sampleSize, final double sampleRate,
            final boolean bigEndian, final boolean pcmSigned) throws IOException {
        this.notifyAll ("Converting input into java object");
        final Sound [] ret = new Sound [channels];
        final long neutral = pcmSigned ? this.frameProcessor.getNeutral (sampleSize) : 0;
        for (int channel = 0 ; channel < channels ; channel++) {
            ret [channel] = new Sound (new long [(int) frameLength], sampleSize, (int) sampleRate, channel);
        }
        for (int position = 0 ; position < frameLength ; position++) {
            final byte [] frame = new byte [sampleSize * channels];
            ais.read (frame);
            this.frameProcessor.byteArrayToFrame (frame, ret, position, bigEndian, pcmSigned, neutral);
        }
        this.notifyAll ("Convert done");
        return ret;
    }

    @Override
    public void log (final LogEvent event) {
        for (final Observer to : this.observers) {
            to.notify (event);
        }

    }

    private void notifyAll (final String s) {
        this.log (new LogEvent (LogLevel.INFO, s));
    }

    @Override
    public void setObservers (final Observer [] observers2) {
        this.observers = observers2;
        for (final Observer observer : observers2) {
            this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
        }
    }

    public AudioInputStream toStream (final Sound [] channels, final AudioFormat audioFormat) {

        final int length = audioFormat.getFrameSize () * channels [0].getSamples ().length;
        final byte [] data = this.frameProcessor.framesToByteArray (channels, audioFormat.getFrameSize () / channels.length, audioFormat.isBigEndian (),
                audioFormat.getEncoding () == Encoding.PCM_SIGNED);
        this.notifyAll ("Creating output file");
        // now save the file
        final ByteArrayInputStream bais = new ByteArrayInputStream (data);
        return new AudioInputStream (bais, audioFormat, length / audioFormat.getFrameSize ());
    }
}