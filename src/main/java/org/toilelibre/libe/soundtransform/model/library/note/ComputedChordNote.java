package org.toilelibre.libe.soundtransform.model.library.note;

class ComputedChordNote extends FormulaNote {

    private static final int   PARTS        = 12;
    private static final int   HALF         = ComputedChordNote.PARTS / 2;
    private static final float FIRST_COEFF  = 0.3f;
    private static final float SECOND_COEFF = 0.2f;
    private static final float THIRD_COEFF  = 0.4f;
    private static final float TWO_PI       = (float) (2 * Math.PI);

    @Override
    protected float applyFormula (final int j, final float frequency, final float sampleRate) {
        return this.sinOnceOtherXTimes (j, frequency, sampleRate, ComputedChordNote.PARTS, 0) * ComputedChordNote.FIRST_COEFF + this.sinOnceOtherXTimes (j, frequency, sampleRate, ComputedChordNote.PARTS, ComputedChordNote.HALF) * ComputedChordNote.SECOND_COEFF
                + this.sinOnceOtherXTimes (j, frequency, sampleRate, 1, 0) * ComputedChordNote.THIRD_COEFF;
    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

    private float sinOnceOtherXTimes (final int j, final float frequency, final float sampleRate, final int xTimes, final int modulo) {
        return (float) (Math.round (j * frequency / sampleRate) % xTimes == modulo ? Math.sin (j * frequency * ComputedChordNote.TWO_PI / sampleRate) : 0);
    }

}
