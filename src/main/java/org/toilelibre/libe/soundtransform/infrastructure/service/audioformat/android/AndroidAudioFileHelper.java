package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleImmutableEntry;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter.ConverterMapping;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.audioformat.converter.ConverterLauncher;
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
            this.closeQuietly (fileInputStream);
        }

        return new ByteArrayInputStream (byteArray);
    }

    private void closeQuietly (InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close ();
            }
        } catch (final IOException e) {
            this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
        }
    }

    @SuppressWarnings ("unchecked")
    private File convertToWavIfNecessary (File inputFile) throws SoundTransformException {
        File outputFile = inputFile;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream (inputFile);
            for (ConverterMapping converters : ConverterMapping.values ()) {
                if (inputFile.getName ().endsWith ("." + converters.name ().toLowerCase ())) {
                    this.log (new LogEvent (AudioFileHelperEventCode.CONVERTING_FIRST, converters.name ()));
                    outputFile = this.createTempFileFromStream ($.select (ConverterLauncher.class).convert (converters.getConverter (), inputStream));
                }
            }
        } catch (FileNotFoundException e) {
            this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
        } finally {
            this.closeQuietly (inputStream);
        }
        return outputFile;
    }

    private File createTempFileFromStream (SimpleImmutableEntry<StreamInfo, ByteArrayOutputStream> streamPair) throws SoundTransformException {
        File result = null;
        WavOutputStream outputStream = null;
        try {
            result = File.createTempFile ("soundtransform", ".wav");
            outputStream = new WavOutputStream (result);
            new AndroidWavHelper ().writeMetadata (streamPair.getKey (), outputStream);
            streamPair.getValue ().writeTo (outputStream);
        } catch (final IOException e) {
            throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_A_TEMP_FILE, e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close ();
                }
            } catch (final IOException e) {
                this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
            }
        }
        return result;
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
