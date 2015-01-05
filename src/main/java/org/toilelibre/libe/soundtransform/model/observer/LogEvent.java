package org.toilelibre.libe.soundtransform.model.observer;

import java.util.Date;

public class LogEvent {

	public enum LogLevel {
		PARANOIAC, VERBOSE, INFO, WARN, ERROR
	}

	private final LogLevel	level;

	private final String	msg;

	public LogEvent (final LogLevel level1, final String msg1) {
		this.level = level1;
		this.msg = msg1;
	}

	public LogLevel getLevel () {
		return this.level;
	}

	@Override
	public String toString () {
		return new Date ().toString () + " [" + this.level + "] " + this.msg;
	}
}
