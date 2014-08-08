package org.toilelibre.soundtransform.observer;

import java.util.Date;


public class LogEvent {

	private LogLevel level;
	private String msg;

	public LogEvent(LogLevel level1, String msg1) {
		this.level = level1;
		this.msg = msg1;
	}

	public enum LogLevel {
		VERBOSE, INFO, WARN, ERROR
	}

	public String toString (){
		return new Date ().toString () + " [" + this.level + "] " + this.msg;
	}
}
