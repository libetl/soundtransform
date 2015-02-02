package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithFile extends FluentClientCommon {

    /**
     * Shortcut for importToStream ().importToSound () : Conversion from a File
     * to a Sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if one of the two import fails
     */
    FluentClientSoundImported convertIntoSound () throws SoundTransformException;

    /**
     * Opens the current file and convert it into an InputStream, ready to be
     * read (or to be written to a file)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             the current file is not valid, or the conversion did not work
     */
    FluentClientWithInputStream importToStream () throws SoundTransformException;

    /**
     * Plays the current audio data and convert it temporarily into a sound
     *
     * @return the client, with a file
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    FluentClientWithFile playIt () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained file
     *
     * @return a file
     */
    File stopWithFile ();

    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.
     *
     * @param packName
     *            the name of the pack
     * @param jsonStream
     *            the input stream
     * @return the client, with a file
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    FluentClientWithFile withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.<br/>
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
     * @return the client, with a file
     * @throws SoundTransformException
     *             the json content is invalid, the json format is not correct,
     *             or some sound files are missing
     */
    FluentClientWithFile withAPack (String packName, String jsonContent) throws SoundTransformException;
}
