package soundtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.sound.sampled.UnsupportedAudioFileException;

public class TestTransformMain {

	private static void copyFile(File source, File dest)
	        throws IOException {
	    FileChannel inputChannel = null;
	    FileChannel outputChannel = null;
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } finally {
	        inputChannel.close();
	        outputChannel.close();
	    }
	}	
	public static void main (String [] args) {
		try {
			File fParam = new File (args [0]);
			File fOrigin = new File ("before.wav");
			File fDest = new File ("after.wav");
			TestTransformMain.copyFile (fParam, fOrigin);
			new TransformSound (new PrintlnTransformObserver ()).transformWav (fOrigin, fDest, 
					new NormalizeSoundTransformation ());
		} catch (FileNotFoundException e) {
			e.printStackTrace ();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
