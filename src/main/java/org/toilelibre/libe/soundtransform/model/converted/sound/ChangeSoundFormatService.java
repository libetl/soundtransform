package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class ChangeSoundFormatService {

    private final SoundAppender soundAppender;

    public ChangeSoundFormatService (SoundAppender soundAppender1) {
        this.soundAppender = soundAppender1;
    }

    public Sound [] change (Sound [] input, InputStreamInfo inputStreamInfo) {
        final Sound [] result = new Sound [input.length];
        for (int i = 0 ; i < input.length ; i++) {
            result [i] = input [i];
            result [i] = this.soundAppender.changeNbBytesPerSample (result [i], inputStreamInfo.getSampleSize ());
            result [i] = this.soundAppender.resizeToSampleRate (result [i], (int) inputStreamInfo.getSampleRate ());
        }
        return result;
    }
}
