package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface BuildableFluentClientOperationSoundImported extends FluentClientSoundImported, BuildableFluentClientOperation {

    /**
     * Appends the sound passed in parameter to the current sound stored in the
     * client
     *
     * @param sound
     *            the sound to append the current sound to
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the sound is null or if there is a problem with the
     *             appending
     */
    @Override
    BuildableFluentClientOperationSoundImported append (Sound sound) throws SoundTransformException;

    /**
     * Applies one transform and continues with the result sound
     *
     * @param st
     *            the SoundTransform to apply
     * @return the client with a sound imported
     * @throws SoundTransformException
     *             if the transform does not work
     */
    @Override
    BuildableFluentClientOperationSoundImported apply (SoundTransform<Channel, Channel> st) throws SoundTransformException;

    /**
     * Applies one transform and stops immediately after with a result
     *
     * @param st
     *            the SoundTransform to apply
     *
     * @param <T>
     *            the output type of the transform and the array component type
     *            of the returned value
     *
     * @return a result in the expected kind
     * @throws SoundTransformException
     *             if the transform does not work
     */
    @Override
    <T> T [] applyAndStop (SoundTransform<Channel, T> st) throws SoundTransformException;

    /**
     * Changes the current imported sound to fit the expected format
     *
     * @param formatInfo
     *            the new expected format
     * @return the client, with a sound imported
     */
    @Override
    BuildableFluentClientOperationSoundImported changeFormat (FormatInfo formatInfo);

    /**
     * Splices a part of the sound between the sample #start and the sample #end
     *
     * @param start
     *            the first sample to cut
     * @param end
     *            the last sample to cut
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the indexes are out of bound
     */
    @Override
    BuildableFluentClientOperationSoundImported cutSubSound (int start, int end) throws SoundTransformException;

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
    @Override
    BuildableFluentClientOperationWithFile exportToClasspathResource (String resource) throws SoundTransformException;

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
    @Override
    BuildableFluentClientOperationWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Shortcut for exportToStream ().writeToFile (file)
     *
     * @param file
     *            the destination file
     * @return the client, with a file written
     * @throws SoundTransformException
     *             if one of the two operations fails
     */
    @Override
    BuildableFluentClientOperationWithFile exportToFile (File file) throws SoundTransformException;

    /**
     * Uses the current imported sound and converts it into an InputStream,
     * ready to be written to a file (or to be read again)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             if the metadata format object is invalid, or if the sound
     *             cannot be converted
     */
    @Override
    BuildableFluentClientOperationWithInputStream exportToStream () throws SoundTransformException;

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
    @Override
    BuildableFluentClientOperationSoundImported extractSubSound (int start, int end) throws SoundTransformException;

    /**
     * Will invoke a soundtransform to find the loudest frequencies of the
     * sound, chronologically Caution : the original sound will be lost, and it
     * will be impossible to revert this conversion. When shaped into a sound,
     * the new sound will only sounds like the instrument you shaped the freqs
     * with
     *
     * @return the client, with a loudest frequencies integer array
     * @throws SoundTransformException
     *             if the convert fails
     */
    @Override
    BuildableFluentClientOperationWithFreqs findLoudestFrequencies () throws SoundTransformException;

    /**
     * Will invoke a soundtransform to find the loudest frequencies of the
     * sound, chronologically Caution : the original sound will be lost, and it
     * will be impossible to revert this conversion. When shaped into a sound,
     * the new sound will only sounds like the instrument you shaped the freqs
     * with
     *
     * @param peakFindSoundTransform
     *            a sound transform whose role is to find the loudest freqs
     *            array
     *
     * @return the client, with a loudest frequencies integer array
     * @throws SoundTransformException
     *             if the convert fails
     */
    @Override
    BuildableFluentClientOperationWithFreqs findLoudestFrequencies (PeakFindSoundTransform<?, ?> peakFindSoundTransform) throws SoundTransformException;

    /**
     * Extract a part of the sound between the sample #start and the sample #end
     *
     * @param length
     *            the number of samples of the result sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the length is not positive
     */
    @Override
    BuildableFluentClientOperationSoundImported loop (int length) throws SoundTransformException;

    /**
     * Converts a stereo sound into a mono sound with the channels mixed
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the sound is null or if the sound is already mono
     */
    @Override
    BuildableFluentClientOperationSoundImported mergeChannels () throws SoundTransformException;

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
    @Override
    BuildableFluentClientOperationSoundImported mixWith (Sound sound) throws SoundTransformException;

    /**
     * Plays the current audio data
     *
     * @return the client, with a sound
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    @Override
    BuildableFluentClientOperationSoundImported playIt () throws SoundTransformException;

    /**
     * Uses the current sound to pick its spectrums and set that as the current
     * data in the pipeline
     *
     * @return the client, with the spectrums
     * @throws SoundTransformException
     *             could not convert the sound into some spectrums
     */
    @Override
    BuildableFluentClientOperationWithSpectrums splitIntoSpectrums () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained sound
     *
     * @return a sound value object
     */
    @Override
    Sound stopWithSound ();
}
