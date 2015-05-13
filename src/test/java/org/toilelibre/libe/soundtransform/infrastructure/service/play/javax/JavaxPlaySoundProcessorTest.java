package org.toilelibre.libe.soundtransform.infrastructure.service.play.javax;

import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
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
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

@PrepareForTest ({ ApplicationInjector.class, LineListenerPlaySoundProcessor.class })
public class JavaxPlaySoundProcessorTest extends SoundTransformTest {

    @Rule
    public PowerMockRule                   rule = new PowerMockRule ();

    @InjectMocks
    private LineListenerPlaySoundProcessor processor;

    @Test
    public void playAMockedSound () throws Exception {
        this.rule.hashCode ();
        final Clip clip = PowerMockito.mock (Clip.class);
        final LineListener [] ll = this.mockNecessaryClasses (clip);
        new Thread () {
            @Override
            public void run () {
                try {
                    Thread.sleep (1000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException ("error during test");
                }
                if (ll [0] != null) {
                    ll [0].update (new LineEvent (Mockito.mock (Clip.class), LineEvent.Type.OPEN, 0));
                }
                try {
                    Thread.sleep (1000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException ("error during test");
                }
                if (ll [0] != null) {
                    ll [0].update (new LineEvent (Mockito.mock (Clip.class), LineEvent.Type.START, 0));
                }
                try {
                    Thread.sleep (4000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException ("error during test");
                }
                if (ll [0] != null) {
                    ll [0].update (new LineEvent (Mockito.mock (Clip.class), LineEvent.Type.STOP, 0));
                }
                try {
                    Thread.sleep (1000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException ("error during test");
                }
                if (ll [0] != null) {
                    ll [0].update (new LineEvent (Mockito.mock (Clip.class), LineEvent.Type.CLOSE, 0));
                }
            }

        }.start ();

        FluentClient.start ().withClasspathResource ("gpiano3.wav").playIt ().convertIntoSound ().splitIntoSpectrums ().playIt ().extractSound ().playIt ().exportToStream ().playIt ();
        FluentClient.start ().inParallel (FluentClientOperation.prepare ().playIt ().build (), 10, FluentClient.start ().withClasspathResource ("gpiano3.wav"));
        Mockito.verify (clip).stop ();
        Mockito.verify (clip).close ();
    }

    private LineListener [] mockNecessaryClasses (final Clip clip) throws InterruptedException {

        final LineListener [] ll = new LineListener [1];
        boolean waited = false;
        while (!waited) {
            PowerMockito.doCallRealMethod ().when (clip).wait ();
            waited = true;
        }
        PowerMockito.doAnswer (new Answer<Void> () {
            @Override
            public Void answer (final InvocationOnMock invocation) throws Throwable {
                ll [0] = invocation.getArgumentAt (0, LineListener.class);
                return null;
            }
        }).when (clip).addLineListener (Matchers.any (LineListener.class));

        PowerMockito.when (clip.isOpen ()).thenAnswer (new Answer<Boolean> () {
            int i = 0;

            @Override
            public Boolean answer (final InvocationOnMock invocation) throws Throwable {
                return this.i++ < 1;
            }
        });
        PowerMockito.spy (ApplicationInjector.class);
        PowerMockito.when (ApplicationInjector.$.select (PlaySoundProcessor.class)).thenReturn (this.processor);
        MemberModifier.stub (MemberMatcher.method (LineListenerPlaySoundProcessor.class, "getLine", Info.class)).toReturn (clip);
        return ll;
    }
}
