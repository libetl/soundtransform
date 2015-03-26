package org.toilelibre.libe.soundtransform;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class FormatInfoTest {

    @Test
    public void formatInfoEquals () {
        final FormatInfo fi1 = new FormatInfo (2, 44100);
        final FormatInfo fi2 = new FormatInfo (2, 44100);
        Assert.assertTrue (fi1.sameFormatAs (fi2));
    }

    @Test
    public void formatInfoEqualsStreamInfo () {
        final FormatInfo fi1 = new FormatInfo (2, 44100);
        final FormatInfo fi2 = new StreamInfo (2, 700000, 2, 44100, false, true, "Fenomenon - The B Minor Suite");
        Assert.assertTrue (fi1.sameFormatAs (fi2));
    }

    @Test
    public void formatInfoDifferentSampleRate () {
        final FormatInfo fi1 = new FormatInfo (2, 44100);
        final FormatInfo fi2 = new FormatInfo (2, 48000);
        Assert.assertFalse (fi1.sameFormatAs (fi2));
    }

    @Test
    public void formatInfoDifferentSampleSize () {
        final FormatInfo fi1 = new FormatInfo (2, 44100);
        final FormatInfo fi2 = new FormatInfo (1, 44100);
        Assert.assertFalse (fi1.sameFormatAs (fi2));
    }

    @Test
    public void formatInfoDifferentBoth () {
        final FormatInfo fi1 = new FormatInfo (2, 44100);
        final FormatInfo fi2 = new FormatInfo (1, 8000);
        Assert.assertFalse (fi1.sameFormatAs (fi2));
    }
}
