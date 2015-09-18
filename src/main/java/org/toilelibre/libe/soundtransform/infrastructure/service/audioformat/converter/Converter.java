package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.AbstractMap;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

interface Converter {
    AbstractMap.SimpleImmutableEntry<StreamInfo, ByteArrayOutputStream> convert (InputStream input) throws SoundTransformException;
}