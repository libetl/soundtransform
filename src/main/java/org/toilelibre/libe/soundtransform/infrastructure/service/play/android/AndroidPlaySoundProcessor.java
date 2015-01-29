package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android.HasInputStreamInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException.PlaySoundErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AndroidPlaySoundProcessor implements PlaySoundProcessor {

    public AndroidPlaySoundProcessor () {

    }

    @Override
    public Object play (final InputStream ais) throws PlaySoundException {
        if (!(ais instanceof HasInputStreamInfo)) {
            throw new PlaySoundException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, new ClassCastException ("Could not cas InputStream as a HasInputStreamInfo class")));

        }
        final HasInputStreamInfo is = (HasInputStreamInfo) ais;
        final AudioTrack audioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, (int) is.getInfo ().getSampleRate (), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, (int) is.getInfo ().getFrameLength (), AudioTrack.MODE_STATIC);
        final byte [] baSoundByteArray = new byte [(int) is.getInfo ().getFrameLength () * is.getInfo ().getSampleSize ()];
        try {
            ais.read (baSoundByteArray);
        } catch (final IOException e1) {
            throw new PlaySoundException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e1));
        }
        audioTrack.write (baSoundByteArray, 0, baSoundByteArray.length);
        audioTrack.flush ();
        audioTrack.play ();

        final Thread thread = new Thread () {
            @Override
            public void run () {
                int lastFrame = -1;
                while (lastFrame != audioTrack.getPlaybackHeadPosition ()) {
                    lastFrame = audioTrack.getPlaybackHeadPosition ();
                    try {
                        Thread.sleep (1000);
                    } catch (final InterruptedException e) {
                        throw new SoundTransformRuntimeException (new PlaySoundException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e)));
                    }
                }
                audioTrack.stop ();
                audioTrack.release ();
            }
        };
        thread.start ();
        return thread;
    }

}
