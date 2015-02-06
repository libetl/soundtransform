package org.toilelibre.libe.soundtransform.model.exception;

public class SoundTransformRuntimeException extends RuntimeException {
    enum RuntimeErrorCode implements ErrorCode {
        RUNTIME_ERROR ("Runtime error");

        private String messageFormat;

        RuntimeErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    /**
     *
     */
    private static final long serialVersionUID = 2154130846010299931L;
    private final ErrorCode   errorCode;
    private final Object []   args;

    public SoundTransformRuntimeException (final ErrorCode errorCode, final Exception cause, final Object... args) {
        super (cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public SoundTransformRuntimeException (final SoundTransformException cause) {
        super (cause);
        this.errorCode = RuntimeErrorCode.RUNTIME_ERROR;
        this.args = new Object [0];
    }

    @Override
    public String getMessage () {
        return String.format (this.errorCode.getMessageFormat (), this.args);
    }

    public ErrorCode getErrorCode () {
        return this.errorCode;
    }
}
