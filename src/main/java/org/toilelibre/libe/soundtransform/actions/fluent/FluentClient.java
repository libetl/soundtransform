package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetInputStreamInfo;
import org.toilelibre.libe.soundtransform.actions.transform.InputStreamToAudioInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class FluentClient implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile {
    public enum FluentClientErrorCode implements ErrorCode {

        INPUT_STREAM_NOT_READY ("Input Stream not ready"), INPUT_STREAM_INFO_UNAVAILABLE ("Input Stream info not available"), NOTHING_TO_WRITE ("Nothing to write to a File"), NO_FILE_IN_INPUT ("No file in input"), CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE (
                "This client did not read a classpath resouce at the start");

        private final String messageFormat;

        FluentClientErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public static FluentClientReady start () {
        return new FluentClient ();
    }

    private Sound []    sounds;
    private InputStream audioInputStream;
    private String      sameDirectoryAsClasspathResource;

    private File        file;

    private FluentClient () {

    }

    @Override
    public FluentClientReady andAfterStart () throws SoundTransformException {
        this.cleanData ();
        return this;
    }

    @Override
    public FluentClientSoundImported apply (SoundTransformation st) throws SoundTransformException {
        final Sound sounds1 [] = new ApplySoundTransform ().apply (this.sounds, st);
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    private void cleanData () {
        this.sounds = null;
        this.audioInputStream = null;
        this.file = null;
    }

    @Override
    public FluentClient playIt () throws SoundTransformException {
        if (this.sounds != null) {
            new PlaySound ().play (this.sounds);
        } else if (this.audioInputStream != null) {
            new PlaySound ().play (this.audioInputStream);
        } else if (this.file != null) {
            File f = this.file;
            this.importToStream ();
            new PlaySound ().play (this.audioInputStream);
            this.cleanData ();
            this.file = f;
        }
        return this;
    }

    @Override
    public FluentClientSoundImported convertIntoSound () throws SoundTransformException {
        return this.importToStream ().importToSound ();
    }

    @Override
    public FluentClientWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource);
    }

    @Override
    public FluentClientWithFile exportToClasspathResource (String resource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResource (resource);
    }

    @Override
    public FluentClientWithFile exportToFile (File file1) throws SoundTransformException {
        return this.exportToStream ().writeToFile (file1);
    }

    @Override
    public FluentClientWithInputStream exportToStream () throws SoundTransformException {
        InputStreamInfo currentInfo = new GetInputStreamInfo ().getInputStreamInfo (this.sounds);
        if (currentInfo == null) {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_INFO_UNAVAILABLE, new NullPointerException ());
        }
        final InputStream audioInputStream1 = new ToInputStream ().toStream (this.sounds, currentInfo);
        this.cleanData ();
        this.audioInputStream = audioInputStream1;
        return this;
    }

    @Override
    public FluentClientSoundImported importToSound () throws SoundTransformException {
        Sound [] sounds1;
        if (this.audioInputStream != null) {
            sounds1 = new ConvertFromInputStream ().fromInputStream (this.audioInputStream);
        } else {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_NOT_READY, new NullPointerException ());
        }
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    @Override
    public FluentClientWithInputStream importToStream () throws SoundTransformException {
        if (this.file == null) {
            throw new SoundTransformException (FluentClientErrorCode.NO_FILE_IN_INPUT, new NullPointerException ());
        }
        final InputStream inputStream = new ToInputStream ().toStream (this.file);
        this.cleanData ();
        this.audioInputStream = inputStream;
        return this;
    }

    @Override
    public FluentClientWithInputStream withAudioInputStream (InputStream ais) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = ais;
        return this;
    }

    @Override
    public FluentClientWithFile withClasspathResource (String resource) throws SoundTransformException {
        this.cleanData ();
        final URL url = Thread.currentThread ().getContextClassLoader ().getResource (resource);
        this.file = new File (url.getFile ());
        this.sameDirectoryAsClasspathResource = this.file.getParent ();
        return this;
    }

    @Override
    public FluentClientWithFile withFile (File file1) throws SoundTransformException {
        this.cleanData ();
        this.file = file1;
        return this;
    }

    @Override
    public FluentClientWithInputStream withRawInputStream (InputStream is, InputStreamInfo isInfo) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = new InputStreamToAudioInputStream ().transformRawInputStream (is, isInfo);
        return this;
    }

    @Override
    public FluentClientSoundImported withSounds (Sound [] sounds1) {
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    @Override
    public FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException {
        if (this.sameDirectoryAsClasspathResource == null) {
            throw new SoundTransformException (FluentClientErrorCode.CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE, new IllegalAccessException ());
        }
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    @Override
    public FluentClientWithFile writeToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException {
        InputStream is = this.audioInputStream;
        this.withClasspathResource (siblingResource);
        this.cleanData ();
        this.audioInputStream = is;
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    @Override
    public FluentClientWithFile writeToFile (File file1) throws SoundTransformException {
        if (this.audioInputStream != null) {
            new ExportAFile ().writeFile (this.audioInputStream, file1);
        }
        this.cleanData ();
        this.file = file1;
        return this;
    }
}
