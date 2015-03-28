package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException.PlaySoundErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

final class AndroidPlaySoundProcessor extends AbstractLogAware<AndroidPlaySoundProcessor> implements PlaySoundProcessor {

    public enum AndroidPlaySoundProcessorEventCode implements EventCode {
        READ_BYTEARRAY_SIZE (LogLevel.PARANOIAC, "Byte array size read : %1d");

        private final String   messageFormat;
        private final LogLevel logLevel;

        AndroidPlaySoundProcessorEventCode (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    protected static final long ONE_SECOND = 1000;

    public AndroidPlaySoundProcessor () {

    }

    @Override
    public Object play (final InputStream ais, final StreamInfo streamInfo) throws PlaySoundException {
        final int channelConf = this.getChannelConfiguration (streamInfo);
        final AudioTrack audioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, (int) streamInfo.getSampleRate (), channelConf, streamInfo.getSampleSize () == 2 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT, (int) streamInfo.getFrameLength () * streamInfo.getSampleSize (),
                AudioTrack.MODE_STATIC);
        final byte [] baSoundByteArray = new byte [(int) streamInfo.getFrameLength () * streamInfo.getSampleSize ()];
        try {
            final int byteArraySize = ais.read (baSoundByteArray);
            this.log (new LogEvent (AndroidPlaySoundProcessorEventCode.READ_BYTEARRAY_SIZE, byteArraySize));
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
                        Thread.sleep (AndroidPlaySoundProcessor.ONE_SECOND);
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

    private int getChannelConfiguration (final StreamInfo streamInfo) {
        final int channelConf;
        switch (streamInfo.getChannels ()) {
            case 1:
                channelConf = AudioFormat.CHANNEL_OUT_MONO;
                break;
            case 2:
                channelConf = AudioFormat.CHANNEL_OUT_STEREO;
                break;
            case 4:
                channelConf = AudioFormat.CHANNEL_OUT_QUAD;
                break;
            case 5:
                channelConf = AudioFormat.CHANNEL_OUT_SURROUND;
                break;
            case 6:
                channelConf = AudioFormat.CHANNEL_OUT_5POINT1;
                break;
            case 8:
                channelConf = AudioFormat.CHANNEL_OUT_7POINT1;
                break;
            default:
                channelConf = 0;
        }
        return channelConf;
    }

}
