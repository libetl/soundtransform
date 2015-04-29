package org.toilelibre.libe.soundtransform;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class RecordTest extends SoundTransformTest {
    public enum RecordTestErrorCode implements ErrorCode {

        TEST_ERROR ("Error during the test");

        private final String messageFormat;

        RecordTestErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    @Test
    public void recordTwoSeconds () throws SoundTransformException {
        Sound sound = null;
        try {
            sound = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 48000, false, true, null)).importToSound ().stopWithSound ();
        } catch (final SoundTransformException ste) {
            // "AUDIO_FORMAT_NOT_SUPPORTED" is thrown by oracle and
            // "TARGET_LINE_UNAVAILABLE" is thrown by openjdk
            if ("AUDIO_FORMAT_NOT_SUPPORTED".equals (ste.getErrorCode ().name ()) || "TARGET_LINE_UNAVAILABLE".equals (ste.getErrorCode ().name ())) {
                // make the test succeeds because we are unable to test the
                // record audio feature on a machine
                // without microphone
                return;
            } else {
                Assert.fail ("Could not record anything (" + ste + ")");
            }
        }

        Assert.assertNotNull (sound.getChannels () [0]);
        Assert.assertNotNull (sound.getChannels () [1]);
        Assert.assertNotEquals (sound.getSamplesLength (), 0);
    }

    @Test
    public void recordTwoSecondsWithAStopObject () throws SoundTransformException {
        final Object stop = new Object ();
        final Channel [] channels = new Channel [2];
        new Thread () {
            @Override
            public void run () {
                try {
                    final Sound sounds2 = FluentClient.start ().withRecordedInputStream (new StreamInfo (2, -1, 2, 48000, false, true, null), stop).importToSound ().stopWithSound ();
                    channels [0] = sounds2.getChannels () [0];
                    channels [1] = sounds2.getChannels () [1];
                } catch (final SoundTransformException ste) {
                    // "AUDIO_FORMAT_NOT_SUPPORTED" is thrown by oracle and
                    // "TARGET_LINE_UNAVAILABLE" is thrown by openjdk
                    if ("AUDIO_FORMAT_NOT_SUPPORTED".equals (ste.getErrorCode ().name ()) || "TARGET_LINE_UNAVAILABLE".equals (ste.getErrorCode ().name ())) {
                        // make the test succeeds because we are unable to test
                        // the record audio feature on a machine
                        // without microphone
                        channels [0] = new Channel (new long [1], new FormatInfo (2, 44100), 0);
                        channels [1] = new Channel (new long [1], new FormatInfo (2, 44100), 1);
                    } else {
                        throw new SoundTransformRuntimeException (ste);
                    }
                }
            }
        }.start ();
        synchronized (stop) {
            try {
                stop.wait (2000);
            } catch (final InterruptedException e) {
                throw new SoundTransformException (RecordTestErrorCode.TEST_ERROR, e);
            }
            stop.notify ();
            try {
                stop.wait (500);
            } catch (final InterruptedException e) {
                throw new SoundTransformException (RecordTestErrorCode.TEST_ERROR, e);
            }
        }
        Assert.assertNotNull (channels [0]);
        Assert.assertNotNull (channels [1]);
        Assert.assertNotEquals (channels [0].getSamplesLength (), 0);
        Assert.assertNotEquals (channels [1].getSamplesLength (), 0);
    }
}
