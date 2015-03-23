package org.toilelibre.libe.soundtransform;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class RecordTest extends SoundTransformTest {
    public enum RecordTestErrorCode implements ErrorCode {

        TEST_ERROR ("Error during the test");

        private final String messageFormat;

        RecordTestErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }
    
    @Test
    public void recordTwoSeconds () throws SoundTransformException{
        Sound [] sounds = null;
        try {
            sounds = FluentClient.start().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 48000, false, true, null)).importToSound().stopWithSounds();
        } catch (SoundTransformException ste){
            if ("AUDIO_FORMAT_NOT_SUPPORTED".equals (ste.getErrorCode().name())){
                //make the test succeeds because we are unable to test the record audio feature on a machine
                //without microphone
                return;
            }else {
                Assert.fail("Could not record anything (" + ste + ")");
            }
        }
        
        Assert.assertNotNull(sounds [0]);
        Assert.assertNotNull(sounds [1]);
        Assert.assertNotEquals(sounds [0].getSamplesLength(), 0);
        Assert.assertNotEquals(sounds [1].getSamplesLength(), 0);
    }
    @Test
    public void recordTwoSecondsWithAStopObject () throws SoundTransformException {
        final Object stop = new Object ();
        final Sound [] sounds = new Sound [2];
        new Thread (){
            public void run (){
                try {
                    Sound sounds2 [] = FluentClient.start().withRecordedInputStream(new StreamInfo (2, -1, 2, 48000, false, true, null), stop).importToSound().stopWithSounds();
                    sounds [0] = sounds2 [0];
                    sounds [1] = sounds2 [1];
                } catch (SoundTransformException ste) {
                    if ("AUDIO_FORMAT_NOT_SUPPORTED".equals (ste.getErrorCode().name())){
                        //make the test succeeds because we are unable to test the record audio feature on a machine
                        //without microphone
                        sounds [0] = new Sound (new long [1], new FormatInfo (2, 44100), 0);
                        sounds [1] = new Sound (new long [1], new FormatInfo (2, 44100), 1);
                    }else {
                        throw new SoundTransformRuntimeException(ste);
                    }
                }
            }
        }.start();
        synchronized (stop){
            try {
                stop.wait(2000);
            } catch (InterruptedException e) {
                throw new SoundTransformException (RecordTestErrorCode.TEST_ERROR, e);
            }
            stop.notify();
            try {
                stop.wait(500);
            } catch (InterruptedException e) {
                throw new SoundTransformException (RecordTestErrorCode.TEST_ERROR, e);
            }
        }
        Assert.assertNotNull(sounds [0]);
        Assert.assertNotNull(sounds [1]);
        Assert.assertNotEquals(sounds [0].getSamplesLength(), 0);
        Assert.assertNotEquals(sounds [1].getSamplesLength(), 0);
    }
}
