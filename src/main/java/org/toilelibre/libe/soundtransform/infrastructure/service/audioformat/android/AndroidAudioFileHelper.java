package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;

public class AndroidAudioFileHelper implements AudioFileHelper {

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        try {
            return new AudioInputStream (inputFile);
        } catch (IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, 
                    inputFile.getName ());
        }
    }

    @Override
    public InputStream toStream (final byte [] byteArray, final Object audioFormat1) throws SoundTransformException {
        return null;
    }

    @Override
    public void writeInputStream (final InputStream ais, final File fDest) throws SoundTransformException {

    }
}
