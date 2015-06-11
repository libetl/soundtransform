package org.toilelibre.libe.soundtransform.actions.fluent;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface BuildableFluentClientOperationWithFreqs extends FluentClientWithFreqs, BuildableFluentClientOperation {
    /**
     * Adjusts the loudest freqs array to match exactly the piano notes
     * frequencies
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    BuildableFluentClientOperationWithFreqs adjust ();

    /**
     * Compresses the loudest freq array (speedup or slowdown). When shaped into
     * a sound, the result will have a different tempo than the original sound
     * but will keep the same pitch
     *
     * @param factor
     *            the factor parameter quantifies how much the stretch or shrink
     *            will be. (i.e if factor = 0.5, then the result will be twice
     *            as long than the original)
     * @return the client, with a loudest frequencies float array
     */
    @Override
    BuildableFluentClientOperationWithFreqs compress (float factor);

    /**
     * Removes the values between low and high in the loudest freqs array
     * (replace them by 0)
     *
     * @param low
     *            low frequency (first one to avoid)
     * @param high
     *            high frequency (last one to avoid)
     * @return the client, with a loudest frequencies float array
     */
    @Override
    BuildableFluentClientOperationWithFreqs filterRange (float low, float high);

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
    BuildableFluentClientOperationWithFreqs insertPart (List<float []> subFreqs, int start);

    /**
     * Changes the loudest frequencies array to become one octave lower
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    BuildableFluentClientOperationWithFreqs octaveDown ();

    /**
     * Changes the loudest frequencies array to become one octave upper
     *
     * @return the client, with a loudest frequencies float array
     */
    @Override
    BuildableFluentClientOperationWithFreqs octaveUp ();

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
    @Override
    BuildableFluentClientOperationWithFreqs replacePart (List<float []> subFreqs, int start);

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
    @Override
    BuildableFluentClientOperationSoundImported shapeIntoSound (String packName, String instrumentName, FormatInfo formatInfo) throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     *
     * @return loudest frequencies array
     */
    @Override
    List<float []> stopWithFreqs ();

    /**
     * Changes the loudest frequencies so every value is between low and high
     *
     * @param low
     *            lowest frequency of the range
     * @param high
     *            highest frequency of the range
     *
     * @return the client, with a loudest frequencies float array
     *
     * @throws SoundTransformException
     *             can occur if low is greater than or equal to high
     */
    @Override
    BuildableFluentClientOperationWithFreqs surroundInRange (final float low, final float high) throws SoundTransformException;

}