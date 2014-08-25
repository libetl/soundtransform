package org.toilelibre.soundtransform.observer;

import java.util.Date;

public class LogEvent {

	private LogLevel	level;
	private String	 msg;

	public LogEvent (LogLevel level1, String msg1) {
		this.level = level1;
		this.msg = msg1;
	}

	public LogLevel getLevel () {
		return level;
	}

	public enum LogLevel {
		PARANOIAC, VERBOSE, INFO, WARN, ERROR
	}

	@Override
	public String toString () {
		return new Date ().toString () + " [" + this.level + "] " + this.msg;
	}
}
