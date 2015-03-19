package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.SoundToNoteService;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

class DefaultAddNoteService extends AbstractLogAware<DefaultAddNoteService> implements AddNoteService<AbstractLogAware<DefaultAddNoteService>> {

    private final SoundToNoteService         sound2NoteService;
    private final InputStreamToSoundService<?> inputStreamToSoundService;
    private final AudioFileService          convertAudioFileService;
    private final AudioFileHelper           audioFileHelper;
    private final AudioFormatParser         audioFormatParser;

    public DefaultAddNoteService (final SoundToNoteService sound2NoteService1, final InputStreamToSoundService<InputStreamToSoundService<?>> inputStreamToSoundService1, final AudioFileService convertAudioFileService1, final AudioFileHelper audioFileHelper1, final AudioFormatParser audioFormatParser1) {
        this.sound2NoteService = sound2NoteService1;
        this.inputStreamToSoundService = inputStreamToSoundService1;
        this.convertAudioFileService = convertAudioFileService1;
        this.audioFileHelper = audioFileHelper1;
        this.audioFormatParser = audioFormatParser1;
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.library.pack.addNoteService#addNote(org.toilelibre.libe.soundtransform.model.library.pack.Range, org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo, java.io.InputStream)
     */
    @Override
    public void addNote (final Range range, final SimpleNoteInfo noteInfo, final InputStream is) throws SoundTransformException {
        try {
            final InputStream ais = this.audioFileHelper.getAudioInputStream (is);
            final StreamInfo si = this.audioFormatParser.getStreamInfo (ais);
            final Note n = this.sound2NoteService.convert (noteInfo, this.inputStreamToSoundService.fromInputStream (ais, si));
            range.put (n.getFrequency (), n);
        } catch (final SoundTransformException e) {
            throw new SoundTransformException (AddNoteErrorCode.COULD_NOT_BE_PARSED, e, noteInfo.getName ());
        }
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.library.pack.addNoteService#addNote(org.toilelibre.libe.soundtransform.model.library.pack.Range, org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo)
     */
    @Override
    public void addNote (final Range range, final SimpleNoteInfo noteInfo) throws SoundTransformException {
        final URL completeURL = this.getURL (noteInfo.getName ());
        try {
            if (completeURL == null) {
                this.log (new LogEvent (AddNoteEventCode.FILE_NOT_FOUND, noteInfo.getName ()));
                return;
            }
            final String completeFileName = completeURL.getFile ();
            final File file = new File (completeFileName);
            final Note n = this.sound2NoteService.convert (noteInfo, this.inputStreamToSoundService.fromInputStream (this.convertAudioFileService.streamFromFile (file)));
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
                if (tmpFile.exists ()) {
                    completeURL = tmpFile.toURI ().toURL ();
                }
            } catch (final MalformedURLException e) {
                this.log (new LogEvent (AddNoteEventCode.NOT_A_FILESYSTEM_ENTRY, fileName, e));
            }
        }
        return completeURL;
    }

    @Override
    public DefaultAddNoteService setObservers (final Observer... observers1) {
        this.inputStreamToSoundService.setObservers (observers1);
        return super.setObservers (observers1);
    }

}
