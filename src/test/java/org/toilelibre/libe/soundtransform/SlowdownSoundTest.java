package org.toilelibre.libe.soundtransform;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms.SlowdownSoundTransformation.SlowdownSoundTransformationErrorCode;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class SlowdownSoundTest extends SoundTransformTest {

    @Test
    public void testSlowdown() throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final File input = new File(classLoader.getResource("before.wav").getFile());
        final File output = new File(new File(classLoader.getResource("before.wav").getFile()).getParent() + "/after.wav");

        $.create(TransformSoundService.class, new Slf4jObserver(LogLevel.WARN)).transformFile(input, output, $.create(SlowdownSoundTransformation.class, 1024, 2.5f, 2048));

    }

    @Test(expected = SoundTransformException.class)
    public void testSlowdownNotPowerOf2() throws SoundTransformException {
        try {
            new SlowdownSoundTransformation(1024, 2.5f, 2049);
            Assert.fail();
        } catch (final SoundTransformException ste) {
            Assert.assertEquals(ste.getErrorCode(), SlowdownSoundTransformationErrorCode.WINDOW_LENGTH_IS_NOT_A_POWER_OF_2);
            throw ste;
        }
    }

    @Test(expected = SoundTransformException.class)
    public void testSlowdownNotTwiceLessThanWindowLength() throws SoundTransformException {
        try {
            new SlowdownSoundTransformation(1025, 2.5f, 2048);
            Assert.fail();
        } catch (final SoundTransformException ste) {
            Assert.assertEquals(ste.getErrorCode(), SlowdownSoundTransformationErrorCode.WINDOW_LENGTH_IS_LOWER_THAN_TWICE_THE_STEP);
            throw ste;
        }
    }
}
