package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class AddNoteService extends AbstractLogAware<AddNoteService> {

    public enum AddNoteErrorCode implements ErrorCode {
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

    public enum AddNoteEventCode implements EventCode {
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
    private final AudioFileHelper             audioFileHelper;
    private final AudioFormatParser           audioFormatParser;

    public AddNoteService (final Sound2NoteService sound2NoteService1, final TransformInputStreamService transformInputStreamService1, final ConvertAudioFileService convertAudioFileService1, final AudioFileHelper audioFileHelper1, final AudioFormatParser audioFormatParser1) {
        this (sound2NoteService1, transformInputStreamService1, convertAudioFileService1, audioFileHelper1, audioFormatParser1, new Observer [0]);
    }

    public AddNoteService (final Sound2NoteService sound2NoteService1, final TransformInputStreamService transformInputStreamService1, final ConvertAudioFileService convertAudioFileService1, final AudioFileHelper audioFileHelper1, final AudioFormatParser audioFormatParser1, final Observer... observers1) {
        this.sound2NoteService = sound2NoteService1;
        this.transformInputStreamService = transformInputStreamService1.setObservers (observers1);
        this.convertAudioFileService = convertAudioFileService1;
        this.audioFileHelper = audioFileHelper1;
        this.audioFormatParser = audioFormatParser1;
        this.observers = observers1;
    }
    
    public void addNote (final Range range, final SimpleNoteInfo noteInfo, final InputStream is) throws SoundTransformException {
        try {
            final InputStream ais = this.audioFileHelper.getAudioInputStream (is);
            final StreamInfo si = this.audioFormatParser.getSoundInfo (ais);
            final Note n = this.sound2NoteService.convert (noteInfo, this.transformInputStreamService.fromInputStream (ais, si));
            range.put (n.getFrequency (), n);
        } catch (final SoundTransformException e) {
            throw new SoundTransformException (AddNoteErrorCode.COULD_NOT_BE_PARSED, e, noteInfo.getName ());
        }
    }
    
    public void addNote (final Range range, final SimpleNoteInfo noteInfo) throws SoundTransformException {
        final URL completeURL = this.getURL (noteInfo.getName ());
        try {
            if (completeURL == null) {
                this.log (new LogEvent (AddNoteEventCode.FILE_NOT_FOUND, noteInfo.getName ()));
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = this.sound2NoteService.convert (noteInfo, this.transformInputStreamService.fromInputStream (this.convertAudioFileService.callConverter (file)));
            range.put (n.getFrequency (), n);
        } catch (final IllegalArgumentException e) {
            throw new SoundTransformException (AddNoteErrorCode.COULD_NOT_BE_PARSED, e, noteInfo.getName ());
        }

    }

    private URL getURL (final String fileName) {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        URL completeURL = classLoader.getResource (fileName);
        if (completeURL == null) {
            this.log (new LogEvent (AddNoteEventCode.NOT_A_CLASSPATH_RESOURCE, fileName));
            try {
                final File tmpFile = new File (fileName);
                if (tmpFile.exists ()){
                    completeURL = tmpFile.toURI ().toURL ();
                }
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
