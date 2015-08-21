package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperEventCode;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

class AndroidFileToWavFileConverter {

    static enum Converters {
        OGG (new OGGConverter ());

        private final Converter converter;

        Converters (final Converter converter1) {
            this.converter = converter1;
        }

        public Converter getConverter () {
            return this.converter;
        }

    }

    interface Converter {
        File convert (File input) throws SoundTransformException;
    }

    static class OGGConverter extends AbstractLogAware<OGGConverter> implements Converter {

        @Override
        public File convert (final File input) throws SoundTransformException {
            File result = null;
            WavOutputStream outputStream = null;
            try {
                final InputStream inputStream = new FileInputStream (input);
                final JorbisDirtyConverter converter = new JorbisDirtyConverter ();
                converter.run (inputStream);
                result = File.createTempFile ("soundtransform", ".wav");
                outputStream = new WavOutputStream (result);
                new AndroidWavHelper ().writeMetadata (converter.getStreamInfo (), outputStream);
                converter.getOutputStream ().writeTo (outputStream);
                outputStream.flush ();
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

    }
}
