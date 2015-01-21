package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
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

    private final AddNoteService   addNoteService;
    private final PackConfigParser packConfigParser;
    private Observer []            observers;

    public ImportPackService () {
        this.addNoteService = $.create (AddNoteService.class).setObservers (this.observers);
        this.packConfigParser = $.select (PackConfigParser.class);
    }

    public void importPack (Library library, String title, InputStream inputStream) throws SoundTransformException {
        final Scanner scanner = new Scanner (inputStream);
        final String content = scanner.useDelimiter ("\\Z").next ();
        scanner.close ();
        this.importPack (library, title, content);
    }

    public void importPack (Library library, String title, String jsonContent) throws SoundTransformException {
        final Map<String, Map<String, String>> map = this.packConfigParser.parse (jsonContent);
        final Pack pack = this.mapToPack (map);
        library.addPack (title, pack);
    }

    private Pack mapToPack (Map<String, Map<String, String>> map) throws SoundTransformException {
        final Pack pack = new Pack ();
        for (final String instrument : map.keySet ()) {
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
                if (frequency > 0) {
                    this.addNoteService.addNote (range, notes.get (frequencyAsString), frequency);
                } else {
                    this.addNoteService.addNote (range, notes.get (frequencyAsString));
                }
            }
        }
        return pack;
    }

}
