package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;

final class AndroidAudioFileHelper extends AbstractLogAware<AndroidAudioFileHelper> implements AudioFileHelper {

    @Override
    public InputStream getUnknownInputStreamFromFile (final File inputFile) throws SoundTransformException {
        try {
            return new FileInputStream (inputFile);
        } catch (final FileNotFoundException fnfe) {
            throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, fnfe, inputFile.getName ());
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
            outputStream = new WavOutputStream (new FileOutputStream (fDest));
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
