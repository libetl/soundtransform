package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.javax;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter.ConverterMapping;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConvertProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConverterLauncher;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatParser;

@Processor
final class JavaxConvertProcessor implements ConvertProcessor {

    @SuppressWarnings ("unchecked")
    @Override
    public <T> InputStream convertToWavStream (final ConverterLauncher<T> launcher, final InputStream inputStream, final String fileName) throws SoundTransformException {

        InputStream result = null;

        for (final ConverterMapping converters : ConverterMapping.values ()) {
            if (fileName.toLowerCase ().endsWith ("." + converters.name ().toLowerCase ())) {
                result = this.createWavStreamFromStream ($.select (ConverterLauncher.class).convert (converters.getConverter (), inputStream));
            }
        }
        return result == null ? $.select (AudioFileHelper.class).getAudioInputStream (inputStream) : result;
    }

    private InputStream createWavStreamFromStream (final Entry<StreamInfo, ByteArrayOutputStream> resultEntry) throws SoundTransformException {
        final Object audioFormat = $.select (AudioFormatParser.class).audioFormatfromStreamInfo (resultEntry.getKey ());
        return $.select (AudioFileHelper.class).toStream (resultEntry.getValue ().toByteArray (), audioFormat);
    }

}
