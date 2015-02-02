package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.InputStream;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithSpectrums extends FluentClientCommon {

    /**
     * Uses the current imported sound and converts it into an InputStream,
     * ready to be written to a file (or to be read again)
     * 
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             if the metadata format object is invalid, or if the sound
     *             cannot be converted
     */
    FluentClientSoundImported extractSound () throws SoundTransformException;

    /**
     * Plays the current audio data and (if needed) convert it temporarily to a
     * sound
     * 
     * @return the client, in its current state.
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    FluentClientWithSpectrums playIt () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained spectrums
     * 
     * @return a list of spectrums for each channel
     */
    List<Spectrum<?> []> stopWithSpectrums ();

    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.
     * 
     * @param packName
     *            the name of the pack
     * @param jsonStream
     *            the input stream
     * @return the client, with the spectrums list
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    FluentClientWithSpectrums withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

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
     * @return the client, with the spectrums list
     * @throws SoundTransformException
     *             the json content is invalid, the json format is not correct,
     *             or some sound files are missing
     */
    FluentClientWithSpectrums withAPack (String packName, String jsonContent) throws SoundTransformException;
}
