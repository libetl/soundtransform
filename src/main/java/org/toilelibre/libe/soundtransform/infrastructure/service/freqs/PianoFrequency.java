package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

public enum PianoFrequency {
    C8 (4186.01f), B7 (3951.07f), A7 (3520f), G7 (3135.96f), F7 (2793.83f), E7 (2637.02f), D7 (2349.32f), C7 (2093f), B6 (1975.53f), A6 (1760f), G6 (1567.98f), F6 (1396.91f), E6 (1318.51f), D6 (1174.66f), C6 (1046.5f), B5 (987.767f), A5 (880f), G5 (783.991f), F5 (698.456f), E5 (659.255f), D5 (
            587.33f), C5 (523.251f), B4 (493.883f), A4 (440f), G4 (391.995f), F4 (349.228f), E4 (329.628f), D4 (293.665f), B3 (246.942f), A3 (220f), G3 (195.998f), F3 (174.614f), E3 (164.814f), D3 (146.832f), C3 (130.813f), B2 (123.471f), A2 (110f), G2 (97.9989f), F2 (87.3071f), E2 (82.4069f), D2 (
                    73.4162f), C2 (65.4064f), B1 (61.7354f), A1 (55f), G1 (48.9995f), F1 (43.6536f), E1 (41.2035f), D1 (36.7081f), C1 (32.7032f);

    private float frequency;

    PianoFrequency (float frequency1) {
        this.frequency = frequency1;
    }

    public float getFrequency () {
        return this.frequency;
    }

    public static PianoFrequency getNearestNote (float value) {
        PianoFrequency nearest = PianoFrequency.A1;
        for (final PianoFrequency pianoFreq : PianoFrequency.values ()) {
            if (Math.abs (value - pianoFreq.frequency) < Math.abs (value - nearest.frequency)) {
                nearest = pianoFreq;
            }
        }
        return nearest;
    }
}
