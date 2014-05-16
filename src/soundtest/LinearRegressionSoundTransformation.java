package soundtest;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class LinearRegressionSoundTransformation implements SoundTransformation {

	private int step = 1;
	
	public LinearRegressionSoundTransformation (int step) {
		this.step = step;
    }


	@Override
    public Sound transform (Sound input) {
		SimpleRegression reg = new SimpleRegression();
		for (int i = 0 ; i < input.getSamples ().length ; i+= step){
			reg.addData (i, input.getSamples () [i]);
		}
		
		reg.regress ();
		Sound outputSound = new Sound (new double [input.getSamples ().length],
				input.getNbBytesPerFrame ());
		for (int i = 0 ; i < input.getSamples ().length ; i+= step){
			outputSound.getSamples () [i] = reg.predict (i);
		}
		
		return outputSound;
    }


}
