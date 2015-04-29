package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PitchSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpeedUpSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

final class ConvertedSoundPitchAndTempoHelper implements SoundPitchAndTempoHelper {

    private static final int   THRESHOLD_DOWN_PITCH               = 98;
    private static final int   THRESHOLD_UP_PITCH                 = 102;

    private static final float THRESHOLD_SLOWDOWN                 = 1.02f;
    private static final float THRESHOLD_SPEEDUP                  = 0.98f;

    private static final int   HELPER_DEFAULT_SPEEDUP_STEP_VALUE  = 100;

    private static final int   HELPER_DEFAULT_SLOWDOWN_STEP_VALUE = 1024;
    private static final int   HELPER_DEFAULT_WINDOW_LENGTH_VALUE = 2 * ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_SLOWDOWN_STEP_VALUE;

    @Override
    public Channel pitchAndSetLength (final Channel sound, final float percent, final float lengthInSeconds) throws SoundTransformException {

        Channel result = sound;

        final PitchSoundTransform pitcher = new PitchSoundTransform (percent);
        if (percent < ConvertedSoundPitchAndTempoHelper.THRESHOLD_DOWN_PITCH || percent > ConvertedSoundPitchAndTempoHelper.THRESHOLD_UP_PITCH) {
            result = pitcher.transform (result);
        }
        final double factor = sound.getSamplesLength () == 0 ? 0 : 1.0 * lengthInSeconds * sound.getSampleRate () / result.getSamplesLength ();
        if (factor == 0) {
            return result;
        } else if (factor < ConvertedSoundPitchAndTempoHelper.THRESHOLD_SPEEDUP || factor > ConvertedSoundPitchAndTempoHelper.THRESHOLD_SLOWDOWN) {
            if (factor < ConvertedSoundPitchAndTempoHelper.THRESHOLD_SPEEDUP) {
                final SpeedUpSoundTransform<Serializable> speedup = new SpeedUpSoundTransform<Serializable> (ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_SPEEDUP_STEP_VALUE, (float) (1 / factor));
                result = speedup.transform (result);

            } else if (factor > ConvertedSoundPitchAndTempoHelper.THRESHOLD_SLOWDOWN) {
                final SlowdownSoundTransform slowdown = new SlowdownSoundTransform (ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_SLOWDOWN_STEP_VALUE, (float) factor, ConvertedSoundPitchAndTempoHelper.HELPER_DEFAULT_WINDOW_LENGTH_VALUE);
                result = slowdown.transform (result);
            }
        }
        return result;
    }
}
