package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter.ConverterMapping;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperEventCode;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConvertProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConverterLauncher;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToByteArrayHelper;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;

@Processor
final class AndroidConvertProcessor extends AbstractLogAware<AndroidConvertProcessor> implements ConvertProcessor {

    @Override
    @SuppressWarnings ("unchecked")
    public <T> InputStream convertToWavStream (final ConverterLauncher<T> launcher, final InputStream inputStream, final String fileName) throws SoundTransformException {
        InputStream result = null;
        for (final ConverterMapping converters : ConverterMapping.values ()) {
            if (fileName.toLowerCase ().endsWith ("." + converters.name ().toLowerCase ())) {
                this.log (new LogEvent (AudioFileHelperEventCode.CONVERTING_FIRST, converters.name ()));
                result = this.createWavStreamFromStream (launcher.convert ((T) converters.getConverter (), inputStream));
            }
        }
        result = result == null ? this.convertToSimpleByteArrayInputStream (inputStream) : result;
        this.closeQuietly (inputStream);
        return result;
    }

    private ByteArrayInputStream convertToSimpleByteArrayInputStream (final InputStream inputStream) throws SoundTransformException {
        return new ByteArrayInputStream ($.select (InputStreamToByteArrayHelper.class).convertToByteArray (inputStream));
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
}
