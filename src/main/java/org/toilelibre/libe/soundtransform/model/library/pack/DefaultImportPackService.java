package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.note.SimpleNoteInfo;
import org.toilelibre.libe.soundtransform.model.library.pack.note.TechnicalInstrument;
import org.toilelibre.libe.soundtransform.model.logging.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent;
import org.toilelibre.libe.soundtransform.model.logging.Observer;

final class DefaultImportPackService extends AbstractLogAware<DefaultImportPackService> implements ImportPackService<AbstractLogAware<DefaultImportPackService>> {

    private static final String     DEFAULT_CHARSET_NAME = "UTF-8";

    private final AddNoteService<?> addNoteService;
    private final PackConfigParser  packConfigParser;
    private final ContextLoader     contextLoader;

    public DefaultImportPackService (final AddNoteService<? extends AddNoteService<?>> addNoteService1, final PackConfigParser packConfigParser1, final ContextLoader contextLoader1) {
        this.addNoteService = addNoteService1;
        this.packConfigParser = packConfigParser1;
        this.contextLoader = contextLoader1;
    }

    private Range fileNotes (final List<Map<String, Object>> list, final String title, final String instrument) throws SoundTransformException {
        final Range range = new Range ();
        for (final Map<String, Object> noteElement : list) {
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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService
     * #getAPack(org.toilelibre.libe.soundtransform.model.library.Library,
     * java.lang.String)
     */
    @Override
    public Pack getAPack (final Library library, final String title) {
        return library.getPack (title);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService
     * #importPack(org.toilelibre.libe.soundtransform.model.library.Library,
     * java.lang.String, java.io.InputStream)
     */
    @Override
    public void importPack (final Library library, final String title, final InputStream inputStream) throws SoundTransformException {
        this.importPack (library, title, null, null, this.readInputStream (inputStream));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService
     * #importPack(org.toilelibre.libe.soundtransform.model.library.Library,
     * java.lang.String, java.lang.Object, java.lang.Class, int)
     */
    @Override
    public void importPack (final Library library, final String title, final Object context, final Class<?> rClass, final int packJsonId) throws SoundTransformException {
        this.importPack (library, title, context, rClass, this.readInputStream (this.contextLoader.read (context, packJsonId)));
    }

    private void importPack (final Library library, final String title, final Object context, final Class<?> rClass, final String jsonContent) throws SoundTransformException {
        this.log (new LogEvent (ImportPackServiceEventCode.STARTING_IMPORT, title));
        final Map<String, List<Map<String, Object>>> map = this.packConfigParser.parse (jsonContent);
        final Pack pack = this.mapToPack (title, map, context, rClass);
        library.addPack (title, pack);
        this.log (new LogEvent (ImportPackServiceEventCode.FINISHED_IMPORT, title));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.model.library.pack.ImportPackService
     * #importPack(org.toilelibre.libe.soundtransform.model.library.Library,
     * java.lang.String, java.lang.String)
     */
    @Override
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
            return new String (contentInBytes, DefaultImportPackService.DEFAULT_CHARSET_NAME);
        } catch (final IOException e) {
            throw new SoundTransformException (ImportPackServiceErrorCode.INVALID_INPUT_STREAM, e);
        }
    }

    @Override
    public DefaultImportPackService setObservers (final Observer... observers1) {
        this.addNoteService.setObservers (observers1);
        return super.setObservers (observers1);
    }

    private Range technicalInstrument (final String title, final String instrument) {
        final Range range = new Range ();
        final TechnicalInstrument technicalInstrument = TechnicalInstrument.of (instrument);
        if (technicalInstrument == null) {
            this.log (new LogEvent (ImportPackServiceEventCode.TECHNICAL_INSTRUMENT_DOES_NOT_EXIST, title, instrument));
        } else {
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_TECHNICAL_INSTRUMENT, title, instrument));
            range.put (Float.valueOf (-1), technicalInstrument.getUniformNote ());
        }
        return range;
    }

    private Range tryToReadNotesFromContext (final List<Map<String, Object>> list, final String title, final String instrument, final Object context, final Class<?> rClass) throws SoundTransformException {
        final Range range = new Range ();
        for (final Map<String, Object> noteElement : list) {
            this.log (new LogEvent (ImportPackServiceEventCode.READING_A_NOTE, title, instrument, noteElement.get ("name")));
            final SimpleNoteInfo noteInfo = new SimpleNoteInfo (noteElement);
            final InputStream is = this.contextLoader.read (context, rClass, noteElement.get ("name").toString ());
            this.addNoteService.addNote (range, noteInfo, is);
        }
        return range;
    }

}
