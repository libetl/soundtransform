package org.toilelibre.libe.soundtransform.actions.notes;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.Library;

public class ImportAPackIntoTheLibrary extends Action {

    public ImportAPackIntoTheLibrary () {

    }

    public void importAPack (final String title, final String jsonContent) throws SoundTransformException {
        this.importPackService.importPack ($.select (Library.class), title, jsonContent);
    }
}
