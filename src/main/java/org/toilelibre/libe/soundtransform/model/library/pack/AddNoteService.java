package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;

public class AddNoteService {

    public static void addNote (final Range range, final String fileName) {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        try {
            final java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
            if (completeURL == null) {
                System.err.println (fileName + " not found");
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = Sound2NoteService.convert (fileName, new TransformInputStreamService ().fromInputStream (new ConvertAudioFileService ().callConverter (file)));
            range.put (n.getFrequency (), n);
        } catch (final UnsupportedAudioFileException e) {
        } catch (final IllegalArgumentException e) {
            System.err.println (fileName + " could not be parsed as an ADSR note");
        } catch (final IOException e) {
        }

    }

    public static void addNote (final Range range, final String fileName, final int frequency) {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        try {
            final java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
            if (completeURL == null) {
                System.err.println (fileName + " not found");
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = Sound2NoteService.convert (fileName, new TransformInputStreamService ().fromInputStream (new ConvertAudioFileService ().callConverter (file)), frequency);
            range.put (n.getFrequency (), n);
        } catch (final UnsupportedAudioFileException e) {
        } catch (final IllegalArgumentException e) {
            System.err.println (fileName + " could not be parsed as an ADSR note");
        } catch (final IOException e) {
        }

    }

    public static void addNotes (final Range range, final String... fileNames) {
        for (final String fileName : fileNames) {
            AddNoteService.addNote (range, fileName);
        }
    }
}
