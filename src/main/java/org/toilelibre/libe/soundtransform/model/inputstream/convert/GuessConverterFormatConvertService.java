package org.toilelibre.libe.soundtransform.model.inputstream.convert;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;

final class GuessConverterFormatConvertService extends AbstractLogAware<GuessConverterFormatConvertService> implements FormatConvertService<AbstractLogAware<GuessConverterFormatConvertService>> {

    private final ConvertProcessor convertProcessor;

    public GuessConverterFormatConvertService (final ConvertProcessor convertProcessor1) {
        this.convertProcessor = convertProcessor1;
    }

    @Override
    public InputStream convertToWav (final InputStream compressedInputStream, final String fileName) throws SoundTransformException {
        return this.convertProcessor.convertToWavStream (compressedInputStream, fileName);

    }
}
