package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;

import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.EightBitsSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

@PrepareForTest ({ ApplicationInjector.class, TargetDataLine.class, TargetDataLineRecordSoundProcessor.class })
public class JavaxRecordSoundProcessorTest extends SoundTransformTest {

    @Rule
    public PowerMockRule                      rule = new PowerMockRule ();

    @InjectMocks
    public TargetDataLineRecordSoundProcessor processor;

    @Test
    public void mockRecordedSound () throws Exception {
        this.rule.hashCode ();
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++) {
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
        Assert.assertThat (is.available (), new GreaterThan<Integer> (0));
    }

    @Test
    public void mockRecordedSoundWithStopObject () throws Exception {
        this.rule.hashCode ();
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++) {
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);
        final Object stopObject = new Object ();
        final InputStream [] is = new InputStream [1];
        new Thread () {
            @Override
            public void run () {
                try {
                    is [0] = FluentClient.start ().withRecordedInputStream (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null), stopObject).stopWithInputStream ();
                } catch (final SoundTransformException e) {
                    throw new RuntimeException (e);
                }
            }
        }.start ();

        Thread.sleep (2000);
        boolean notified = false;
        synchronized (stopObject) {
            while (!notified) {
                stopObject.notify ();
                notified = true;
            }
        }
        Thread.sleep (1000);

        Assert.assertThat (is [0].available (), new GreaterThan<Integer> (0));
    }
    
    @Test
    public void mockRecordAndProcessSoundWithStopObject () throws Exception {
        this.rule.hashCode ();
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++) {
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);
        final Object stopObject = new Object ();
        final List<Sound> list = new LinkedList<Sound> ();
        new Thread () {
            @Override
            public void run () {
                try {
                    list.addAll (FluentClient.start ().recordProcessAndTransformInBackgroundTask (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null), stopObject, FluentClientOperation.prepare ().importToSound ().apply (new EightBitsSoundTransform (25)).build (), Sound.class));
                } catch (final SoundTransformException e) {
                    throw new RuntimeException (e);
                }
            }
        }.start ();

        Thread.sleep (4000);
        boolean notified = false;
        synchronized (stopObject) {
            while (!notified) {
                stopObject.notify ();
                notified = true;
            }
        }
        Thread.sleep (4000);

        Assert.assertThat (list, new IsNot<List<Sound>> (new IsNull<List<Sound>> ()));
        Assert.assertNotEquals (list.size (), 0);
    }
    
    private void mockRecordSoundProcessor (final byte [][] buffers) throws Exception {
        final TargetDataLine dataLine = Mockito.mock (TargetDataLine.class);
        Mockito.when (dataLine.getBufferSize ()).thenReturn (8192);
        Mockito.when (dataLine.read (Matchers.any (byte [].class), Matchers.any (int.class), Matchers.any (int.class))).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                System.arraycopy (buffers [Math.min (14, this.i)], 0, invocation.getArgumentAt (0, Object.class), 0, buffers [Math.min (14, this.i)].length);
                return buffers [Math.min (14, this.i++)].length;
            }

        });
        PowerMockito.spy (ApplicationInjector.class);
        PowerMockito.when (ApplicationInjector.$.select (RecordSoundProcessor.class)).thenReturn (this.processor);
        MemberModifier.stub (MemberMatcher.method (TargetDataLineRecordSoundProcessor.class, "getDataLine", Info.class)).toReturn (dataLine);
        MemberModifier.stub (MemberMatcher.method (TargetDataLineRecordSoundProcessor.class, "checkLineSupported", Info.class)).toReturn (true);
    }
}