package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.converter;

public enum ConverterMapping {
    OGG (new JorbisCleanConverter ()), MP3 (new MP3JLayerConverter ());

    private final Converter converter;

    ConverterMapping (final Converter converter1) {
        this.converter = converter1;
    }

    public Converter getConverter () {
        return this.converter;
    }

}