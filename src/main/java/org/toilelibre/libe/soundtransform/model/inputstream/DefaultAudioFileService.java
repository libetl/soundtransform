package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;

final class DefaultAudioFileService extends AbstractLogAware<DefaultAudioFileService> implements AudioFileService<AbstractLogAware<DefaultAudioFileService>> {

    private final AudioFileHelper              audioFileHelper;
    private final AudioFormatParser            audioFormatParser;
    private final InputStreamToByteArrayHelper inputStreamToByteArrayHelper;

    public DefaultAudioFileService (final AudioFileHelper helper1, final AudioFormatParser audioFormatParser1, final InputStreamToByteArrayHelper inputStreamToByteArrayHelper1) {
        this.audioFileHelper = helper1;
        this.audioFormatParser = audioFormatParser1;
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
        return this.audioFileHelper.getAudioInputStream (file);
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
        return this.audioFileHelper.toStream (this.inputStreamToByteArrayHelper.convertToByteArray (is), this.audioFormatParser.audioFormatfromStreamInfo (streamInfo));
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
    public InputStream streamFromInputStream (InputStream is) throws SoundTransformException {
        final InputStream ais = this.audioFileHelper.getAudioInputStream (is);
        final StreamInfo si = this.audioFormatParser.getStreamInfo (ais);
        return this.streamFromRawStream (ais, si);
    }
}
