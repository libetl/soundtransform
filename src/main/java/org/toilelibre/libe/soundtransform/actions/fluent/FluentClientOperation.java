package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class FluentClientOperation implements BuildableFluentClientOperationSoundImported, BuildableFluentClientOperationReady, BuildableFluentClientOperationWithInputStream, BuildableFluentClientOperationWithFile, BuildableFluentClientOperationWithFreqs,
        BuildableFluentClientOperationWithParallelizedClients, BuildableFluentClientOperationWithSpectrums, FluentClientInterface {

    public abstract class Step {
        void run (final FluentClientInterface client) throws SoundTransformException {
            client.hashCode ();
        }

        void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
            ("" + invocationNumber).hashCode ();
            this.run (client);
        }
    }

    public enum FluentClientOperationErrorCode implements ErrorCode {

        NOT_POSSIBLE_IN_AN_OPERATION ("An operation cannot return something");

        private final String messageFormat;

        FluentClientOperationErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private final List<Step> steps;

    private FluentClientOperation () {
        this.steps = new LinkedList<Step> ();
    }

    public static FluentClientOperation prepare () {
        return new FluentClientOperation ();
    }

    List<Step> getSteps () {
        return this.steps;
    }

    @Override
    public FluentClientOperation andAfterStart () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.andAfterStart ();
            }
        });
        return this;
    }

    @Override
    public <T> T [] applyAndStop (final SoundTransform<Channel, T> st, final Class<T> resultClass) throws SoundTransformException {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }
    
    @Override
    public Pack stopWithAPack (final String title) {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public Observer [] stopWithObservers () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public List<Spectrum<Serializable> []> stopWithSpectrums () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public <T> T [] stopWithResults (final Class<T> resultClass) {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public InputStream stopWithInputStream () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public StreamInfo stopWithStreamInfo () throws SoundTransformException {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public List<float []> stopWithFreqs () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public Sound stopWithSound () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public File stopWithFile () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public BuildableFluentClientOperationSoundImported extractSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.extractSound ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation playIt () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.playIt ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs adjust () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.adjust ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs compress (final float factor) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.compress (factor);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs filterRange (final float low, final float high) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.filterRange (low, high);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs insertPart (final List<float []> subFreqs, final int start) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.insertPart (subFreqs, start);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs octaveDown () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.octaveDown ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs octaveUp () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.octaveUp ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs replacePart (final List<float []> subFreqs, final int start) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.replacePart (subFreqs, start);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported shapeIntoSound (final String packName, final String instrumentName, final FormatInfo formatInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.shapeIntoSound (packName, instrumentName, formatInfo);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported convertIntoSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.convertIntoSound ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithInputStream importToStream () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.importToStream ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported importToSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.importToSound ();
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile writeToClasspathResource (final String resource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.writeToClasspathResource (String.format (resource, invocationNumber));
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile writeToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.writeToClasspathResourceWithSiblingResource (String.format (resource, invocationNumber), siblingResource);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile writeToFile (final File file) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.writeToFile (new File (String.format (file.getAbsolutePath (), invocationNumber)));
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation withAnObserver (final Observer... observers) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAnObserver (observers);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported withAMixedSound (final Sound... sounds) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAMixedSound (sounds);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation withAPack (final String packName, final InputStream jsonStream) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAPack (packName, jsonStream);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation withAPack (final String packName, final Object context, final Class<?> rClass, final int packJsonId) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAPack (packName, context, rClass, packJsonId);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation withAPack (final String packName, final String jsonContent) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAPack (packName, jsonContent);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithInputStream withAudioInputStream (final InputStream is) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAudioInputStream (is);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile withClasspathResource (final String resource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withClasspathResource (resource);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile withFile (final File file) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withFile (file);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs withFreqs (final List<float []> freqs) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withFreqs (freqs);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithInputStream withLimitedTimeRecordedInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withLimitedTimeRecordedInputStream (streamInfo);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithInputStream withRawInputStream (final InputStream is, final StreamInfo isInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withRawInputStream (is, isInfo);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithInputStream withRecordedInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withRecordedInputStream (streamInfo, stop);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported withSound (final Sound sound) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withSound (sound);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithSpectrums withSpectrums (final List<Spectrum<Serializable> []> spectrums) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withSpectrums (spectrums);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported append (final Sound sound) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.append (sound);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported apply (final SoundTransform<Channel, Channel> st) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.apply (st);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported changeFormat (final FormatInfo formatInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.changeFormat (formatInfo);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported cutSubSound (final int start, final int end) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.cutSubSound (start, end);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile exportToClasspathResource (final String resource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.exportToClasspathResource (String.format (resource, invocationNumber));
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile exportToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.exportToClasspathResourceWithSiblingResource (String.format (resource, invocationNumber), siblingResource);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFile exportToFile (final File file) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.exportToFile (file);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithInputStream exportToStream () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.exportToStream ();
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported extractSubSound (final int start, final int end) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.extractSubSound (start, end);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithFreqs findLoudestFrequencies () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.findLoudestFrequencies ();
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported loop (final int length) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.loop (length);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported mixAllInOneSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.mixAllInOneSound ();
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationSoundImported mixWith (final Sound sound) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.mixWith (sound);
            }
        });

        return this;
    }

    @Override
    public BuildableFluentClientOperationWithSpectrums splitIntoSpectrums () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.splitIntoSpectrums ();
            }
        });

        return this;
    }

    @Override
    public <T extends FluentClientCommon> BuildableFluentClientOperationWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final T... clients) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, clients);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final Sound... sounds) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, sounds);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final InputStream... inputStreams) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, inputStreams);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final File... files) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, files);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final List<float []>... freqs) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, freqs);
            }
        });
        return this;
    }

    @Override
    public BuildableFluentClientOperationWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final String... classpathResources) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, classpathResources);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation build () {
        return this;
    }
}
