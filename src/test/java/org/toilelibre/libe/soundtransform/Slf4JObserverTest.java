package org.toilelibre.libe.soundtransform;

import java.util.MissingFormatArgumentException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.Slf4jObserver;
import org.toilelibre.libe.soundtransform.model.observer.EventCode;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class Slf4JObserverTest {

    enum FakeLogEvent implements EventCode {
        PARANOIAC(LogLevel.PARANOIAC, "Paranoiac event"), VERBOSE(LogLevel.VERBOSE, "Verbose event"), INFO(LogLevel.INFO, "Info event with this %1s arg"), WARN(LogLevel.WARN, "Warning event"), ERROR(LogLevel.ERROR, "Error event");

        private final LogLevel level;
        private final String messageFormat;

        private FakeLogEvent(final LogLevel ll, final String mf) {
            this.level = ll;
            this.messageFormat = mf;
        }

        @Override
        public LogLevel getLevel() {
            return this.level;
        }

        @Override
        public String getMessageFormat() {
            return this.messageFormat;
        }

    }

    @Test
    public void levels() {
        final Observer observer = new Slf4jObserver(LogLevel.PARANOIAC);
        observer.notify(new LogEvent(FakeLogEvent.PARANOIAC));
        observer.notify(new LogEvent(FakeLogEvent.VERBOSE));
        observer.notify(new LogEvent(FakeLogEvent.INFO, "bou"));
        observer.notify(new LogEvent(FakeLogEvent.WARN));
        observer.notify(new LogEvent(FakeLogEvent.ERROR));
    }

    @Test(expected = MissingFormatArgumentException.class)
    public void missingParameter() {
        final Observer observer = new Slf4jObserver(LogLevel.PARANOIAC);
        observer.notify(new LogEvent(FakeLogEvent.INFO));
    }

    @Test
    public void notCalled() {
        final Observer observer = new Slf4jObserver(LogLevel.WARN);
        observer.notify(new LogEvent(FakeLogEvent.INFO));
    }
}
