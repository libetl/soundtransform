package org.toilelibre.libe.soundtransform.model.record;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.converted.sound.SegmentedSound;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatService;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;

@Service
final class DefaultRecordSoundService extends AbstractLogAware<DefaultRecordSoundService> implements RecordSoundService<AbstractLogAware<DefaultRecordSoundService>> {

    private static class StreamReaderThread<T extends Serializable> extends Thread {
        private final List<Sound>                      results;
        private final StreamInfo                       streamInfo;
        private final ByteBuffer                       targetByteBuffer;
        private final AudioFileService<T>              audioFileService1;
        private final InputStreamToSoundService<T>     isToSoundService1;
        private final AmplitudeObserver                amplitudeObserver;
        private final FourierTransformHelper<T>        fourierTransformHelper;
        private final SimpleFrequencySoundTransform<T> findAmplitude;
        private boolean                                waiting;

        private StreamReaderThread (final List<Sound> results, final AmplitudeObserver amplitudeObserver, final StreamInfo streamInfo, final ByteBuffer targetByteBuffer, 
                final AudioFileService<T> audioFileService1, final InputStreamToSoundService<T> isToSoundService1, final FourierTransformHelper<T> fourierTransformHelper1) {
            this.results = results;
            this.amplitudeObserver = amplitudeObserver;
            this.streamInfo = streamInfo;
            this.targetByteBuffer = targetByteBuffer;
            this.audioFileService1 = audioFileService1;
            this.isToSoundService1 = isToSoundService1;
            this.fourierTransformHelper = fourierTransformHelper1;
            this.waiting = true;
            this.findAmplitude = new SimpleFrequencySoundTransform<T> () {

                @Override
                public boolean isReverseNecessary () {
                    return false;
                }

                @Override
                public boolean rawSpectrumPrefered () {
                    return true;
                }

                @Override
                public void transformFrequencies (final double [][] spectrumAsDoubles, final float sampleRate, final int offset, final int powOf2NearestLength, final int length, final float soundLevel) {
                    StreamReaderThread.this.amplitudeObserver.update (soundLevel);
                }
            };
            this.setName (this.getClass ().getSimpleName ());
        }

        private void waitForNewBytes (final ByteBuffer targetByteBuffer) throws SoundTransformException {
            boolean waited = false;
            synchronized (targetByteBuffer) {
                try {
                    while (!waited) {
                        targetByteBuffer.wait ();
                        waited = true;
                    }
                } catch (final InterruptedException e) {
                    waited = true;
                }
            }
        }

        private void stopWaiting () {
            this.waiting = false;
        }

