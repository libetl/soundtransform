package org.toilelibre.libe.soundtransform.model.library.note;

public class PureNote extends FormulaNote {

    @Override
    protected float applyFormula (final int j, final float frequency, final float sampleRate) {
        return (float) Math.sin ((j * frequency * 2 * Math.PI) / sampleRate);
    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

}
