package org.toilelibre.libe.soundtransform.model.converted;

import java.io.IOException;
import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class CallTransformService implements LogAware {

	Observer []	observers	= new Observer [0];

	public CallTransformService (Observer... observers) {
		this.setObservers (observers);
	}

	private void notifyAll (String s) {
		this.log (new LogEvent (LogLevel.INFO, s));
	}

	public Sound [] transformAudioStream (Sound [] input, SoundTransformation... sts) throws IOException {
		Sound [] output = Arrays.copyOf (input, input.length);
		int transformNumber = 0;
		for (SoundTransformation st : sts) {
			for (int i = 0; i < input.length; i++) {
				this.notifyAll ("Transform " + (transformNumber + 1) + "/" + sts.length + " (" + st.getClass ().getSimpleName () + "), channel " + (i + 1) + "/" + input.length);
				if (st instanceof LogAware) {
					((LogAware) st).setObservers (this.observers);
				}
				output [i] = st.transform (output [i]);
			}
			transformNumber++;
		}
		if (sts.length == 0) {
			output = input;
		}
		this.notifyAll ("Transforms done");
		return output;

	}

	@Override
	public void setObservers (Observer [] observers2) {
		this.observers = observers2;
		for (Observer observer : observers2) {
			this.notifyAll ("Adding observer " + observer.getClass ().getSimpleName ());
		}
	}

	@Override
	public void log (LogEvent event) {
		for (Observer to : this.observers) {
			to.notify (event);
		}

	}
}
