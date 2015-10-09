package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.EventCode;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

final class MagnitudeADSRHelper extends AbstractLogAware<MagnitudeADSRHelper> implements ADSRHelper {

    public enum MagnitudeADSRHelperEventCode implements EventCode {
        FOUND_EDGE (LogLevel.PARANOIAC, "Found an edge %1s");

        private final String   messageFormat;
        private final LogLevel logLevel;

        MagnitudeADSRHelperEventCode (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    @Override
    public int findDecay (final double [] magnitudeArray, final int attack) throws SoundTransformException {
        int decayIndex = attack;

        final double [] decayArray = new double [magnitudeArray.length - attack];
        System.arraycopy (magnitudeArray, attack, decayArray, 0, magnitudeArray.length - attack);
        try {
            MathArrays.checkOrder (decayArray, MathArrays.OrderDirection.INCREASING, true);
        } catch (final NonMonotonicSequenceException nmse) {
            this.log (new LogEvent (MagnitudeADSRHelperEventCode.FOUND_EDGE, nmse));
            decayIndex = nmse.getIndex () - 1;
        }
        return decayIndex;
    }

    @Override
    public int findRelease (final double [] magnitudeArray) throws SoundTransformException {
        int releaseIndexFromReversed = 0;
        final double [] reversed = new double [magnitudeArray.length];
        System.arraycopy (magnitudeArray, 0, reversed, 0, reversed.length);
        Collections.reverse (Arrays.asList (reversed));

        try {
            MathArrays.checkOrder (magnitudeArray, MathArrays.OrderDirection.INCREASING, true);
        } catch (final NonMonotonicSequenceException nmse) {
            this.log (new LogEvent (MagnitudeADSRHelperEventCode.FOUND_EDGE, nmse));
            releaseIndexFromReversed = nmse.getIndex () - 1;
        }
        return magnitudeArray.length - releaseIndexFromReversed;
    }

    @Override
    public int findSustain (final double [] magnitudeArray, final int decay) throws SoundTransformException {
        int sustainIndex = decay;

        final int start = decay;
        final double [] sustainArray = new double [magnitudeArray.length - start];
        System.arraycopy (magnitudeArray, start, sustainArray, 0, magnitudeArray.length - start);
        try {
            MathArrays.checkOrder (sustainArray, MathArrays.OrderDirection.DECREASING, true);
        } catch (final NonMonotonicSequenceException nmse) {
            this.log (new LogEvent (MagnitudeADSRHelperEventCode.FOUND_EDGE, nmse));
            sustainIndex = nmse.getIndex () - 1;
        }
        return Math.max (decay, sustainIndex);
    }
}
