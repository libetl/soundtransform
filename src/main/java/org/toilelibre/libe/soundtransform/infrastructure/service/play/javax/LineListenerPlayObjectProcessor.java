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

import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectException;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;

final class LineListenerPlayObjectProcessor implements PlayObjectProcessor {

    public LineListenerPlayObjectProcessor () {

    }

    private void addLineListener (final Clip clip) {
        clip.addLineListener (new LineListener () {

            @Override
            public void update (final LineEvent event) {
                final LineEvent.Type type = event.getType ();
                if (type == LineEvent.Type.STOP) {
                    synchronized (clip) {
                        clip.stop ();
                        clip.close ();
                        clip.notifyAll ();
                    }
                }

            }

        });

    }

    @Override
    public Object play (final InputStream ais, final StreamInfo streamInfo) throws PlayObjectException {
        try {
            final Clip clip = this.prepareClip (ais);
            clip.start ();
            synchronized (clip) {
                while (clip.isOpen ()) {
                    clip.wait ();
                }
            }
            return clip;
        } catch (final InterruptedException e) {
            throw new PlayObjectException (e);
        }
    }

    private Clip prepareClip (final InputStream ais) throws PlayObjectException {
        if (!(ais instanceof AudioInputStream)) {
            throw new PlayObjectException (new IllegalArgumentException (ais == null ? "null" : ais.toString ()));
        }
        try {
            final Line.Info linfo = new Line.Info (Clip.class);
            final Line line = this.getLine (linfo);
            final Clip clip = (Clip) line;
            this.addLineListener (clip);
            clip.open ((AudioInputStream) ais);

            return clip;
        } catch (final LineUnavailableException lineUnavailableException) {
            throw new PlayObjectException (lineUnavailableException);
        } catch (final IOException e) {
            throw new PlayObjectException (e);
        } catch (final IllegalArgumentException e) {
            throw new PlayObjectException (e);
        }
    }

    private Line getLine (final Info linfo) throws LineUnavailableException {
        return AudioSystem.getLine (linfo);
    }

}
