package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.InputStream;

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
    public void playAMockedSound () throws Exception {
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
        synchronized (o) {
            o.wait ();
        }
        Mockito.verify (audioTrack, Mockito.times (13)).getPlaybackHeadPosition ();
    }
}
