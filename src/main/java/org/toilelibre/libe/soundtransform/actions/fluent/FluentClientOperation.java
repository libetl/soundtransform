package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
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

public class FluentClientOperation {

    public static FluentClientOperationBuilder declare (){
        return new FluentClientOperationBuilder ();
    }
    
    List<Step> getSteps() {
        return steps;
    }

    public enum FluentClientOperationErrorCode implements ErrorCode {

        NOT_POSSIBLE_IN_AN_OPERATION("An operation cannot return something");

        private final String messageFormat;

        FluentClientOperationErrorCode(final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }
    }

    interface Step {
        void run(FluentClient client) throws SoundTransformException;
    }

    private List<Step> steps;

    private FluentClientOperation(List<Step> steps1) {
        this.steps = Collections.unmodifiableList(steps1);
    }

    public static class FluentClientOperationBuilder implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile, FluentClientWithFreqs, FluentClientWithParallelizedClients, FluentClientWithSpectrums {

        private List<Step> steps = new LinkedList<Step> ();
        
        public FluentClientOperation build() {
            return new FluentClientOperation(steps);
        }

        @Override
        public FluentClientOperationBuilder andAfterStart() {
            return this;
        }

        @Override
        public Pack stopWithAPack(String title) {
            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public Observer[] stopWithObservers() {
            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public FluentClientOperationBuilder extractSound() throws SoundTransformException {

            return this;
        }

        @Override
        public FluentClientOperationBuilder playIt() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.playIt();
                }
            });
            return this;
        }

        @Override
        public List<Spectrum<Serializable>[]> stopWithSpectrums() {

            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public FluentClientOperationBuilder adjust() {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.adjust();
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder compress(final float factor) {

            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.compress(factor);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder filterRange(final float low, final float high) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.filterRange(low, high);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder insertPart(final float[] subFreqs, final int start) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.insertPart(subFreqs, start);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder octaveDown() {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.octaveDown();
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder octaveUp() {

            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.octaveUp();
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder replacePart(final float[] subFreqs, final int start) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.replacePart(subFreqs, start);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder shapeIntoSound(final String packName, final String instrumentName, final FormatInfo fi) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.shapeIntoSound(packName, instrumentName, fi);
                }
            });

            return this;
        }

        @Override
        public float[] stopWithFreqs() {

            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public FluentClientOperationBuilder convertIntoSound() throws SoundTransformException {

            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.convertIntoSound();
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder importToStream() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.importToStream();
                }
            });
            return this;
        }

        @Override
        public File stopWithFile() {

            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public FluentClientOperationBuilder importToSound() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.importToSound();
                }
            });
            return this;
        }

        @Override
        public InputStream stopWithInputStream() {

            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public StreamInfo stopWithStreamInfo() throws SoundTransformException {

            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }

        @Override
        public FluentClientOperationBuilder writeToClasspathResource(final String resource) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.writeToClasspathResource(resource);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder writeToClasspathResourceWithSiblingResource(final String resource, final String siblingResource) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.writeToClasspathResourceWithSiblingResource(resource, siblingResource);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder writeToFile(final File file) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.writeToFile(file);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder withAnObserver(final Observer... observers) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withAnObserver(observers);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder withAPack(final String packName, final InputStream jsonStream) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withAPack(packName, jsonStream);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder withAPack(final String packName, final Object context, final Class<?> rClass, final int packJsonId) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withAPack(packName, context, rClass, packJsonId);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder withAPack(final String packName, final String jsonContent) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withAPack(packName, jsonContent);
                }
            });
            return this;
        }

        @Override
        public FluentClientOperationBuilder withAudioInputStream(final InputStream is) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withAudioInputStream (is);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withClasspathResource(final String resource) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withClasspathResource (resource);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withFile(final File file) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withFile (file);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withFreqs(final float[] freqs) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withFreqs (freqs);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withLimitedTimeRecordedInputStream(final StreamInfo streamInfo) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withLimitedTimeRecordedInputStream (streamInfo);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withRawInputStream(final InputStream is, final StreamInfo isInfo) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withRawInputStream (is , isInfo);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withRecordedInputStream(final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withRecordedInputStream (streamInfo, stop);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withSounds(final Sound[] sounds) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withSounds (sounds);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder withSpectrums(final List<Spectrum<Serializable>[]> spectrums) {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.withSpectrums (spectrums);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder append(final Sound[] sound) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.append (sound);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder apply(final SoundTransformation st) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.apply (st);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder changeFormat(final FormatInfo formatInfo) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.changeFormat (formatInfo);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder cutSubSound(final int start, final int end) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.cutSubSound (start, end);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder exportToClasspathResource(final String resource) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.exportToClasspathResource (resource);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder exportToClasspathResourceWithSiblingResource(final String resource, final String siblingResource) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.exportToClasspathResourceWithSiblingResource (resource, siblingResource);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder exportToFile(final File file) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.exportToFile (file);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder exportToStream() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.exportToStream ();
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder extractSubSound(final int start, final int end) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.extractSubSound (start, end);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder findLoudestFrequencies() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.findLoudestFrequencies ();
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder loop(final int length) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.loop (length);
                }
            });

            return this;
        }


        @Override
        public FluentClientSoundImported mixAllInOneSound() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.mixAllInOneSound ();
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder mixWith(final Sound[] sound) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.mixWith (sound);
                }
            });

            return this;
        }

        @Override
        public FluentClientOperationBuilder splitIntoSpectrums() throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.splitIntoSpectrums ();
                }
            });

            return this;
        }

        @Override
        public Sound[] stopWithSounds() {

            throw new SoundTransformRuntimeException(FluentClientOperationErrorCode.NOT_POSSIBLE_IN_AN_OPERATION, new UnsupportedOperationException());
        }


        @Override
        public <T extends FluentClientCommon> FluentClientWithParallelizedClients inParallel(final FluentClientOperation op, final int timeoutInSeconds, final T... clients) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.inParallel (op, timeoutInSeconds, clients);
                }
            });
            return this;
        }
        
        @Override
        public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final Sound []... sounds) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.inParallel (op, timeoutInSeconds, sounds);
                }
            });
            return this;}
        @Override
        public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final InputStream... inputStreams) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.inParallel (op, timeoutInSeconds, inputStreams);
                }
            });
            return this;}
        @Override
        public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final File... files) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.inParallel (op, timeoutInSeconds, files);
                }
            });
            return this;}
        @Override
        public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final float []... freqs) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.inParallel (op, timeoutInSeconds, freqs);
                }
            });
            return this;}
        @Override
        public FluentClientWithParallelizedClients inParallel (final FluentClientOperation op, final int timeoutInSeconds, final String... classpathResources) throws SoundTransformException {
            this.steps.add(new Step() {

                @Override
                public void run(FluentClient client) throws SoundTransformException {
                    client.inParallel (op, timeoutInSeconds, classpathResources);
                }
            });
            return this;
        }


    }
}
