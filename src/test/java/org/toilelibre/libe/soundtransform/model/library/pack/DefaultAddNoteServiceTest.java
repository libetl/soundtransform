package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class DefaultAddNoteServiceTest {

    @Test (expected = SoundTransformException.class)
    public void addNoteWithInvalidInputStream () throws SoundTransformException, UnsupportedEncodingException {
        $.select (AddNoteService.class).addNote (new Range (), new SimpleNoteInfo (""), new ByteArrayInputStream ("dfsqfdsqfdsqdfdfqsqsdf".getBytes ("UTF-8")));
    }
}
