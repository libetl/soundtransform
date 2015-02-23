package org.toilelibre.libe.soundtransform.model.library.note;

public class ComputedOrganNote extends FormulaNote {

    @Override
    protected float applyFormula (int j, float frequency, float sampleRate) {
        return (float) (Math.sin ((j * frequency * 2 * Math.PI) / sampleRate) * 0.5 +
                Math.sin ((j * frequency * 4 * Math.PI + Math.PI / 2) / sampleRate) * 0.25 +
                Math.sin ((j * frequency * 8 * Math.PI + Math.PI) / sampleRate) * 0.12 +
                Math.sin ((j * frequency * 16 * Math.PI + 3 * Math.PI / 2) / sampleRate) * 0.12);
    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

}
