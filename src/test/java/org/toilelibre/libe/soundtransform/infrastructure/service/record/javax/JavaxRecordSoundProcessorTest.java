package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.InputStream;
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
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.record.javax.TargetDataLineRecordSoundProcessor.TargetDataLineRecordSoundProcessorErrorCode;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
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
    public void shapeAndMockRecordedSoundInParallel () throws Exception {
        this.rule.hashCode ();
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++) {
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);

        FluentClient.start ().withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));

        final Object stop = new Object ();
        new Thread ("Wait300MillisInTheTest") {

            @Override
            public void run () {
                try {
                    Thread.sleep (2000);
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

        final Sound resultSound = FluentClient.start ().whileRecordingASound (new StreamInfo (2, 1024, 2, 8000.0f, false, true, null), stop).stopWithSound ();

        try {
            Thread.sleep (2500);
        } catch (final InterruptedException e) {
            throw new RuntimeException (e);
        }

        Assert.assertThat (resultSound, new IsNot<Sound> (new IsNull<Sound> ()));
        Assert.assertNotNull (resultSound.getChannels ());
        Assert.assertEquals (resultSound.getChannels ().length, 1);
        Assert.assertNotEquals (resultSound.getChannels () [0].getSamplesLength (), 0);
    }

    @Test
    public void stopBeforeInitRecordedSoundInParallel () throws Exception {
        this.rule.hashCode ();
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++) {
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);

        FluentClient.start ().withAPack ("default", Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("defaultpackjavax.json"));

        final Object stop = new Object ();
        new Thread ("Wait400MillisInTheTest") {

            @Override
            public void run () {
                try {
                    Thread.sleep (400);
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
                this.stopThread ("main");

                try {
                    Thread.sleep (400);
                } catch (final InterruptedException e) {
                    throw new RuntimeException (e);
                }
                this.stopThread ("StreamReaderThread");
                this.stopThread ("StopDetectorThread");
                this.stopThread ("SleepThread");

            }

            private void stopThread (final String name) {
                final Thread [] threads = new Thread [Thread.activeCount ()];
                Thread.enumerate (threads);
                for (final Thread thread : threads) {
                    if (thread != null && name.equals (thread.getName ())) {
                        thread.interrupt ();
                    }
                }
            }

        }.start ();

        try {
            FluentClient.start ().whileRecordingASound (new StreamInfo (2, 1024, 2, 8000.0f, false, true, null), stop);
        } catch (final SoundTransformRuntimeException stre) {
            new Slf4jObserver ().notify (stre.toString ());
        }

        try {
            Thread.sleep (1500);
        } catch (final InterruptedException e) {
        }
    }

    @Test
    public void startAndInterruptBackgroundRecording () throws Exception {
        this.rule.hashCode ();
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++) {
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);

        final Object stop = new Object ();
        new Thread ("Wait1500MillisInTheTest") {

            @Override
            public void run () {
                try {
                    Thread.sleep (1500);
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
                this.stopThread ("TargetDataLineReaderThread");
                this.stopThread ("StreamReaderThread");
                this.stopThread ("StopDetectorThread");
                this.stopThread ("StopProperlyThread");
                this.stopThread ("SleepThread");
            }

            private void stopThread (final String name) {
                final Thread [] threads = new Thread [Thread.activeCount ()];
                Thread.enumerate (threads);
                for (final Thread thread : threads) {
                    if (thread != null && name.equals (thread.getName ())) {
                        thread.interrupt ();
                    }
                }
            }

        }.start ();

        FluentClient.start ().whileRecordingASound (new StreamInfo (2, 1024, 2, 8000.0f, false, true, null), stop);

        try {
            Thread.sleep (1500);
        } catch (final InterruptedException e) {
            throw new RuntimeException (e);
        }
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

    @Test
    public void notAudioFormatObjectShouldFail () throws SoundTransformException {
        try {
            new TargetDataLineRecordSoundProcessor ().recordRawInputStream (new Object (), new Object ());
            Assert.fail ("should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED);
        }
    }

    @Test
    public void notAudioFormatObjectShouldFail2 () throws SoundTransformException {
        try {
            new TargetDataLineRecordSoundProcessor ().startRecordingAndReturnByteBuffer (new Object (), new Object ());
            Assert.fail ("should have failed");
        } catch (final SoundTransformException ste) {
            Assert.assertEquals (ste.getErrorCode (), TargetDataLineRecordSoundProcessorErrorCode.AUDIO_FORMAT_EXPECTED);
        }
    }

}