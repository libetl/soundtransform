package org.toilelibre.libe.soundtransform.model.observer;

public class LogEvent {

    public enum LogLevel {
        PARANOIAC, VERBOSE, INFO, WARN, ERROR
    }

    private final LogLevel level;

    private final String   msg;

    public LogEvent (final LogLevel level1, final String msg1) {
        this.level = level1;
        this.msg = msg1;
    }

    public LogLevel getLevel () {
        return this.level;
    }

    public String getMsg () {
        return this.msg;
    }

    @Override
    public String toString () {
        return "LogEvent [" + this.level + "] " + this.msg;
    }
}
