package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.logging.EventCode;
import org.toilelibre.libe.soundtransform.model.logging.LogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

public interface ImportPackService<T> extends LogAware<T> {

    public enum ImportPackServiceErrorCode implements ErrorCode {
        EXPECTED_A_FREQUENCY ("%1s is not an Integer, could not know which frequency was expected"), EMPTY_INPUT_STREAM ("No input stream to read while trying to import a pack"), INVALID_INPUT_STREAM ("Invalid input stream");

        private final String messageFormat;

        ImportPackServiceErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    public enum ImportPackServiceEventCode implements EventCode {
        STARTING_IMPORT (LogLevel.VERBOSE, "starting the import of a pack : %1s"), STARTING_ANALYSIS_OF_AN_INSTRUMENT (LogLevel.VERBOSE, "%1s, starting the analysis of an instrument : %2s"), READING_A_NOTE (LogLevel.VERBOSE, "%1s, instrument %2s, reading a note : %3s"), FINISHED_ANALYSIS_OF_AN_INSTRUMENT (
                LogLevel.VERBOSE, "%1s, finished the analysis of an instrument : %2s"), FINISHED_IMPORT (LogLevel.VERBOSE, "finished the import of a pack : %1s"), READING_A_TECHNICAL_INSTRUMENT (LogLevel.VERBOSE, "%1s, reading a technical instrument : %2s"), TECHNICAL_INSTRUMENT_DOES_NOT_EXIST (
                LogLevel.WARN, "%1s, the technical instrument : %2s does not exist"), COULD_NOT_READ_A_NOTE (LogLevel.ERROR, "Could not read a note : %s");

        private final String   messageFormat;
        private final LogLevel logLevel;

        ImportPackServiceEventCode (final LogLevel ll, final String mF) {
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

    Pack getAPack (Library library, String title);

    void importPack (Library library, String title, InputStream inputStream) throws SoundTransformException;

    void importPack (Library library, String title, Object context, Class<?> rClass, int packJsonId) throws SoundTransformException;

    void importPack (Library library, String title, String jsonContent) throws SoundTransformException;

}