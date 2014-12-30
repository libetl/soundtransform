package org.toilelibre.libe.soundtransform.actions;

public abstract class Action {

	protected org.toilelibre.libe.soundtransform.model.TransformSoundService	transformSound;

	public Action (final org.toilelibre.libe.soundtransform.model.observer.Observer... observers) {
		this.transformSound = new org.toilelibre.libe.soundtransform.model.TransformSoundService (observers);
	}
}
