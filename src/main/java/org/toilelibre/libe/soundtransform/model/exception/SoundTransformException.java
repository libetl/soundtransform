package org.toilelibre.libe.soundtransform.model.exception;

public class SoundTransformException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 2154130846010299931L;
    private final ErrorCode   errorCode;
    private final Object []   args;

    public SoundTransformException (final ErrorCode errorCode, final Exception cause, final Object... args) {
        super (cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode (){
        return this.errorCode;
    }
    
    @Override
    public String getMessage () {
        return String.format (this.errorCode.getMessageFormat (), this.args);
    }
}
