package org.toilelibre.libe.soundtransform.infrastructure.service.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public class Slf4jObserver implements Observer {

    private LogLevel threshold;

    public Slf4jObserver () {
        this.threshold = LogLevel.PARANOIAC;
    }

    public Slf4jObserver (final LogLevel threshold1) {
        this.threshold = threshold1;
    }

    @Override
    public void notify (final LogEvent logEvent) {
        String className = this.getCallerClassName ();
        Logger logger = LoggerFactory.getLogger (className);
        if (logEvent.getLevel ().ordinal () >= this.threshold.ordinal ()) {
            this.log (logger, logEvent);
        }
    }

    private void log (Logger logger, LogEvent logEvent) {
        switch (logEvent.getLevel ()) {
            case PARANOIAC:
                logger.trace (logEvent.getMsg ());
                break;
            case VERBOSE:
                logger.debug (logEvent.getMsg ());
                break;
            case INFO:
                logger.info (logEvent.getMsg ());
                break;
            case WARN:
                logger.warn (logEvent.getMsg ());
                break;
            case ERROR:
                logger.error (logEvent.getMsg ());
                break;
            default:
                break;
        }
    }

    private String getCallerClassName () {
        return Thread.currentThread ().getStackTrace () [3].getClassName ();
    }

}
