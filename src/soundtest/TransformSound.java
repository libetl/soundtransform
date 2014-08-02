package soundtest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class TransformSound {

	TransformObserver[] observers = new TransformObserver [0];
	
	public TransformSound (TransformObserver... observers) {
		this.observers = observers;
		for (TransformObserver observer : observers){
			this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
		}
    }

	public TransformSound () {
    }

	public AudioInputStream transformAudioStream (AudioInputStream ais, SoundTransformation... sts) throws IOException {
		Sound [] input = this.fromAudioInputStream (ais);
		Sound [] output = Arrays.copyOf (input, input.length);
		int transformNumber = 0;
		for (SoundTransformation st : sts){
			for (int i = 0; i < input.length; i++) {
				this.notifyAll ("Transform n°" + (transformNumber + 1) + "/" + sts.length + " (" +
			    st.getClass ().getSimpleName ()
			    + "), channel n°" + (i + 1) + "/" + input.length);
				output [i] = st.transform (output [i]);
			}
			transformNumber++;
		}
		if (sts.length == 0){
			output = input;
		}
		this.notifyAll ("Transforms done");
		return this.toStream (output, ais.getFormat ());

	}

	private void notifyAll (String s){
		for (TransformObserver to : this.observers){
			to.notify (s);
		}
	}
	
	private Sound [] fromAudioInputStream (AudioInputStream ais) throws IOException {
		this.notifyAll ("Converting input into java object");
		int channels = ais.getFormat ().getChannels();
		int currentChannel = 0;
		Sound [] ret = new Sound [channels];
		int length = (int) (ais.getFrameLength() / channels);
		for (int channel = 0 ; channel < channels ; channel++){
			ret [channel] = new Sound (new double [length], ais.getFormat().getFrameSize());
		}
		for (int position = 0; position < length;) {
			byte [] frame = new byte [ais.getFormat ().getFrameSize ()];
			ais.read (frame);
			this.byteArrayToFrame (frame, ret [currentChannel], position, 
					ais.getFormat ().isBigEndian ());
			currentChannel = (currentChannel + 1) % channels;
			if (currentChannel == 0){
				position++;
			}
		}
		this.notifyAll ("Convert done");
		return ret;
	}

	public Byte[] toObject (byte[] array) {
	    if (array == null) {
		  return null;
		} else if (array.length == 0) {
		  return new Byte [0];
		}
		final Byte[] result = new Byte [array.length];
		for (int i = 0; i < array.length; i++) {
		  result[i] = Byte.valueOf (array[i]);
		}
		return result;
	}
	
	private void byteArrayToFrame (byte [] frame, Sound sound, int position,
			boolean bigEndian) {
		double value = 0;
		int destination = (bigEndian ? 0 : frame.length - 1);
		for (int j = 0 ; j < frame.length ; j++){
			int i = (bigEndian ? frame.length - j - 1: j);
			int fromIndex = (i < destination ? i : destination);
			int toIndex = (i < destination ? destination : i);
			if (fromIndex < toIndex && !new HashSet<Object> (
					Arrays.asList (this.toObject(frame)).subList (fromIndex, toIndex)).equals (
							new HashSet<Object> (Arrays.asList (new byte [] {0})))){
			  value *= 256;
			  value += frame [i];
			}
		}
	    sound.getSamples () [position] = value;
    }

	private AudioInputStream toStream (Sound [] channels, AudioFormat audioFormat) {

		int length = channels.length * audioFormat.getFrameSize () * channels [0].getSamples ().length;
		byte [] data = new byte [length];
		
		for (int i = 0 ; i < data.length ; i++){
			
			int currentFrameByte = i % audioFormat.getFrameSize ();
			int currentChannel = (i / audioFormat.getFrameSize ()) % channels.length;
			int currentFrame = i / (audioFormat.getFrameSize () * channels.length);

			if (!audioFormat.isBigEndian ()){
				data [i] = (byte) ((int)(channels [currentChannel].getSamples () [currentFrame]) >> (8 * currentFrameByte));
			}else{
				data [i] = (byte) ((int)(channels [currentChannel].getSamples () [currentFrame]) >> (8 * (audioFormat.getFrameSize () - 1 - currentFrameByte)));				
			}
		}
		
		this.notifyAll ("Creating output file");
		// now save the file
		ByteArrayInputStream bais = new ByteArrayInputStream (data);
		return new AudioInputStream (bais, audioFormat, channels [0].getSamples ().length * channels.length);
	}

	public void transformWav (File fOrigin, File fDest, SoundTransformation... sts) throws UnsupportedAudioFileException, IOException {
			File file = fOrigin;
			AudioInputStream ais1 = AudioSystem.getAudioInputStream (file);
			this.notifyAll ("input : " + ais1.getFormat ().toString ());
			AudioInputStream ais2 = ais1;
			ais2 = this.transformAudioStream (ais1, sts);
			AudioSystem.write (ais2, AudioFileFormat.Type.WAVE, fDest);
			this.notifyAll ("Wrote output");
			this.notifyAll ("output : " + ais2.getFormat ().toString ());
	}
}
