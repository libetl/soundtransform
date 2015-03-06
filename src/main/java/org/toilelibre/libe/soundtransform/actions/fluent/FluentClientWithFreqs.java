package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithFreqs extends FluentClientCommon {

    /**
     * Adjust the loudest freqs array to match exactly the piano notes
     * frequencies
     *
     * @return the client, with a loudest frequencies float array
     */
    FluentClientWithFreqs adjust ();

    /**
     * Compresses the loudest freq array (speedup or slowdown). When shaped into
     * a sound, the result will have a different tempo than the original sound
     * but will keep the same pitch
     *
     * @param factor
     *            the factor parameter quantifies how much the stretch or shrink will be.
     *            (i.e if factor = 0.5, then the result will be twice as long than
     *            the original)
     * @return the client, with a loudest frequencies float array
     */
    FluentClientWithFreqs compress (float factor);

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
    FluentClientWithFreqs filterRange (float low, float high);

    /**
     * Changes the loudest frequencies array to become one octave lower
     *
     * @return the client, with a loudest frequencies float array
     */
    FluentClientWithFreqs octaveDown ();

    /**
     * Changes the loudest frequencies array to become one octave upper
     *
     * @return the client, with a loudest frequencies float array
     */
    FluentClientWithFreqs octaveUp ();

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
    FluentClientWithFreqs replacePart (float [] subFreqs, int start);

    /**
     * Shapes these loudest frequencies array into a sound and set the converted
     * sound in the pipeline
     *
     * @param packName
     *            reference to an existing imported pack (must be invoked before
     *            the shapeIntoSound method by using withAPack)
     * @param instrumentName
     *            the name of the instrument that will map the freqs object
     * @param fi
     *            the wanted format for the future sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             could not call the soundtransform to shape the freqs
     */
    FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, FormatInfo fi) throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     *
     * @return loudest frequencies array
     */
    float [] stopWithFreqs ();

    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.
     *
     * @param packName
     *            the name of the pack
     * @param jsonStream
     *            the input stream
     * @return the client, with the loudest frequencies float array
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    FluentClientWithFreqs withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

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
     * @return the client, with the loudest frequencies float array
     * @throws SoundTransformException
     *             the json content is invalid, the json format is not correct,
     *             or some sound files are missing
     */
    FluentClientWithFreqs withAPack (String packName, String jsonContent) throws SoundTransformException;
}
