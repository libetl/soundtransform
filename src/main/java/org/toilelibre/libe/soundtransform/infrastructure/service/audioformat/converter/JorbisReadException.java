package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

public class JorbisReadException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6967135552733918866L;

    public JorbisReadException (String detailMessage, Throwable throwable) {
        super (detailMessage, throwable);
    }

    public JorbisReadException (String detailMessage) {
        super (detailMessage);
    }

}
