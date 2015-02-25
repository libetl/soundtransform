package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class AddNoteService extends AbstractLogAware<AddNoteService> {

    enum AddNoteErrorCode implements ErrorCode {
        COULD_NOT_BE_PARSED ("%1s could not be parsed as an ADSR note"), NOT_READABLE ("%1s could not be read"), NOT_SUPPORTED ("%1s is not yet a supported sound file"), ;

        private String messageFormat;

        AddNoteErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    enum AddNoteEventCode implements EventCode {
        FILE_NOT_FOUND (LogLevel.ERROR, "%1s not found"), NOT_A_CLASSPATH_RESOURCE (LogLevel.WARN, "%1s is not a classpath resource"), NOT_A_FILESYSTEM_ENTRY (LogLevel.ERROR, "%1s is not a filesystem entry (%2s)");

        private String   messageFormat;
        private LogLevel logLevel;

        AddNoteEventCode (final LogLevel ll, final String mF) {
            this.messageFormat = mF;
            this.logLevel = ll;
        }

        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final Sound2NoteService           sound2NoteService;
    private final TransformInputStreamService transformInputStreamService;
    private final ConvertAudioFileService     convertAudioFileService;

    public AddNoteService (final Sound2NoteService sound2NoteService1, final TransformInputStreamService transformInputStreamService1, final ConvertAudioFileService convertAudioFileService1) {
        this (sound2NoteService1, transformInputStreamService1, convertAudioFileService1, new Observer [0]);
    }

    public AddNoteService (final Sound2NoteService sound2NoteService1, final TransformInputStreamService transformInputStreamService1, final ConvertAudioFileService convertAudioFileService1, final Observer... observers1) {
        this.sound2NoteService = sound2NoteService1;
        this.transformInputStreamService = transformInputStreamService1.setObservers (observers1);
        this.convertAudioFileService = convertAudioFileService1;
        this.observers = observers1;
    }

    public void addNote (final Range range, final String fileName) throws SoundTransformException {
        final URL completeURL = this.getURL (fileName);
        try {
            if (completeURL == null) {
                this.log (new LogEvent (AddNoteEventCode.FILE_NOT_FOUND, fileName));
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = this.sound2NoteService.convert (fileName, this.transformInputStreamService.fromInputStream (this.convertAudioFileService.callConverter (file)));
            range.put (n.getFrequency (), n);
        } catch (final IllegalArgumentException e) {
            throw new SoundTransformException (AddNoteErrorCode.COULD_NOT_BE_PARSED, e, fileName);
        }

    }

    public void addNote (final Range range, final String fileName, final int frequency) throws SoundTransformException {
        final URL completeURL = this.getURL (fileName);
        try {
            if (completeURL == null) {
                this.log (new LogEvent (AddNoteEventCode.FILE_NOT_FOUND, fileName));
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = this.sound2NoteService.convert (fileName, this.transformInputStreamService.fromInputStream (this.convertAudioFileService.callConverter (file)), frequency);
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

    private URL getURL (final String fileName) {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        URL completeURL = classLoader.getResource (fileName);
        if (completeURL == null) {
            this.log (new LogEvent (AddNoteEventCode.NOT_A_CLASSPATH_RESOURCE, fileName));
            try {
                completeURL = new File (fileName).toURI ().toURL ();
            } catch (final MalformedURLException e) {
                this.log (new LogEvent (AddNoteEventCode.NOT_A_FILESYSTEM_ENTRY, fileName, e));
            }
        }
        return completeURL;
    }

    @Override
    public AddNoteService setObservers (final Observer... observers1) {
        this.transformInputStreamService.setObservers (observers1);
        return super.setObservers (observers1);
    }

}
