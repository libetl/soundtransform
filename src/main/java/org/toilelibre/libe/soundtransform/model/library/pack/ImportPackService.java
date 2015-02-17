package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
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
                LogLevel.VERBOSE, "%1s, finished the analysis of an instrument : %2s"), FINISHED_IMPORT (LogLevel.VERBOSE, "finished the import of a pack : %1s");

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

    public ImportPackService (AddNoteService addNoteService1, PackConfigParser packConfigParser1) {
        this (addNoteService1, packConfigParser1, null);
    }

    public ImportPackService (AddNoteService addNoteService1, PackConfigParser packConfigParser1, final Observer [] observers1) {
        this.observers = observers1;
        this.addNoteService = addNoteService1.setObservers (this.observers);
        this.packConfigParser = packConfigParser1;
    }

    public void importPack (final Library library, final String title, final InputStream inputStream) throws SoundTransformException {
        final Scanner scanner = new Scanner (inputStream);
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
        for (final String instrument : map.keySet ()) {
            this.log (new LogEvent (ImportPackServiceEventCode.STARTING_ANALYSIS_OF_AN_INSTRUMENT, title, instrument));
            final Range range = new Range ();
            pack.put (instrument, range);
            final Map<String, String> notes = map.get (instrument);
            for (final String frequencyAsString : notes.keySet ()) {
                int frequency;
                try {
                    frequency = Integer.parseInt (frequencyAsString);
                } catch (final NumberFormatException nfe) {
                    throw new SoundTransformException (ImportPackServiceErrorCode.EXPECTED_A_FREQUENCY, nfe, frequencyAsString);
                }
                this.log (new LogEvent (ImportPackServiceEventCode.READING_A_NOTE, title, instrument, notes.get (frequencyAsString)));
                if (frequency > 0) {
                    this.addNoteService.addNote (range, notes.get (frequencyAsString), frequency);
                } else {
                    this.addNoteService.addNote (range, notes.get (frequencyAsString));
                }
            }
            this.log (new LogEvent (ImportPackServiceEventCode.FINISHED_ANALYSIS_OF_AN_INSTRUMENT, title, instrument));
        }
        return pack;
    }

}
