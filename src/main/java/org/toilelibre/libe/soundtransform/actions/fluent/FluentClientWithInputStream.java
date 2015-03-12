package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithInputStream extends FluentClientCommon {

    /**
     * Uses the current input stream object to convert it into a sound (with one
     * or more channels)
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             the inputStream is invalid, or the convert did not work
     */
    FluentClientSoundImported importToSound () throws SoundTransformException;

    /**
     * Plays the current audio data and convert it temporarily into a sound
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    FluentClientWithInputStream playIt () throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained input stream
     *
     * @return an input stream
     */
    InputStream stopWithInputStream ();

    /**
     * Stops the client pipeline and returns the obtained stream info
     * object
     *
     * @return a streamInfo object
     * @throws SoundTransformException
     *             could not read the StreamInfo from the current
     *             inputstream
     */
    FormatInfo stopWithStreamInfo () throws SoundTransformException;

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
    FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException;

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
    FluentClientWithFile writeToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException;

    /**
     * Writes the current InputStream in a file
     *
     * @param file
     *            the destination file
     * @return the client, with a file
     * @throws SoundTransformException
     *             The file could not be written
     */
    FluentClientWithFile writeToFile (File file) throws SoundTransformException;
}
