package org.toilelibre.libe.soundtransform.infrastructure.service.observer;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;
import org.toilelibre.libe.soundtransform.model.observer.Observer;
import org.toilelibre.libe.soundtransform.model.observer.TextLogEvent;

public class Slf4jObserver implements Observer {

    private final Map<String, Logger> loggersByClassName = new HashMap<String, Logger> ();
    private static final String       OBSERVER_CLASSNAME = Slf4jObserver.class.getName ();
    private static final String       LOGAWARE_CLASSNAME = AbstractLogAware.class.getName ();

    private final LogLevel            threshold;

    public Slf4jObserver () {
        this.threshold = LogLevel.PARANOIAC;
    }

    public Slf4jObserver (final LogLevel threshold1) {
        this.threshold = threshold1;
    }

    private String getCallerClassName () {
        int i = 1;
        final StackTraceElement [] stackTrace = Thread.currentThread ().getStackTrace ();
        while (i < stackTrace.length && (stackTrace [i].getClassName ().equals (Slf4jObserver.OBSERVER_CLASSNAME) || stackTrace [i].getClassName ().equals (Slf4jObserver.LOGAWARE_CLASSNAME))) {
            i++;
        }
        return stackTrace [i].getClassName ();
    }

    private void log (final Logger logger, final LogEvent logEvent) {
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

    @Override
    public void notify (final LogEvent logEvent) {
        if (logEvent.getLevel ().ordinal () < this.threshold.ordinal ()) {
            return;
        }
        final String className = this.getCallerClassName ();
        final Logger logger = this.getLoggerByClassName (className);
        this.log (logger, logEvent);
    }

    // shortcut for notify with level "info"
    public void notify (final String msg) {
        final String className = this.getCallerClassName ();
        final Logger logger = this.getLoggerByClassName (className);
        this.log (logger, new TextLogEvent (msg));
    }

    private synchronized Logger getLoggerByClassName (final String className) {

        if (!this.loggersByClassName.containsKey (className)) {
            this.loggersByClassName.put (className, LoggerFactory.getLogger (className));
        }
        return this.loggersByClassName.get (className);
    }

}
