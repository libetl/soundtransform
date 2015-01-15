package org.toilelibre.libe.soundtransform.model;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.CallTransformService;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class TransformSoundService implements LogAware<TransformSoundService> {

    private Observer []                       observers = new Observer [0];

    private final TransformInputStreamService transformInputStreamService;
    private final CallTransformService        callTransformService;
    private final ConvertAudioFileService     convertAudioFileService;

    public TransformSoundService () {
        this (new Observer [0]);
    }

    public TransformSoundService (final Observer... observers) {
        this.setObservers (observers);
        this.transformInputStreamService = $.create (TransformInputStreamService.class, new Object [] { observers });
        this.callTransformService = $.create (CallTransformService.class, new Object [] { observers });
        this.convertAudioFileService = $.create (ConvertAudioFileService.class);
    }

    public Sound [] convertAndApply (final InputStream ais, final SoundTransformation... transforms) throws SoundTransformException {
        final Sound [] in = this.transformInputStreamService.fromInputStream (ais);
        final Sound [] out = this.callTransformService.transformAudioStream (in, transforms);
        return out;
    }

    public Sound [] fromInputStream (final InputStream ais) throws SoundTransformException {
        return this.transformInputStreamService.fromInputStream (ais);
    }

    public Sound [] fromInputStream (final InputStream ais, final InputStreamInfo isInfo) throws SoundTransformException {
        return this.transformInputStreamService.fromInputStream (ais, isInfo);
    }

    @Override
    public void log (final LogEvent event) {
        for (final Observer to : this.observers) {
            to.notify (event);
        }

    }

    private void notifyAll (final String s) {
        this.log (new LogEvent (LogLevel.INFO, s));
    }

    @Override
    public TransformSoundService setObservers (final Observer... observers2) {
        this.observers = observers2;
        for (final Observer observer : observers2) {
            this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
        }
        return this;
    }

    public InputStream toStream (final Sound [] channels, final InputStreamInfo inputStreamInfo) throws SoundTransformException {
        this.notifyAll ("Creating output file");
        final byte [] byteArray = this.transformInputStreamService.soundToByteArray (channels, inputStreamInfo);
        return this.convertAudioFileService.toStream (byteArray, inputStreamInfo);
    }

    public InputStream transformAudioStream (final InputStream ais, final SoundTransformation... transforms) throws SoundTransformException {
        return this.toStream (this.convertAndApply (ais, transforms), this.convertAudioFileService.callAudioFormatParser (ais));
    }

    public void transformFile (final File fOrigin, final File fDest, final SoundTransformation... sts) throws SoundTransformException {
        final File file = fOrigin;
        final InputStream ais1 = this.convertAudioFileService.callConverter (file);
        final InputStreamInfo aisi1 = this.convertAudioFileService.callAudioFormatParser (ais1);
        this.notifyAll ("input : " + aisi1.toString ());
        InputStream ais2 = ais1;
        ais2 = this.transformAudioStream (ais1, sts);
        final InputStreamInfo aisi2 = this.convertAudioFileService.callAudioFormatParser (ais2);
        this.convertAudioFileService.writeInputStream (ais2, fDest);
        this.notifyAll ("Wrote output");
        this.notifyAll ("output : " + aisi2.toString ());
    }

    public InputStreamInfo getInputStreamInfo (InputStream ais) throws SoundTransformException {
        return this.transformInputStreamService.getInputStreamInfo (ais);
    }
}
