package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public interface FluentClientReady extends FluentClientCommon {

    /**
     * Stops the client pipeline and returns the pack whose title is in
     * parameter
     *
     * @param title
     *            the title of the pack
     * @return a pack object
     */
    @Override
    Pack stopWithAPack (String title);

    /**
     * Stops the client pipeline and returns the currently subscribed observers
     *
     * @return the observers
     */
    @Override
    Observer [] stopWithObservers ();

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
    <T extends FluentClientCommon> FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, T... clients) throws SoundTransformException;

    /**
     * Alias for the inParallel method using a list of sounds
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
    FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, Sound... sounds) throws SoundTransformException;

    /**
     * Alias for the inParallel method using a list of inputStreams
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
    FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, InputStream... inputStreams) throws SoundTransformException;

    /**
     * Alias for the inParallel method using a list of files
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
     * Alias for the inParallel method using a list of freqs
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
    FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, List<float []>... freqs) throws SoundTransformException;

    /**
     * Alias for the inParallel method using a list of classpathResources
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
     * Tells the client to add an observer that will be notified of different
     * kind of updates from the library. It is ok to call withAnObserver several
     * times.<br/>
     * If the andAfterStart method is called, the subscribed observers are
     * removed
     *
     * @param observers
     *            one or more observer(s)
     * @return the client, ready to start
     */
    FluentClientReady withAnObserver (Observer... observers);

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
    FluentClientSoundImported withAMixedSound (Sound... sounds) throws SoundTransformException;

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
    FluentClientReady withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

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
    FluentClientReady withAPack (String packName, Object context, Class<?> rClass, int packJsonId) throws SoundTransformException;

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
    FluentClientReady withAPack (String packName, String jsonContent) throws SoundTransformException;

    /**
     * Tells the client to work first with an InputStream. It will not be read
     * yet<br/>
     * The passed inputStream must own a format metadata object. Therefore it
     * must be an AudioInputStream.
     *
     * @param inputStream
     *            the input stream
     * @return the client, with an input stream
     */
    FluentClientWithInputStream withAudioInputStream (InputStream inputStream);

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
    FluentClientWithFile withClasspathResource (String resource) throws SoundTransformException;

    /**
     * Tells the client to work first with a file. It will not be read yet
     *
     * @param file
     *            source file
     * @return the client, with a file
     */
    FluentClientWithFile withFile (File file);

    /**
     * Tells the client to work first with a loudest frequencies integer array.
     * It will not be used yet
     *
     * @param freqs
     *            the loudest frequencies float array
     * @return the client, with a loudest frequencies float array
     */
    FluentClientWithFreqs withFreqs (List<float []> freqs);

    /**
     * Tells the client to open the microphone and to record a
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
    FluentClientWithInputStream withLimitedTimeRecordedInputStream (final StreamInfo streamInfo) throws SoundTransformException;

    /**
     * Tells the client to work first with a byte array InputStream or any
     * readable DataInputStream. It will be read and transformed into an
     * AudioInputStream<br/>
     * The passed inputStream must not contain any metadata piece of
     * information.
     *
     * @param inputStream
     *            the input stream
     * @param streamInfo
     *            the stream info
     * @return the client, with an input stream
     * @throws SoundTransformException
     *             the input stream cannot be read, or the conversion did not
     *             work
     */
    FluentClientWithInputStream withRawInputStream (InputStream inputStream, StreamInfo streamInfo) throws SoundTransformException;

    /**
     * Tells the client to open the microphone and to record a
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
    FluentClientWithInputStream withRecordedInputStream (final StreamInfo streamInfo, Object stop) throws SoundTransformException;

    /**
     * Tells the client to work first with a sound object
     *
     * @param sound
     *            the sound object
     * @return the client, with an imported sound
     */
    FluentClientSoundImported withSound (Sound sound);

    /**
     * Tells the client to work first with a spectrum formatted sound.<br/>
     * The spectrums inside must be in a list (each item must correspond to a
     * channel) The spectrums are ordered in an array in chronological order
     *
     * @param spectrums
     *            the spectrums
     * @return the client, with the spectrums
     */
    FluentClientWithSpectrums withSpectrums (List<Spectrum<Serializable> []> spectrums);

}
