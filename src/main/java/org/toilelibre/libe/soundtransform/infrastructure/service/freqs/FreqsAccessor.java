package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.infrastructure.service.frames.FrameProcessorAccessor;
import org.toilelibre.libe.soundtransform.model.freqs.AdjustFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.ChangeOctaveProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.CompressFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.FilterFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.ReplaceFrequenciesProcessor;

public abstract class FreqsAccessor extends FrameProcessorAccessor {

    protected AdjustFrequenciesProcessor provideAdjustFrequenciesProcessor () {
        return new AdjustFrequenciesToPianoProcessor ();
    }

    protected ChangeOctaveProcessor provideChangeOctaveProcessor () {
        return new SimpleChangeOctaveProcessor ();
    }

    protected CompressFrequenciesProcessor provideCompressFrequenciesProcessor () {
        return new SimpleCompressFrequenciesProcessor ();
    }

    protected FilterFrequenciesProcessor provideFilterFrequenciesProcessor () {
        return new SimpleFilterFrequenciesProcessor ();
    }

    protected ReplaceFrequenciesProcessor provideReplaceFrequenciesProcessor () {
        return new SimpleReplaceFrequenciesProcessor ();
    }

}
