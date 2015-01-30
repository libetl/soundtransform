package org.toilelibre.libe.soundtransform.model.observer;

public class AbstractLogAware<T extends AbstractLogAware<T>> implements LogAware<AbstractLogAware<T>> {

    protected Observer [] observers;

    @Override
    public void log (final LogEvent event) {
        if (this.observers == null) {
            return;
        }
        for (final Observer observer : this.observers) {
            observer.notify (event);
        }
    }

    @Override
    @SuppressWarnings ("unchecked")
    public T setObservers (final Observer... observers1) {
        this.observers = observers1;
        return (T) this;
    }
}
