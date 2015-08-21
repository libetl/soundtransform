package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.AndroidFileToWavFileConverter.Converters;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

final class AndroidAudioFileHelper extends AbstractLogAware<AndroidAudioFileHelper> implements AudioFileHelper {

    public ByteArrayInputStream convertFileToBaos (final File inputFile) throws SoundTransformException {
        final byte [] byteArray = new byte [(int) inputFile.length ()];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream (this.convertToWavIfNecessary (inputFile));
            fileInputStream.read (byteArray);
        } catch (final FileNotFoundException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close ();
                }
            } catch (final IOException e) {
                this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
            }
        }

        return new ByteArrayInputStream (byteArray);
    }

    private File convertToWavIfNecessary (File inputFile) throws SoundTransformException {
        File outputFile = inputFile;
        for (Converters converters : Converters.values ()) {
            if (inputFile.getName ().endsWith ("." + converters.name ().toLowerCase ())) {
                outputFile = converters.getConverter ().convert (inputFile);
            }
        }
        return outputFile == null ? inputFile : outputFile;
    }

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        try {
            return this.getAudioInputStream (this.convertFileToBaos (inputFile));
        } catch (final SoundTransformException ste) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste, inputFile.getName ());
        }
    }

    @Override
    public InputStream getAudioInputStream (final InputStream rawInputStream) throws SoundTransformException {
        try {
            final AudioInputStream ais = new AudioInputStream (rawInputStream);
            ais.setInfo (new AndroidWavHelper ().readMetadata (ais));
            return ais;
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT_IS, e);
        }
    }

    @Override
    public InputStream toStream (final byte [] byteArray, final Object audioFormat1) throws SoundTransformException {
        if (!(audioFormat1 instanceof StreamInfo)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ());
        }
        return new ByteArrayWithAudioFormatInputStream (byteArray, (StreamInfo) audioFormat1);
    }

    @Override
    public void writeInputStream (final InputStream ais, final File fDest) throws SoundTransformException {
        if (!(ais instanceof ByteArrayWithAudioFormatInputStream)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ());
        }
        final ByteArrayWithAudioFormatInputStream audioInputStream = (ByteArrayWithAudioFormatInputStream) ais;
        WavOutputStream outputStream = null;
        try {
            outputStream = new WavOutputStream (fDest);
            new AndroidWavHelper ().writeMetadata (audioInputStream, outputStream);
            outputStream.write (audioInputStream.getAllContent ());
            outputStream.flush ();
        } catch (final FileNotFoundException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_AN_OUTPUT_FILE, e);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, fDest.getName ());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close ();
                }
                audioInputStream.close ();
            } catch (final IOException e) {
                this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
            }
        }
    }
}
