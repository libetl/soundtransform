package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SegmentedChannel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SegmentedSound;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class SegmentedSoundTest {

    @Test
    public void aSegmentedSoundAppendsSeveralSoundsInOne () throws SoundTransformException {
        final Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        final Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        final Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));
        final long [] sound12array = new long [sound1.getSamplesLength () + sound2.getSamplesLength ()];
        sound12.getChannels () [0].copyTo (sound12array);
        final Sound sound12Bis = new Sound (new Channel [] { new Channel (sound12array, sound1.getFormatInfo (), 0) });
        Assert.assertEquals (sound1.getChannels () [0].viewSamplesArray ().replace ("]", ", " + sound2.getChannels () [0].viewSamplesArray ().substring (1)), sound12Bis.getChannels () [0].viewSamplesArray ());
    }

    @Test
    public void aSegmentedCannotBeDisplayed () throws SoundTransformException {
        final Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        final Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        final Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));

        Assert.assertEquals (sound12.getChannels () [0].viewSamplesArray (), SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED);
        Assert.assertEquals (sound12.getChannels () [0].toString (), SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED);
    }

    @Test
    public void aSegmentedSoundCopiesAsAnAppendedSound () throws SoundTransformException {
        final Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        final Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        final Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));

        final long [] sound12array = new long [sound1.getSamplesLength () + sound2.getSamplesLength ()];
        final Sound sound12Bis = new Sound (new Channel [] { new Channel (sound12array, sound1.getFormatInfo (), 0) });
        sound12.getChannels () [0].copyTo (sound12Bis.getChannels () [0]);

        Assert.assertEquals (sound1.getChannels () [0].viewSamplesArray ().replace ("]", ", " + sound2.getChannels () [0].viewSamplesArray ().substring (1)), sound12Bis.getChannels () [0].viewSamplesArray ());
    }

    @Test
    public void setSampleAtWorksAsUsual () throws SoundTransformException {
        new SegmentedSound (new FormatInfo (2, 11025), Arrays.asList (FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound (), FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ())).getChannels () [0].setSampleAt (
                0, 329);

    }

    @Test
    public void getSampleAtWorksAsUsual () throws SoundTransformException {
        new SegmentedSound (new FormatInfo (2, 11025), Arrays.asList (FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound (), FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ())).getChannels () [0]
                .getSampleAt (0);

    }

    @Test
    public void aSegmentedSoundCopyOfAPartReturnsTheCorrectPart () throws SoundTransformException {
        final Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        final Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        final Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));

        final long [] sound12array = new long [sound1.getSamplesLength ()];
        final Sound sound12Bis = new Sound (new Channel [] { new Channel (sound12array, sound1.getFormatInfo (), 0) });
        sound12.getChannels () [0].copyTo (sound12Bis.getChannels () [0], 0, 0, sound1.getSamplesLength ());

        Assert.assertEquals (sound1.getChannels () [0].viewSamplesArray (), sound12Bis.getChannels () [0].viewSamplesArray ());
    }

    @Test
    public void segmentedSoundBrowse () {
        final Sound sound1 = new Sound (new Channel [] { new Channel (this.generateSequence (0, 13), new FormatInfo (1, 1), 0) });
        final Sound sound2 = new Sound (new Channel [] { new Channel (this.generateSequence (14, 27), new FormatInfo (1, 1), 0) });
        final Sound sound3 = new Sound (new Channel [] { new Channel (this.generateSequence (28, 41), new FormatInfo (1, 1), 0) });
        final Sound sound4 = new Sound (new Channel [] { new Channel (this.generateSequence (42, 55), new FormatInfo (1, 1), 0) });
        final Sound sound5 = new Sound (new Channel [] { new Channel (this.generateSequence (56, 69), new FormatInfo (1, 1), 0) });

        final Sound sound = new SegmentedSound (new FormatInfo (1, 1), Arrays.asList (sound1, sound2, sound3, sound4, sound5));
        Assert.assertEquals (sound.getChannels () [0].getSampleAt (10), 10);
        Assert.assertEquals (sound.getChannels () [0].getSampleAt (25), 25);
        Assert.assertEquals (sound.getChannels () [0].getSampleAt (26), 26);
        Assert.assertEquals (sound.getChannels () [0].getSampleAt (25), 25);
        Assert.assertEquals (sound.getChannels () [0].getSampleAt (42), 42);
        Assert.assertEquals (sound.getChannels () [0].getSampleAt (40), 40);
        try {
            sound.getChannels () [0].getSampleAt (70);
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException stre) {

        }

        try {
            sound.getChannels () [0].getSampleAt (-5);
            Assert.fail ("should have failed");
        } catch (final SoundTransformRuntimeException stre) {

        }
    }

    private long [] generateSequence (final int i, final int j) {
        final long [] result = new long [j - i + 1];
        for (int k = i ; k <= j ; k++) {
            result [k - i] = k;
        }
        return result;
    }

}
