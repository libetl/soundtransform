package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;

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
    FluentClientSoundImported convertIntoSound() throws SoundTransformException;

    /**
     * Opens the current file and convert it into an InputStream, ready to be
     * read (or to be written to a file)
     *
     * @return the client, with an inputStream
     * @throws SoundTransformException
     *             the current file is not valid, or the conversion did not work
     */
    FluentClientWithInputStream importToStream() throws SoundTransformException;

    /**
     * Plays the current audio data and convert it temporarily into a sound
     *
     * @return the client, with a file
     * @throws SoundTransformException
     *             could not play the current audio data
     */
    FluentClientWithFile playIt() throws SoundTransformException;

    /**
     * Stops the client pipeline and returns the obtained file
     *
     * @return a file
     */
    File stopWithFile();

}
