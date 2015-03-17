package org.toilelibre.libe.soundtransform.model.converted;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.ModifySoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformSoundService extends AbstractLogAware<TransformSoundService> {

    public enum TransformSoundServiceEventCode implements EventCode {

        CREATING_OUTPUT_FILE(LogLevel.INFO, "Creating output file"), INPUT_IS_THIS_INPUTSTREAM(LogLevel.INFO, "input : %1s"), OUTPUT_IS_THIS_INPUTSTREAM(LogLevel.INFO, "output : %1s"), WROTE_OUTPUT(LogLevel.INFO, "Wrote output");

        private final String messageFormat;
        private final LogLevel logLevel;

        TransformSoundServiceEventCode(final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public LogLevel getLevel() {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private final TransformInputStreamService transformInputStreamService;
    private final CallTransformService callTransformService;
    private final ConvertAudioFileService convertAudioFileService;
    private final ModifySoundService modifySoundService;

    public TransformSoundService(final TransformInputStreamService transformInputStreamService1, final CallTransformService callTransformService1, final ConvertAudioFileService convertAudioFileService1, final ModifySoundService changeSoundFormatService1) {
        this(transformInputStreamService1, callTransformService1, convertAudioFileService1, changeSoundFormatService1, new Observer[0]);
    }

    public TransformSoundService(final TransformInputStreamService transformInputStreamService1, final CallTransformService callTransformService1, final ConvertAudioFileService convertAudioFileService1, final ModifySoundService modifySoundService1, final Observer... observers) {
        this.transformInputStreamService = transformInputStreamService1.setObservers(observers);
        this.callTransformService = callTransformService1.setObservers(observers);
        this.convertAudioFileService = convertAudioFileService1;
        this.modifySoundService = modifySoundService1;
        this.setObservers(observers);
    }

    public Sound[] append(final Sound[] sounds1, final Sound[] sounds2) throws SoundTransformException {
        return this.modifySoundService.append(sounds1, sounds2);
    }

    public Sound[] apply(final Sound[] in, final SoundTransformation... transforms) throws SoundTransformException {
        return this.callTransformService.transformAudioStream(in, transforms);
    }

    public Sound[] changeSoundFormat(final Sound[] input, final FormatInfo formatInfo) throws SoundTransformException {
        return this.modifySoundService.changeFormat(input, formatInfo);
    }

    public Sound[] convertAndApply(final InputStream ais, final SoundTransformation... transforms) throws SoundTransformException {
        final Sound[] in = this.transformInputStreamService.fromInputStream(ais);
        return this.callTransformService.transformAudioStream(in, transforms);
    }

    public InputStream fromFile(final File fOrigin) throws SoundTransformException {
        return this.convertAudioFileService.callConverter(fOrigin);
    }

    public Sound[] fromInputStream(final InputStream ais) throws SoundTransformException {
        return this.transformInputStreamService.fromInputStream(ais);
    }

    public Sound[] fromInputStream(final InputStream ais, final StreamInfo isInfo) throws SoundTransformException {
        return this.transformInputStreamService.fromInputStream(ais, isInfo);
    }

    public StreamInfo getStreamInfo(final InputStream ais) throws SoundTransformException {
        return this.transformInputStreamService.getStreamInfo(ais);
    }

    public FormatInfo getFormatInfo(final Sound[] sounds) {
        return sounds[0].getFormatInfo();
    }

    public InputStream toStream(final Sound[] channels, final StreamInfo streamInfo) throws SoundTransformException {
        this.log(new LogEvent(TransformSoundServiceEventCode.CREATING_OUTPUT_FILE));
        final byte[] byteArray = this.transformInputStreamService.soundToByteArray(channels, streamInfo);
        return this.convertAudioFileService.toStream(byteArray, streamInfo);
    }

    public InputStream transformAudioStream(final InputStream ais, final SoundTransformation... transforms) throws SoundTransformException {
        return this.toStream(this.convertAndApply(ais, transforms), this.convertAudioFileService.callAudioFormatParser(ais));
    }

    public void transformFile(final File fOrigin, final File fDest, final SoundTransformation... sts) throws SoundTransformException {
        final InputStream ais1 = this.convertAudioFileService.callConverter(fOrigin);
        final FormatInfo fi1 = this.convertAudioFileService.callAudioFormatParser(ais1);
        this.log(new LogEvent(TransformSoundServiceEventCode.INPUT_IS_THIS_INPUTSTREAM, fi1.toString()));
        InputStream ais2 = ais1;
        ais2 = this.transformAudioStream(ais1, sts);
        final FormatInfo fi2 = this.convertAudioFileService.callAudioFormatParser(ais2);
        this.convertAudioFileService.writeInputStream(ais2, fDest);
        this.log(new LogEvent(TransformSoundServiceEventCode.WROTE_OUTPUT));
        this.log(new LogEvent(TransformSoundServiceEventCode.OUTPUT_IS_THIS_INPUTSTREAM, fi2.toString()));
    }

    public InputStream transformRawInputStream(final InputStream ais, final StreamInfo isi) throws SoundTransformException {
        return this.convertAudioFileService.toStream(ais, isi);
    }

    public void writeFile(final InputStream is, final File fDest) throws SoundTransformException {
        this.convertAudioFileService.writeInputStream(is, fDest);
    }

}
