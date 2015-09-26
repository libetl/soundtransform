package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter.ConverterMapping;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.audioformat.converter.ConverterLauncher;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToByteArrayHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

final class AndroidAudioFileHelper extends AbstractLogAware<AndroidAudioFileHelper> implements AudioFileHelper {

    @SuppressWarnings ("unchecked")
    private InputStream convertToWavIfNecessary (final InputStream inputStream, final String fileName) throws SoundTransformException {
        InputStream result = null;
        for (final ConverterMapping converters : ConverterMapping.values ()) {
            if (fileName.endsWith ("." + converters.name ().toLowerCase ())) {
                this.log (new LogEvent (AudioFileHelperEventCode.CONVERTING_FIRST, converters.name ()));
                result = this.createWavStreamFromStream ($.select (ConverterLauncher.class).convert (converters.getConverter (), inputStream));
            }
        }
        return result == null ? new ByteArrayInputStream ($.select (InputStreamToByteArrayHelper.class).convertToByteArray (inputStream)) : result;
    }

    private InputStream createWavStreamFromStream (final Entry<StreamInfo, ByteArrayOutputStream> streamPair) throws SoundTransformException {
        WavReadableOutputStream outputStream = null;
        try {
            outputStream = new WavReadableOutputStream (new ByteArrayOutputStream ());
            new AndroidWavHelper ().writeMetadata (streamPair.getKey (), outputStream);
            streamPair.getValue ().writeTo (outputStream);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_AN_OUTPUT_FILE, e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close ();
                }
            } catch (final IOException e) {
                this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
            }
        }
        return new ByteArrayInputStream (outputStream.getContent ());
    }

    @Override
    public InputStream getAudioInputStream (final File inputFile) throws SoundTransformException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream (inputFile);
            return this.getAudioInputStream (this.convertToWavIfNecessary (fileInputStream, inputFile.getName ()));
        } catch (final SoundTransformException ste) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, ste, inputFile.getName ());
        } catch (final FileNotFoundException fnfe) {
            throw new SoundTransformException (AudioFileHelperErrorCode.NO_SOURCE_INPUT_STREAM, fnfe, inputFile.getName ());
        } finally {
            this.closeQuietly (fileInputStream);
        }
    }

    private void closeQuietly (final InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close ();
            }
        } catch (final IOException e) {
            this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
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
