package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation.Step;
import org.toilelibre.libe.soundtransform.actions.notes.ImportAPackIntoTheLibrary;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.record.RecordSound;
import org.toilelibre.libe.soundtransform.actions.transform.AppendSound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ChangeLoudestFreqs;
import org.toilelibre.libe.soundtransform.actions.transform.ChangeSoundFormat;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetStreamInfo;
import org.toilelibre.libe.soundtransform.actions.transform.InputStreamToAudioInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CutSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.LoopSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MixSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundToSpectrumsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpectrumsToSoundSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SubSoundExtractSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class FluentClient implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile, FluentClientWithFreqs, FluentClientWithParallelizedClients, FluentClientWithSpectrums, FluentClientInterface {

    public enum FluentClientErrorCode implements ErrorCode {

        PROBLEM_WITH_SIMULTANEOUS_FLOWS ("Problem with simultaneous flows : %1s"), MISSING_SOUND_IN_INPUT ("Missing sound in input"), INPUT_STREAM_NOT_READY ("Input Stream not ready"), NOTHING_TO_WRITE ("Nothing to write to a File"), NO_FILE_IN_INPUT ("No file in input"), CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE (
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

    private static final int                DEFAULT_STEP_VALUE = 100;
    private static List<Observer>           defaultObservers   = new LinkedList<Observer> ();

    private Sound []                        sounds;
    private InputStream                     audioInputStream;
    private String                          sameDirectoryAsClasspathResource;
    private float []                        freqs;
    private FluentClientCommon []           parallelizedClients;

    private File                            file;

    private List<Spectrum<Serializable> []> spectrums;

    private List<Observer>                  observers;

    private FluentClient () {
        this.andAfterStart ();
    }

    /**
     * Set the passed observers as the default value when a FluentClient is
     * started
     *
     * It can be useful if you are going to use the FluentClient several times
     * but you want to declare the subscribed observers only once
     *
     * @param defaultObservers1
     *            one or more observer(s)
     *
     * @return the client, in its current state.
     */
    public static void setDefaultObservers (final Observer... defaultObservers1) {
        FluentClient.defaultObservers = new LinkedList<Observer> (Arrays.<Observer> asList (defaultObservers1));
    }

    /**
     * Startup the client
     *
     * @return the client, ready to start
     */
    public static FluentClientReady start () {
        return new FluentClient ();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #adjust()
     */
    @Override
    public FluentClientWithFreqs adjust () {
        this.freqs = new ChangeLoudestFreqs ().adjust (this.freqs);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #andAfterStart()
     */
    @Override
    public FluentClientReady andAfterStart () {
        this.cleanData ();
        this.cleanObservers ();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #append(org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    public FluentClientSoundImported append (final Sound [] sounds1) throws SoundTransformException {
        this.sounds = new AppendSound (this.getObservers ()).append (this.sounds, sounds1);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #apply
     * (org.toilelibre.libe.soundtransform.model.converted.sound.transform.
     * SoundTransformation)
     */
    @Override
    /**
     * Apply one transform and continue with the current imported sound
     * @param st the SoundTransformation to apply
     * @return the client with a sound imported
     * @throws SoundTransformException if the transform does not work
     */
    public FluentClientSoundImported apply (final SoundTransformation st) throws SoundTransformException {
        final Sound [] sounds1 = new ApplySoundTransform (this.getObservers ()).apply (this.sounds, st);
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #changeFormat
     * (org.toilelibre.libe.soundtransform.model.converted.FormatInfo)
     */
    @Override
    public FluentClientSoundImported changeFormat (final FormatInfo formatInfo) throws SoundTransformException {
        this.sounds = new ChangeSoundFormat (this.getObservers ()).changeFormat (this.sounds, formatInfo);
        return this;
    }

    /**
     * Reset the state of the FluentClient
     */
    private void cleanData () {
        this.sounds = null;
        this.audioInputStream = null;
        this.file = null;
        this.freqs = null;
        this.spectrums = null;
        this.parallelizedClients = null;
    }

    /**
     * Reset the list of the subscribed observers
     */
    private void cleanObservers () {
        this.observers = FluentClient.defaultObservers;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #compress(float)
     */
    @Override
    /**
     * Compresses the loudest freq array (speedup or slowdown) When shaped into
     * a sound, the result will have a different tempo than the original sound
     * but will keep the same pitch
     *
     * @param factor
     *            the factor parameter quantifies how much the stretch will be
     *            (i.e if factor = 0.5, then the result will be twice as long than
     *            the original)
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs compress (final float factor) {
        this.freqs = new ChangeLoudestFreqs ().compress (this.freqs, factor);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #convertIntoSound()
     */
    @Override
    /**
     * Shortcut for importToStream ().importToSound () : Conversion from a File to a Sound
     * @return the client, with a sound imported
     * @throws SoundTransformException if one of the two import fails
     */
    public FluentClientSoundImported convertIntoSound () throws SoundTransformException {
        return this.importToStream ().importToSound ();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #cutSubSound(int, int)
     */
    @Override
    public FluentClientSoundImported cutSubSound (final int start, final int end) throws SoundTransformException {
        return this.apply (new CutSoundTransformation (start, end));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #exportToClasspathResource(java.lang.String)
     */
    @Override
    /**
     * Shortcut for exportToStream ().writeToClasspathResource (resource) : Conversion from a Sound to a File
     * @param resource a resource that can be found in the classpath
     * @return the client, with a file written
     * @throws SoundTransformException if one of the two operations fails
     */
    public FluentClientWithFile exportToClasspathResource (final String resource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResource (resource);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #exportToClasspathResourceWithSiblingResource(java.lang.String,
     * java.lang.String)
     */
    @Override
    /**
     * Shortcut for exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource)
     * @param resource a resource that may or may not exist in the classpath
     * @param siblingResource a resource that can be found in the classpath.
     * @return the client, with a file written
     * @throws SoundTransformException if one of the two operations fails
     */
    public FluentClientWithFile exportToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #exportToFile(java.io.File)
     */
    @Override
    /**
     * Shortcut for exportToStream ().writeToFile (file)
     * @param file1 the destination file
     * @return the client, with a file written
     * @throws SoundTransformException if one of the two operations fails
     */
    public FluentClientWithFile exportToFile (final File file1) throws SoundTransformException {
        return this.exportToStream ().writeToFile (file1);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #exportToStream()
     */
    @Override
    /**
     * Uses the current imported sound and converts it into an InputStream, ready to be written to a file (or to be read again)
     * @return the client, with an inputStream
     * @throws SoundTransformException if the metadata format object is invalid, or if the sound cannot be converted
     */
    public FluentClientWithInputStream exportToStream () throws SoundTransformException {
        final FormatInfo currentInfo = this.sounds [0].getFormatInfo ();
        final InputStream audioInputStream1 = new ToInputStream (this.getObservers ()).toStream (this.sounds, StreamInfo.from (currentInfo, this.sounds));
        this.cleanData ();
        this.audioInputStream = audioInputStream1;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #extractSound()
     */
    @Override
    /**
     * Uses the current available spectrums objects to convert them into a sound (with one or more channels)
     * @return the client, with a sound imported
     * @throws SoundTransformException if the spectrums are in an invalid format, or if the transform to sound does not work
     */
    public FluentClientSoundImported extractSound () throws SoundTransformException {
        if (this.spectrums == null || this.spectrums.isEmpty () || this.spectrums.get (0).length == 0) {
            throw new SoundTransformException (FluentClientErrorCode.NO_SPECTRUM_IN_INPUT, new IllegalArgumentException ());
        }
        final Sound [] input = new Sound [this.spectrums.size ()];
        for (int i = 0 ; i < input.length ; i++) {
            input [i] = new Sound (new long [0], this.spectrums.get (0) [0].getFormatInfo (), i);
        }
        final Sound [] sounds1 = new ApplySoundTransform (this.getObservers ()).apply (input, new SpectrumsToSoundSoundTransformation (this.spectrums));
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #extractSubSound(int, int)
     */
    @Override
    public FluentClientSoundImported extractSubSound (final int start, final int end) throws SoundTransformException {
        return this.apply (new SubSoundExtractSoundTransformation (start, end));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #filterRange(float, float)
     */
    @Override
    public FluentClientWithFreqs filterRange (final float low, final float high) {
        this.freqs = new ChangeLoudestFreqs ().filterRange (this.freqs, low, high);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #findLoudestFrequencies()
     */
    @Override
    public FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException {
        final PeakFindWithHPSSoundTransformation<Serializable> peakFind = new PeakFindWithHPSSoundTransformation<Serializable> (FluentClient.DEFAULT_STEP_VALUE);
        new ApplySoundTransform (this.getObservers ()).apply (this.sounds, peakFind);
        this.cleanData ();
        this.freqs = peakFind.getLoudestFreqs ();
        return this;
    }

    /**
     * Transforms the observers list into an array returns that
     *
     * @return an array of observers
     */
    private Observer [] getObservers () {
        return this.observers.toArray (new Observer [this.observers.size ()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #importToSound()
     */
    @Override
    /**
     * Uses the current input stream object to convert it into a sound (with one or more channels)
     * @return the client, with a sound imported
     * @throws SoundTransformException the inputStream is invalid, or the convert did not work
     */
    public FluentClientSoundImported importToSound () throws SoundTransformException {
        Sound [] sounds1;
        if (this.audioInputStream != null) {
            sounds1 = new ConvertFromInputStream (this.getObservers ()).fromInputStream (this.audioInputStream);
        } else {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_NOT_READY, new NullPointerException ());
        }
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #importToStream()
     */
    @Override
    /**
     * Opens the current file and convert it into an InputStream, ready to be read (or to be written to a file)
     * @return the client, with an inputStream
     * @throws SoundTransformException the current file is not valid, or the conversion did not work
     */
    public FluentClientWithInputStream importToStream () throws SoundTransformException {
        if (this.file == null) {
            throw new SoundTransformException (FluentClientErrorCode.NO_FILE_IN_INPUT, new NullPointerException ());
        }
        final InputStream inputStream = new ToInputStream (this.getObservers ()).toStream (this.file);
        this.cleanData ();
        this.audioInputStream = inputStream;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #insertPart(float[], int)
     */
    @Override
    public FluentClientWithFreqs insertPart (final float [] subFreqs, final int start) {
        this.freqs = new ChangeLoudestFreqs ().insertPart (this.freqs, subFreqs, start);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #inParallel
     * (org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation,
     * int, T)
     */
    @Override
    public <T extends FluentClientCommon> FluentClientWithParallelizedClients inParallel (final FluentClientOperation operation, final int timeoutInSeconds, final T... clients) throws SoundTransformException {
        final ExecutorService threadService = Executors.newFixedThreadPool (clients.length);
        for (int i = 0 ; i < clients.length ; i++) {
            final FluentClientCommon client = clients [i];
            final int invocationNumber = i;
            threadService.submit (new Runnable () {
                @Override
                public void run () {
                    for (final Step step : operation.getSteps ()) {
                        try {
                            step.run ((FluentClientInterface) client, invocationNumber);
                        } catch (final SoundTransformException ste) {
                            throw new SoundTransformRuntimeException (ste);
                        }
                    }
                }
            });
        }
        try {
            threadService.shutdown ();
            threadService.awaitTermination (timeoutInSeconds, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            throw new SoundTransformException (FluentClientErrorCode.PROBLEM_WITH_SIMULTANEOUS_FLOWS, e, e.getMessage ());
        }
        this.cleanData ();
        this.parallelizedClients = clients;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #inParallel
     * (org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation,
     * int, org.toilelibre.libe.soundtransform.model.converted.sound.Sound)
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final Sound []... sounds1) throws SoundTransformException {

        final FluentClientCommon [] clients = new FluentClientCommon [sounds1.length];
        for (int i = 0 ; i < sounds1.length ; i++) {
            clients [i] = FluentClient.start ().withSounds (sounds1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #inParallel
     * (org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation,
     * int, java.io.InputStream)
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final InputStream... inputStreams1) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [inputStreams1.length];
        for (int i = 0 ; i < inputStreams1.length ; i++) {
            clients [i] = FluentClient.start ().withAudioInputStream (inputStreams1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #inParallel
     * (org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation,
     * int, java.io.File)
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final File... files1) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [files1.length];
        for (int i = 0 ; i < files1.length ; i++) {
            clients [i] = FluentClient.start ().withFile (files1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #inParallel
     * (org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation,
     * int, float)
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final float []... freqs1) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [freqs1.length];
        for (int i = 0 ; i < freqs1.length ; i++) {
            clients [i] = FluentClient.start ().withFreqs (freqs1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #inParallel
     * (org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation,
     * int, java.lang.String)
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final String... classpathResources) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [classpathResources.length];
        for (int i = 0 ; i < classpathResources.length ; i++) {
            clients [i] = FluentClient.start ().withClasspathResource (classpathResources [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #loop(int)
     */
    @Override
    public FluentClientSoundImported loop (final int length) throws SoundTransformException {
        return this.apply (new LoopSoundTransformation (length));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #mixWith
     * (org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    /**
     * Combines the current sound with another sound. The operation is not reversible
     * @param sound the sound to mix the current sound with
     * @return the client, with a sound imported
     * @throws SoundTransformException if the sound is null or if there is a problem with the mix
     */
    public FluentClientSoundImported mixWith (final Sound [] sound) throws SoundTransformException {
        return this.apply (new MixSoundTransformation (Arrays.<Sound []> asList (sound)));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #mixAllInOneSound()
     */
    @Override
    public FluentClientSoundImported mixAllInOneSound () throws SoundTransformException {
        final FluentClientCommon [] savedClients = this.parallelizedClients;

        this.cleanData ();

        if (savedClients == null || savedClients.length == 0 || ((FluentClient) savedClients [0]).sounds == null) {
            throw new SoundTransformException (FluentClient.FluentClientErrorCode.MISSING_SOUND_IN_INPUT, new IllegalArgumentException ());
        }
        this.sounds = ((FluentClient) savedClients [0]).stopWithSounds ();

        for (int i = 1 ; i < savedClients.length ; i++) {
            final Sound [] otherSound = ((FluentClient) savedClients [i]).sounds;
            this.apply (new MixSoundTransformation (Arrays.<Sound []> asList (otherSound)));
        }

        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #octaveDown()
     */
    @Override
    public FluentClientWithFreqs octaveDown () {
        this.freqs = new ChangeLoudestFreqs ().octaveDown (this.freqs);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #octaveUp()
     */
    @Override
    public FluentClientWithFreqs octaveUp () {
        this.freqs = new ChangeLoudestFreqs ().octaveUp (this.freqs);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #playIt()
     */
    @Override
    /**
     * Plays the current audio data and (if needed) convert it temporarily to a sound
     * @return the client, in its current state.
     * @throws SoundTransformException could not play the current audio data
     */
    public FluentClient playIt () throws SoundTransformException {
        if (this.sounds != null) {
            new PlaySound ().play (this.sounds);
        } else if (this.audioInputStream != null) {
            new PlaySound ().play (this.audioInputStream);
        } else if (this.spectrums != null) {
            final List<Spectrum<Serializable> []> savedSpectrums = this.spectrums;
            this.extractSound ();
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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #replacePart(float[], int)
     */
    @Override
    /**
     * Replace some of the values of the loudest freqs array from the "start"
     * index (replace them by the values of subfreqs)
     *
     * @param subFreqs
     *            replacement loudest freqs array
     * @param start
     *            index where to start the replacement
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs replacePart (final float [] subFreqs, final int start) {
        this.freqs = new ChangeLoudestFreqs ().replacePart (this.freqs, subFreqs, start);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #shapeIntoSound(java.lang.String, java.lang.String,
     * org.toilelibre.libe.soundtransform.model.converted.FormatInfo)
     */
    @Override
    /**
     * Shapes these loudest frequencies array into a sound and set the converted sound in the pipeline
     * @param packName reference to an existing imported pack (must be invoked before the shapeIntoSound method by using withAPack)
     * @param instrumentName the name of the instrument that will map the freqs object
     * @param fi the wanted format for the future sound
     * @return the client, with a sound imported
     * @throws SoundTransformException could not call the soundtransform to shape the freqs
     */
    public FluentClientSoundImported shapeIntoSound (final String packName, final String instrumentName, final FormatInfo fi) throws SoundTransformException {
        final SoundTransformation soundTransformation = new ShapeSoundTransformation (packName, instrumentName, this.freqs, fi);
        this.cleanData ();
        this.sounds = new ApplySoundTransform (this.getObservers ()).apply (new Sound [] { new Sound (new long [0], new FormatInfo (0, 0), 0) }, soundTransformation);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #splitIntoSpectrums()
     */
    @Override
    /**
     * Uses the current sound to pick its spectrums and set that as the current data in the pipeline
     * @return the client, with the spectrums
     * @throws SoundTransformException could not convert the sound into some spectrums
     */
    public FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException {
        final SoundToSpectrumsSoundTransformation sound2Spectrums = new SoundToSpectrumsSoundTransformation ();
        new ApplySoundTransform (this.getObservers ()).apply (this.sounds, sound2Spectrums);
        this.cleanData ();
        this.spectrums = sound2Spectrums.getSpectrums ();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithAPack(java.lang.String)
     */
    @Override
    public Pack stopWithAPack (final String title) {
        return new ImportAPackIntoTheLibrary (this.getObservers ()).getPack (title);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithFile()
     */
    @Override
    /**
     * Stops the client pipeline and returns the obtained file
     * @return a file
     */
    public File stopWithFile () {
        return this.file;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithFreqs()
     */
    @Override
    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     * @return loudest frequencies array
     */
    public float [] stopWithFreqs () {
        return this.freqs.clone ();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithInputStream()
     */
    @Override
    /**
     * Stops the client pipeline and returns the obtained input stream
     * @return an input stream
     */
    public InputStream stopWithInputStream () {
        return this.audioInputStream;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithObservers()
     */
    @Override
    /**
     * Stops the client pipeline and returns the currently subscribed observers
     * @return the observers
     */
    public Observer [] stopWithObservers () {
        return this.getObservers ();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithResults(java.lang.Class)
     */
    @Override
    @SuppressWarnings ("unchecked")
    public <T> T [] stopWithResults (final Class<T> resultClass) {
        final T [] results = (T []) Array.newInstance (resultClass, this.parallelizedClients.length);
        int i = 0;
        for (final FluentClientCommon fcc : this.parallelizedClients) {
            if (resultClass == float [].class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithFreqs ();
            } else if (resultClass == Sound [].class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithSounds ();
            } else if (resultClass == InputStream.class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithInputStream ();
            } else if (resultClass == File.class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithFile ();
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithSounds()
     */
    @Override
    /**
     * Stops the client pipeline and returns the obtained sound
     * @return a sound value object
     */
    public Sound [] stopWithSounds () {
        return this.sounds.clone ();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithSpectrums()
     */
    @Override
    /**
     * Stops the client pipeline and returns the obtained spectrums
     * @return a list of spectrums for each channel
     */
    public List<Spectrum<Serializable> []> stopWithSpectrums () {
        return this.spectrums;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #stopWithStreamInfo()
     */
    @Override
    /**
     * Stops the client pipeline and returns the obtained stream info
     * object
     *
     * @return a streamInfo object
     * @throws SoundTransformException
     *             could not read the StreamInfo from the current
     *             inputstream
     */
    public StreamInfo stopWithStreamInfo () throws SoundTransformException {
        return new GetStreamInfo (this.getObservers ()).getStreamInfo (this.audioInputStream);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withAnObserver
     * (org.toilelibre.libe.soundtransform.model.observer.Observer)
     */
    @Override
    /**
     * Tells the client to add an observer that will be notified of different kind of updates
     * from the library. It is ok to call withAnObserver several times.<br/>
     * If the andAfterStart method is called, the subscribed observers are removed
     *
     * @param observers1
     *            one or more observer(s)
     * @return the client, ready to start
     */
    public FluentClientReady withAnObserver (final Observer... observers1) {
        this.observers.addAll (Arrays.<Observer> asList (observers1));
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withAPack(java.lang.String, java.io.InputStream)
     */
    @Override
    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.
     *
     * @param packName
     *            the name of the pack
     * @param jsonStream
     *            the input stream
     * @return the client, ready to start
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    public FluentClient withAPack (final String packName, final InputStream jsonStream) throws SoundTransformException {
        new ImportAPackIntoTheLibrary (this.getObservers ()).importAPack (packName, jsonStream);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withAPack(java.lang.String, java.lang.Object, java.lang.Class, int)
     */
    @Override
    /**
     * Tells the client to work with a pack. Uses the context object to find the resource from the R object
     * passed in parameter
     *
     * @param context
     *            the Android context (should be an instance of `android.content.Context`, but left as Object so the FluentClient
     *            can be used in a non-android project)
     * @param rClass
     *            R.raw.getClass () (either from soundtransform or from your pack) should be passed in parameter
     * @param packJsonId
     *            the id value of your json pack file (should be a field inside R.raw)
     * @return the client, ready to start
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    public FluentClientReady withAPack (final String packName, final Object context, final Class<?> rClass, final int packJsonId) throws SoundTransformException {
        new ImportAPackIntoTheLibrary (this.getObservers ()).importAPack (packName, context, rClass, packJsonId);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withAPack(java.lang.String, java.lang.String)
     */
    @Override
    /**
     * Tells the client to work with a pack. Reads the whole string content. A
     * pattern must be followed in the jsonContent to enable the import.<br/>
     *
     * Here is the format allowed in the file
     *
     * <pre>
     * {
     *   "instrumentName" :
     *     {
     *         {"name" : "unknownDetailsFile"},
     *         {"name" : "knownDetailsFile.wav",
     *          "frequency": 192.0,
     *          "attack": 0,
     *          "decay": 300,
     *          "sustain": 500,
     *          "release": 14732},
     *         ...
     *     },
     *   ...
     * }
     * </pre>
     *
     * If a note (one of the records inside the `instrumentName` structure) does not own any detail,
     * it will be obtained by digging in the file samples, and can take a really long time.
     * It is advisable to fill in the details in each note.
     *
     * @param packName
     *            the name of the pack
     * @param jsonContent
     *            a string containing the definition of the pack
     * @return the client, ready to start
     * @throws SoundTransformException
     *             the json content is invalid, the json format is not correct,
     *             or some sound files are missing
     */
    public FluentClient withAPack (final String packName, final String jsonContent) throws SoundTransformException {
        new ImportAPackIntoTheLibrary (this.getObservers ()).importAPack (packName, jsonContent);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withAudioInputStream(java.io.InputStream)
     */
    @Override
    /**
     * Tells the client to work first with an InputStream. It will not be read yet<br/>
     * The passed inputStream must own a format metadata object. Therefore it must be an AudioInputStream.
     * @param ais the input stream
     * @return the client, with an input stream
     */
    public FluentClientWithInputStream withAudioInputStream (final InputStream ais) {
        this.cleanData ();
        this.audioInputStream = ais;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withClasspathResource(java.lang.String)
     */
    @Override
    /**
     * Tells the client to work first with a classpath resource. It will be converted in a File
     * @param resource a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException the classpath resource was not found
     */
    public FluentClientWithFile withClasspathResource (final String resource) throws SoundTransformException {
        this.cleanData ();
        final URL fileURL = Thread.currentThread ().getContextClassLoader ().getResource (resource);
        if (fileURL == null) {
            throw new SoundTransformException (FluentClientErrorCode.NO_FILE_IN_INPUT, new NullPointerException ());
        }
        this.file = new File (fileURL.getFile ());
        this.sameDirectoryAsClasspathResource = this.file.getParent ();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withFile(java.io.File)
     */
    @Override
    /**
     * Tells the client to work first with a file. It will not be read yet
     * @param file source file
     * @return the client, with a file
     */
    public FluentClientWithFile withFile (final File file1) {
        this.cleanData ();
        this.file = file1;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withFreqs(float[])
     */
    @Override
    /**
     * Tells the client to work first with a loudest frequencies float array. It will not be used yet
     * @param freqs1 the loudest frequencies integer array
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs withFreqs (final float [] freqs1) {
        this.cleanData ();
        this.freqs = freqs1.clone ();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withLimitedTimeRecordedInputStream
     * (org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo)
     */
    @Override
    public FluentClientWithInputStream withLimitedTimeRecordedInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        this.cleanData ();
        return this.withRawInputStream (new RecordSound ().recordLimitedTimeRawInputStream (streamInfo), streamInfo);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withRawInputStream(java.io.InputStream,
     * org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo)
     */
    @Override
    /**
     * Tells the client to work first with a byte array InputStream or any readable DataInputStream.
     * It will be read and transformed into an AudioInputStream<br/>
     * The passed inputStream must not contain any metadata piece of information.
     * @param is the input stream
     * @param isInfo the stream info
     * @return the client, with an input stream
     * @throws SoundTransformException the input stream cannot be read, or the conversion did not work
     */
    public FluentClientWithInputStream withRawInputStream (final InputStream is, final StreamInfo isInfo) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = new InputStreamToAudioInputStream (this.getObservers ()).transformRawInputStream (is, isInfo);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withRecordedInputStream
     * (org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo,
     * java.lang.Object)
     */
    @Override
    public FluentClientWithInputStream withRecordedInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        this.cleanData ();
        return this.withRawInputStream (new RecordSound ().recordRawInputStream (streamInfo, stop), streamInfo);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withSounds
     * (org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    /**
     * Tells the client to work first with a sound object
     * @param sounds1 the sound object
     * @return the client, with an imported sound
     */
    public FluentClientSoundImported withSounds (final Sound [] sounds1) {
        this.cleanData ();
        this.sounds = sounds1.clone ();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #withSpectrums(java.util.List)
     */
    @Override
    /**
     * Tells the client to work first with a spectrum formatted sound.<br/>
     * The spectrums inside must be in a list (each item must correspond to a channel)
     * The spectrums are ordered in an array in chronological order
     * @param spectrums the spectrums
     * @return the client, with the spectrums
     */
    public FluentClientWithSpectrums withSpectrums (final List<Spectrum<Serializable> []> spectrums) {
        this.cleanData ();
        this.spectrums = spectrums;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #writeToClasspathResource(java.lang.String)
     */
    @Override
    /**
     * Writes the current InputStream in a classpath resource in the same folder as a previously imported classpath resource.
     * Caution : if no classpath resource was imported before, this operation will not work. Use writeToClasspathResourceWithSiblingResource instead
     * @param resource a classpath resource.
     * @return the client, with a file
     * @throws SoundTransformException there is no predefined classpathresource directory, or the file could not be written
     */
    public FluentClientWithFile writeToClasspathResource (final String resource) throws SoundTransformException {
        if (this.sameDirectoryAsClasspathResource == null) {
            throw new SoundTransformException (FluentClientErrorCode.CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE, new IllegalAccessException ());
        }
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #writeToClasspathResourceWithSiblingResource(java.lang.String,
     * java.lang.String)
     */
    @Override
    /**
     * Writes the current InputStream in a classpath resource in the same folder as a the sibling resource.
     * @param resource a classpath resource that may or may not exist yet
     * @param siblingResource a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException no such sibling resource, or the file could not be written
     */
    public FluentClientWithFile writeToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        final InputStream is = this.audioInputStream;
        this.withClasspathResource (siblingResource);
        this.cleanData ();
        this.audioInputStream = is;
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.actions.fluent.FluentClientInterface
     * #writeToFile(java.io.File)
     */
    @Override
    /**
     * Writes the current InputStream in a file
     * @param file1 the destination file
     * @return the client, with a file
     * @throws SoundTransformException The file could not be written
     */
    public FluentClientWithFile writeToFile (final File file1) throws SoundTransformException {
        if (this.audioInputStream == null) {
            throw new SoundTransformException (FluentClientErrorCode.NOTHING_TO_WRITE, new NullPointerException ());
        }
        new ExportAFile (this.getObservers ()).writeFile (this.audioInputStream, file1);
        this.cleanData ();
        this.file = file1;
        return this;
    }

}
