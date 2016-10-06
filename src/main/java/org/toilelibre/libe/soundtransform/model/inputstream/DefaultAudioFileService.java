package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.FormatConvertService;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToByteArrayHelper;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;

@Service
final class DefaultAudioFileService extends AbstractLogAware<DefaultAudioFileService> implements AudioFileService<AbstractLogAware<DefaultAudioFileService>> {

    private final AudioFileHelper              audioFileHelper;
    private final AudioFormatService           audioFormatService;
    private final FormatConvertService<?>      formatConvertService;
    private final InputStreamToByteArrayHelper inputStreamToByteArrayHelper;

    public DefaultAudioFileService (final AudioFileHelper helper1, final AudioFormatService audioFormatService1, final InputStreamToByteArrayHelper inputStreamToByteArrayHelper1, final FormatConvertService<?> formatConvertService1) {
        this.audioFileHelper = helper1;
        this.audioFormatService = audioFormatService1;
        this.formatConvertService = formatConvertService1;
        this.inputStreamToByteArrayHelper = inputStreamToByteArrayHelper1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.inputstream.AudioFIleService
     * #streamFromFile(java.io.File)
     */
    @Override
    public InputStream streamFromFile (final File file) throws SoundTransformException {
        final InputStream unknownInputStream = this.audioFileHelper.getUnknownInputStreamFromFile (file);
        return this.formatConvertService.convertToWav (unknownInputStream, file.getName ());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.inputstream.AudioFIleService
     * #streamFromRawStream(java.io.InputStream,
     * org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo)
     */
    @Override
    public InputStream streamFromRawStream (final InputStream is, final StreamInfo streamInfo) throws SoundTransformException {
        return this.audioFileHelper.toStream (this.inputStreamToByteArrayHelper.convertToByteArray (is), this.audioFormatService.audioFormatfromStreamInfo (streamInfo));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.inputstream.AudioFIleService
     * #fileFromStream(java.io.InputStream, java.io.File)
     */
    @Override
    public void fileFromStream (final InputStream ais2, final File fDest) throws SoundTransformException {
        this.audioFileHelper.writeInputStream (ais2, fDest);
    }

    @Override
    public InputStream streamFromInputStream (final InputStream is) throws SoundTransformException {
        final InputStream ais = this.audioFileHelper.getAudioInputStream (is);
        final StreamInfo si = this.audioFormatService.getStreamInfo (ais);
        return this.streamFromRawStream (ais, si);
    }
}
