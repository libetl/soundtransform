package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Silence;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

/**
 * Create a sound with notes matching the input sound loudest frequencies. It
 * uses a soundtransform to get the loudest frequencies, then it shapes a sound
 * consisting of the notes heard in the freqs array. If the constructor using a
 * float array is used, only the shaping step will be processed
 */
public class ShapeSoundTransform extends AbstractLogAware<ShapeSoundTransform> implements SoundTransform<float [], Channel> {
    public enum ShapeSoundTransformErrorCode implements ErrorCode {

        NO_LOUDEST_FREQS_IN_ATTRIBUTE ("No loudest freqs array passed in attribute"), NOT_AN_INSTRUMENT ("%1s is not a valid instrument"), NO_PACK_IN_PARAMETER ("No pack in parameter. Please instantiate a ShapeSoundTransform with a not null Pack");

        private final String messageFormat;

        ShapeSoundTransformErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public enum ShapeSoundTransformEventCode implements EventCode {

        NOTE_FOUND (LogLevel.VERBOSE, "Note (%1dHz) between %2d/%3d and %4d/%5d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        ShapeSoundTransformEventCode (final LogLevel ll, final String mF) {
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

    private final Pack          pack;
    private final String        instrument;
    private final SoundAppender soundAppender;
    private final Silence       silence;
    private float []            freqs;
    private final FormatInfo    formatInfo;

    /**
     * Default Constructor
     *
     * @param packName
     *            Pack name, should be already imported
     * @param instrument
     *            instrument of the pack which will be used to shape the sound
     * @param freqs
     *            the loudest freqs array
     * @param formatInfo1
     *            the format info
     */
    public ShapeSoundTransform (final String packName, final String instrument, final FormatInfo formatInfo1) {
        this.silence = new Silence ();
        this.pack = $.select (Library.class).getPack (packName);
        this.instrument = instrument;
        this.soundAppender = $.select (SoundAppender.class);
        this.formatInfo = formatInfo1;
    }

    private Note findNote (final double lastFreq, final int sampleRate, final int i, final int lastBegining) throws SoundTransformException {
        Note note = this.silence;
        if (lastFreq > 50 && Math.abs (sampleRate - lastFreq) > 100) {
            this.log (new LogEvent (ShapeSoundTransformEventCode.NOTE_FOUND, (int) lastFreq, lastBegining, this.freqs.length, i, this.freqs.length));
            if (!this.pack.containsKey (this.instrument)) {
                throw new SoundTransformException (ShapeSoundTransformErrorCode.NOT_AN_INSTRUMENT, new NullPointerException (), this.instrument);
            }
            note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
        }
        return note;
    }

    private boolean freqHasChanged (final float freq1, final float freq2) {
        return Math.abs (freq1 - freq2) > freq1 * 5.0 / 100;
    }

    private boolean isNewNote (final int i, final float lastFreq, final boolean firstNote) {
        final boolean freqChangedAtI = this.freqHasChanged (this.freqs [i - 1], this.freqs [i]);
        final boolean freqChangedAtIMinusOne = this.freqHasChanged (this.freqs [i - 2], this.freqs [i - 1]);
        final boolean freqChangedAtIMinusTwo = this.freqHasChanged (this.freqs [i - 3], this.freqs [i - 2]);
        final boolean freqChangedAtIMinusThree = this.freqHasChanged (this.freqs [i - 4], this.freqs [i - 3]);
        final boolean freqChangedFromLastNote = this.freqHasChanged (this.freqs [i], lastFreq);
        final boolean frequencyDidNotChangeBetweenIAndIMinusTwo = !freqChangedAtI && !freqChangedAtIMinusOne && !freqChangedAtIMinusTwo;

        final boolean thereIsANewFrequencyValue = !freqChangedFromLastNote || firstNote;

        return frequencyDidNotChangeBetweenIAndIMinusTwo && freqChangedAtIMinusThree && thereIsANewFrequencyValue;
    }

    private Channel transform (final int step, final int channelNum, final int soundLength) throws SoundTransformException {
        final Channel builtSound = new Channel (new long [soundLength], this.formatInfo, channelNum);
        int lastBegining = 0;
        float lastFreq = 0;
        boolean firstNote = true;
        for (int i = 4 ; i < this.freqs.length ; i++) {
            if (i == this.freqs.length - 1 || this.isNewNote (i, lastFreq, firstNote)) {
                final int endOfNoteIndex = i == this.freqs.length - 1 ? i : i - 4;
                final float lengthInSeconds = (endOfNoteIndex - lastBegining < 1 ? this.freqs [i] * step : (endOfNoteIndex - 1 - lastBegining) * step * 1.0f) / this.formatInfo.getSampleRate ();
                final Note note = this.findNote (this.freqs [endOfNoteIndex], (int) this.formatInfo.getSampleRate (), endOfNoteIndex + 1, lastBegining + 1);
                this.soundAppender.appendNote (builtSound, note, this.freqs [endOfNoteIndex], step * lastBegining, channelNum, lengthInSeconds);
                lastBegining = endOfNoteIndex;
                lastFreq = this.freqs [endOfNoteIndex];
                firstNote = false;
            }
        }

        this.freqs = null;
        return builtSound;
    }

    @Override
    public Channel transform (final float [] freqs1) throws SoundTransformException {
        if (this.pack == null) {
            throw new SoundTransformException (ShapeSoundTransformErrorCode.NO_PACK_IN_PARAMETER, new NullPointerException ());
        }
        final int step = 100;
        final int channelNum = 0;
        int soundLength = 0;

        if (freqs1 == null) {
            throw new SoundTransformException (ShapeSoundTransformErrorCode.NO_LOUDEST_FREQS_IN_ATTRIBUTE, new NullPointerException ());
        }
        this.freqs = freqs1.clone ();
        if (soundLength == 0) {
            soundLength = step * this.freqs.length;
        }
        return this.transform (step, channelNum, soundLength);
    }

}
