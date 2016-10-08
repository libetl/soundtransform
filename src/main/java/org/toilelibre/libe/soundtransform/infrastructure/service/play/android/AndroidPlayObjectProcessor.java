package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.IOException;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.EventCode;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectException;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectException.PlaySoundErrorCode;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

@Processor
final class AndroidPlayObjectProcessor extends AbstractLogAware<AndroidPlayObjectProcessor> implements PlayObjectProcessor {

    private static final String SOUND_PLAYER_MONITOR = "SoundPlayerMonitor";
    private static final int EIGHT = 8;
    private static final int SIX   = 6;
    private static final int FIVE  = 5;
    private static final int FOUR  = 4;
    private static final int TWO   = 2;
    private static final int ONE   = 1;

    private enum AndroidPlaySoundProcessorEventCode implements EventCode {
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

    private static final long ONE_SECOND = 1000;

    AndroidPlayObjectProcessor () {

    }

    @Override
    public Object play (final InputStream ais, final StreamInfo streamInfo, final Object stopMonitor, final int skipMilliSeconds) throws PlayObjectException {
        final int channelConf = this.getChannelConfiguration (streamInfo);
        final int frameLength;
        try {
            frameLength = findFrameLength (ais, streamInfo);
        } catch (IOException ioe) {
            throw new PlayObjectException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, ioe));
        }
 
        final AudioTrack audioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, (int) streamInfo.getSampleRate (), channelConf, streamInfo.getSampleSize () == AndroidPlayObjectProcessor.TWO ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT, 
                frameLength * streamInfo.getSampleSize (), AudioTrack.MODE_STATIC);
        final byte [] baSoundByteArray = new byte [frameLength * streamInfo.getSampleSize ()];
        try {
            final int byteArraySize = ais.read (baSoundByteArray);
            this.log (new LogEvent (AndroidPlaySoundProcessorEventCode.READ_BYTEARRAY_SIZE, byteArraySize));
        } catch (final IOException e1) {
            throw new PlayObjectException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e1));
        }
        audioTrack.write (baSoundByteArray, 0, baSoundByteArray.length);
        audioTrack.flush ();
        audioTrack.setPlaybackHeadPosition ((int) (streamInfo.getSampleRate () * 1.0 * skipMilliSeconds / 1000.0));
        audioTrack.play ();

        final Thread soundMonitorThread = this.getSoundMonitorThread (stopMonitor, audioTrack);
        final Thread playFrameMonitorThread = this.getPlayFrameMonitorThread (stopMonitor, audioTrack);
        playFrameMonitorThread.start ();
        if (soundMonitorThread != null) {
            soundMonitorThread.start ();
        }
        return playFrameMonitorThread;
    }

    private int findFrameLength (final InputStream ais, final StreamInfo streamInfo) throws IOException {
        return (int) (streamInfo.getFrameLength () != -1 ? streamInfo.getFrameLength () : ais.available () * 1.0 / (streamInfo.getSampleSize () * streamInfo.getChannels ()));
    }

    private Thread getSoundMonitorThread (final Object stopMonitor, final AudioTrack audioTrack) {
        if (stopMonitor == null) {
            return null;
        }
        return new Thread (AndroidPlayObjectProcessor.SOUND_PLAYER_MONITOR) {
            public void run () {
                synchronized (stopMonitor) {
                    try {
                        stopMonitor.wait ();
                    } catch (InterruptedException e) {
                    }

                    audioTrack.stop ();
                    audioTrack.release ();
                }
            }
        };
    }

    private Thread getPlayFrameMonitorThread (final Object stopMonitor, final AudioTrack audioTrack) {
        return new Thread ("PlayFrameMonitor") {
            @Override
            public void run () {
                int lastFrame = -AndroidPlayObjectProcessor.ONE;
                while (lastFrame != audioTrack.getPlaybackHeadPosition ()) {
                    lastFrame = audioTrack.getPlaybackHeadPosition ();
                    try {
                        Thread.sleep (AndroidPlayObjectProcessor.ONE_SECOND);
                    } catch (final InterruptedException e) {
                        throw new SoundTransformRuntimeException (new PlayObjectException (new SoundTransformException (PlaySoundErrorCode.COULD_NOT_PLAY_SOUND, e)));
                    }
                }
                if (stopMonitor != null) {
                    synchronized (stopMonitor) {
                      stopMonitor.notifyAll ();
                    }
                } else {
                    audioTrack.stop ();
                    audioTrack.release ();
                }
            }
        };
    }

    private int getChannelConfiguration (final StreamInfo streamInfo) {
        final int channelConf;
        switch (streamInfo.getChannels ()) {
            case AndroidPlayObjectProcessor.ONE :
                channelConf = AudioFormat.CHANNEL_OUT_MONO;
                break;
            case AndroidPlayObjectProcessor.TWO :
                channelConf = AudioFormat.CHANNEL_OUT_STEREO;
                break;
            case AndroidPlayObjectProcessor.FOUR :
                channelConf = AudioFormat.CHANNEL_OUT_QUAD;
                break;
            case AndroidPlayObjectProcessor.FIVE :
                channelConf = AudioFormat.CHANNEL_OUT_SURROUND;
                break;
            case AndroidPlayObjectProcessor.SIX :
                channelConf = AudioFormat.CHANNEL_OUT_5POINT1;
                break;
            case AndroidPlayObjectProcessor.EIGHT :
                channelConf = AudioFormat.CHANNEL_OUT_7POINT1;
                break;
            default :
                channelConf = 0;
                break;
        }
        return channelConf;
    }

}
