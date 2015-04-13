package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class FluentClientOperation implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile, FluentClientWithFreqs, FluentClientWithParallelizedClients, FluentClientWithSpectrums, FluentClientInterface {

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
    public float [] stopWithFreqs () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }
    
    @Override
    public Sound [] stopWithSounds () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public File stopWithFile () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public FluentClientOperation extractSound () throws SoundTransformException {
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
    public FluentClientOperation adjust () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.adjust ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation compress (final float factor) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.compress (factor);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation filterRange (final float low, final float high) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.filterRange (low, high);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation insertPart (final float [] subFreqs, final int start) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.insertPart (subFreqs, start);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation octaveDown () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.octaveDown ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation octaveUp () {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.octaveUp ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation replacePart (final float [] subFreqs, final int start) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.replacePart (subFreqs, start);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation shapeIntoSound (final String packName, final String instrumentName, final FormatInfo formatInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.shapeIntoSound (packName, instrumentName, formatInfo);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation convertIntoSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.convertIntoSound ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation importToStream () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.importToStream ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation importToSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.importToSound ();
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation writeToClasspathResource (final String resource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.writeToClasspathResource (String.format (resource, invocationNumber));
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation writeToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.writeToClasspathResourceWithSiblingResource (String.format (resource, invocationNumber), siblingResource);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation writeToFile (final File file) throws SoundTransformException {
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
    public FluentClientOperation withAMixedSound (final Sound []... sounds) throws SoundTransformException {
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
    public FluentClientOperation withAudioInputStream (final InputStream is) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withAudioInputStream (is);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withClasspathResource (final String resource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withClasspathResource (resource);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withFile (final File file) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withFile (file);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withFreqs (final float [] freqs) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withFreqs (freqs);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withLimitedTimeRecordedInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withLimitedTimeRecordedInputStream (streamInfo);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withRawInputStream (final InputStream is, final StreamInfo isInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withRawInputStream (is, isInfo);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withRecordedInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withRecordedInputStream (streamInfo, stop);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withSounds (final Sound [] sounds) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withSounds (sounds);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation withSpectrums (final List<Spectrum<Serializable> []> spectrums) {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.withSpectrums (spectrums);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation append (final Sound [] sound) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.append (sound);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation apply (final SoundTransformation st) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.apply (st);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation changeFormat (final FormatInfo formatInfo) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.changeFormat (formatInfo);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation cutSubSound (final int start, final int end) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.cutSubSound (start, end);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation exportToClasspathResource (final String resource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.exportToClasspathResource (String.format (resource, invocationNumber));
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation exportToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
                client.exportToClasspathResourceWithSiblingResource (String.format (resource, invocationNumber), siblingResource);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation exportToFile (final File file) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.exportToFile (file);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation exportToStream () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.exportToStream ();
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation extractSubSound (final int start, final int end) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.extractSubSound (start, end);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation findLoudestFrequencies () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.findLoudestFrequencies ();
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation loop (final int length) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.loop (length);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation mixAllInOneSound () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.mixAllInOneSound ();
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation mixWith (final Sound [] sound) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.mixWith (sound);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation splitIntoSpectrums () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.splitIntoSpectrums ();
            }
        });

        return this;
    }

    @Override
    public <T extends FluentClientCommon> FluentClientOperation inParallel (final FluentClientOperation op, final int timeoutInSeconds, final T... clients) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, clients);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation inParallel (final FluentClientOperation op, final int timeoutInSeconds, final Sound []... sounds) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, sounds);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation inParallel (final FluentClientOperation op, final int timeoutInSeconds, final InputStream... inputStreams) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, inputStreams);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation inParallel (final FluentClientOperation op, final int timeoutInSeconds, final File... files) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, files);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation inParallel (final FluentClientOperation op, final int timeoutInSeconds, final float []... freqs) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, freqs);
            }
        });
        return this;
    }

    @Override
    public FluentClientOperation inParallel (final FluentClientOperation op, final int timeoutInSeconds, final String... classpathResources) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.inParallel (op, timeoutInSeconds, classpathResources);
            }
        });
        return this;
    }

}
