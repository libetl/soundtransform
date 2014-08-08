package org.toilelibre.soundtransform.observer;


public class PrintlnTransformObserver implements TransformObserver {

	@Override
	public void notify (String s) {
		System.out.println (s);
	}

}
