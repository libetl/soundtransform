package org.toilelibre.libe.soundtransform.model.observer;

public class TextLogEvent extends LogEvent {

    public enum NoEventCode implements EventCode {
        PLAIN_TEXT_EVENT;

        @Override
        public LogLevel getLevel() {
            return LogLevel.INFO;
        }

        @Override
        public String getMessageFormat() {
            return "%1s";
        }
    }

    public TextLogEvent(final String msg) {
        super(NoEventCode.PLAIN_TEXT_EVENT, msg);
    }

}
