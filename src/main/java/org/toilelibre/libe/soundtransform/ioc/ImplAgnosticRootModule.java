package org.toilelibre.libe.soundtransform.ioc;

import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundAppender;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.fourier.CommonsMath3FourierTransformHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.frames.ByteArrayFrameProcessor;
import org.toilelibre.libe.soundtransform.infrastructure.service.pack.GsonPackConfigParser;
import org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.CallHPSFrequencyHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.MagnitudeADSRHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.sound2string.GraphSound2StringHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.GraphSpectrumToStringHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.HPSSpectrumHelper;
import org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.NaiveSpectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound2StringHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

import se.jbee.inject.bind.BinderModule;

public abstract class ImplAgnosticRootModule extends BinderModule {

    @Override
    protected void declare () {
        super.bind (PlaySoundProcessor.class).to (this.providePlaySoundProcessor ());
        super.bind (AudioFileHelper.class).to (this.provideAudioFileHelper ());
        super.bind (AudioFormatParser.class).to (this.provideAudioFormatParser ());

        super.bind (Sound2StringHelper.class).to (new GraphSound2StringHelper ());
        super.bind (SoundAppender.class).to (new ConvertedSoundAppender ());
        super.bind (SoundPitchAndTempoHelper.class).to (new ConvertedSoundPitchAndTempoHelper ());
        super.bind (FourierTransformHelper.class).to (new CommonsMath3FourierTransformHelper ());
        super.bind (Spectrum2CepstrumHelper.class).to (new NaiveSpectrum2CepstrumHelper ());
        super.bind (SpectrumHelper.class).to (new HPSSpectrumHelper ());
        super.bind (SpectrumToStringHelper.class).to (new GraphSpectrumToStringHelper ());
        super.bind (FrameProcessor.class).to (new ByteArrayFrameProcessor ());
        super.bind (ADSRHelper.class).to (new MagnitudeADSRHelper ());
        super.bind (FrequencyHelper.class).to (new CallHPSFrequencyHelper ());
        super.bind (PackConfigParser.class).to (new GsonPackConfigParser ());
        super.bind (Library.class).to (new Library ());
    }

    protected abstract AudioFileHelper provideAudioFileHelper ();

    protected abstract AudioFormatParser provideAudioFormatParser ();

    protected abstract <T> PlaySoundProcessor<T> providePlaySoundProcessor ();

}
