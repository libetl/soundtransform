package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindSoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.observer.Observer;
import org.toilelibre.libe.soundtransform.model.record.RunnableWithInputStream;

public class FluentClientOperation implements BuildableFluentClientOperationSoundImported, BuildableFluentClientOperationWithInputStream, BuildableFluentClientOperationWithFile, BuildableFluentClientOperationWithFreqs, BuildableFluentClientOperationWithParallelizedClients,
BuildableFluentClientOperationWithSpectrums, FluentClientInterface {

    public abstract class Step {
        void run (final FluentClientInterface client) throws SoundTransformException {
            client.hashCode ();
        }

        void run (final FluentClientInterface client, final int invocationNumber) throws SoundTransformException {
            ("" + invocationNumber).substring (0);
            this.run (client);
        }
    }

    public static class FluentClientOperationRunnable implements RunnableWithInputStream {

        private final FluentClientOperation operation;
        private FluentClientInterface clientInterface;
        private final int                   clientId;

        public FluentClientOperationRunnable (final FluentClientOperation operation1, final FluentClientInterface clientInterface1, final int clientId1) {
            this.operation = operation1;
            this.clientInterface = clientInterface1;
            this.clientId = clientId1;
        }

        @Override
        public void run () {
            for (final FluentClientOperation.Step step : this.operation.getSteps ()) {
                try {
                    step.run (this.clientInterface, this.clientId);
                } catch (final SoundTransformException ste) {
                    throw new SoundTransformRuntimeException (ste);
                }
            }
        }

        @Override
        public <T> T runWithInputStreamAndGetResult (final InputStream inputStream, final StreamInfo streamInfo, final Class<T> resultClass) throws SoundTransformException {
            this.clientInterface = (FluentClientInterface) FluentClient.start ().withRawInputStream (inputStream, streamInfo);
            this.run ();
            return ((FluentClient) this.clientInterface).getResult (resultClass);
        }
    }

    public enum FluentClientOperationErrorCode implements ErrorCode {

        NO_RETURN_IN_AN_OPERATION ("An operation cannot return something"), NO_RESTART_IN_AN_OPERATION ("An operation cannot use something else than the current data");

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
    public BuildableFluentClientOperationReady andAfterStart () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RESTART_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public BuildableFluentClientOperationSoundImported mixAllInOneSound () throws SoundTransformException {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RESTART_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public <T> T [] applyAndStop (final SoundTransform<Channel, T> st) throws SoundTransformException {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public Pack stopWithAPack (final String title) {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public Observer [] stopWithObservers () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public List<Spectrum<Serializable> []> stopWithSpectrums () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public <T> T [] stopWithResults (final Class<T> resultClass) {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public InputStream stopWithInputStream () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public StreamInfo stopWithStreamInfo () throws SoundTransformException {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public List<float []> stopWithFreqs () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public Sound stopWithSound () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
    }

    @Override
    public File stopWithFile () {
        throw new SoundTransformRuntimeException (FluentClientOperationErrorCode.NO_RETURN_IN_AN_OPERATION, new UnsupportedOperationException ());
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
    public BuildableFluentClientOperationSoundImported changeFormat (final FormatInfo formatInfo) {
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
    public BuildableFluentClientOperationWithFreqs findLoudestFrequencies (final PeakFindSoundTransform<?, ?> peakFindSoundTransform) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.findLoudestFrequencies (peakFindSoundTransform);
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
    public BuildableFluentClientOperationSoundImported mergeChannels () throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.mergeChannels ();
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
    public BuildableFluentClientOperationWithFreqs surroundInRange (final float low, final float high) throws SoundTransformException {
        this.steps.add (new Step () {

            @Override
            public void run (final FluentClientInterface client) throws SoundTransformException {
                client.surroundInRange (low, high);
            }
        });

        return this;
    }

    @Override
    public FluentClientOperation build () {
        return this;
    }
}
