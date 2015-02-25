package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

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
        EXPECTED_A_FREQUENCY ("%1s is not an Integer, could not know which frequency was expected");

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
                LogLevel.VERBOSE, "%1s, finished the analysis of an instrument : %2s"), FINISHED_IMPORT (LogLevel.VERBOSE, "finished the import of a pack : %1s"), READING_A_TECHNICAL_INSTRUMENT (LogLevel.VERBOSE, "%1s, reading a technical instrument : %2s"), TECHNICAL_INSTRUMENT_DOES_NOT_EXIST (LogLevel.WARN, "%1s, the technical instrument : %2s does not exist");

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

    private final AddNoteService   addNoteService;
    private final PackConfigParser packConfigParser;

    public ImportPackService (final AddNoteService addNoteService1, final PackConfigParser packConfigParser1) {
        this (addNoteService1, packConfigParser1, new Observer [0]);
    }

    public ImportPackService (final AddNoteService addNoteService1, final PackConfigParser packConfigParser1, final Observer... observers1) {
        this.observers = observers1.clone ();
        this.addNoteService = addNoteService1.setObservers (this.observers);
        this.packConfigParser = packConfigParser1;
    }

    private Range fileNotes (final Map<String, String> notes, final String title, final String instrument) throws SoundTransformException {
        final Range range = new Range ();
        for (final Entry<String, String> notesEntry : notes.entrySet ()) {
            int frequency;
            try {
                frequency = Integer.parseInt (notesEntry.getKey ());
            } catch (final NumberFormatException nfe) {
                throw new SoundTransformException (ImportPackServiceErrorCode.EXPECTED_A_FREQUENCY, nfe, notesEntry.getKey ());
            }
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_NOTE, title, instrument, notesEntry.getValue ()));
            if (frequency > 0) {
                this.addNoteService.addNote (range, notesEntry.getValue (), frequency);
            } else {
                this.addNoteService.addNote (range, notesEntry.getValue ());
            }
        }
        return range;
    }

    private Range fillInstrument (final Map<String, String> notes, final String title, final String instrument) throws SoundTransformException {
        if (notes.isEmpty ()) {
            return this.technicalInstrument (title, instrument);
        }
        return this.fileNotes (notes, title, instrument);
    }

    public Pack getAPack (final Library library, final String title) {
        return library.getPack (title);
    }
    
    public void importPack (final Library library, final String title, final InputStream inputStream) throws SoundTransformException {
        final Scanner scanner = new Scanner (inputStream, Charset.defaultCharset ().name ());
        final String content = scanner.useDelimiter ("\\Z").next ();
        scanner.close ();
        this.importPack (library, title, content);
    }

    public void importPack (final Library library, final String title, final String jsonContent) throws SoundTransformException {
        this.log (new LogEvent (ImportPackServiceEventCode.STARTING_IMPORT, title));
        final Map<String, Map<String, String>> map = this.packConfigParser.parse (jsonContent);
        final Pack pack = this.mapToPack (title, map);
        library.addPack (title, pack);
        this.log (new LogEvent (ImportPackServiceEventCode.FINISHED_IMPORT, title));
    }

    private Pack mapToPack (final String title, final Map<String, Map<String, String>> map) throws SoundTransformException {
        final Pack pack = new Pack ();
        for (final Entry<String, Map<String, String>> instrument : map.entrySet ()) {
            this.log (new LogEvent (ImportPackServiceEventCode.STARTING_ANALYSIS_OF_AN_INSTRUMENT, title, instrument.getKey ()));
            pack.put (instrument.getKey (), this.fillInstrument (instrument.getValue (), title, instrument.getKey ()));
            this.log (new LogEvent (ImportPackServiceEventCode.FINISHED_ANALYSIS_OF_AN_INSTRUMENT, title, instrument.getKey ()));
        }
        return pack;
    }

    private Range technicalInstrument (final String title, final String instrument) {
        final Range range = new Range ();
        final TechnicalInstrument technicalInstrument = TechnicalInstrument.of (instrument);
        if (technicalInstrument != null) {
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_TECHNICAL_INSTRUMENT, title, instrument));
            range.put (Float.valueOf (-1), technicalInstrument.getUniformNote ());
        }else {
            this.log (new LogEvent (ImportPackServiceEventCode.TECHNICAL_INSTRUMENT_DOES_NOT_EXIST, title, instrument));
        }
        return range;
    }

    @Override
    public ImportPackService setObservers (final Observer... observers1) {
        this.addNoteService.setObservers (observers1);
        return super.setObservers (observers1);
    }

}
