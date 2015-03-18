package org.toilelibre.libe.soundtransform.model.library.note;

public enum TechnicalInstrument {

    PURE_NOTE (new PureNote ()), COMPUTED_ORGAN_NOTE (new ComputedOrganNote ()), COMPUTED_CHORD_NOTE (new ComputedChordNote ());

    private Note note;

    TechnicalInstrument (final Note note1) {
        this.note = note1;
    }

    public static TechnicalInstrument of (final String clazzName) {
        for (final TechnicalInstrument instrument : TechnicalInstrument.values ()) {
            if (instrument.note.getName ().equals (clazzName)) {
                return instrument;
            }
        }
        return null;
    }

    public Note getUniformNote () {
        return this.note;
    }
}
