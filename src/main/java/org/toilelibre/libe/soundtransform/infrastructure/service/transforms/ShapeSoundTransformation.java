package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Silence;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class ShapeSoundTransformation implements SoundTransformation, LogAware<ShapeSoundTransformation> {
    public enum ShapeSoundTransformationErrorCode implements ErrorCode {

        NOT_AN_INSTRUMENT ("%1s is not a valid instrument");

        private final String messageFormat;

        ShapeSoundTransformationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private Observer []         observers;
    private final Pack          pack;
    private final String        instrument;
    private final SoundAppender soundAppender;
    private final Silence       silence;
    private int []              freqs;

    public ShapeSoundTransformation (final Pack pack, final String instrument) {
        this.silence = new Silence ();
        this.pack = pack;
        this.instrument = instrument;
        this.soundAppender = $.select (SoundAppender.class);
    }

    public ShapeSoundTransformation (final Pack pack, final String instrument, final int [] freqs) {
        this (pack, instrument);
        this.freqs = freqs;
    }

    private Note findNote (final double lastFreq, final int sampleRate, final int i, final int lastBegining) throws SoundTransformException {
        Note note = this.silence;
        if (lastFreq > 50 && Math.abs (sampleRate - lastFreq) > 100) {
            this.log (new LogEvent (LogLevel.VERBOSE, "Note (" + lastFreq + "Hz) between " + lastBegining + "/" + this.freqs.length + " and " + i + "/" + this.freqs.length));
            if (!this.pack.containsKey (this.instrument)) {
                throw new SoundTransformException (ShapeSoundTransformationErrorCode.NOT_AN_INSTRUMENT, new NullPointerException (), this.instrument);
            }
            note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
        }
        return note;
    }

    private int [] getLoudestFreqs (final Sound sound, final int threshold) {
        final PeakFindWithHPSSoundTransformation peak = $.create (PeakFindWithHPSSoundTransformation.class, threshold, -1);
        peak.setObservers (this.observers);
        peak.transform (sound);
        return peak.getLoudestFreqs ();
    }

    @Override
    public void log (final LogEvent logEvent) {
        for (final Observer transformObserver : this.observers) {
            transformObserver.notify (logEvent);
        }
    }

    @Override
    public ShapeSoundTransformation setObservers (final Observer... observers1) {
        this.observers = observers1;
        return this;
    }

    public Sound transform (final int length, final int threshold, final int nbBytesPerSample, final int sampleRate, final int channelNum) throws SoundTransformException {
        final Sound builtSound = new Sound (new long [length], nbBytesPerSample, sampleRate, channelNum);
        int lastBegining = 0;
        int lastFreq = 0;
        boolean firstNote = true;
        for (int i = 4 ; i < this.freqs.length ; i++) {
            final boolean freqChanged = this.freqHasChanged (this.freqs [i - 1], this.freqs [i]);
            final boolean freqChangedBefore = this.freqHasChanged (this.freqs [i - 2], this.freqs [i - 1]);
            final boolean freqChangedBeforeBefore = this.freqHasChanged (this.freqs [i - 3], this.freqs [i - 2]);
            final boolean freqChangedBeforeBeforeBefore = this.freqHasChanged (this.freqs [i - 4], this.freqs [i - 3]);
            final boolean freqChangedFromLastNote = this.freqHasChanged (this.freqs [i], lastFreq);
            final boolean newNote = !freqChanged && !freqChangedBefore && !freqChangedBeforeBefore && freqChangedBeforeBeforeBefore && 
                    (!freqChangedFromLastNote || firstNote);
            if (i == this.freqs.length - 1 || newNote) {
                int endOfNoteIndex = i == this.freqs.length - 1 ? i : i - 4;
                final float lengthInSeconds = (endOfNoteIndex - lastBegining < 1 ? this.freqs [i] * threshold : (endOfNoteIndex - 1 - lastBegining) * threshold * 1.0f) / sampleRate;
                final Note note = this.findNote (freqs [endOfNoteIndex], sampleRate, endOfNoteIndex + 1, lastBegining + 1);
                this.soundAppender.appendNote (builtSound, note, freqs [endOfNoteIndex], threshold * lastBegining, channelNum, lengthInSeconds);
                lastBegining = endOfNoteIndex;
                lastFreq = freqs [endOfNoteIndex];
                firstNote = false;
            }
        }

        this.freqs = null;
        return builtSound;
    }

    private boolean freqHasChanged (int freq1, int freq2) {
        return Math.abs (freq1 - freq2) > freq1 * 5.0 / 100;
    }

    @Override
    public Sound transform (final Sound sound) throws SoundTransformException {
        final int threshold = 100;
        final int channelNum = sound.getChannelNum ();

        this.log (new LogEvent (LogLevel.VERBOSE, "Finding loudest frequencies"));

        if (this.freqs == null) {
            this.freqs = this.getLoudestFreqs (sound, threshold);
        }
        return this.transform (sound.getSamples ().length, threshold, sound.getNbBytesPerSample (), sound.getSampleRate (), channelNum);
    }

}
