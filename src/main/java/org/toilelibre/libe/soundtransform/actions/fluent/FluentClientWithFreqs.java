package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public interface FluentClientWithFreqs extends FluentClientCommon {

    /**
     * Shapes these loudest frequencies array into a sound and set the converted
     * sound in the pipeline
     *
     * @param packName
     *            reference to an existing imported pack (must be invoked before
     *            the shapeIntoSound method by using withAPack)
     * @param instrumentName
     *            the name of the instrument that will map the freqs object
     * @param isi
     *            the wanted format for the future sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             could not call the soundtransform to shape the freqs
     */
    FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, InputStreamInfo isi) throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     *
     * @return loudest frequencies array
     */
    int [] stopWithFreqs ();

    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.
     *
     * @param packName
     *            the name of the pack
     * @param jsonStream
     *            the input stream
     * @return the client, with the loudest frequencies integer array
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    FluentClientWithFreqs withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

    /**
     * Tells the client to work with a pack. Reads the whole string content. A pattern must be followed in the jsonContent to
     * enable the import.<br/>
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
     * @return the client, with the loudest frequencies integer array
     * @throws SoundTransformException
     *             the json content is invalid, the json format is not correct,
     *             or some sound files are missing
     */
    FluentClientWithFreqs withAPack (String packName, String jsonContent) throws SoundTransformException;
}
