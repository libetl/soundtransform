package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import android.media.AudioTrack;

@PrepareForTest (AndroidPlaySoundProcessor.class)
public class AndroidPlaySoundProcessorTest extends SoundTransformAndroidTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test
    public void playAMockedInputStream () throws Exception {
        this.rule.hashCode ();
        final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
        Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return Math.min (5, this.i++ / 2);
            }

        });
        PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
        .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
        final InputStream inputStream = FluentClient.start ().withClasspathResource ("before.wav").importToStream ().stopWithInputStream ();
        final StreamInfo streamInfo = FluentClient.start ().withAudioInputStream (inputStream).stopWithStreamInfo ();
        final AndroidPlaySoundProcessor processor = new AndroidPlaySoundProcessor ();
        final Object o = processor.play (inputStream, streamInfo);
        boolean waited = false;
        synchronized (o) {
            while (!waited) {
                waited = true;
                o.wait ();
            }
        }
        Mockito.verify (audioTrack, Mockito.times (13)).getPlaybackHeadPosition ();
    }

    @Test
    public void playAMockedSound () throws Exception {
        this.rule.hashCode ();
        final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
        Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return Math.min (5, this.i++ / 2);
            }

        });
        PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
        .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
        FluentClient.start ().withClasspathResource ("before.wav").convertIntoSound ().playIt ();
        Mockito.verify (audioTrack, Mockito.atLeast (1)).getPlaybackHeadPosition ();
    }
    
    @Test
    public void playRandomBytes () throws Exception {
        for (final int j : new int [] { 1, 2, 4, 5, 6, 8 }) {
            final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
            Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
                int i = 0;

                @Override
                public Integer answer (final InvocationOnMock invocation) throws Throwable {
                    return Math.min (1, this.i++ / 2);
                }

            });
            PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
            .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
            final InputStream inputStream = this.generateRandomBytes ();
            final StreamInfo streamInfo = new StreamInfo (j, 100000, 2, 44100, false, true, null);
            final AndroidPlaySoundProcessor processor = new AndroidPlaySoundProcessor ();
            final Object o = processor.play (inputStream, streamInfo);
            boolean waited = false;
            synchronized (o) {
                while (!waited) {
                    waited = true;
                    o.wait ();
                }
            }
            Mockito.verify (audioTrack, Mockito.times (5)).getPlaybackHeadPosition ();
        }
    }

    private InputStream generateRandomBytes () {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [65536];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        return new ByteArrayInputStream (data);
    }
}
