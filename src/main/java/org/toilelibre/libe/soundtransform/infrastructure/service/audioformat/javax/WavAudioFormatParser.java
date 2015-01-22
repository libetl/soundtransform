package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor.FrameProcessorErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class WavAudioFormatParser implements AudioFormatParser {

    @Override
    public Object audioFormatfromInputStreamInfo (final InputStreamInfo info) {
        final int channels = info.getChannels ();
        final int frameSize = info.getSampleSize () * 8;
        final double sampleRate = info.getSampleRate ();
        final boolean bigEndian = info.isBigEndian ();
        final boolean pcmSigned = info.isPcmSigned ();
        return new AudioFormat ((float) sampleRate, frameSize, channels, pcmSigned, bigEndian);
    }

    @Override
    public InputStreamInfo fromAudioFormat (final Object audioFormat1, final long l) {
        final AudioFormat audioFormat = (AudioFormat) audioFormat1;
        final int channels = audioFormat.getChannels ();
        final long frameLength = l;
        final int sampleSize = audioFormat.getFrameSize () / channels;
        final double sampleRate = audioFormat.getSampleRate ();
        final boolean bigEndian = audioFormat.isBigEndian ();
        final boolean pcmSigned = audioFormat.getEncoding () == Encoding.PCM_SIGNED;
        return new InputStreamInfo (channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned);
    }

    @Override
    public InputStreamInfo getInputStreamInfo (final InputStream is) throws SoundTransformException {
        if (!(is instanceof AudioInputStream)) {
            throw new SoundTransformException (FrameProcessorErrorCode.WRONG_TYPE, new IllegalArgumentException (), is);
        }
        final AudioInputStream ais = (AudioInputStream) is;
        return this.fromAudioFormat (ais.getFormat (), ais.getFrameLength ());
    }
}
