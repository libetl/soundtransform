package org.toilelibre.libe.soundtransform.model.library.note;

public class PureNote extends FormulaNote {

    private static final float TWO_PI = (float) (2 * Math.PI);

    @Override
    protected float applyFormula (final int j, final float frequency, final float sampleRate) {
        return (float) Math.sin ((j * frequency * PureNote.TWO_PI) / sampleRate);
    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

}
