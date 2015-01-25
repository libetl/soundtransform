package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class AndroidAudioFileHelper implements AudioFileHelper {

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        try {
            final AudioInputStream ais = new AudioInputStream (inputFile);
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
    public void writeInputStream (final InputStream ais, final File fDest) throws SoundTransformException {
        if (!(ais instanceof ByteArrayWithAudioFormatInputStream)) {
            throw new SoundTransformException (AudioFileHelperErrorCode.AUDIO_FORMAT_COULD_NOT_BE_READ, new IllegalArgumentException ());
        }
        final ByteArrayWithAudioFormatInputStream audioInputStream = (ByteArrayWithAudioFormatInputStream) ais;
        try {
            final WavOutputStream outputStream = new WavOutputStream (fDest);
            new AndroidWavHelper ().writeMetadata (audioInputStream, outputStream);
            outputStream.write (audioInputStream.getAllContent ());
            outputStream.flush ();
            audioInputStream.close ();
            outputStream.close ();
        } catch (final FileNotFoundException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_AN_OUTPUT_FILE, e);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, fDest.getName ());
        }
    }
}
