package org.toilelibre.libe.soundtransform.benchmarks;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientSoundImported;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.CepstrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.HarmonicProductSpectrumSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.MaximumLikelihoodSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransform;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

@State (Scope.Benchmark)
@BenchmarkMode (Mode.SingleShotTime)
@Warmup (iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit (TimeUnit.MILLISECONDS)
@Fork (value = 10)
@Measurement (iterations = 40, time = 5, timeUnit = TimeUnit.SECONDS)
public class NotePeakFindBenchmark {

    private FluentClientSoundImported client;

    @Setup
    public void init () {
        final File file = new File (Thread.currentThread ().getContextClassLoader ().getResource ("piano1c.wav").getFile ());
        try {
            this.client = FluentClient.start ().withAnObserver (new Slf4jObserver (LogLevel.WARN)).withFile (file).convertIntoSound ();
        } catch (final SoundTransformException e) {
            throw new RuntimeException (e);
        }
    }

    @Benchmark
    public float [][] cepstrum () {
        return this.applyOrThrowRuntimeException (new CepstrumSoundTransform<Serializable> (100, true));
    }

    @Benchmark
    public float [][] hps () {
        return this.applyOrThrowRuntimeException (new HarmonicProductSpectrumSoundTransform<Serializable> (true, true));
    }

    @Benchmark
    public float [][] maxlikelihood () {
        return this.applyOrThrowRuntimeException (new MaximumLikelihoodSoundTransform (48000, 4000, 100, 800));
    }

    private float [][] applyOrThrowRuntimeException (final PeakFindSoundTransform<Serializable, ?> peakFindSoundTransform) {
        try {
            return this.apply (peakFindSoundTransform);
        } catch (final SoundTransformException e) {
            throw new RuntimeException (e);
        }
    }

    private float [][] apply (final PeakFindSoundTransform<Serializable, ?> peakFindSoundTransform) throws SoundTransformException {
        return this.client.applyAndStop (peakFindSoundTransform);
    }

}
