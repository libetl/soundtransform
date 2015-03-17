package org.toilelibre.libe.soundtransform.ioc;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound2StringHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;
import org.toilelibre.libe.soundtransform.model.freqs.AdjustFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.ChangeOctaveProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.CompressFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.FilterFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.ReplaceFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;
import org.toilelibre.libe.soundtransform.model.library.note.Pack2StringHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;
import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

abstract class ImplAgnosticRootModule extends ImplAgnosticFinalAccessor {

    protected void declare() {
        super.bind(PlaySoundProcessor.class).to(this.providePlaySoundProcessor());
        super.bind(AudioFileHelper.class).to(this.provideAudioFileHelper());
        super.bind(AudioFormatParser.class).to(this.provideAudioFormatParser());
        super.bind(ContextLoader.class).to(this.provideContextLoader());

        super.bind(Sound2StringHelper.class).to(this.provideSound2StringHelper());
        super.bind(Pack2StringHelper.class).to(this.providePack2StringHelper());
        super.bind(SoundAppender.class).to(this.provideSoundAppender());
        super.bind(SoundPitchAndTempoHelper.class).to(this.provideSoundPitchAndTempoHelper());
        super.bind(FourierTransformHelper.class).to(this.provideFourierTransformHelper());
        super.bind(Spectrum2CepstrumHelper.class).to(this.provideSpectrum2CepstrumHelper());
        super.bind(SpectrumHelper.class).to(this.provideSpectrumHelper());
        super.bind(SpectrumToStringHelper.class).to(this.provideSpectrumToStringHelper());
        super.bind(FrameProcessor.class).to(this.provideFrameProcessor());
        super.bind(ADSRHelper.class).to(this.provideAdsrHelper());
        super.bind(FrequencyHelper.class).to(this.provideFrequencyHelper());
        super.bind(PackConfigParser.class).to(this.providePackConfigParser());
        super.bind(ChangeOctaveProcessor.class).to(this.provideChangeOctaveProcessor());
        super.bind(AdjustFrequenciesProcessor.class).to(this.provideAdjustFrequenciesProcessor());
        super.bind(FilterFrequenciesProcessor.class).to(this.provideFilterFrequenciesProcessor());
        super.bind(ReplaceFrequenciesProcessor.class).to(this.provideReplaceFrequenciesProcessor());
        super.bind(CompressFrequenciesProcessor.class).to(this.provideCompressFrequenciesProcessor());
        super.bind(Library.class).to(this.provideLibrary());

    }

    private Library provideLibrary() {
        return new Library();
    }

    protected abstract AudioFileHelper provideAudioFileHelper();

    protected abstract AudioFormatParser provideAudioFormatParser();

    protected abstract ContextLoader provideContextLoader();

    protected abstract PlaySoundProcessor providePlaySoundProcessor();

}
