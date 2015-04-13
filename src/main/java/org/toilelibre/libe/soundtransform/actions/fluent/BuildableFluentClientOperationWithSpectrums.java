package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.Serializable;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;



public interface BuildableFluentClientOperationWithSpectrums extends FluentClientWithSpectrums, BuildableFluentClientOperation {

    /**
     * Uses the current available spectrums objects to convert them into a sound
     * (with one or more channels)
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the spectrums are in an invalid format, or if the
     *             transform to sound does not work
     */
    BuildableFluentClientOperationSoundImported extractSound () throws SoundTransformException;

    /**
     * Plays the current audio data and (if needed) convert it temporarily to a
     * sound
     *
     * @return the client, with the spectrums list
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    BuildableFluentClientOperationWithSpectrums playIt () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained spectrums
     *
     * @return a list of spectrums for each channel
     */
    List<Spectrum<Serializable> []> stopWithSpectrums ();
}
