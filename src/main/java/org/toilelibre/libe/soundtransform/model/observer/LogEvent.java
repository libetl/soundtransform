package org.toilelibre.libe.soundtransform.model.observer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogEvent {

    public enum LogLevel {
        PARANOIAC, VERBOSE, INFO, WARN, ERROR
    }

    private final EventCode eventCode;

    private final Date date;

    private final Object[] params;

    public LogEvent(final EventCode eventCode, final Object... params1) {
        this.eventCode = eventCode;
        this.date = new Date();
        this.params = params1;
    }

    private String getDateInIso8601Format() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US).format(this.date);
    }

    public EventCode getEventCode() {
        return this.eventCode;
    }

    public LogLevel getLevel() {
        return this.eventCode.getLevel();
    }

    public String getMsg() {
        return String.format(this.eventCode.getMessageFormat(), this.params);
    }

    @Override
    public String toString() {
        return this.getDateInIso8601Format() + " [" + this.eventCode.getLevel() + "," + this.eventCode.name() + "] " + this.getMsg();
    }
}
