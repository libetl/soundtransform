package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.ADSREnveloppeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class MagnitudeADSRHelper extends AbstractLogAware<MagnitudeADSRHelper> implements ADSRHelper {

    public enum MagnitudeADSRHelperEventCode implements EventCode {
        FOUND_EDGE(LogLevel.PARANOIAC, "Found an edge %1s");

        private final String messageFormat;
        private final LogLevel logLevel;

        MagnitudeADSRHelperEventCode(final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public LogLevel getLevel() {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    private static final int ACCURATE_STEP_FOR_ADSR_HELPER = 100;

    private double[] magnitude = null;

    @Override
    public int findDecay(final Sound channel1, final int attack) throws SoundTransformException {
        int decayIndex = attack;
        this.ensureComputedMagnitudeArray(channel1, MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER);

        final double[] decayArray = new double[magnitude.length - attack];
        System.arraycopy(magnitude, attack, decayArray, 0, magnitude.length - attack);
        try {
            MathArrays.checkOrder(decayArray, MathArrays.OrderDirection.INCREASING, true);
        } catch (final NonMonotonicSequenceException nmse) {
            this.log(new LogEvent(MagnitudeADSRHelperEventCode.FOUND_EDGE, nmse));
            decayIndex = (nmse.getIndex() - 1) * MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER;
        }
        return decayIndex;
    }

    @Override
    public int findRelease(final Sound channel1) throws SoundTransformException {
        int releaseIndexFromReversed = 0;
        this.ensureComputedMagnitudeArray(channel1, MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER);
        final double[] reversed = new double[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, reversed, 0, reversed.length);
        Collections.reverse(Arrays.asList(reversed));

        try {
            MathArrays.checkOrder(magnitude, MathArrays.OrderDirection.INCREASING, true);
        } catch (final NonMonotonicSequenceException nmse) {
            this.log(new LogEvent(MagnitudeADSRHelperEventCode.FOUND_EDGE, nmse));
            releaseIndexFromReversed = (nmse.getIndex() - 1) * MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER;
        }
        return channel1.getSamplesLength() - releaseIndexFromReversed;
    }

    @Override
    public int findSustain(final Sound channel1, final int decay) throws SoundTransformException {
        int sustainIndex = decay;
        this.ensureComputedMagnitudeArray(channel1, MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER);

        final int start = decay / MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER;
        final double[] sustainArray = new double[magnitude.length - start];
        System.arraycopy(magnitude, start, sustainArray, 0, magnitude.length - start);
        try {
            MathArrays.checkOrder(sustainArray, MathArrays.OrderDirection.DECREASING, true);
        } catch (final NonMonotonicSequenceException nmse) {
            this.log(new LogEvent(MagnitudeADSRHelperEventCode.FOUND_EDGE, nmse));
            sustainIndex = (nmse.getIndex() - 1) * MagnitudeADSRHelper.ACCURATE_STEP_FOR_ADSR_HELPER;
        }
        return sustainIndex;
    }

    private void ensureComputedMagnitudeArray(final Sound sound, final int step) {
        if (this.magnitude == null) {
            final ADSREnveloppeSoundTransformation soundTransform = new ADSREnveloppeSoundTransformation(step);
            soundTransform.transform(sound);
            this.magnitude = soundTransform.getMagnitude();
        }
    }
}
