package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public interface FluentClientInterface {

    /**
     * Adjust the loudest freqs array to match exactly the piano notes
     * frequencies
     *
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs adjust ();

    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    public abstract FluentClientReady andAfterStart ();

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
    public abstract FluentClientSoundImported append (Sound [] sound) throws SoundTransformException;

    /**
     * Apply one transform and continue with the result sound
     *
     * @param st
     *            the SoundTransformation to apply
     * @return the client with a sound imported
     * @throws SoundTransformException
     *             if the transform does not work
     */
    public abstract FluentClientSoundImported apply (SoundTransformation st) throws SoundTransformException;

    /**
     * Changes the current imported sound to fit the expected format
     *
     * @param formatInfo
     *            the new expected format
     * @return the client, with a sound imported
     * @throws SoundTransformException
     */
    public abstract FluentClientSoundImported changeFormat (FormatInfo formatInfo) throws SoundTransformException;

    /**
     * Compresses the loudest freq array (speedup or slowdown) When shaped into
     * a sound, the result will have a different tempo than the original sound
     * but will keep the same pitch
     *
     * @param factor
     *            the factor parameter quantifies how much the stretch will be
     *            (i.e if factor = 0.5, then the result will be twice as long
     *            than the original)
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs compress (float factor);

    /**
     * Shortcut for importToStream ().importToSound () : Conversion from a File
     * to a Sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if one of the two import fails
     */
    public abstract FluentClientSoundImported convertIntoSound () throws SoundTransformException;

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
    public abstract FluentClientSoundImported cutSubSound (int start, int end) throws SoundTransformException;

    /**
     * Shortcut for exportToStream ().writeToClasspathResource (resource) :
     * Conversion from a Sound to a File
     *
     * @param resource
     *            a resource that can be found in the classpath
     * @return the client, with a file written
     * @throws SoundTransformException
     *             if one of the two operations fails
     */
    public abstract FluentClientWithFile exportToClasspathResource (String resource) throws SoundTransformException;

    /**
     * Shortcut for exportToStream
     * ().writeToClasspathResourceWithSiblingResource (resource,
     * siblingResource)
     *
     * @param resource
     *            a resource that may or may not exist in the classpath
     * @param siblingResource
     *            a resource that can be found in the classpath.
     * @return the client, with a file written
     * @throws SoundTransformException
     *             if one of the two operations fails
     */
    public abstract FluentClientWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Shortcut for exportToStream ().writeToFile (file)
     *
     * @param file
     *            the destination file
     * @return the client, with a file written
     * @throws SoundTransformException
     *             if one of the two operations fails
     */
    public abstract FluentClientWithFile exportToFile (File file) throws SoundTransformException;

    /**
     * Uses the current imported sound and converts it into an InputStream,
     * ready to be written to a file (or to be read again)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             if the metadata format object is invalid, or if the sound
     *             cannot be converted
     */
    public abstract FluentClientWithInputStream exportToStream () throws SoundTransformException;

    /**
     * Uses the current available spectrums objects to convert them into a sound
     * (with one or more channels)
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the spectrums are in an invalid format, or if the
     *             transform to sound does not work
     */
    public abstract FluentClientSoundImported extractSound () throws SoundTransformException;

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
    public abstract FluentClientSoundImported extractSubSound (int start, int end) throws SoundTransformException;

    /**
     * Remove the values between low and high in the loudest freqs array
     * (replace them by 0)
     *
     * @param low
     *            low frequency (first one to avoid)
     * @param high
     *            high frequency (last one to avoid)
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs filterRange (float low, float high);

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
    public abstract FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException;

    /**
     * Uses the current input stream object to convert it into a sound (with one
     * or more channels)
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             the inputStream is invalid, or the convert did not work
     */
    public abstract FluentClientSoundImported importToSound () throws SoundTransformException;

    /**
     * Opens the current file and convert it into an InputStream, ready to be
     * read (or to be written to a file)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             the current file is not valid, or the conversion did not work
     */
    public abstract FluentClientWithInputStream importToStream () throws SoundTransformException;

    /**
     * Add some new values in the loudest freqs array from the "start" index
     * (add the values of subfreqs)
     *
     * @param subFreqs
     *            loudest freqs array to insert
     * @param start
     *            index where to start the insert
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs insertPart (float [] subFreqs, int start);

    /**
     * Runs asynchronously the same operations on a varargs of started
     * FluentClients
     *
     * @param op
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
    public abstract <T extends FluentClientCommon> FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, T... clients) throws SoundTransformException;

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
     *            a list of Sounds (each Sound object is a sound channel)
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    public abstract FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, Sound []... sounds) throws SoundTransformException;

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
     * @param inputStreams
     *            a list of inputStreams
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    public abstract FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, InputStream... inputStreams) throws SoundTransformException;

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
     * @param files
     *            a list of Files
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, File... files) throws SoundTransformException;

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
     * @param freqs
     *            a list of loudest freqs arrays
     * @return the client, with a list of clients inside holding a value each
     * @throws SoundTransformException
     *             can happen if there was a problem during the flow, or if the
     *             threads were interrupted
     */
    FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, float []... freqs) throws SoundTransformException;

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
    FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, String... classpathResources) throws SoundTransformException;

    /**
     * Extract a part of the sound between the sample #start and the sample #end
     *
     * @param length
     *            the number of samples of the result sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the length is not positive
     */
    public abstract FluentClientSoundImported loop (int length) throws SoundTransformException;

    /**
     * Combines the current sound with another sound. The operation is not
     * reversible
     *
     * @param sound
     *            the sound to mix the current sound with
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the sound is null or if there is a problem with the mix
     */
    public abstract FluentClientSoundImported mixWith (Sound [] sound) throws SoundTransformException;

    /**
     * Uses the sounds inside the nested clients to mix them all and to produce
     * a single sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the nested clients are not in the Sound imported state
     */
    public abstract FluentClientSoundImported mixAllInOneSound () throws SoundTransformException;

    /**
     * Changes the loudest frequencies array to become one octave lower
     *
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs octaveDown ();

    /**
     * Changes the loudest frequencies array to become one octave upper
     *
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs octaveUp ();

    /**
     * Plays the current audio data and (if needed) convert it temporarily to a
     * sound
     *
     * @return the client, in its current state.
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    public abstract FluentClientInterface playIt () throws SoundTransformException;

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
    public abstract FluentClientWithFreqs replacePart (float [] subFreqs, int start);

    /**
     * Shapes these loudest frequencies array into a sound and set the converted
     * sound in the pipeline
     *
     * @param packName
     *            reference to an existing imported pack (must be invoked before
     *            the shapeIntoSound method by using withAPack)
     * @param instrumentName
     *            the name of the instrument that will map the freqs object
     * @param formatInfo
     *            the wanted format for the future sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             could not call the soundtransform to shape the freqs
     */
    public abstract FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, FormatInfo formatInfo) throws SoundTransformException;

    /**
     * Uses the current sound to pick its spectrums and set that as the current
     * data in the pipeline
     *
     * @return the client, with the spectrums
     * @throws SoundTransformException
     *             could not convert the sound into some spectrums
     */
    public abstract FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the pack whose title is in
     * parameter
     *
     * @param title
     *            the title of the pack
     * @return a pack object
     */
    public abstract Pack stopWithAPack (String title);

    /**
     * Stops the client pipeline and returns the obtained file
     *
     * @return a file
     */
    public abstract File stopWithFile ();

    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     *
     * @return loudest frequencies array
     */
    public abstract float [] stopWithFreqs ();

    /**
     * Stops the client pipeline and returns the obtained input stream
     *
     * @return an input stream
     */
    public abstract InputStream stopWithInputStream ();

    /**
     * Stops the client pipeline and returns the currently subscribed observers
     *
     * @return the observers
     */
    public abstract Observer [] stopWithObservers ();

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
    public abstract <T> T [] stopWithResults (Class<T> resultClass);

    /**
     * Stops the client pipeline and returns the obtained sound
     *
     * @return a sound value object
     */
    public abstract Sound [] stopWithSounds ();

    /**
     * Stops the client pipeline and returns the obtained spectrums
     *
     * @return a list of spectrums for each channel
     */
    public abstract List<Spectrum<Serializable> []> stopWithSpectrums ();

    /**
     * Stops the client pipeline and returns the obtained stream info object
     *
     * @return a streamInfo object
     * @throws SoundTransformException
     *             could not read the StreamInfo from the current inputstream
     */
    public abstract StreamInfo stopWithStreamInfo () throws SoundTransformException;

    /**
     * Tells the client to add an observer that will be notified of different
     * kind of updates from the library. It is ok to call withAnObserver several
     * times.<br/>
     * If the andAfterStart method is called, the subscribed observers are
     * removed
     *
     * @param observers1
     *            one or more observer(s)
     * @return the client, ready to start
     */
    public abstract FluentClientReady withAnObserver (Observer... observers1);

    /**
     * Tells the client to use the sounds passed in parameter by mixing them all
     * into one
     *
     * @param sounds
     *            a var-arg value of arrays of sounds (each value inside the
     *            arrays is a sound channel)
     * @return the client, with an imported sound
     * @throws SoundTransformException
     *             the sound files are invalid
     */
    public abstract FluentClientSoundImported withAMixedSound (Sound []... sounds) throws SoundTransformException;

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
    public abstract FluentClientInterface withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    /**
     * Tells the client to work with a pack. Uses the context object to find the
     * resource from the R object passed in parameter
     *
     * @param context
     *            the Android context (should be an instance of
     *            `android.content.Context`, but left as Object so the
     *            FluentClient can be used in a non-android project)
     * @param rClass
     *            R.raw.getClass () (either from soundtransform or from your
     *            pack) should be passed in parameter
     * @param packJsonId
     *            the id value of your json pack file (should be a field inside
     *            R.raw)
     * @return the client, ready to start
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    public abstract FluentClientReady withAPack (String packName, Object context, Class<?> rClass, int packJsonId) throws SoundTransformException;

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
     * If a note (one of the records inside the `instrumentName` structure) does
     * not own any detail, it will be obtained by digging in the file samples,
     * and can take a really long time. It is advisable to fill in the details
     * in each note.
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
    public abstract FluentClientInterface withAPack (String packName, String jsonContent) throws SoundTransformException;

    /**
     * Tells the client to work first with an InputStream. It will not be read
     * yet<br/>
     * The passed inputStream must own a format metadata object. Therefore it
     * must be an AudioInputStream.
     *
     * @param ais
     *            the input stream
     * @return the client, with an input stream
     */
    public abstract FluentClientWithInputStream withAudioInputStream (InputStream ais);

    /**
     * Tells the client to work first with a classpath resource. It will be
     * converted in a File
     *
     * @param resource
     *            a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException
     *             the classpath resource was not found
     */
    public abstract FluentClientWithFile withClasspathResource (String resource) throws SoundTransformException;

    /**
     * Tells the client to work first with a file. It will not be read yet
     *
     * @param file
     *            source file
     * @return the client, with a file
     */
    public abstract FluentClientWithFile withFile (File file1);

    /**
     * Tells the client to work first with a loudest frequencies float array. It
     * will not be used yet
     *
     * @param freqs1
     *            the loudest frequencies integer array
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs withFreqs (float [] freqs1);

    /**
     * Tells the client to work first to open the microphone and to record a
     * sound The result will be of an InputStream type The recording time will
     * be the one passed in the streamInfo
     *
     * @param streamInfo
     *            the future input stream info
     * @return the client, with an input stream
     * @throws SoundTransformException
     *             the mic could not be read, the recorder could not start, or
     *             the buffer did not record anything
     */
    public abstract FluentClientWithInputStream withLimitedTimeRecordedInputStream (StreamInfo streamInfo) throws SoundTransformException;

    /**
     * Tells the client to work first with a byte array InputStream or any
     * readable DataInputStream. It will be read and transformed into an
     * AudioInputStream<br/>
     * The passed inputStream must not contain any metadata piece of
     * information.
     *
     * @param is
     *            the input stream
     * @param isInfo
     *            the stream info
     * @return the client, with an input stream
     * @throws SoundTransformException
     *             the input stream cannot be read, or the conversion did not
     *             work
     */
    public abstract FluentClientWithInputStream withRawInputStream (InputStream is, StreamInfo isInfo) throws SoundTransformException;

    /**
     * Tells the client to work first to open the microphone and to record a
     * sound The result will be of an InputStream type The frameLength in the
     * streamInfo will be ignored
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
    public abstract FluentClientWithInputStream withRecordedInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException;

    /**
     * Tells the client to work first with a sound object
     *
     * @param sound
     *            the sound object
     * @return the client, with an imported sound
     */
    public abstract FluentClientSoundImported withSounds (Sound [] sound);

    /**
     * Tells the client to work first with a spectrum formatted sound.<br/>
     * The spectrums inside must be in a list (each item must correspond to a
     * channel) The spectrums are ordered in an array in chronological order
     *
     * @param spectrums
     *            the spectrums
     * @return the client, with the spectrums
     */
    public abstract FluentClientWithSpectrums withSpectrums (List<Spectrum<Serializable> []> spectrums);

    /**
     * Writes the current InputStream in a classpath resource in the same folder
     * as a previously imported classpath resource. Caution : if no classpath
     * resource was imported before, this operation will not work. Use
     * writeToClasspathResourceWithSiblingResource instead
     *
     * @param resource
     *            a classpath resource.
     * @return the client, with a file
     * @throws SoundTransformException
     *             there is no predefined classpathresource directory, or the
     *             file could not be written
     */
    public abstract FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException;

    /**
     * Writes the current InputStream in a classpath resource in the same folder
     * as a the sibling resource.
     *
     * @param resource
     *            a classpath resource that may or may not exist yet
     * @param siblingResource
     *            a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException
     *             no such sibling resource, or the file could not be written
     */
    public abstract FluentClientWithFile writeToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Writes the current InputStream in a file
     *
     * @param file
     *            the destination file
     * @return the client, with a file
     * @throws SoundTransformException
     *             The file could not be written
     */
    public abstract FluentClientWithFile writeToFile (File file) throws SoundTransformException;

}