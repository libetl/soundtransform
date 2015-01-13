package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

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

	        NOT_AN_INSTRUMENT("%1s is not a valid instrument");

	        private final String    messageFormat;

	        ShapeSoundTransformationErrorCode (final String mF) {
	            this.messageFormat = mF;
	        }

	        @Override
	        public String getMessageFormat () {
	            return this.messageFormat;
	        }
	    }
    private Observer []            observers;
    private final Pack            pack;
    private final String        instrument;
    private final SoundAppender    soundAppender;
    private int []   freqs;

    public ShapeSoundTransformation (final Pack pack, final String instrument) {
        this.pack = pack;
        this.instrument = instrument;
        this.soundAppender = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender ();
    }
    
    public ShapeSoundTransformation (final Pack pack, final String instrument, int[] freqs) {
        this.pack = pack;
        this.instrument = instrument;
        this.soundAppender = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender ();
        this.freqs = freqs;
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

    @Override
    public Sound transform (final Sound sound) throws SoundTransformException {
        final int threshold = 100;
        final int channelNum = sound.getChannelNum ();

        this.log (new LogEvent (LogLevel.VERBOSE, "Finding loudest frequencies"));

        if (this.freqs == null){
        	this.getLoudestFreqs (sound, threshold);
        }
        return this.transform (sound.getSamples ().length, threshold, sound.getNbBytesPerSample (), sound.getSampleRate (), channelNum);
    }

	public Sound transform (int length, int threshold, int nbBytesPerSample, int sampleRate, int channelNum) throws SoundTransformException {
        final Sound builtSound = new Sound (new long [length], nbBytesPerSample, sampleRate, channelNum);
        final Note silence = new Silence ();
        double lastFreq = freqs [0];
        int lastBegining = 0;
        int countZeros = 0;
        for (int i = 0; i < freqs.length; i++) {
            final float lengthInSeconds = (i - lastBegining < 1 ? freqs [i] * threshold : (i - 1 - lastBegining) * threshold * 1.0f) / sampleRate;
            final boolean freqChanged = Math.abs (freqs [i] - lastFreq) > freqs [i] / 100 && lengthInSeconds > 0.5;
            if (freqChanged && freqs [i]  == 0) {
                countZeros++;
            } else {
                countZeros = 0;
            }
            if (i == freqs.length - 1 || freqChanged && (lastFreq == 0 || freqs [i]  == 0 && countZeros >= 3)) {
                countZeros = 0;
                Note note = silence;
                if (lastFreq > 50 && Math.abs (sampleRate - lastFreq) > 100) {
                    this.log (new LogEvent (LogLevel.VERBOSE, "Note between " + lastBegining + "/" + freqs.length + " and " + i + "/" + freqs.length));
                    if (!this.pack.containsKey (this.instrument)){
                    	throw new SoundTransformException (ShapeSoundTransformationErrorCode.NOT_AN_INSTRUMENT, 
                    			new NullPointerException (), this.instrument);
                    }
                    note = this.pack.get (this.instrument).getNearestNote ((int) lastFreq);
                }
                if (lengthInSeconds < 0.6) {
                    final Sound sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds * 2);
                    this.soundAppender.append (builtSound, threshold * lastBegining, sustain);
                } else {
                    final Sound attack = note.getAttack ((int) lastFreq, channelNum, lengthInSeconds);
                    final Sound decay = note.getDecay ((int) lastFreq, channelNum, lengthInSeconds);
                    final Sound sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds);
                    final Sound release = note.getRelease ((int) lastFreq, channelNum, lengthInSeconds);
                    this.soundAppender.append (builtSound, threshold * lastBegining, attack, decay, sustain, release);
                }
                lastBegining = i;
                lastFreq = freqs [i] ;
            }
        }

        return builtSound;
    }

	private void getLoudestFreqs (Sound sound, int threshold) {
        final PeakFindWithHPSSoundTransformation peak = new PeakFindWithHPSSoundTransformation (threshold, -1);
        peak.setObservers (this.observers);
        peak.transform (sound);
        freqs = peak.getLoudestFreqs ();
    }

}
