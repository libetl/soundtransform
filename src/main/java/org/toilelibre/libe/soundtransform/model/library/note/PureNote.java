package org.toilelibre.libe.soundtransform.model.library.note;

public class PureNote extends FormulaNote {

    @Override
    protected float applyFormula (int j, float frequency, float sampleRate) {
        return (float) Math.sin ((j * frequency * 2 * Math.PI) / sampleRate);
    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

}
