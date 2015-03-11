package org.toilelibre.libe.soundtransform.infrastructure.service.play.javax;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class LineListenerPlaySoundProcessor implements PlaySoundProcessor {

    public LineListenerPlaySoundProcessor () {

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
                        clip.notify ();
                    }
                }

            }

        });

    }

    @Override
    public Object play (final InputStream ais) throws PlaySoundException {
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
            throw new PlaySoundException (e);
        }
    }

    private Clip prepareClip (final InputStream ais) throws PlaySoundException {
        if (!(ais instanceof AudioInputStream)) {
            throw new PlaySoundException (new IllegalArgumentException ("" + ais));
        }
        try {
            final Line.Info linfo = new Line.Info (Clip.class);
            final Line line = AudioSystem.getLine (linfo);
            final Clip clip = (Clip) line;
            this.addLineListener (clip);
            clip.open ((AudioInputStream) ais);

            return clip;
        } catch (final LineUnavailableException lineUnavailableException) {
            throw new PlaySoundException (lineUnavailableException);
        } catch (final IOException e) {
            throw new PlaySoundException (e);
        } catch (final IllegalArgumentException e) {
            throw new PlaySoundException (e);
        }
    }

}
