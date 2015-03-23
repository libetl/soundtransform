package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public interface AddNoteService<T> extends LogAware<T> {

    public enum AddNoteErrorCode implements ErrorCode {
        COULD_NOT_BE_PARSED ("%1s could not be parsed as an ADSR note"), NOT_READABLE ("%1s could not be read"), NOT_SUPPORTED ("%1s is not yet a supported sound file"), ;

        private final String messageFormat;

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

        private final String   messageFormat;
        private final LogLevel logLevel;

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

    public abstract void addNote (Range range, SimpleNoteInfo noteInfo, InputStream is) throws SoundTransformException;

    public abstract void addNote (Range range, SimpleNoteInfo noteInfo) throws SoundTransformException;

}