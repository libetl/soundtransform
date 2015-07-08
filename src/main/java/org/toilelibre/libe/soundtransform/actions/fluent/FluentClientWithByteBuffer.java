package org.toilelibre.libe.soundtransform.actions.fluent;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FluentClientWithByteBuffer extends FluentClientCommon {

    /**
     * Reads the available bytes in the ByteBuffer to extract an InputStream for further operations.
     * Warn : this will not read the whole buffer if it is under processing.
     * The result inputStream will only save what the buffer contains at the precise moment of the call
     * 
     * @return a client, with an input stream
     * @throws SoundTransformException if the import into inputstream fails
     */
    FluentClientWithInputStream readBuffer () throws SoundTransformException;

}
