package org.toilelibre.libe.soundtransform.model.library.note;

public class ComputedChordNote extends FormulaNote {

    @Override
    protected float applyFormula (final int j, final float frequency, final float sampleRate) {
        return (float) (this.sinOnceOtherXTimes (j, frequency, sampleRate, 12, 0) * 0.3 + this.sinOnceOtherXTimes (j, frequency, sampleRate, 12, 6) * 0.2 + this.sinOnceOtherXTimes (j, frequency, sampleRate, 1, 0) * 0.4);
    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

    private float sinOnceOtherXTimes (final int j, final float frequency, final float sampleRate, final int xTimes, final int modulo) {
        return (float) (Math.round (j * frequency / sampleRate) % xTimes == modulo ? Math.sin (j * frequency * 2 * Math.PI / sampleRate) : 0);
    }

}
