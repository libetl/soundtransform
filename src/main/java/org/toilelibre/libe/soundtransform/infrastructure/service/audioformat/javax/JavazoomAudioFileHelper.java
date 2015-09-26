package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;

final class JavazoomAudioFileHelper implements AudioFileHelper {

    private static final int HIGH_SAMPLE_RATE = 48000;
    private static final int TWO_BYTES_SAMPLE = 2 * Byte.SIZE;
    private static final int STEREO           = 2;

    private InputStream convertIntoWavStream (final File inputFile) throws SoundTransformException {
        final AudioFileReader afr = this.getMpegAudioFileReader ();
        final AudioInputStream ais = this.getAudioInputStreamFromAudioFileReader (afr, inputFile);
        final AudioFormat cdFormat = new AudioFormat (JavazoomAudioFileHelper.HIGH_SAMPLE_RATE, JavazoomAudioFileHelper.TWO_BYTES_SAMPLE, JavazoomAudioFileHelper.STEREO, true, false);
        return this.getDecodedAudioInputStream (cdFormat, ais);
    }

    private AudioInputStream getAudioInputStreamFromAudioFileReader (final AudioFileReader afr, final File inputFile) throws SoundTransformException {
        try {
            return afr.getAudioInputStream (inputFile);
        } catch (final UnsupportedAudioFileException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.WRONG_TYPE, e, inputFile.getName ());
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
        }
    }

    private InputStream getAudioInputSreamFromWavFile (final File readFile) throws SoundTransformException {
        try {
            final BufferedInputStream inputStream = new BufferedInputStream (new FileInputStream (readFile));
            return this.getAudioInputStream (inputStream);
        } catch (final FileNotFoundException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, readFile.getPath ());
        }
    }

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        final File readFile = inputFile;
        if (inputFile.getName ().toLowerCase (Locale.getDefault ()).endsWith (".mp3")) {
            return this.convertIntoWavStream (inputFile);
        }
        return this.getAudioInputSreamFromWavFile (readFile);
    }

    @Override
    public InputStream getAudioInputStream (final InputStream rawInputStream) throws SoundTransformException {
        try {
            return AudioSystem.getAudioInputStream (rawInputStream);
        } catch (final UnsupportedAudioFileException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.WRONG_TYPE, e, rawInputStream.toString ());
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, rawInputStream.toString ());
        }
    }

    private AudioInputStream getDecodedAudioInputStream (final AudioFormat cdFormat, final AudioInputStream ais) throws SoundTransformException {
        return new javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream (cdFormat, ais);
    }

    private AudioFileReader getMpegAudioFileReader () throws SoundTransformException {
        return new javazoom.spi.mpeg.sampled.file.MpegAudioFileReader ();
    }

    @Override
    public InputStream toStream (final byte [] byteArray, final Object audioFormat1) throws SoundTransformException {
        if (!(audioFormat1 instanceof AudioFormat)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ("" + audioFormat1));
        }
        final AudioFormat audioFormat = (AudioFormat) audioFormat1;
        final ByteArrayInputStream bais = new ByteArrayInputStream (byteArray);
        return new AudioInputStream (bais, audioFormat, byteArray.length / audioFormat.getFrameSize ());
    }

    @Override
    public void writeInputStream (final InputStream ais, final File fDest) throws SoundTransformException {
        if (!(ais instanceof AudioInputStream)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, new IllegalArgumentException ("" + ais), ais);
        }
        try {
            AudioSystem.write ((AudioInputStream) ais, AudioFileFormat.Type.WAVE, fDest);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, fDest.getName ());
        }
    }
}
