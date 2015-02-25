package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.WriteInputStreamToBuffer;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AndroidAudioFileHelper implements AudioFileHelper {

    public ByteArrayInputStream convertFileToBaos (final File inputFile) throws SoundTransformException {
        final byte [] byteArray = new byte [(int) inputFile.length ()];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream (inputFile);
            fileInputStream.read (byteArray);
        } catch (final FileNotFoundException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, e, inputFile.getName ());
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close ();
                }
            } catch (IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CLOSE, e);
            }            
        }

        return new ByteArrayInputStream (byteArray);
    }

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {

        try {
            final AudioInputStream ais = new AudioInputStream (this.convertFileToBaos (inputFile));
            ais.setInfo (new AndroidWavHelper ().readMetadata (ais));
            return ais;
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, inputFile.getName ());
        }
    }

    @Override
    public InputStream toStream (final byte [] byteArray, final Object audioFormat1) throws SoundTransformException {
        if (!(audioFormat1 instanceof InputStreamInfo)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ());
        }
        return new ByteArrayWithAudioFormatInputStream (byteArray, (InputStreamInfo) audioFormat1);
    }

    @Override
    public InputStream toStream (final InputStream is, final Object audioFormat1) throws SoundTransformException {
        if (!(audioFormat1 instanceof InputStreamInfo)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ());
        }
        final InputStreamInfo isi = (InputStreamInfo) audioFormat1;
        try {
            return new ByteArrayWithAudioFormatInputStream (new WriteInputStreamToBuffer ().write (is), isi);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e);
        }
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
                if (outputStream != null){
                    outputStream.close ();
                }
                audioInputStream.close ();
            } catch (IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CLOSE, e);
            }            
        }
    }
}
