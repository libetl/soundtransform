package org.toilelibre.libe.soundtransform.model.converted;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformSoundService extends AbstractLogAware<TransformSoundService> {

    private final TransformInputStreamService transformInputStreamService;
    private final CallTransformService        callTransformService;
    private final ConvertAudioFileService     convertAudioFileService;

    public TransformSoundService () {
        this (new Observer [0]);
    }

    public TransformSoundService (final Observer... observers) {
        this.setObservers (observers);
        this.transformInputStreamService = $.create (TransformInputStreamService.class).setObservers (observers);
        this.callTransformService = $.create (CallTransformService.class).setObservers (observers);
        this.convertAudioFileService = $.create (ConvertAudioFileService.class);
    }

    public Sound [] apply (final Sound [] in, final SoundTransformation... transforms) throws SoundTransformException {
        return this.callTransformService.transformAudioStream (in, transforms);
    }

    public Sound [] convertAndApply (final InputStream ais, final SoundTransformation... transforms) throws SoundTransformException {
        final Sound [] in = this.transformInputStreamService.fromInputStream (ais);
        return this.callTransformService.transformAudioStream (in, transforms);
    }

    public InputStream fromFile (final File fOrigin) throws SoundTransformException {
        return this.convertAudioFileService.callConverter (fOrigin);
    }

    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.transformInputStreamService.fromInputStream (ais);
    }

    public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
        return this.transformInputStreamService.fromInputStream (ais, isInfo);
    }

    public InputStreamInfo getInputStreamInfo (final InputStream ais) throws SoundTransformException {
        return this.transformInputStreamService.getInputStreamInfo (ais);
    }

    public InputStreamInfo getInputStreamInfo (final Sound [] sounds) {
        return InputStreamInfo.of (sounds);
    }

    public InputStream toStream (final Sound [] channels, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
        this.log (new LogEvent (LogLevel.INFO, "Creating output file"));
        final byte [] byteArray = this.transformInputStreamService.soundToByteArray (channels, inputStreamInfo);
        return this.convertAudioFileService.toStream (byteArray, inputStreamInfo);
    }

    public InputStream transformAudioStream (final InputStream ais, final SoundTransformation... transforms) throws SoundTransformException {
        return this.toStream (this.convertAndApply (ais, transforms), this.convertAudioFileService.callAudioFormatParser (ais));
    }

    public void transformFile (final File fOrigin, final File fDest, final SoundTransformation... sts) throws SoundTransformException {
        final InputStream ais1 = this.convertAudioFileService.callConverter (fOrigin);
        final InputStreamInfo aisi1 = this.convertAudioFileService.callAudioFormatParser (ais1);
        this.log (new LogEvent (LogLevel.INFO, "input : " + aisi1.toString ()));
        InputStream ais2 = ais1;
        ais2 = this.transformAudioStream (ais1, sts);
        final InputStreamInfo aisi2 = this.convertAudioFileService.callAudioFormatParser (ais2);
        this.convertAudioFileService.writeInputStream (ais2, fDest);
        this.log (new LogEvent (LogLevel.INFO, "Wrote output"));
        this.log (new LogEvent (LogLevel.INFO, "output : " + aisi2.toString ()));
    }

    public InputStream transformRawInputStream (final InputStream ais, final InputStreamInfo isi) throws SoundTransformException {
        return this.convertAudioFileService.toStream (ais, isi);
    }

    public void writeFile (final InputStream is, final File fDest) throws SoundTransformException {
        this.convertAudioFileService.writeInputStream (is, fDest);
    }
}
