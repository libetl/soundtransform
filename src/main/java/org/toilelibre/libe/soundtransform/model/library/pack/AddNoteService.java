package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.File;
import java.io.FileNotFoundException;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;

public class AddNoteService {

    enum AddNoteErrorCode implements ErrorCode {
        FILE_NOT_FOUND ("%1s not found"), COULD_NOT_BE_PARSED ("%1s could not be parsed as an ADSR note"), NOT_READABLE ("%1s could not be read"), NOT_SUPPORTED ("%1s is not yet a supported sound file"), ;

        private String messageFormat;

        AddNoteErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    public AddNoteService () {

    }

    public void addNote (final Range range, final String fileName) throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        try {
            final java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
            if (completeURL == null) {
                throw new SoundTransformException (AddNoteErrorCode.FILE_NOT_FOUND, new FileNotFoundException (fileName), fileName);
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = $.create (Sound2NoteService.class).convert (fileName, $.create (TransformInputStreamService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (file)));
            range.put (n.getFrequency (), n);
        } catch (final IllegalArgumentException e) {
            throw new SoundTransformException (AddNoteErrorCode.COULD_NOT_BE_PARSED, e, fileName);
        }

    }

    public void addNote (final Range range, final String fileName, final int frequency) throws SoundTransformException {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        try {
            final java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
            if (completeURL == null) {
                System.err.println (fileName + " not found");
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = $.create (Sound2NoteService.class).convert (fileName, $.create (TransformInputStreamService.class).fromInputStream ($.create (ConvertAudioFileService.class).callConverter (file)), frequency);
            range.put (n.getFrequency (), n);
        } catch (final IllegalArgumentException e) {
            throw new SoundTransformException (AddNoteErrorCode.COULD_NOT_BE_PARSED, e, fileName);
        }

    }

    public void addNotes (final Range range, final String... fileNames) throws SoundTransformException {
        for (final String fileName : fileNames) {
            this.addNote (range, fileName);
        }
    }
}
