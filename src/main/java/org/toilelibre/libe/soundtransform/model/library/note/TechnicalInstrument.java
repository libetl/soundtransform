package org.toilelibre.libe.soundtransform.model.library.note;

public enum TechnicalInstrument {

    PureNote (new PureNote ());

    public static TechnicalInstrument of (String clazzName) {
        for (final TechnicalInstrument instrument : TechnicalInstrument.values ()) {
            if (instrument.note.getName ().equals (clazzName)) {
                return instrument;
            }
        }
        return null;
    }

    private Note note;

    TechnicalInstrument (Note note1) {
        this.note = note1;
    }

    public Note getUniformNote () {
        return this.note;
    }
}