        @Override
        public void run () {
            while (this.waiting) {
                try {
                    this.waitForNewBytes (this.targetByteBuffer);
                    final InputStream inputStream = this.audioFileService1.streamFromRawStream (new ByteArrayInputStream (this.targetByteBuffer.array ()), this.streamInfo);
                    if (inputStream.available () > 0) {
                        this.results.add (this.isToSoundService1.fromInputStream (inputStream, this.streamInfo));
                        if (this.amplitudeObserver != null) {
                            this.fourierTransformHelper.transform (this.findAmplitude, this.results.get (this.results.size () - 1).getChannels () [0]);
                        }
                    }
                } catch (final IOException e) {
                    throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING, e));
                } catch (final SoundTransformException e) {
                    throw new SoundTransformRuntimeException (e);
                }
            }
        }
    }

    private static final class StopDetectorThread extends Thread {
        private final ByteBuffer         targetByteBuffer;
        private final Object             stop;
        private final StreamReaderThread<Serializable> streamReader;

        private StopDetectorThread (final ByteBuffer targetByteBuffer, final Object stop, final StreamReaderThread<Serializable> streamReader) {
            this.targetByteBuffer = targetByteBuffer;
            this.stop = stop;
            this.streamReader = streamReader;
            this.setName (this.getClass ().getSimpleName ());
        }

        @Override
        public void run () {
            synchronized (this.stop) {
                boolean waited = false;
                try {
                    while (!waited) {
                        waited = true;
                        this.stop.wait ();
                    }
                    this.notifyTargetByteBuffer (waited);
                } catch (final InterruptedException e) {
                    this.notifyTargetByteBuffer (waited);
                }
            }
            this.streamReader.stopWaiting ();
        }

        private void notifyTargetByteBuffer (final boolean waited) {
            synchronized (this.targetByteBuffer) {
                if (waited) {
                    this.targetByteBuffer.notifyAll ();
                }
            }
        }
    }

    private static class SleepThread extends Thread {
        private final Object stop;
        private final long   millis;

        private SleepThread (final Object stop, final long millis) {
            this.stop = stop;
            this.millis = millis;
            this.setName (this.getClass ().getSimpleName ());
        }

        @Override
        public void run () {
            try {
                Thread.sleep (this.millis);
            } catch (final InterruptedException e) {
                throw new SoundTransformRuntimeException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e, e.getMessage ());
            }
            synchronized (this.stop) {
                this.stop.notifyAll ();
            }
        }
    }

    private enum DefaultRecordSoundServiceErrorCode implements ErrorCode {

        NOT_ABLE ("Not able to wait for a recording (%1s)"), PROBLEM_WHILE_READING_THE_BUFFER_IN_A_CONTINUOUS_RECORDING ("Problem while reading the buffer in a continuous recording");

        private final String messageFormat;

        DefaultRecordSoundServiceErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    private static final float                 MS_PER_SECOND                                               = 1000.0f;
    private static final long                  ARBITRARY_SLEEP_TIME_TO_ENSURE_THE_STREAMING_IS_INITIALIZED = 1000;
    private final RecordSoundProcessor         processor;
    private final AudioFormatService           audioFormatService;
    private final AudioFileService<?>          audioFileService;
    private final InputStreamToSoundService<?> isToSoundService;
    private final FourierTransformHelper<?>    fourierTransformHelper;

    public DefaultRecordSoundService (final RecordSoundProcessor processor1, final AudioFormatService audioFormatService1, final AudioFileService<?> audioFileService1, 
            final InputStreamToSoundService<?> isToSoundService1, final FourierTransformHelper<?> fourierTransformHelper1) {
        this.processor = processor1;
        this.audioFormatService = audioFormatService1;
        this.audioFileService = audioFileService1;
        this.isToSoundService = isToSoundService1;
        this.fourierTransformHelper = fourierTransformHelper1;
    }

    @Override
    public InputStream recordRawInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return this.processor.recordRawInputStream (this.audioFormatService.audioFormatfromStreamInfo (streamInfo), stop);
    }

    @Override
    public InputStream recordLimitedTimeRawInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        final long millis = (long) (streamInfo.getFrameLength () / streamInfo.getSampleRate () * DefaultRecordSoundService.MS_PER_SECOND);
        final Object stop = new Object ();
        new SleepThread (stop, millis).start ();
        return this.recordRawInputStream (streamInfo, stop);
    }

    private ByteBuffer startRecordingAndReturnByteBuffer (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return this.processor.startRecordingAndReturnByteBuffer (this.audioFormatService.audioFormatfromStreamInfo (streamInfo), stop);
    }

    private List<Sound> recordInBackgroundTask (final StreamInfo streamInfo, final AmplitudeObserver amplitudeObserver, final Object stop) throws SoundTransformException {
        final ByteBuffer targetByteBuffer = this.startRecordingAndReturnByteBuffer (streamInfo, stop);
        final List<Sound> results = new ArrayList<Sound> ();

        final StreamReaderThread<Serializable>  streamReader = this.getStreamReader (streamInfo, targetByteBuffer, amplitudeObserver, results);
        streamReader.start ();
        try {
            Thread.sleep (DefaultRecordSoundService.ARBITRARY_SLEEP_TIME_TO_ENSURE_THE_STREAMING_IS_INITIALIZED);
        } catch (final InterruptedException e) {
            throw new SoundTransformRuntimeException (new SoundTransformException (DefaultRecordSoundServiceErrorCode.NOT_ABLE, e, e.getMessage ()));
        }

        this.stopDetector (stop, streamReader, targetByteBuffer).start ();

        return results;
    }

    private Thread stopDetector (final Object stop, final StreamReaderThread<Serializable>  streamReader, final ByteBuffer targetByteBuffer) {
        return new StopDetectorThread (targetByteBuffer, stop, streamReader);
    }

    @SuppressWarnings ("unchecked")
    private StreamReaderThread<Serializable> getStreamReader (final StreamInfo streamInfo, final ByteBuffer targetByteBuffer, final AmplitudeObserver amplitudeObserver, final List<Sound> results) {
        final AudioFileService<Serializable> audioFileService1 = (AudioFileService<Serializable>) this.audioFileService;
        final InputStreamToSoundService<Serializable> isToSoundService1 = (InputStreamToSoundService<Serializable>) this.isToSoundService;
        final FourierTransformHelper<Serializable> fourierTransformHelper1 = (FourierTransformHelper<Serializable>) this.fourierTransformHelper;
        return new StreamReaderThread<Serializable> (results, amplitudeObserver, streamInfo, targetByteBuffer, audioFileService1, isToSoundService1, fourierTransformHelper1);
    }

    @Override
    public Sound startRecordingASound (final StreamInfo streamInfo, final AmplitudeObserver amplitudeObserver, final Object stop) throws SoundTransformException {
        return new SegmentedSound (streamInfo, this.recordInBackgroundTask (streamInfo, amplitudeObserver, stop));
    }
}
