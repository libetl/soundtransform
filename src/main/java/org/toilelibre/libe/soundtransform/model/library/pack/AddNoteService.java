package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.inputstream.TransformInputStreamService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;

public class AddNoteService {

	public static void addNotes (Range range, String... fileNames) {
		for (String fileName : fileNames) {
			AddNoteService.addNote (range, fileName);
		}
	}

	public static void addNote (Range range, String fileName) {
		ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		try {
			java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
			if (completeURL == null) {
				System.err.println (fileName + " not found");
				return;
			}
			String completeFileName = completeURL.getFile ();
			File file = new File (completeFileName);
			Note n = Sound2NoteService.convert (fileName, 
					new TransformInputStreamService ().fromInputStream (
							new ConvertAudioFileService ().callConverter (file)));
			range.put (n.getFrequency (), n);
		} catch (UnsupportedAudioFileException e) {
		} catch (IllegalArgumentException e) {
			System.err.println (fileName + " could not be parsed as an ADSR note");
		} catch (IOException e) {
		}

	}

	public static void addNote (Range range, String fileName, int frequency) {
		ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		try {
			java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
			if (completeURL == null) {
				System.err.println (fileName + " not found");
				return;
			}
			String completeFileName = completeURL.getFile ();
			File file = new File (completeFileName);
			Note n = Sound2NoteService.convert (fileName, new TransformInputStreamService ().fromInputStream (
					new ConvertAudioFileService ().callConverter (file)), frequency);
			range.put (n.getFrequency (), n);
		} catch (UnsupportedAudioFileException e) {
		} catch (IllegalArgumentException e) {
			System.err.println (fileName + " could not be parsed as an ADSR note");
		} catch (IOException e) {
		}

	}
}
