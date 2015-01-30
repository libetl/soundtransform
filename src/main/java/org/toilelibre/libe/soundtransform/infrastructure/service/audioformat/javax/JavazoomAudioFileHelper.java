package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.WriteInputStreamToBuffer;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;

public class JavazoomAudioFileHelper implements AudioFileHelper {

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        File readFile = inputFile;
        if (inputFile.getName ().toLowerCase (Locale.getDefault ()).endsWith (".mp3")) {
            File tempFile;
            try {
                tempFile = File.createTempFile ("soundtransform", ".wav");
            } catch (final IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_A_TEMP_FILE, e);
            }
            AudioInputStream ais;
            try {
                final Object mpegInstance = Class.forName ("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader").newInstance ();
                ais = (AudioInputStream) mpegInstance.getClass ().getDeclaredMethod ("getAudioInputStream", InputStream.class).invoke (mpegInstance, inputFile);
                final AudioFormat cdFormat = new AudioFormat (44100, 16, 2, true, false);
                final AudioInputStream decodedais = (AudioInputStream) Class.forName ("javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream").getConstructor (AudioFormat.class, AudioInputStream.class).newInstance (cdFormat, ais);
                AudioSystem.write (decodedais, AudioFileFormat.Type.WAVE, tempFile);
                readFile = tempFile;
                return AudioSystem.getAudioInputStream (readFile);
            } catch (final InstantiationException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final IllegalAccessException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final ClassNotFoundException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final IllegalArgumentException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final InvocationTargetException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final NoSuchMethodException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final SecurityException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
            } catch (final IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
            } catch (final UnsupportedAudioFileException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
            }

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
    public InputStream toStream (final InputStream is, final Object audioFormat1) throws SoundTransformException {
        if (!(audioFormat1 instanceof AudioFormat)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ("" + audioFormat1));
        }
        final AudioFormat audioFormat = (AudioFormat) audioFormat1;
        byte [] byteArray;
        try {
            byteArray = new WriteInputStreamToBuffer ().write (is);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e);
        }
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
