package soundtest;

public class PrintlnTransformObserver implements TransformObserver {

	@Override
	public void notify (String s) {
		System.out.println (s);
	}

}
