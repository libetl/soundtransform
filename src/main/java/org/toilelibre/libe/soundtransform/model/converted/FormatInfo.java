package org.toilelibre.libe.soundtransform.model.converted;

import java.io.Serializable;
import java.util.Locale;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;

public class FormatInfo implements Cloneable, Serializable {
    enum FormatInfoErrorCode implements ErrorCode {
        CLONE_FAILED ("Clone operation on a format info object failed");

        private String messageFormat;

        FormatInfoErrorCode (final String mF) {
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
    private static final long serialVersionUID = 2807016563144431421L;
    private final int         sampleSize;

    private final float       sampleRate;

    public FormatInfo (final int sampleSize, final float sampleRate) {
        super ();
        this.sampleSize = sampleSize;
        this.sampleRate = sampleRate;
    }

    public float getSampleRate () {
        return this.sampleRate;
    }

    public int getSampleSize () {
        return this.sampleSize;
    }

    public boolean sameFormatAs (final FormatInfo fi) {
        return this.sampleRate == fi.sampleRate && this.sampleSize == fi.sampleSize;
    }

    @Override
    public String toString () {
        return String.format (Locale.ENGLISH, "%6.1f Hz, %1d bit", this.getSampleRate (), this.getSampleSize () * Byte.SIZE);
    }

}
