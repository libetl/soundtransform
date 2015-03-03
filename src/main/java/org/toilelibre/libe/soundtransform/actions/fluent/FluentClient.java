package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.actions.notes.ImportAPackIntoTheLibrary;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.AppendSound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ChangeSoundFormat;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetInputStreamInfo;
import org.toilelibre.libe.soundtransform.actions.transform.InputStreamToAudioInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ChangeLoudestFreqs;
import org.toilelibre.libe.soundtransform.actions.transform.ToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CutSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.LoopSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MixSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundToSpectrumsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpectrumsToSoundSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SubSoundExtractSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class FluentClient implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile, FluentClientWithFreqs, FluentClientWithSpectrums {

    private static final int     DEFAULT_STEP_VALUE = 100;
    private Sound []             sounds;
    private InputStream          audioInputStream;
    private String               sameDirectoryAsClasspathResource;
    private float []             freqs;
    private File                 file;
    private List<Spectrum<Serializable> []> spectrums;

    private List<Observer>       observers;

    private int                  step;

    private FluentClient () {
        this.andAfterStart ();
    }
    
    public enum FluentClientErrorCode implements ErrorCode {

        INPUT_STREAM_NOT_READY ("Input Stream not ready"), NOTHING_TO_WRITE ("Nothing to write to a File"), NO_FILE_IN_INPUT ("No file in input"), CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE ("This client did not read a classpath resouce at the start"), NO_SPECTRUM_IN_INPUT ("No spectrum in input");

        private final String messageFormat;

        FluentClientErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    /**
     * Startup the client
     *
     * @return the client, ready to start
     */
    public static FluentClientReady start () {
        return new FluentClient ();
    }

    /**
     * Adjust the loudest freqs array to match exactly the piano notes frequencies
     *
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs adjust (){
        this.freqs = new ChangeLoudestFreqs ().adjust (this.freqs);
        return this;
    }
    
    @Override
    /**
     * Start over the client : reset the state and the value objects nested in the client
     * @return the client, ready to start
     */
    public FluentClientReady andAfterStart () {
        this.cleanData ();
        this.cleanObservers ();
        this.step = FluentClient.DEFAULT_STEP_VALUE;
        return this;
    }

    /**
     * Append the sound passed in parameter to the current sound stored in the
     * client
     *
     * @param sound
     *            the sound to append the current sound to
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the sound is null or if there is a problem with the
     *             appending please ensure that both sounds have the same number
     *             of channels
     */
    @Override
    public FluentClientSoundImported append (final Sound [] sounds1) throws SoundTransformException {
        this.sounds = new AppendSound (this.getObservers ()).append (this.sounds, sounds1);
        return this;
    }

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

    /**
     * Changes the current imported sound to fit the expected format
     *
     * @param inputStreamInfo
     *            only the sampleSize and the sampleRate pieces of data will be
     *            used
     * @return the client, with a sound imported
     * @throws SoundTransformException
     */
    @Override
    public FluentClientSoundImported changeFormat (final InputStreamInfo inputStreamInfo) throws SoundTransformException {
        this.sounds = new ChangeSoundFormat (this.getObservers ()).changeFormat (this.sounds, inputStreamInfo);
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
    }

    /**
     * Reset the list of the subscribed observers
     */
    private void cleanObservers () {
        this.observers = new LinkedList<Observer> ();
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
     * Splice a part of the sound between the sample #start and the sample #end
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
        return this.apply (new CutSoundTransformation (start, end));
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
        final InputStreamInfo currentInfo = new GetInputStreamInfo (this.getObservers ()).getInputStreamInfo (this.sounds);
        final InputStream audioInputStream1 = new ToInputStream (this.getObservers ()).toStream (this.sounds, currentInfo);
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
        if ((this.spectrums == null) || this.spectrums.isEmpty () || (this.spectrums.get (0).length == 0)) {
            throw new SoundTransformException (FluentClientErrorCode.NO_SPECTRUM_IN_INPUT, new IllegalArgumentException ());
        }
        final Sound [] input = new Sound [this.spectrums.size ()];
        for (int i = 0 ; i < input.length ; i++) {
            input [i] = new Sound (new long [0], this.spectrums.get (0) [0].getNbBytes (), this.spectrums.get (0) [0].getSampleRate (), i);
        }
        final Sound [] sounds1 = new ApplySoundTransform (this.getObservers ()).apply (input, new SpectrumsToSoundSoundTransformation (this.spectrums));
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    /**
     * Extract a part of the sound between the sample #start and the sample #end
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
        return this.apply (new SubSoundExtractSoundTransformation (start, end));
    }

    
    /**
     * Remove the values between low and high in the loudest freqs array
     * (replace them by 0)
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    public FluentClientWithFreqs filterRange (float low, float high) {
        this.freqs = new ChangeLoudestFreqs ().filterRange (this.freqs, low, high);
        return this;
    }
    
    /**
     * Will invoke a soundtransform to find the loudest frequencies of the
     * sound, chronologically<br/>
     * Caution : the original sound will be lost, and it will be impossible to
     * revert this conversion.<br/>
     * When shaped into a sound, the new sound will only sounds like the
     * instrument you shaped the freqs with
     *
     * @return the client, with a loudest frequencies float array
     * @throws SoundTransformException
     *             if the convert fails
     */
    @Override
    public FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException {
        final PeakFindWithHPSSoundTransformation<Serializable> peakFind = new PeakFindWithHPSSoundTransformation<Serializable> (this.step);
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

    /**
     * Extract a part of the sound between the sample #start and the sample #end
     *
     * @param length
     *            the number of samples of the result sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the length is not positive
     */
    @Override
    public FluentClientSoundImported loop (final int length) throws SoundTransformException {
        return this.apply (new LoopSoundTransformation (length));
    }

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

    @Override
    /**
     * Replace some of the values of the loudest freqs array from the "start" index
     * (replace them by the values of subfreqs)
     *
     * @return the client, with a loudest frequencies float array
     */
    public FluentClientWithFreqs replacePart (float [] subFreqs, int start) {
        this.freqs = new ChangeLoudestFreqs ().replacePart (this.freqs, subFreqs, start);
        return this;
    }
    
    @Override
    /**
     * Shapes these loudest frequencies array into a sound and set the converted sound in the pipeline
     * @param packName reference to an existing imported pack (must be invoked before the shapeIntoSound method by using withAPack)
     * @param instrumentName the name of the instrument that will map the freqs object
     * @param isi the wanted format for the future sound
     * @return the client, with a sound imported
     * @throws SoundTransformException could not call the soundtransform to shape the freqs
     */
    public FluentClientSoundImported shapeIntoSound (final String packName, final String instrumentName, final InputStreamInfo isi) throws SoundTransformException {
        final SoundTransformation soundTransformation = new ShapeSoundTransformation (packName, instrumentName, this.freqs, (int) isi.getFrameLength (), isi.getSampleSize (), (int) isi.getSampleRate ());
        this.cleanData ();
        this.sounds = new ApplySoundTransform (this.getObservers ()).apply (new Sound [] { new Sound (new long [0], 0, 0, 0) }, soundTransformation);
        return this;
    }

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
    public float [] stopWithFreqs () {
        return this.freqs.clone ();
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
     * Stops the client pipeline and returns the obtained input stream info
     * object
     *
     * @return an inputStreamInfo object
     * @throws SoundTransformException
     *             could not read the inputstreaminfo from the current
     *             inputstream
     */
    public InputStreamInfo stopWithInputStreamInfo () throws SoundTransformException {
        return new GetInputStreamInfo (this.getObservers ()).getInputStreamInfo (this.audioInputStream);
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained sound
     * @return a sound value object
     */
    public Sound [] stopWithSounds () {
        return this.sounds.clone ();
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
     * Tells the client to work with a pack. Reads the whole inputStream. A pattern must be followed in the jsonStream to
     * enable the import.
     * @param packName the name of the pack
     * @param jsonStream the input stream
     * @return the client, in its current state.
     * @throws SoundTransformException the input stream cannot be read, or the json format is not correct, or some sound files are missing
     */
    public FluentClient withAPack (final String packName, final InputStream jsonStream) throws SoundTransformException {
        new ImportAPackIntoTheLibrary (this.getObservers ()).importAPack (packName, jsonStream);
        return this;
    }

    @Override
    /**
     * Tells the client to work with a pack. Reads the whole string content. A pattern must be followed in the jsonContent to
     * enable the import.<br/>
     * Here is the format allowed in the file
     * <pre>
     * {
     *   "instrumentName" :
     *   {
     *     -1 : "/data/mypackage.myapp/unknownFrequencyFile.wav",
     *    192 : "/data/mypackage.myapp/knownFrequencyFile.wav",
     *    ...
     *   },
     *   ...
     * }
     * </pre>
     * Do not assign the same frequency for two notes in the same instrument. If several notes must have their frequencies
     * detected by the soundtransform lib, set different negative values (-1, -2, -3, ...)
     * @param packName the name of the pack
     * @param jsonContent a string containing the definition of the pack
     * @return the client, in its current state.
     * @throws SoundTransformException the json content is invalid, the json format is not correct, or some sound files are missing
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
    public FluentClientWithFreqs withFreqs (final float [] freqs1) {
        this.cleanData ();
        this.freqs = freqs1.clone ();
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a byte array InputStream or any readable DataInputStream.
     * It will be read and transformed into an AudioInputStream<br/>
     * The passed inputStream must not contain any metadata piece of information.
     * @param is the input stream
     * @param isInfo the audio format (named "InputStreamInfo")
     * @return the client, with an input stream
     * @throws SoundTransformException the input stream cannot be read, or the conversion did not work
     */
    public FluentClientWithInputStream withRawInputStream (final InputStream is, final InputStreamInfo isInfo) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = new InputStreamToAudioInputStream (this.getObservers ()).transformRawInputStream (is, isInfo);
        return this;
    }

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
        if (this.audioInputStream != null) {
            new ExportAFile (this.getObservers ()).writeFile (this.audioInputStream, file1);
        }
        this.cleanData ();
        this.file = file1;
        return this;
    }

}
