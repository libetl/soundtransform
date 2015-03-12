package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.TechnicalInstrument;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class ImportPackService extends AbstractLogAware<ImportPackService> {
    public enum ImportPackServiceErrorCode implements ErrorCode {
        EXPECTED_A_FREQUENCY ("%1s is not an Integer, could not know which frequency was expected"), EMPTY_INPUT_STREAM ("No input stream to read while trying to import a pack"),
        INVALID_INPUT_STREAM ("Invalid input stream");

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

    private static final String    DEFAULT_CHARSET_NAME = "UTF-8";

    private final AddNoteService   addNoteService;
    private final PackConfigParser packConfigParser;
    private final ContextLoader    contextLoader;

    public ImportPackService (final AddNoteService addNoteService1, final PackConfigParser packConfigParser1, final ContextLoader contextLoader1) {
        this (addNoteService1, packConfigParser1, contextLoader1, new Observer [0]);
    }

    public ImportPackService (final AddNoteService addNoteService1, final PackConfigParser packConfigParser1, final ContextLoader contextLoader1, final Observer... observers1) {
        this.observers = observers1.clone ();
        this.addNoteService = addNoteService1.setObservers (this.observers);
        this.packConfigParser = packConfigParser1;
        this.contextLoader = contextLoader1;
    }

    private Range fileNotes (final List<Map<String, Object>> list, final String title, final String instrument) throws SoundTransformException {
        final Range range = new Range ();
        for (Map<String, Object> noteElement : list){
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_NOTE, title, instrument, noteElement.get ("name")));
            this.addNoteService.addNote (range, new SimpleNoteInfo (noteElement));
        }
        return range;
    }

    private Range fillInstrument (final List<Map<String, Object>> list, final String title, final String instrument, final Object context, final Class<?> rClass) throws SoundTransformException {
        if (list.isEmpty ()) {
            return this.technicalInstrument (title, instrument);
        }
        if (context != null && rClass != null) {
            return this.tryToReadNotesFromContext (list, title, instrument, context, rClass);
        }
        return this.fileNotes (list, title, instrument);
    }

    public Pack getAPack (final Library library, final String title) {
        return library.getPack (title);
    }

    public void importPack (final Library library, final String title, final InputStream inputStream) throws SoundTransformException {
        this.importPack (library, title, null, null, this.readInputStream (inputStream));
    }

    public void importPack (final Library library, final String title, final Object context, final Class<?> rClass, final int packJsonId) throws SoundTransformException {
        this.importPack (library, title, context, rClass, this.readInputStream (this.contextLoader.read (context, rClass, packJsonId)));
    }

    private void importPack (final Library library, final String title, final Object context, final Class<?> rClass, final String jsonContent) throws SoundTransformException {
        this.log (new LogEvent (ImportPackServiceEventCode.STARTING_IMPORT, title));
        final Map<String, List<Map<String, Object>>> map = this.packConfigParser.parse (jsonContent);
        final Pack pack = this.mapToPack (title, map, context, rClass);
        library.addPack (title, pack);
        this.log (new LogEvent (ImportPackServiceEventCode.FINISHED_IMPORT, title));
    }

    public void importPack (final Library library, final String title, final String jsonContent) throws SoundTransformException {
        this.importPack (library, title, null, null, jsonContent);
    }

    private Pack mapToPack (final String title, final Map<String, List<Map<String, Object>>> map, final Object context, final Class<?> rClass) throws SoundTransformException {
        final Pack pack = new Pack ();
        for (final Entry<String, List<Map<String, Object>>> instrument : map.entrySet ()) {
            this.log (new LogEvent (ImportPackServiceEventCode.STARTING_ANALYSIS_OF_AN_INSTRUMENT, title, instrument.getKey ()));
            pack.put (instrument.getKey (), this.fillInstrument (instrument.getValue (), title, instrument.getKey (), context, rClass));
            this.log (new LogEvent (ImportPackServiceEventCode.FINISHED_ANALYSIS_OF_AN_INSTRUMENT, title, instrument.getKey ()));
        }
        return pack;
    }

    private String readInputStream (final InputStream inputStream) throws SoundTransformException {
        if (inputStream == null) {
            throw new SoundTransformException (ImportPackServiceErrorCode.EMPTY_INPUT_STREAM, new NullPointerException ());
        }
        try {
            final byte [] contentInBytes = new byte [inputStream.available ()];
            inputStream.read (contentInBytes);
            return new String (contentInBytes, ImportPackService.DEFAULT_CHARSET_NAME);
        } catch (IOException e) {
            throw new SoundTransformException (ImportPackServiceErrorCode.INVALID_INPUT_STREAM, e);
        }
    }

    @Override
    public ImportPackService setObservers (final Observer... observers1) {
        this.addNoteService.setObservers (observers1);
        return super.setObservers (observers1);
    }

    private Range technicalInstrument (final String title, final String instrument) {
        final Range range = new Range ();
        final TechnicalInstrument technicalInstrument = TechnicalInstrument.of (instrument);
        if (technicalInstrument != null) {
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_TECHNICAL_INSTRUMENT, title, instrument));
            range.put (Float.valueOf (-1), technicalInstrument.getUniformNote ());
        } else {
            this.log (new LogEvent (ImportPackServiceEventCode.TECHNICAL_INSTRUMENT_DOES_NOT_EXIST, title, instrument));
        }
        return range;
    }

    private Range tryToReadNotesFromContext (final List<Map<String, Object>> list, final String title, final String instrument, final Object context, final Class<?> rClass) throws SoundTransformException {
        final Range range = new Range ();
        for (Map<String, Object> noteElement : list){
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_NOTE, title, instrument, noteElement.get ("name")));
            final SimpleNoteInfo noteInfo = new SimpleNoteInfo (noteElement);
            final InputStream is = this.contextLoader.read (context, rClass, noteElement.get ("name").toString ());
            this.addNoteService.addNote (range, noteInfo, is);
        }
        return range;
    }

}
