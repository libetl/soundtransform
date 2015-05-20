package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithParallelizedClients extends FluentClientCommon {

    /**
     * Uses the sounds inside the nested clients to mix them all and to produce
     * a single sound
     *
     * @return the client, with a sound imported
     * @throws SoundTransformException
     *             if the nested clients are not in the Sound imported state
     */
    FluentClientSoundImported mixAllInOneSound () throws SoundTransformException;

    /**
     * Stops the client pipeline and get all the values inside each nested
     * client
     *
     * @param resultClass
     *            You have to specify what type of result you expect. the value
     *            can be one of this list : (Sound.class, InputStream.class,
     *            File.class, String.class, float [].class)
     *
     * @param <T> the array component type of the returned value
     *
     * @return an array of results
     */
    <T> T [] stopWithResults (Class<T> resultClass);
}
