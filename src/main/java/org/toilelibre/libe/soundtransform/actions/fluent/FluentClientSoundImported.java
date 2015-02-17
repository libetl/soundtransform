package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientSoundImported extends FluentClientCommon {

    /**
     * Apply one transform and continue with the current imported sound
     *
     * @param st
     *            the SoundTransformation to apply
     * @return the client with a sound imported
     * @throws SoundTransformException
     *             if the transform does not work
     */
    FluentClientSoundImported apply (SoundTransformation st) throws SoundTransformException;

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
    FluentClientWithFile exportToClasspathResource (String resource) throws SoundTransformException;

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
    FluentClientWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Shortcut for exportToStream ().writeToFile (file)
     *
     * @param file
     *            the destination file
     * @return the client, with a file written
     * @throws SoundTransformException
     *             if one of the two operations fails
     */
    FluentClientWithFile exportToFile (File file) throws SoundTransformException;

    /**
     * Uses the current imported sound and converts it into an InputStream,
     * ready to be written to a file (or to be read again)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             if the metadata format object is invalid, or if the sound
     *             cannot be converted
     */
    FluentClientWithInputStream exportToStream () throws SoundTransformException;

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
    FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException;

    /**
     * Combines the current sound with another sound. The operation is not reversible
     * @return the client, with a sound imported
     * @throws SoundTransformException if the sound is null or if there is a problem with the mix
     */
    FluentClientSoundImported mixWith (Sound [] sound) throws SoundTransformException;
    
    /**
     * Plays the current audio data
     *
     * @return the client, with a sound
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    FluentClientSoundImported playIt () throws SoundTransformException;

    /**
     * Uses the current sound to pick its spectrums and set that as the current
     * data in the pipeline
     *
     * @return the client, with the spectrums
     * @throws SoundTransformException
     *             could not convert the sound into some spectrums
     */
    FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained sound
     *
     * @return a sound value object
     */
    Sound [] stopWithSounds ();

    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A
     * pattern must be followed in the jsonStream to enable the import.
     *
     * @param packName
     *            the name of the pack
     * @param jsonStream
     *            the input stream
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             the input stream cannot be read, or the json format is not
     *             correct, or some sound files are missing
     */
    FluentClientSoundImported withAPack (String packName, InputStream jsonStream) throws SoundTransformException;

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
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             the json content is invalid, the json format is not correct,
     *             or some sound files are missing
     */
    FluentClientSoundImported withAPack (String packName, String jsonContent) throws SoundTransformException;
}