package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.notes.ImportAPackIntoTheLibrary;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetInputStreamInfo;
import org.toilelibre.libe.soundtransform.actions.transform.InputStreamToAudioInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundToSpectrumsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpectrumsToSoundSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class FluentClient implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile, FluentClientWithFreqs, FluentClientWithSpectrums {
    public enum FluentClientErrorCode implements ErrorCode {

        INPUT_STREAM_NOT_READY ("Input Stream not ready"), INPUT_STREAM_INFO_UNAVAILABLE ("Input Stream info not available"), NOTHING_TO_WRITE ("Nothing to write to a File"), NO_FILE_IN_INPUT ("No file in input"), CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE (
                "This client did not read a classpath resouce at the start"), NO_SPECTRUM_IN_INPUT ("No spectrum in input");

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

    private Sound []       sounds;
    private InputStream    audioInputStream;
    private String         sameDirectoryAsClasspathResource;
    private int []         freqs;
    private File           file;
    private Spectrum<?> [] spectrums;

    private FluentClient () {

    }

    @Override
    public FluentClientReady andAfterStart () throws SoundTransformException {
        this.cleanData ();
        return this;
    }

    @Override
    public FluentClientSoundImported apply (final SoundTransformation st) throws SoundTransformException {
        final Sound sounds1 [] = new ApplySoundTransform ().apply (this.sounds, st);
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    private void cleanData () {
        this.sounds = null;
        this.audioInputStream = null;
        this.file = null;
        this.freqs = null;
        this.spectrums = null;
    }

    @Override
    public FluentClientSoundImported convertIntoSound () throws SoundTransformException {
        return this.importToStream ().importToSound ();
    }

    @Override
    public FluentClientWithFile exportToClasspathResource (final String resource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResource (resource);
    }

    @Override
    public FluentClientWithFile exportToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource);
    }

    @Override
    public FluentClientWithFile exportToFile (final File file1) throws SoundTransformException {
        return this.exportToStream ().writeToFile (file1);
    }

    @Override
    public FluentClientWithInputStream exportToStream () throws SoundTransformException {
        final InputStreamInfo currentInfo = new GetInputStreamInfo ().getInputStreamInfo (this.sounds);
        if (currentInfo == null) {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_INFO_UNAVAILABLE, new NullPointerException ());
        }
        final InputStream audioInputStream1 = new ToInputStream ().toStream (this.sounds, currentInfo);
        this.cleanData ();
        this.audioInputStream = audioInputStream1;
        return this;
    }

    @Override
    public FluentClientSoundImported extractSound () throws SoundTransformException {
        if ((this.spectrums == null) || (this.spectrums.length == 0)) {
            throw new SoundTransformException (FluentClientErrorCode.NO_SPECTRUM_IN_INPUT, new IllegalArgumentException ());
        }
        final Sound sounds1 [] = new ApplySoundTransform ().apply (new Sound [] { new Sound (null, this.spectrums [0].getNbBytes (), this.spectrums [0].getSampleRate (), 0) }, new SpectrumsToSoundSoundTransformation (this.spectrums));
        this.cleanData ();
        this.sounds = sounds1;
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
    public FluentClient playIt () throws SoundTransformException {
        if (this.sounds != null) {
            new PlaySound ().play (this.sounds);
        } else if (this.audioInputStream != null) {
            new PlaySound ().play (this.audioInputStream);
        } else if (this.spectrums != null) {
            final Spectrum<?> [] savedSpectrums = this.spectrums;
            this.convertIntoSound ();
            new PlaySound ().play (this.sounds);
            this.cleanData ();
            this.spectrums = savedSpectrums;
        } else if (this.file != null) {
            final File f = this.file;
            this.importToStream ();
            new PlaySound ().play (this.audioInputStream);
            this.cleanData ();
            this.file = f;
        }
        return this;
    }

    @Override
    public FluentClientSoundImported shapeIntoSound (final String packName, final String instrumentName, final InputStreamInfo isi) throws SoundTransformException {
        final SoundTransformation soundTransformation = new ShapeSoundTransformation (packName, instrumentName, this.freqs, (int) isi.getFrameLength (), isi.getSampleSize (), (int) isi.getSampleRate ());
        this.cleanData ();
        this.sounds = new ApplySoundTransform ().apply (new Sound [] { new Sound (new long [0], 0, 0, 0) }, soundTransformation);
        return this;
    }

    @Override
    public FluentClientWithSpectrums splitIntoSpectrums (int channelNum) throws SoundTransformException {
        final SoundToSpectrumsSoundTransformation<?> sound2Spectrums = new SoundToSpectrumsSoundTransformation<Object> ();
        new ApplySoundTransform ().apply (this.sounds, sound2Spectrums);
        this.cleanData ();
        this.spectrums = sound2Spectrums.getSpectrums ();
        return this;
    }

    @Override
    public File stopWithFile () {
        return this.file;
    }

    @Override
    public int [] stopWithFreqs () {
        return this.freqs;
    }

    @Override
    public InputStream stopWithInputStream () {
        return this.audioInputStream;
    }

    @Override
    public Sound [] stopWithSounds () {
        return this.sounds;
    }

    @Override
    public Spectrum<?> [] stopWithSpectrums () {
        return this.spectrums;
    }

    @Override
    public FluentClient withAPack (final String packName, final InputStream jsonStream) throws SoundTransformException {
        new ImportAPackIntoTheLibrary ().importAPack (packName, jsonStream);
        return this;
    }

    @Override
    public FluentClient withAPack (final String packName, final String jsonContent) throws SoundTransformException {
        new ImportAPackIntoTheLibrary ().importAPack (packName, jsonContent);
        return this;
    }

    @Override
    public FluentClientWithInputStream withAudioInputStream (final InputStream ais) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = ais;
        return this;
    }

    @Override
    public FluentClientWithFile withClasspathResource (final String resource) throws SoundTransformException {
        this.cleanData ();
        this.file = new File (Thread.currentThread ().getContextClassLoader ().getResource (resource).getFile ());
        this.sameDirectoryAsClasspathResource = this.file.getParent ();
        return this;
    }

    @Override
    public FluentClientWithFile withFile (final File file1) throws SoundTransformException {
        this.cleanData ();
        this.file = file1;
        return this;
    }

    @Override
    public FluentClientWithFreqs withFreqs (final int [] freqs1) throws SoundTransformException {
        this.cleanData ();
        this.freqs = freqs1;
        return this;
    }

    @Override
    public FluentClientWithInputStream withRawInputStream (final InputStream is, final InputStreamInfo isInfo) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = new InputStreamToAudioInputStream ().transformRawInputStream (is, isInfo);
        return this;
    }

    @Override
    public FluentClientSoundImported withSounds (final Sound [] sounds1) {
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    @Override
    public FluentClientWithSpectrums withSpectrums (final Spectrum<?> [] spectrums) throws SoundTransformException {
        this.cleanData ();
        this.spectrums = spectrums;
        return this;
    }

    @Override
    public FluentClientWithFile writeToClasspathResource (final String resource) throws SoundTransformException {
        if (this.sameDirectoryAsClasspathResource == null) {
            throw new SoundTransformException (FluentClientErrorCode.CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE, new IllegalAccessException ());
        }
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    @Override
    public FluentClientWithFile writeToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        final InputStream is = this.audioInputStream;
        this.withClasspathResource (siblingResource);
        this.cleanData ();
        this.audioInputStream = is;
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    @Override
    public FluentClientWithFile writeToFile (final File file1) throws SoundTransformException {
        if (this.audioInputStream != null) {
            new ExportAFile ().writeFile (this.audioInputStream, file1);
        }
        this.cleanData ();
        this.file = file1;
        return this;
    }
}
