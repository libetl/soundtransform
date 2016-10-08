package org.toilelibre.libe.soundtransform.infrastructure.service.play.javax;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectException;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;

@Processor
final class LineListenerPlayObjectProcessor implements PlayObjectProcessor {

    private static final String SOUND_PLAYER_MONITOR = "SoundPlayerMonitor";

    LineListenerPlayObjectProcessor () {

    }

    private LineListener addLineListener (final Clip clip, final Object stopMonitor) {
        final LineListener lineListener = new LineListener () {

            @Override
            public void update (final LineEvent event) {
                final LineEvent.Type type = event.getType ();
                if (type == LineEvent.Type.STOP) {
                    synchronized (clip) {
                        if (stopMonitor != null) {
                            synchronized (stopMonitor) {
                                stopMonitor.notifyAll ();
                            }
                        }
                        if (event.getFramePosition () != -1) {
                            clip.stop ();
                            clip.close ();
                            clip.notifyAll ();
                        }
                    }
                }

            }

        };
        clip.addLineListener (lineListener);
        return lineListener;
    }

    private void addSoundPlayerMonitor (final Clip clip, final LineListener lineListener, final Object stopMonitor) {
        if (stopMonitor != null) {
            new Thread (LineListenerPlayObjectProcessor.SOUND_PLAYER_MONITOR) {
                public void run () {
                    synchronized (stopMonitor) {
                        try {
                            stopMonitor.wait ();
                        } catch (InterruptedException e) {
                        }
                        lineListener.update (new LineEvent (clip, LineEvent.Type.STOP, -1));
                    }
                }
            }.start ();
        }
    }

    @Override
    public Object play (final InputStream ais, final StreamInfo streamInfo, final Object stopMonitor, final int skipMilliSeconds) throws PlayObjectException {
        final Clip clip = this.prepareClip (ais, stopMonitor, skipMilliSeconds);
        clip.start ();
        return clip;
    }

    private Clip prepareClip (final InputStream ais, final Object stopMonitor, final int skipMilliSeconds) throws PlayObjectException {
        this.ensureCompatibleInputStream (ais);
        try {
            final Clip clip = this.getClip ();
            this.addSoundPlayerMonitor (clip, this.addLineListener (clip, stopMonitor), stopMonitor);
            final AudioInputStream aisCasted = (AudioInputStream) ais;
            final int framePosition = (int) (aisCasted.getFormat ().getSampleRate () * 1.0 * skipMilliSeconds / 1000.0);
            clip.open (aisCasted);
            
            clip.setFramePosition (framePosition);
            return clip;
        } catch (final LineUnavailableException lineUnavailableException) {
            throw new PlayObjectException (lineUnavailableException);
        } catch (final IOException e) {
            throw new PlayObjectException (e);
        } catch (final IllegalArgumentException e) {
            throw new PlayObjectException (e);
        }
    }

    private Clip getClip () throws LineUnavailableException {
        final Line.Info linfo = new Line.Info (Clip.class);
        final Line line = this.getLine (linfo);
        return (Clip) line;

    }

    private void ensureCompatibleInputStream (final InputStream ais) throws PlayObjectException {

        if (! (ais instanceof AudioInputStream)) {
            throw new PlayObjectException (new IllegalArgumentException (ais == null ? "null" : ais.toString ()));
        }
    }

    private Line getLine (final Info linfo) throws LineUnavailableException {
        return AudioSystem.getLine (linfo);
    }

}
