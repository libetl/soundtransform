package org.toilelibre.soundtransform;

public class PrintlnTransformObserver implements TransformObserver {

	@Override
	public void notify (String s) {
		System.out.println (s);
	}

}
