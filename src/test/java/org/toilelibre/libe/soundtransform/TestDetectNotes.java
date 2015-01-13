package org.toilelibre.libe.soundtransform;

import java.util.List;
import java.util.LinkedList;

import org.junit.Assert;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.SwingWrapper;

public class TestDetectNotes {

	//@Test
	public void test1 () throws SoundTransformException{
		final List<String> messages = new LinkedList<String> ();
		int [] t = new int [2000];
		for (int i = 200 ; i < 600 ; i++){
			t [i] = 200;
		}
		for (int i = 800 ; i < 1000 ; i++){
			t [i] = 200;
		}
		for (int i = 1100 ; i < 1600 ; i++){
			t [i] = 200;
		}

        double[] xFreqs = new double [t.length];
        double[] yFreqs = new double [t.length];
        for (int i = 0 ; i < 2000 ; i++){
        	xFreqs [i] = i;
        	yFreqs [i] = (t [i] > 2000 ? 0 : t [i]);
        }
        Chart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xFreqs, yFreqs);
        new SwingWrapper(chart).displayChart();
        
		new ShapeSoundTransformation (Library.defaultPack, "simple_piano", t).setObservers (
				new PrintlnTransformObserver (), new Observer (){

					@Override
                    public void notify (LogEvent logEvent) {
						messages.add (logEvent.toString ());	                    
                    }}
					).transform (200000, 100, 2, 44100, 1);
		Assert.assertTrue (messages.get (0).endsWith ("Note between 200/2000 and 600/2000"));
		Assert.assertTrue (messages.get (1).endsWith ("Note between 800/2000 and 1000/2000"));
		Assert.assertTrue (messages.get (2).endsWith ("Note between 1100/2000 and 1600/2000"));
	}
}
