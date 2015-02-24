package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public interface FluentClientReady {

    /**
     * Stops the client pipeline and returns the pack whose title is in parameter
     *
     * @param title the title of the pack
     * @return a pack object
     */
    Pack stopWithAPack (String title);
    
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
     * Tells the client to work with a pack. Reads the whole string content. A
     * pattern must be followed in the jsonContent to enable the import.<br/>
     *
     * Here is the format allowed in the file
     *
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
     *
     * Do not assign the same frequency for two notes in the same instrument. If
     * several notes must have their frequencies detected by the soundtransform
     * lib, set different negative values (-1, -2, -3, ...)
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
     * Tells the client to work first with a byte array InputStream or any
     * readable DataInputStream. It will be read and transformed into an
     * AudioInputStream<br/>
     * The passed inputStream must not contain any metadata piece of
     * information.
     *
     * @param is
     *            the input stream
     * @param isInfo
     *            the audio format (named "InputStreamInfo")
     * @return the client, with an input stream
     * @throws SoundTransformException
     *             the input stream cannot be read, or the conversion did not
     *             work
     */
    FluentClientWithInputStream withRawInputStream (InputStream is, InputStreamInfo isInfo) throws SoundTransformException;

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
    FluentClientWithSpectrums withSpectrums (List<Spectrum<?> []> spectrums);
}
