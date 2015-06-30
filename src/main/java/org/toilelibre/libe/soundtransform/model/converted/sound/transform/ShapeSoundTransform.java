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
 * shapes a sound consisting of the notes heard in the freqs array.
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

    private static final int THREE = 3;

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
     * @param instrument1
     *            instrument of the pack which will be used to shape the sound
     * @param formatInfo1
     *            the format info
     */
    public ShapeSoundTransform (final String packName, final String instrument1, final FormatInfo formatInfo1) {
        this.silence = new Silence ();
        this.pack = $.select (Library.class).getPack (packName);
        this.instrument = instrument1;
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

    private boolean freqHasChanged (final int index1, final int index2) {
        final float freq1 = index1 < 0 || index1 > this.freqs.length - 1 ? 0 : this.freqs [index1];
        final float freq2 = index2 < 0 || index2 > this.freqs.length - 1 ? 0 : this.freqs [index2];
        return this.freqHasChanged (freq1, freq2) || index1 > this.freqs.length - 1 || index2 > this.freqs.length - 1;
    }
    
    private boolean freqHasChanged (final float freq1, final float freq2) {
        return Math.abs (freq1 - freq2) > freq1 * 5.0 / 100;
    }


    private boolean noteHasChanged (final int i) {
        final boolean freqChangedAtI = this.freqHasChanged (i - 1,  i);
        final boolean freqChangedAtIMinusOne = this.freqHasChanged (i - 2,  i - 1);
        final boolean freqChangedAtIMinusTwo = this.freqHasChanged (i - 3,  i - 2);
        final boolean freqChangedAtIMinusThree = this.freqHasChanged (i - 4,  i - 3);
        final boolean frequencyDidNotChangeBetweenIAndIMinusTwo = !freqChangedAtI && !freqChangedAtIMinusOne && !freqChangedAtIMinusTwo;

        return frequencyDidNotChangeBetweenIAndIMinusTwo && freqChangedAtIMinusThree;
    }


    private int findNextNoteStart (final int startIndex) {
        int result = startIndex;
        while (result < this.freqs.length && !this.noteHasChanged (result)){
            result++;
        }
        return result;
    }

    private Channel transform (final int step, final int channelNum, final int soundLength) throws SoundTransformException {
        final Channel builtSound = new Channel (new long [soundLength], this.formatInfo, channelNum);
        int noteStart = 0;
        int noteEnd = 0;
        while (noteStart + ShapeSoundTransform.THREE  + 1 < this.freqs.length) {
            noteStart = this.findNextNoteStart (noteEnd) - ShapeSoundTransform.THREE;
            noteEnd = noteStart;
            while (!this.freqHasChanged (noteStart, noteEnd)) {
                noteEnd = this.findNextNoteStart (noteEnd + ShapeSoundTransform.THREE + 1) - ShapeSoundTransform.THREE;
            }
            noteEnd = noteEnd + 3 < this.freqs.length ? noteEnd : this.freqs.length - 1;
            final float lengthInSeconds = (noteEnd - 1 - noteStart) * step * 1.0f / this.formatInfo.getSampleRate ();
            final Note note = this.findNote (this.freqs [noteEnd - 1], (int) this.formatInfo.getSampleRate (), noteEnd, noteStart);
            this.soundAppender.appendNote (builtSound, note, this.freqs [noteEnd - 1], step * noteStart, channelNum, lengthInSeconds);
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
