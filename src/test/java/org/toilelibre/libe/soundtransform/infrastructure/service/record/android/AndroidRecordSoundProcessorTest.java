package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.InputStream;
import java.util.List;

import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.record.android.AndroidRecordSoundProcessor.AndroidRecordSoundProcessorErrorCode;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.record.AmplitudeObserver;

import android.media.AudioRecord;

import static org.mockito.ArgumentMatchers.any;

@PrepareForTest ({ AudioRecord.class, AndroidRecordSoundProcessor.class })
public class AndroidRecordSoundProcessorTest extends SoundTransformAndroidTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test
    public void unintialized () {
        this.rule.hashCode ();
        TestAudioRecordInitializer.audioRecord = Mockito.mock (AudioRecord.class);
        TestAudioRecordInitializer.minBufferSize = 1024;
        try {
            FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
            Assert.fail ("Record should have failed because the recorder in not initialized");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), AndroidRecordSoundProcessorErrorCode.STREAM_INFO_NOT_SUPPORTED);
        }
    }

    @Test
    public void audioBufferBadValue () {
        TestAudioRecordInitializer.audioRecord = Mockito.mock (AudioRecord.class);
        TestAudioRecordInitializer.minBufferSize = AudioRecord.ERROR_BAD_VALUE;
        try {
            FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
            Assert.fail ("Record should have failed because the recorder in not initialized");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), AndroidRecordSoundProcessorErrorCode.STREAM_INFO_NOT_SUPPORTED);
        }
    }

    @Test
    public void stateOkButNotRecording () throws Exception {
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        Mockito.when (audioRecord.getState ()).thenReturn (AudioRecord.STATE_INITIALIZED);

        TestAudioRecordInitializer.audioRecord = audioRecord;
        TestAudioRecordInitializer.minBufferSize = 1024;
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
        Assert.assertEquals (0, is.available ());
    }

    @Test
    public void mockRecordedSound () throws Exception {
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        Mockito.when (audioRecord.getState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.getRecordingState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.read (any (short [].class), any (int.class), any (int.class))).thenReturn (1024);

        TestAudioRecordInitializer.audioRecord = audioRecord;
        TestAudioRecordInitializer.minBufferSize = 1024;
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null)).stopWithInputStream ();

        Assert.assertTrue (is.available () > 0);
    }

    @Test
    public void shapeAndMockRecordedSoundInParallel () throws Exception {
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        Mockito.when (audioRecord.getState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.getRecordingState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.read (any (short [].class), any (int.class), any (int.class))).thenReturn (1024);

        TestAudioRecordInitializer.audioRecord = audioRecord;
        TestAudioRecordInitializer.minBufferSize = 1024;
        final Object stop = new Object ();
        new Thread ("Wait4000msInTheTest") {

            @Override
            public void run () {
                try {
                    Thread.sleep (4000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException (e);
                }

                boolean notified = false;
                synchronized (stop) {
                    while (!notified) {
                        stop.notifyAll ();
                        notified = true;
                    }
                }
            }

        }.start ();

        final List<float []> resultFloats = FluentClient.start ().whileRecordingASound (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null),
          stop, new AmplitudeObserver () {

            @Override
            public void update (float soundLevel) {
                new Slf4jObserver ().notify ("recorded sound level " + soundLevel);
            }
        }).findLoudestFrequencies ().stopWithFreqs ();

        Assert.assertThat (resultFloats, new IsNot<List<float []>> (new IsNull<List<float []>> ()));
        Assert.assertNotEquals (resultFloats.size (), 0);
        Assert.assertNotEquals (resultFloats.get (0).length, 0);
    }

    @Test
    public void earlyEndOfSink () throws Exception {
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        Mockito.when (audioRecord.getState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.getRecordingState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.read (any (short [].class), any (int.class), any (int.class))).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return this.i++ < 2 ? 1024 : 0;
            }

        });

        TestAudioRecordInitializer.audioRecord = audioRecord;
        TestAudioRecordInitializer.minBufferSize = 1024;
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 80000, 2, 44100.0f, false, true, null)).stopWithInputStream ();

        Assert.assertTrue (is.available () > 0);
    }

    @Test (expected = SoundTransformException.class)
    public void startRecordingWithBadAudioFormatObject () throws Exception {
        try {
            new AndroidRecordSoundProcessor ().startRecordingAndReturnByteBuffer (new Object (), new Object ());
            Assert.fail ("should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AndroidRecordSoundProcessorErrorCode.STREAM_INFO_EXPECTED, ste.getErrorCode ());
            throw ste;
        }
    }

    @Test (expected = SoundTransformException.class)
    public void recordRawInputStreamWithBadAudioFormatObject () throws Exception {
        try {
            new AndroidRecordSoundProcessor ().recordRawInputStream (new Object (), new Object ());
            Assert.fail ("should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (AndroidRecordSoundProcessorErrorCode.STREAM_INFO_EXPECTED, ste.getErrorCode ());
            throw ste;
        }
    }
}
