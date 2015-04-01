package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import java.io.InputStream;
import java.util.Random;

import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

@PrepareForTest ({ ApplicationInjector.class, TargetDataLine.class, TargetDataLineRecordSoundProcessor.class })
public class JavaxRecordSoundProcessorTest extends SoundTransformTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test
    public void mockRecordedSound () throws Exception {
        final byte [][] buffers = new byte [15] [1024];
        for (int i = 0 ; i < 14 ; i++){
            new Random ().nextBytes (buffers [i]);
        }
        buffers [14] = new byte [0];
        this.mockRecordSoundProcessor (buffers);
        final InputStream is = FluentClient.start ().withLimitedTimeRecordedInputStream (new StreamInfo (2, 10000, 2, 44100.0f, false, true, null)).stopWithInputStream ();
        Assert.assertThat (is.available (), new GreaterThan<Integer> (0));
    }

    private void mockRecordSoundProcessor (final byte[][] buffers) throws Exception {
        TargetDataLineRecordSoundProcessor processor = Mockito.spy (new TargetDataLineRecordSoundProcessor ());
        TargetDataLine dataLine = Mockito.mock (TargetDataLine.class);
        Mockito.when (dataLine.getBufferSize ()).thenReturn (8192);
        Mockito.when (dataLine.read(Mockito.any (byte[].class), Mockito.any (int.class), Mockito.any (int.class))).thenAnswer(new Answer<Integer> (){
            int i = 0;
            
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                System.arraycopy (buffers [Math.min (14, i)], 0, invocation.getArgumentAt (0, Object.class), 0, buffers [Math.min (14, i)].length);
                return buffers [Math.min (14, i++)].length;
            }
            
        });
        PowerMockito.spy (ApplicationInjector.class);
        PowerMockito.when (ApplicationInjector.$.select (RecordSoundProcessor.class)).thenReturn (processor);
        PowerMockito.stub (PowerMockito.method (TargetDataLineRecordSoundProcessor.class, "getDataLine", Info.class)).toReturn (dataLine);
        PowerMockito.stub (PowerMockito.method (TargetDataLineRecordSoundProcessor.class, "checkLineSupported", Info.class)).toReturn (true);
    }
}