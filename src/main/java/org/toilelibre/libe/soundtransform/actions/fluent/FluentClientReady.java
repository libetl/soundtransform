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

public interface FluentClientReady {

    /**
     * Stops the client pipeline and returns the pack whose title is in
     * parameter
     *
     * @param title
     *            the title of the pack
     * @return a pack object
     */
    Pack stopWithAPack (String title);

    /**
     * Stops the client pipeline and returns the currently subscribed observers
     *
     * @return the observers
     */
    Observer [] stopWithObservers ();

    <T extends FluentClientCommon> FluentClientReady inParallel (FluentClientOperation op, int timeoutInSeconds, T... clients) throws SoundTransformException;

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
     * @param is
     *            the input stream
     * @return the client, with an input stream
     */
    FluentClientWithInputStream withAudioInputStream (InputStream is);

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
    FluentClientWithFreqs withFreqs (float [] freqs);

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
    FluentClientWithInputStream withLimitedTimeRecordedInputStream (final StreamInfo streamInfo) throws SoundTransformException;

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
    FluentClientWithInputStream withRawInputStream (InputStream is, StreamInfo isInfo) throws SoundTransformException;

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
    FluentClientWithInputStream withRecordedInputStream (final StreamInfo streamInfo, Object stop) throws SoundTransformException;

    /**
     * Tells the client to work first with a sound object
     *
     * @param sounds
     *            the sound object
     * @return the client, with an imported sound
     */
    FluentClientSoundImported withSounds (Sound [] sounds);

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
