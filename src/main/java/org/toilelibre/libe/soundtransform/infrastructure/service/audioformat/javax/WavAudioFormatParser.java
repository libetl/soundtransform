package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor.FrameProcessorErrorCode;

public class WavAudioFormatParser implements AudioFormatParser {

    @Override
    public Object audioFormatfromSoundInfo (final StreamInfo info) {
        final int channels = info.getChannels ();
        final int sampleSizeInBits = info.getSampleSize () * Byte.SIZE;
        final double sampleRate = info.getSampleRate ();
        final boolean bigEndian = info.isBigEndian ();
        final boolean pcmSigned = info.isPcmSigned ();
        return new AudioFormat ((float) sampleRate, sampleSizeInBits, channels, pcmSigned, bigEndian);
    }

    @Override
    public StreamInfo fromAudioFormat (final Object audioFormat1, final long frameLength) {
        final AudioFormat audioFormat = (AudioFormat) audioFormat1;
        final int channels = audioFormat.getChannels ();
        final int sampleSize = audioFormat.getFrameSize () / channels;
        final float sampleRate = audioFormat.getSampleRate ();
        final boolean bigEndian = audioFormat.isBigEndian ();
        final boolean pcmSigned = audioFormat.getEncoding () == Encoding.PCM_SIGNED;
        return new StreamInfo (channels, frameLength, sampleSize, sampleRate, bigEndian, pcmSigned, null);
    }

    @Override
    public StreamInfo getSoundInfo (final InputStream is) throws SoundTransformException {
        if (is instanceof AudioInputStream) {
            final AudioInputStream ais = (AudioInputStream) is;
            return this.fromAudioFormat (ais.getFormat (), ais.getFrameLength ());
        }
        try {
            final AudioFileFormat aff = AudioSystem.getAudioFileFormat (is);
            return this.fromAudioFormat (aff.getFormat (), aff.getFrameLength ());
        } catch (final UnsupportedAudioFileException e) {
            throw new SoundTransformException (FrameProcessorErrorCode.WRONG_TYPE, e, is);
        } catch (final IOException e) {
            throw new SoundTransformException (FrameProcessorErrorCode.WRONG_TYPE, e, is);
        }
    }
}
