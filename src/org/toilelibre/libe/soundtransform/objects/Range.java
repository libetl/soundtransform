package org.toilelibre.libe.soundtransform.objects;

import java.util.HashMap;

public class Range extends HashMap<Integer, Note> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6526477231719258055L;

	public Note getNearestNote (int frequency) {
		int nearest = Integer.MIN_VALUE;
		for (Integer i : this.keySet ()) {
			if (Math.abs (frequency - i.intValue ()) < Math.abs (frequency - nearest)) {
				nearest = i.intValue ();
			}
		}
		return this.get (new Integer (nearest));
	}
}
