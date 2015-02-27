package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

public class PianoFrequency {
    public static enum PianoValues {

        C8 (PianoFrequency.C8N), B7 (PianoFrequency.B7N), A7 (PianoFrequency.A7N), G7 (PianoFrequency.G7N), F7 (PianoFrequency.F7N), E7 (PianoFrequency.E7N), D7 (PianoFrequency.D7N), C7 (PianoFrequency.C7N), B6 (PianoFrequency.B6N), A6 (PianoFrequency.A6N), G6 (PianoFrequency.G6N), F6 (
                PianoFrequency.F6N), E6 (PianoFrequency.E6N), D6 (PianoFrequency.D6N), C6 (PianoFrequency.C6N), B5 (PianoFrequency.B5N), A5 (PianoFrequency.A5N), G5 (PianoFrequency.G5N), F5 (PianoFrequency.F5N), E5 (PianoFrequency.E5N), D5 (PianoFrequency.D5N), C5 (PianoFrequency.C5N), B4 (
                PianoFrequency.B4N), A4 (PianoFrequency.A4N), G4 (PianoFrequency.G4N), F4 (PianoFrequency.F4N), E4 (PianoFrequency.E4N), D4 (PianoFrequency.D4N), B3 (PianoFrequency.B3N), A3 (PianoFrequency.A3N), G3 (PianoFrequency.G3N), F3 (PianoFrequency.F3N), E3 (PianoFrequency.E3N), D3 (
                PianoFrequency.D3N), C3 (PianoFrequency.C3N), B2 (PianoFrequency.B2N), A2 (PianoFrequency.A2N), G2 (PianoFrequency.G2N), F2 (PianoFrequency.F2N), E2 (PianoFrequency.E2N), D2 (PianoFrequency.D2N), C2 (PianoFrequency.C2N), B1 (PianoFrequency.B1N), A1 (PianoFrequency.A1N), G1 (
                PianoFrequency.G1N), F1 (PianoFrequency.F1N), E1 (PianoFrequency.E1N), D1 (PianoFrequency.D1N), C1 (PianoFrequency.C1N);

        public static PianoValues getNearestNote (float value) {
            PianoValues nearest = PianoValues.A1;
            for (final PianoValues pianoFreq : PianoValues.values ()) {
                if (Math.abs (value - pianoFreq.frequency) < Math.abs (value - nearest.frequency)) {
                    nearest = pianoFreq;
                }
            }
            return nearest;
        }

        private final float frequency;

        PianoValues (float frequency1) {
            this.frequency = frequency1;
        }

        public float getFrequency () {
            return this.frequency;
        }
    }

    private static final float C8N = 4186.01f;
    private static final float B7N = 3951.07f;
    private static final float A7N = 3520f;
    private static final float G7N = 3135.96f;
    private static final float F7N = 2793.83f;
    private static final float E7N = 2637.02f;
    private static final float D7N = 2349.32f;
    private static final float C7N = 2093f;
    private static final float B6N = 1975.53f;
    private static final float A6N = 1760f;
    private static final float G6N = 1567.98f;
    private static final float F6N = 1396.91f;
    private static final float E6N = 1318.51f;
    private static final float D6N = 1174.66f;
    private static final float C6N = 1046.5f;
    private static final float B5N = 987.767f;
    private static final float A5N = 880f;
    private static final float G5N = 783.991f;
    private static final float F5N = 698.456f;
    private static final float E5N = 659.255f;
    private static final float D5N = 587.33f;
    private static final float C5N = 523.251f;
    private static final float B4N = 493.883f;
    private static final float A4N = 440f;
    private static final float G4N = 391.995f;
    private static final float F4N = 349.228f;
    private static final float E4N = 329.628f;
    private static final float D4N = 293.665f;
    private static final float B3N = 246.942f;
    private static final float A3N = 220f;
    private static final float G3N = 195.998f;
    private static final float F3N = 174.614f;
    private static final float E3N = 164.814f;
    private static final float D3N = 146.832f;
    private static final float C3N = 130.813f;
    private static final float B2N = 123.471f;
    private static final float A2N = 110f;
    private static final float G2N = 97.9989f;
    private static final float F2N = 87.3071f;
    private static final float E2N = 82.4069f;
    private static final float D2N = 73.4162f;
    private static final float C2N = 65.4064f;
    private static final float B1N = 61.7354f;
    private static final float A1N = 55f;
    private static final float G1N = 48.9995f;
    private static final float F1N = 43.6536f;
    private static final float E1N = 41.2035f;
    private static final float D1N = 36.7081f;
    private static final float C1N = 32.7032f;
}
