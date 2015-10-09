package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.note.SimpleNoteInfo;

public class DefaultAddNoteServiceTest {

    @Test (expected = SoundTransformException.class)
    public void addNoteWithInvalidInputStream () throws SoundTransformException, UnsupportedEncodingException {
        $.select (AddNoteService.class).addNote (new Range (), new SimpleNoteInfo (""), new ByteArrayInputStream ("dfsqfdsqfdsqdfdfqsqsdf".getBytes ("UTF-8")));
    }

    @Test (expected = SoundTransformException.class)
    public void addNoteWithValidAbsoluteFile () throws SoundTransformException, UnsupportedEncodingException {
        $.select (AddNoteService.class).addNote (new Range (), new SimpleNoteInfo ("/dev/null"));
    }

    @Test
    public void addNoteWithInvalidRelativeFile () throws SoundTransformException, UnsupportedEncodingException {
        $.select (AddNoteService.class).addNote (new Range (), new SimpleNoteInfo ("unknownfile.txt"));
    }

    @Test (expected = SoundTransformException.class)
    public void addNoteWithValidRelativeFile () throws SoundTransformException, UnsupportedEncodingException {
        $.select (AddNoteService.class).addNote (new Range (), new SimpleNoteInfo ("pom.xml"));
    }

}
