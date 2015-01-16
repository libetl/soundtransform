package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;

public class JavazoomAudioFileHelper implements AudioFileHelper {

    @Override
    public AudioInputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        File readFile = inputFile;
        if (inputFile.getName ().toLowerCase ().endsWith (".mp3")) {
            File tempFile;
            try {
                tempFile = File.createTempFile ("soundtransform", ".wav");
            } catch (final IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_A_TEMP_FILE, e);
            }
            AudioInputStream ais;
            try {
                ais = new javazoom.spi.mpeg.sampled.file.MpegAudioFileReader ().getAudioInputStream (inputFile);
            } catch (final UnsupportedAudioFileException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            }
            final AudioFormat cdFormat = new AudioFormat (44100, 16, 2, true, false);
            final javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream decodedais = new javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream (cdFormat, ais);
            try {
                AudioSystem.write (decodedais, AudioFileFormat.Type.WAVE, tempFile);
            } catch (final IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
            }
            readFile = tempFile;
        }
        try {
            return AudioSystem.getAudioInputStream (readFile);
        } catch (final UnsupportedAudioFileException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
        }
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
