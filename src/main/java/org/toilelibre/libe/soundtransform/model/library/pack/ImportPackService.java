package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface ImportPackService<T> extends LogAware<T> {

    public abstract Pack getAPack (Library library, String title);

    public abstract void importPack (Library library, String title, InputStream inputStream) throws SoundTransformException;

    public abstract void importPack (Library library, String title, Object context, Class<?> rClass, int packJsonId) throws SoundTransformException;

    public abstract void importPack (Library library, String title, String jsonContent) throws SoundTransformException;

}