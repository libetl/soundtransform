package org.toilelibre.libe.soundtransform.infrastructure.service.audioformat.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.gagravarr.ogg.OggFile;
import org.gagravarr.vorbis.VorbisAudioData;
import org.gagravarr.vorbis.VorbisFile;
import org.gagravarr.vorbis.VorbisInfo;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperErrorCode;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper.AudioFileHelperEventCode;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.AbstractLogAware;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;

class AndroidFileToWavFileConverter {

    static enum Converters {
        OGG (new OGGConverter ());

        private final Converter converter;

        Converters (Converter converter1) {
            this.converter = converter1;
        }

        public Converter getConverter () {
            return converter;
        }

    }

    interface Converter {
        File convert (File input) throws SoundTransformException;
    }

    static class OGGConverter extends AbstractLogAware<OGGConverter> implements Converter {

        @Override
        public File convert (File input) throws SoundTransformException {
            File result = null;

            ByteArrayOutputStream baos = this.readOggVorbisData (input);

            StreamInfo audioFormat = this.readOggVorbisMetadata (input, baos.size ());
            ByteArrayWithAudioFormatInputStream readyToWriteInputStream = new ByteArrayWithAudioFormatInputStream (baos.toByteArray (), audioFormat);

            try {
                result = File.createTempFile ("soundtransform", ".wav");
                new AndroidAudioFileHelper ().writeInputStream (readyToWriteInputStream, result);
            } catch (IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CREATE_A_TEMP_FILE, e);
            } finally {
                try {
                    readyToWriteInputStream.close ();
                } catch (IOException e) {
                    this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
                }
            }
            return result;
        }

        private StreamInfo readOggVorbisMetadata (File input, int streamSize) throws SoundTransformException {
            try {
                VorbisFile vorbisFile = new VorbisFile (new OggFile (new FileInputStream (input)));
                VorbisInfo info = vorbisFile.getInfo ();
                vorbisFile.close ();
                return new StreamInfo (info.getChannels (), streamSize, 2, info.getRate (), false, false, "ogg" + info.getVersion ());
            } catch (FileNotFoundException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, input.getPath ());
            } catch (IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, input.getPath ());
            }
        }

        private ByteArrayOutputStream readOggVorbisData (File input) throws SoundTransformException {
            VorbisFile vorbisFile = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream ();
            try {
                vorbisFile = new VorbisFile (new OggFile (new FileInputStream (input)));
                VorbisAudioData packet = vorbisFile.getNextAudioPacket ();
                while (packet != null) {
                    baos.write (packet.write ().getData ());
                    packet = vorbisFile.getNextAudioPacket ();
                }
            } catch (FileNotFoundException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, input.getPath ());
            } catch (IOException e) {
                throw new SoundTransformException (AudioFileHelperErrorCode.COULD_NOT_CONVERT, e, input.getPath ());
            } finally {
                if (vorbisFile != null) {
                    try {
                        vorbisFile.close ();
                    } catch (IOException e) {
                        this.log (new LogEvent (AudioFileHelperEventCode.COULD_NOT_CLOSE, e));
                    }
                }
            }
            return baos;
        }

    }
}
