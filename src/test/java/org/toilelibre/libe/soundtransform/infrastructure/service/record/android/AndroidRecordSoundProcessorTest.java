package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.infrastructure.service.record.android.AndroidRecordSoundProcessor.AndroidRecordSoundProcessorErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import android.media.AudioRecord;

@PrepareForTest ({ AudioRecord.class, AndroidRecordSoundProcessor.class })
public class AndroidRecordSoundProcessorTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test
    public void unintialized () throws Exception {
        this.rule.hashCode();
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        Mockito.when (audioRecord.getState ()).thenReturn (AudioRecord.STATE_UNINITIALIZED);
        PowerMockito.mockStatic (AudioRecord.class, new Answer<Object> () {

            @Override
            public Object answer (final InvocationOnMock invocation) throws Throwable {
                if ("getMinBufferSize".equals (invocation.getMethod ().getName ())) {
                    return 1024;
                }
                return invocation.callRealMethod ();
            }
        });
        PowerMockito.whenNew (AudioRecord.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class).withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioRecord);
        try {
            FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
            Assert.fail ("Record should have failed because the recorder in not initialized");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), AndroidRecordSoundProcessorErrorCode.STREAM_INFO_NOT_SUPPORTED);
        }
    }

    @Test
    public void audioBufferBadValue () throws Exception {
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        PowerMockito.mockStatic (AudioRecord.class, new Answer<Object> () {

            @Override
            public Object answer (final InvocationOnMock invocation) throws Throwable {
                if ("getMinBufferSize".equals (invocation.getMethod ().getName ())) {
                    return AudioRecord.ERROR_BAD_VALUE;
                }
                return invocation.callRealMethod ();
            }
        });
        PowerMockito.whenNew (AudioRecord.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class).withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioRecord);
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
        PowerMockito.mockStatic (AudioRecord.class, new Answer<Object> () {

            @Override
            public Object answer (final InvocationOnMock invocation) throws Throwable {
                if ("getMinBufferSize".equals (invocation.getMethod ().getName ())) {
                    return 1024;
                }
                return invocation.callRealMethod ();
            }
        });
        PowerMockito.whenNew (AudioRecord.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class).withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioRecord);
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 100000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
        Assert.assertEquals (is.available (), 0);
    }

    @Test
    public void mockRecordedSound () throws Exception {
        final AudioRecord audioRecord = Mockito.mock (AudioRecord.class);
        Mockito.when (audioRecord.getState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.getRecordingState ()).thenReturn (AudioRecord.STATE_INITIALIZED);
        Mockito.when (audioRecord.read (Matchers.any (short [].class), Matchers.any (int.class), Matchers.any (int.class))).thenReturn (1024);
        PowerMockito.mockStatic (AudioRecord.class, new Answer<Object> () {

            @Override
            public Object answer (final InvocationOnMock invocation) throws Throwable {
                if ("getMinBufferSize".equals (invocation.getMethod ().getName ())) {
                    return 1024;
                }
                return invocation.callRealMethod ();
            }
        });
        PowerMockito.whenNew (AudioRecord.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class).withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioRecord);
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
        Assert.assertThat (is.available (), new GreaterThan<Integer> (0));
    }
}
