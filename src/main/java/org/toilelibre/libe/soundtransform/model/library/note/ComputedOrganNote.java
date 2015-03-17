package org.toilelibre.libe.soundtransform.model.library.note;

class ComputedOrganNote extends FormulaNote {

    private static final float TWO_PI           = (float) (2 * Math.PI);
    private static final float FIRST_COEFF      = 0.12f;
    private static final float SECOND_COEFF     = 0.25f;
    private static final float THIRD_COEFF      = 0.5f;
    private static final float FOURTH_COEFF     = 0.5f;
    private static final float ONCE             = 1;
    private static final float TWICE            = 2;
    private static final float FOUR_TIMES       = 4;
    private static final float EIGHT_TIMES      = 8;
    private static final float NO_GAP           = 0;
    private static final float ONE_FOURTH_GAP   = (float) Math.PI / 2;
    private static final float HALF_GAP         = (float) Math.PI;
    private static final float THREE_FOURTH_GAP = (float) ((3 * Math.PI) / 2);

    @Override
    protected float applyFormula (final int j, final float frequency, final float sampleRate) {
        return (float) ((Math.sin (((j * ComputedOrganNote.ONCE * frequency * ComputedOrganNote.TWO_PI) / sampleRate) + ComputedOrganNote.NO_GAP) * ComputedOrganNote.FIRST_COEFF)
                + (Math.sin (((j * ComputedOrganNote.TWICE * frequency * ComputedOrganNote.TWO_PI) / sampleRate) + ComputedOrganNote.ONE_FOURTH_GAP) * ComputedOrganNote.SECOND_COEFF)
                + (Math.sin (((j * ComputedOrganNote.FOUR_TIMES * frequency * ComputedOrganNote.TWO_PI) / sampleRate) + ComputedOrganNote.HALF_GAP) * ComputedOrganNote.THIRD_COEFF) + (Math.sin (((j * ComputedOrganNote.EIGHT_TIMES * frequency * ComputedOrganNote.TWO_PI) / sampleRate)
                        + ComputedOrganNote.THREE_FOURTH_GAP) * ComputedOrganNote.FOURTH_COEFF));

    }

    @Override
    public String getName () {
        return this.getClass ().getName ();
    }

}
