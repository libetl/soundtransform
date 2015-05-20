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
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CutSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.LoopSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MixSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundToSpectrumsSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpectrumsToSoundSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SubSoundExtractSoundTransform;
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

    private Sound                           sound;
    private InputStream                     audioInputStream;
    private String                          sameDirectoryAsClasspathResource;
    private List<float []>                  freqs;
    private FluentClientCommon []           parallelizedClients;

    private File                            file;

    private List<Spectrum<Serializable> []> spectrums;

    private List<Observer>                  observers;

    private FluentClient () {
        this.andAfterStart ();
    }

    /**
     * Sets the passed observers as the default value when a FluentClient is
     * started
     *
     * It can be useful if you are going to use the FluentClient several times
     * but you want to declare the subscribed observers only once
     *
     * @param defaultObservers1
     *            one or more observer(s)
     *
     */
    public static void setDefaultObservers (final Observer... defaultObservers1) {
        FluentClient.defaultObservers = new LinkedList<Observer> (Arrays.<Observer> asList (defaultObservers1));
    }

    /**
     * Start up the client
     *
     * @return the client, ready to start
     */
    public static FluentClientReady start () {
        return new FluentClient ();
    }

    /**
     * Adjusts the loudest freqs array to match exactly the piano notes
     * frequencies
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    public FluentClientWithFreqs adjust () {
        this.freqs = new ChangeLoudestFreqs ().adjust (this.freqs);
        return this;
    }

    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    @Override
    public FluentClientReady andAfterStart () {
        this.cleanData ();
        this.cleanObservers ();
        return this;
    }

    /**
     * Appends the sound passed in parameter to the current sound stored in the
     * client
     *
     * @param sound1
     *            the sound to append the current sound to
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the sound is null or if there is a problem with the
     *             appending
     */
    @Override
    public FluentClientSoundImported append (final Sound sound1) throws SoundTransformException {
        this.sound = new AppendSound (this.getObservers ()).append (this.sound, sound1);
        return this;
    }

    @Override
    /**
     * Applies one transform and continues with the result sound
     * @param st the SoundTransform to apply
     * @return the client with a sound imported
     * @throws SoundTransformException if the transform does not work
     */
    public FluentClientSoundImported apply (final SoundTransform<Channel, Channel> st) throws SoundTransformException {
        final Channel [] channels1 = new ApplySoundTransform (this.getObservers ()).apply (this.sound.getChannels (), st);
        this.cleanData ();
        this.sound = new Sound (channels1);
        return this;
    }

    /**
     * Applies one transform and stops the pipeline immediately after with a
     * result
     *
     * @param st
     *            the SoundTransform to apply
     * 
     * @param <T> the output type of the transform and the array component type of the returned value
     * @return a result in the expected kind
     * @throws SoundTransformException
     *             if the transform does not work
     */
    @Override
    @SuppressWarnings ("unchecked")
    public <T> T [] applyAndStop (final SoundTransform<Channel, T> st) throws SoundTransformException {
        final Object result = new ApplySoundTransform (this.getObservers ()).apply (this.sound.getChannels (), st);
        return (T []) result;
    }
    
    /**
     * Changes the current imported sound to fit the expected format
     *
     * @param formatInfo
     *            the new expected format
     * @return the client, with a sound imported
     */
    @Override
    public FluentClientSoundImported changeFormat (final FormatInfo formatInfo) {
        this.sound = new ChangeSoundFormat (this.getObservers ()).changeFormat (this.sound, formatInfo);
        return this;
    }

    /**
     * Resets the state of the FluentClient
     */
    private void cleanData () {
        this.sound = null;
        this.audioInputStream = null;
        this.file = null;
        this.freqs = null;
        this.spectrums = null;
        this.parallelizedClients = null;
    }

    /**
     * Resets the list of the subscribed observers
     */
    private void cleanObservers () {
        this.observers = FluentClient.defaultObservers;
    }

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

    @Override
    /**
     * Shortcut for importToStream ().importToSound () : Conversion from a File to a Sound
     * @return the client, with a sound imported
     * @throws SoundTransformException if one of the two import fails
     */
    public FluentClientSoundImported convertIntoSound () throws SoundTransformException {
        return this.importToStream ().importToSound ();
    }

    /**
     * Splices a part of the sound between the sample #start and the sample #end
     *
     * @param start
     *            the first sample to cut
     * @param end
     *            the last sample to cut
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the indexes are out of bound
     */
    @Override
    public FluentClientSoundImported cutSubSound (final int start, final int end) throws SoundTransformException {
        return this.apply (new CutSoundTransform (start, end));
    }

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

    @Override
    /**
     * Uses the current imported sound and converts it into an InputStream, ready to be written to a file (or to be read again)
     * @return the client, with an inputStream
     * @throws SoundTransformException if the metadata format object is invalid, or if the sound cannot be converted
     */
    public FluentClientWithInputStream exportToStream () throws SoundTransformException {
        final FormatInfo currentInfo = this.sound.getFormatInfo ();
        final InputStream audioInputStream1 = new ToInputStream (this.getObservers ()).toStream (this.sound, StreamInfo.from (currentInfo, this.sound));
        this.cleanData ();
        this.audioInputStream = audioInputStream1;
        return this;
    }

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
        @SuppressWarnings ("unchecked")
        final Channel [] sound1 = new ApplySoundTransform (this.getObservers ()).<Spectrum<Serializable> [], Channel> apply (this.spectrums.toArray (new Spectrum [0] [0]), new SpectrumsToSoundSoundTransform ());
        this.cleanData ();
        this.sound = new Sound (sound1);
        return this;
    }

    /**
     * Extracts a part of the sound between the sample #start and the sample
     * #end
     *
     * @param start
     *            the first sample to extract
     * @param end
     *            the last sample to extract
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the indexes are out of bound
     */
    @Override
    public FluentClientSoundImported extractSubSound (final int start, final int end) throws SoundTransformException {
        return this.apply (new SubSoundExtractSoundTransform (start, end));
    }

    /**
     * Removes the values between low and high in the loudest freqs array
     * (replace them by 0)
     *
     * @param low
     *            low frequency (first one to avoid)
     * @param high
     *            high frequency (last one to avoid)
     * @return the client, with a loudest frequencies float array
     * @throws SoundTransformException can occur if low is greater than or equal to high
     */
    @Override
    public FluentClientWithFreqs filterRange (final float low, final float high) throws SoundTransformException {
        this.freqs = new ChangeLoudestFreqs ().filterRange (this.freqs, low, high);
        return this;
    }

    /**
     * Will invoke a soundtransform to find the loudest frequencies of the
     * sound, chronologically Caution : the original sound will be lost, and it
     * will be impossible to revert this conversion. When shaped into a sound,
     * the new sound will only sounds like the instrument you shaped the freqs
     * with
     *
     * @return the client, with a loudest frequencies integer array
     * @throws SoundTransformException
     *             if the convert fails
     */
    @Override
    public FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException {
        final PeakFindSoundTransform<Serializable, ?> peakFind = new PeakFindWithHPSSoundTransform<Serializable> (FluentClient.DEFAULT_STEP_VALUE);
        final Channel [] savedChannels = this.sound.getChannels ();
        this.cleanData ();
        this.freqs = Arrays.asList (new ApplySoundTransform (this.getObservers ()).apply (savedChannels, peakFind));
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

    @Override
    /**
     * Uses the current input stream object to convert it into a sound
     * @return the client, with a sound imported
     * @throws SoundTransformException the inputStream is invalid, or the convert did not work
     */
    public FluentClientSoundImported importToSound () throws SoundTransformException {
        Sound sound1;
        if (this.audioInputStream != null) {
            sound1 = new ConvertFromInputStream (this.getObservers ()).fromInputStream (this.audioInputStream);
        } else {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_NOT_READY, new NullPointerException ());
        }
        this.cleanData ();
        this.sound = sound1;
        return this;
    }

    @Override
    /**
     * Opens the current file and converts it into an InputStream, ready to be read (or to be written to a file)
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

    /**
     * Adds some new values in the loudest freqs array from the "start" index
     * (add the values of subfreqs)
     *
     * @param subFreqs
     *            loudest freqs array to insert
     * @param start
     *            index where to start the insert
     * @return the client, with a loudest frequencies float array
     */
    @Override
    public FluentClientWithFreqs insertPart (final List<float []> subFreqs, final int start) {
        this.freqs = new ChangeLoudestFreqs ().insertPart (this.freqs, subFreqs, start);
        return this;
    }

    /**
     * Runs asynchronously the same operations on a varargs of started
     * FluentClients
     *
     * @param operation
     *            a list of operation to apply
     * @param timeoutInSeconds
     *            a timeout value. After that, the operation will be stopped,
     *            even if it is still processing. You can choose
     *            Integer.MAX_VALUE as a value if you are convinced that it will
     *            finish.
     * @param clients
     *            a list of started FluentClients
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
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

    /**
     * Alias for the inParallel method using a list of clients
     *
     * @param op
     *            a list of operation to apply
     * @param timeoutInSeconds
     *            a timeout value. After that, the operation will be stopped,
     *            even if it is still processing. You can choose
     *            Integer.MAX_VALUE as a value if you are convinced that it will
     *            finish.
     * @param sounds
     *            a list of sound
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final Sound... sounds) throws SoundTransformException {

        final FluentClientCommon [] clients = new FluentClientCommon [sounds.length];
        for (int i = 0 ; i < sounds.length ; i++) {
            clients [i] = FluentClient.start ().withSound (sounds [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /**
     * Alias for the inParallel method using a list of clients
     *
     * @param op
     *            a list of operation to apply
     * @param timeoutInSeconds
     *            a timeout value. After that, the operation will be stopped,
     *            even if it is still processing. You can choose
     *            Integer.MAX_VALUE as a value if you are convinced that it will
     *            finish.
     * @param inputStreams1
     *            a list of inputStreams
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final InputStream... inputStreams1) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [inputStreams1.length];
        for (int i = 0 ; i < inputStreams1.length ; i++) {
            clients [i] = FluentClient.start ().withAudioInputStream (inputStreams1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /**
     * Alias for the inParallel method using a list of clients
     *
     * @param op
     *            a list of operation to apply
     * @param timeoutInSeconds
     *            a timeout value. After that, the operation will be stopped,
     *            even if it is still processing. You can choose
     *            Integer.MAX_VALUE as a value if you are convinced that it will
     *            finish.
     * @param files1
     *            a list of Files
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final File... files1) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [files1.length];
        for (int i = 0 ; i < files1.length ; i++) {
            clients [i] = FluentClient.start ().withFile (files1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /**
     * Alias for the inParallel method using a list of clients
     *
     * @param op
     *            a list of operation to apply
     * @param timeoutInSeconds
     *            a timeout value. After that, the operation will be stopped,
     *            even if it is still processing. You can choose
     *            Integer.MAX_VALUE as a value if you are convinced that it will
     *            finish.
     * @param freqs1
     *            a list of loudest freqs arrays
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final List<float []>... freqs1) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [freqs1.length];
        for (int i = 0 ; i < freqs1.length ; i++) {
            clients [i] = FluentClient.start ().withFreqs (freqs1 [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /**
     * Alias for the inParallel method using a list of clients
     *
     * @param op
     *            a list of operation to apply
     * @param timeoutInSeconds
     *            a timeout value. After that, the operation will be stopped,
     *            even if it is still processing. You can choose
     *            Integer.MAX_VALUE as a value if you are convinced that it will
     *            finish.
     * @param classpathResources
     *            a list of classpathResources
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    @Override
    public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final String... classpathResources) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [classpathResources.length];
        for (int i = 0 ; i < classpathResources.length ; i++) {
            clients [i] = FluentClient.start ().withClasspathResource (classpathResources [i]);
        }
        return this.inParallel (op, timeoutInSeconds, clients);
    }

    /**
     * Extracts a part of the sound between the sample #start and the sample
     * #end
     *
     * @param length
     *            the number of samples of the result sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the length is not positive
     */
    @Override
    public FluentClientSoundImported loop (final int length) throws SoundTransformException {
        return this.apply (new LoopSoundTransform (length));
    }

    @Override
    /**
     * Combines the current sound with another sound. The operation is not reversible
     * @param sound the sound to mix the current sound with
     * @return the client, with a sound imported
     * @throws SoundTransformException if the sound is null or if there is a problem with the mix
     */
    public FluentClientSoundImported mixWith (final Sound sound1) throws SoundTransformException {
        return this.apply (new MixSoundTransform (Arrays.<Sound> asList (sound1)));
    }

    /**
     * Uses the sound inside the nested clients to mix them all and to produce a
     * single sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the nested clients are not in the Sound imported state
     */
    @Override
    public FluentClientSoundImported mixAllInOneSound () throws SoundTransformException {
        final FluentClientCommon [] savedClients = this.parallelizedClients;

        this.cleanData ();

        if (savedClients == null || savedClients.length == 0 || ((FluentClient) savedClients [0]).sound == null) {
            throw new SoundTransformException (FluentClient.FluentClientErrorCode.MISSING_SOUND_IN_INPUT, new IllegalArgumentException ());
        }
        this.sound = ((FluentClient) savedClients [0]).stopWithSound ();

        for (int i = 1 ; i < savedClients.length ; i++) {
            final Sound otherSound = ((FluentClient) savedClients [i]).sound;
            this.apply (new MixSoundTransform (Arrays.<Sound> asList (otherSound)));
        }

        return this;
    }

    /**
     * Changes the loudest frequencies array to become one octave lower
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    public FluentClientWithFreqs octaveDown () {
        this.freqs = new ChangeLoudestFreqs ().octaveDown (this.freqs);
        return this;
    }

    /**
     * Changes the loudest frequencies array to become one octave upper
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    public FluentClientWithFreqs octaveUp () {
        this.freqs = new ChangeLoudestFreqs ().octaveUp (this.freqs);
        return this;
    }

    @Override
    /**
     * Plays the current audio data and (if needed) convert it temporarily to a sound
     * @return the client, in its current state.
     * @throws SoundTransformException could not play the current audio data
     */
    public FluentClient playIt () throws SoundTransformException {
        if (this.sound != null) {
            new PlaySound ().play (this.sound);
        } else if (this.audioInputStream != null) {
            new PlaySound ().play (this.audioInputStream);
        } else if (this.spectrums != null) {
            final List<Spectrum<Serializable> []> savedSpectrums = this.spectrums;
            this.extractSound ();
            new PlaySound ().play (this.sound);
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
    /**
     * Replaces some of the values of the loudest freqs array from the "start"
     * index (replace them by the values of subfreqs)
     *
     * @param subFreqs
     *            replacement loudest freqs array
     * @param start
     *            index where to start the replacement
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs replacePart (final List<float []> subFreqs, final int start) {
        this.freqs = new ChangeLoudestFreqs ().replacePart (this.freqs, subFreqs, start);
        return this;
    }

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
        final SoundTransform<float [], Channel> soundTransform = new ShapeSoundTransform (packName, instrumentName, fi);
        final List<float []> savedFreqs = this.freqs;
        this.cleanData ();
        this.sound = new Sound (new ApplySoundTransform (this.getObservers ()).<float [], Channel> apply (savedFreqs.toArray (new float [0] [0]), soundTransform));
        return this;
    }

    @Override
    /**
     * Uses the current sound to pick its spectrums and set that as the current data in the pipeline
     * @return the client, with the spectrums
     * @throws SoundTransformException could not convert the sound into some spectrums
     */
    public FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException {
        final SoundToSpectrumsSoundTransform sound2Spectrums = new SoundToSpectrumsSoundTransform ();
        final Sound savedSound = this.sound;
        this.cleanData ();
        this.spectrums = Arrays.asList (new ApplySoundTransform (this.getObservers ()).apply (savedSound.getChannels (), sound2Spectrums));
        return this;
    }

    /**
     * Stops the client pipeline and returns the pack whose title is in
     * parameter
     *
     * @param title
     *            the title of the pack
     * @return a pack object
     */
    @Override
    public Pack stopWithAPack (final String title) {
        return new ImportAPackIntoTheLibrary (this.getObservers ()).getPack (title);
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained file
     * @return a file
     */
    public File stopWithFile () {
        return this.file;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     * @return loudest frequencies array
     */
    public List<float []> stopWithFreqs () {
        return this.freqs;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained input stream
     * @return an input stream
     */
    public InputStream stopWithInputStream () {
        return this.audioInputStream;
    }

    @Override
    /**
     * Stops the client pipeline and returns the currently subscribed observers
     * @return the observers
     */
    public Observer [] stopWithObservers () {
        return this.getObservers ();
    }

    /**
     * Stops the client pipeline and get all the values inside each nested
     * client
     *
     * @param resultClass
     *            You have to specify what type of result you expect. the value
     *            can be one of this list : (Sound.class, InputStream.class,
     *            File.class, String.class, float [].class)
     * @return an array of results
     */
    @Override
    @SuppressWarnings ("unchecked")
    public <T> T [] stopWithResults (final Class<T> resultClass) {
        final T [] results = (T []) Array.newInstance (resultClass, this.parallelizedClients.length);
        int i = 0;
        for (final FluentClientCommon fcc : this.parallelizedClients) {
            if (resultClass == List.class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithFreqs ();
            } else if (resultClass == Sound.class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithSound ();
            } else if (resultClass == InputStream.class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithInputStream ();
            } else if (resultClass == File.class) {
                results [i++] = (T) ((FluentClient) fcc).stopWithFile ();
            }
        }
        return results;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained sound
     * @return a sound value object
     */
    public Sound stopWithSound () {
        return this.sound;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained spectrums
     * @return a list of spectrums for each channel
     */
    public List<Spectrum<Serializable> []> stopWithSpectrums () {
        return this.spectrums;
    }

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

    /**
     * Changes the loudest frequencies so every value is between low and high
     * 
     * @param low lowest frequency of the range
     * @param high highest frequency of the range
     *
     * @return the client, with a loudest frequencies float array

     * @throws SoundTransformException can occur if low is greater than or equal to high
     */
    @Override
    public FluentClientWithFreqs surroundInRange (final float low, final float high) throws SoundTransformException {
        this.freqs = new ChangeLoudestFreqs ().surroundInRange (this.freqs, low, high);
        return this;
    }
    
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

    @Override
    /**
     * Tells the client to use the sound passed in parameter by mixing them all into one
     *
     * @param sound
     *            a var-arg value of arrays of sound (each value inside the arrays is a sound channel)
     * @return the client, with an imported sound
     * @throws SoundTransformException
     *             the sound files are invalid
     */
    public FluentClientSoundImported withAMixedSound (final Sound... sounds) throws SoundTransformException {
        final FluentClientCommon [] clients = new FluentClientCommon [sounds.length];
        for (int i = 0 ; i < sounds.length ; i++) {
            clients [i] = FluentClient.start ().withSound (sounds [i]);
        }
        this.parallelizedClients = clients;
        return this.mixAllInOneSound ();
    }

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

    @Override
    /**
     * Tells the client to work first with a loudest frequencies float array. It will not be used yet
     * @param freqs1 the loudest frequencies integer array
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs withFreqs (final List<float []> freqs1) {
        this.cleanData ();
        this.freqs = new LinkedList<float []> (freqs1);
        return this;
    }

    /**
     * Tells the client to open the microphone and to record a sound The result
     * will be of an InputStream type The recording time will be the one passed
     * in the streamInfo
     *
     * @param streamInfo
     *            the future input stream info
     * @return the client, with an input stream
     * @throws SoundTransformException
     *             the mic could not be read, the recorder could not start, or
     *             the buffer did not record anything
     */
    @Override
    public FluentClientWithInputStream withLimitedTimeRecordedInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        this.cleanData ();
        return this.withRawInputStream (new RecordSound ().recordLimitedTimeRawInputStream (streamInfo), streamInfo);
    }

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

    /**
     * Tells the client to open the microphone and to record a sound The result
     * will be of an InputStream type The frameLength in the streamInfo will be
     * ignored
     *
     * /!\ : blocking method, the `stop.notify` method must be called in another
     * thread.
     *
     * @param streamInfo
     *            the future input stream info
     * @param stop
     *            the method notify must be called to stop the recording
     * @return the client, with an input stream
     * @throws SoundTransformException
     *             the mic could not be read, the recorder could not start, or
     *             the buffer did not record anything
     */
    @Override
    public FluentClientWithInputStream withRecordedInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        this.cleanData ();
        return this.withRawInputStream (new RecordSound ().recordRawInputStream (streamInfo, stop), streamInfo);
    }

    @Override
    /**
     * Tells the client to work first with a sound object
     * @param sound1 the sound object
     * @return the client, with an imported sound
     */
    public FluentClientSoundImported withSound (final Sound sound1) {
        this.cleanData ();
        this.sound = sound1;
        return this;
    }

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
