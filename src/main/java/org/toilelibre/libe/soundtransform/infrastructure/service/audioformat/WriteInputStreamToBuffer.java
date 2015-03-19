package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class WriteInputStreamToBuffer {

    private static final int ARBITRARY_ARRAY_LENGTH = 16384;

    public WriteInputStreamToBuffer () {

    }

    public byte [] write (final InputStream is) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream ();

        int nRead;
        final byte [] data = new byte [WriteInputStreamToBuffer.ARBITRARY_ARRAY_LENGTH];
        while ((nRead = is.read (data, 0, data.length)) != -1) {
            buffer.write (data, 0, nRead);
        }

        buffer.flush ();

        return buffer.toByteArray ();

    }
}
