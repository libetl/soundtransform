package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientInterface {

    /**
     * Adjusts the loudest freqs array to match exactly the piano notes
     * frequencies
     *
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs adjust ();

    /**
     * Start over the client : reset the state and the value objects nested in
     * the client
     *
     * @return the client, ready to start
     */
    public abstract FluentClientReady andAfterStart ();

    /**
     * Appends the sound passed in parameter to the current sound stored in the
     * client
     *
     * @param sound
     *            the sound to append the current sound to
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the sound is null or if there is a problem with the
     *             appending please ensure that both sounds have the same number
     *             of channels
     */
    public abstract FluentClientSoundImported append (Sound sound) throws SoundTransformException;

    /**
     * Applies one transform and continue with the result sound
     *
     * @param st
     *            the SoundTransform to apply
     * @return the client with a sound imported
     * @throws SoundTransformException
     *             if the transform does not work
     */
    public abstract FluentClientSoundImported apply (SoundTransform<Channel, Channel> st) throws SoundTransformException;

    /**
     * Applies one transform and stop immediately after with a result
     *
     * @param st
     *            the SoundTransform to apply
     * @param <T> the output type of the transform and the array component type of the returned value
     * @return a result in the expected kind
     * @throws SoundTransformException
     *             if the transform does not work
     */
    public abstract <T> T [] applyAndStop (SoundTransform<Channel, T> st) throws SoundTransformException;

    /**
     * Changes the current imported sound to fit the expected format
     *
     * @param formatInfo
     *            the new expected format
     * @return the client, with a sound imported
     */
    public abstract FluentClientSoundImported changeFormat (FormatInfo formatInfo);

    /**
     * Compresses the loudest freq array (speedup or slowdown) When shaped into
     * a sound, the result will have a different tempo than the original sound
     * but will keep the same pitch
     *
     * @param factor
     *            the factor parameter quantifies how much the stretch will be
     *            (i.e if factor = 0.5, then the result will be twice as long
     *            than the original)
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs compress (float factor);

    /**
     * Shortcut for importToStream ().importToSound () : Conversion from a File
     * to a Sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if one of the two import fails
     */
    public abstract FluentClientSoundImported convertIntoSound () throws SoundTransformException;

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
    public abstract FluentClientSoundImported cutSubSound (int start, int end) throws SoundTransformException;

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
    public abstract FluentClientWithFile exportToClasspathResource (String resource) throws SoundTransformException;

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
    public abstract FluentClientWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Shortcut for exportToStream ().writeToFile (file)
     *
     * @param file
     *            the destination file
     * @return the client, with a file written
     * @throws SoundTransformException
     *             if one of the two operations fails
     */
    public abstract FluentClientWithFile exportToFile (File file) throws SoundTransformException;

    /**
     * Uses the current imported sound and converts it into an InputStream,
     * ready to be written to a file (or to be read again)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             if the metadata format object is invalid, or if the sound
     *             cannot be converted
     */
    public abstract FluentClientWithInputStream exportToStream () throws SoundTransformException;

    /**
     * Uses the current available spectrums objects to convert them into a sound
     * (with one or more channels)
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the spectrums are in an invalid format, or if the
     *             transform to sound does not work
     */
    public abstract FluentClientSoundImported extractSound () throws SoundTransformException;

    /**
     * Extracts a part of the sound between the sample #start and the sample
     * #end
     *
     * @param start
     *            the first sample to extract
     * @param end
     *            the last sample to extract
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the indexes are out of bound
     */
    public abstract FluentClientSoundImported extractSubSound (int start, int end) throws SoundTransformException;

    /**
     * Removes the values between low and high in the loudest freqs array
     * (replace them by 0)
     *
     * @param low
     *            low frequency (first one to avoid)
     * @param high
     *            high frequency (last one to avoid)
     * @return the client, with a loudest frequencies float array
     * 
     * @throws SoundTransformException can occur if low is greater than or equal to high
     */
    public abstract FluentClientWithFreqs filterRange (float low, float high) throws SoundTransformException;

    /**
     * <p>Will invoke a soundtransform to find the loudest frequencies of the
     * sound, chronologically</p>
     * <p>Caution : the original sound will be lost, and it will be impossible to
     * revert this conversion.</p>
     * When shaped into a sound, the new sound will only sounds like the
     * instrument you shaped the freqs with
     *
     * @return the client, with a loudest frequencies float array
     * @throws SoundTransformException
     *             if the convert fails
     */
    public abstract FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException;

    /**
     * Uses the current input stream object to convert it into a sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             the inputStream is invalid, or the convert did not work
     */
    public abstract FluentClientSoundImported importToSound () throws SoundTransformException;

    /**
     * Opens the current file and convert it into an InputStream, ready to be
     * read (or to be written to a file)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             the current file is not valid, or the conversion did not work
     */
    public abstract FluentClientWithInputStream importToStream () throws SoundTransformException;

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
    public abstract FluentClientWithFreqs insertPart (List<float []> subFreqs, int start);

    /**
     * Extracts a part of the sound between the sample #start and the sample
     * #end
     *
     * @param length
     *            the number of samples of the result sound
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the length is not positive
     */
    public abstract FluentClientSoundImported loop (int length) throws SoundTransformException;

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
    public abstract FluentClientSoundImported mixWith (Sound sound) throws SoundTransformException;

    /**
     * Changes the loudest frequencies array to become one octave lower
     *
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs octaveDown ();

    /**
     * Changes the loudest frequencies array to become one octave upper
     *
     * @return the client, with a loudest frequencies float array
     */
    public abstract FluentClientWithFreqs octaveUp ();

    /**
     * Plays the current audio data and (if needed) converts it temporarily to a
     * sound
     *
     * @return the client, in its current state.
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    public abstract FluentClientInterface playIt () throws SoundTransformException;

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
    public abstract FluentClientWithFreqs replacePart (List<float []> subFreqs, int start);

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
    public abstract FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, FormatInfo formatInfo) throws SoundTransformException;

    /**
     * Uses the current sound to pick its spectrums and set that as the current
     * data in the pipeline
     *
     * @return the client, with the spectrums
     * @throws SoundTransformException
     *             could not convert the sound into some spectrums
     */
    public abstract FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException;
    
    /**
     * Changes the loudest frequencies so every value is between low and high
     * 
     * @param low lowest frequency of the range
     * @param high highest frequency of the range
     *
     * @return the client, with a loudest frequencies float array

     * @throws SoundTransformException can occur if low is greater than or equal to high
     */
    public abstract FluentClientWithFreqs surroundInRange (final float low, final float high) throws SoundTransformException;

    /**
     * Writes the current InputStream in a classpath resource in the same folder
     * as a previously imported classpath resource. Caution : if no classpath
     * resource was imported before, this operation will not work. Use
     * writeToClasspathResourceWithSiblingResource instead
     *
     * @param resource
     *            a classpath resource.
     * @return the client, with a file
     * @throws SoundTransformException
     *             there is no predefined classpathresource directory, or the
     *             file could not be written
     */
    public abstract FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException;

    /**
     * Writes the current InputStream in a classpath resource in the same folder
     * as a the sibling resource.
     *
     * @param resource
     *            a classpath resource that may or may not exist yet
     * @param siblingResource
     *            a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException
     *             no such sibling resource, or the file could not be written
     */
    public abstract FluentClientWithFile writeToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Writes the current InputStream in a file
     *
     * @param file
     *            the destination file
     * @return the client, with a file
     * @throws SoundTransformException
     *             The file could not be written
     */
    public abstract FluentClientWithFile writeToFile (File file) throws SoundTransformException;

}