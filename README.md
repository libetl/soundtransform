soundtransform
==============

[![Build Status](https://travis-ci.org/libetl/soundtransform.svg?branch=master)](https://travis-ci.org/libetl/soundtransform)

Android library to shape a voice with an instrument.
#### How to use the library :
* Insert the aar into your project dependencies :
```xml
<dependency>
	<groupId>org.toilelibre.libe</groupId>
	<artifactId>soundtransform</artifactId>
	<version>x.x.x</version>
	<type>aar</type>
	<exclusions>
		<exclusion>
			<groupId>com.googlecode.soundlibs</groupId>
			<artifactId>tritonus-share</artifactId>
		</exclusion>
	</exclusions>
</dependency>
``` 
* Remove the ```<exclusions/>``` tag if your enclosing project is a Java project (and not android)
* Replace x.x.x with the version you need (do not hesitate to use the latest one).
*  Make sure you have access to the FluentClient class in your project (try the autocompletion feature of your IDE if you have one)
* Read the below documentation about the FluentClient API
* Use the lib by yourself

#### FluentClient Javadoc :
-  -  #### Method Detail

        -   #### start

                public static FluentClientReady start()

            Startup the client

            Returns:  
            the client, ready to start

        -   #### adjust

                public FluentClientWithFreqs adjust()

            Adjust the loudest freqs array to match exactly the piano notes frequencies

            **Specified by:**  
            `adjust` in interface `FluentClientWithFreqs`

            Returns:  
            the client, with a loudest frequencies float array

        -   #### andAfterStart

                public FluentClientReady andAfterStart()

            **Description copied from interface: `FluentClientCommon`**

            Start over the client : reset the state and the value objects nested in the client

            **Specified by:**  
            `andAfterStart` in interface `FluentClientCommon`

            Returns:  
            the client, ready to start

        -   #### append

                public FluentClientSoundImported append(Sound[] sounds1)
                                                 throws SoundTransformException

            Append the sound passed in parameter to the current sound stored in the client

            **Specified by:**  
            `append` in interface `FluentClientSoundImported`

            Parameters:  
            `sounds1` - the sound to append the current sound to

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if the sound is null or if there is a problem with the appending please ensure that both sounds have the same number of channels

        -   #### apply

                public FluentClientSoundImported apply(SoundTransformation st)
                                                throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Apply one transform and continue with the current imported sound

            **Specified by:**  
            `apply` in interface `FluentClientSoundImported`

            Parameters:  
            `st` - the SoundTransformation to apply

            Returns:  
            the client with a sound imported

            Throws:  
            `SoundTransformException` - if the transform does not work

        -   #### changeFormat

                public FluentClientSoundImported changeFormat(InputStreamInfo inputStreamInfo)
                                                       throws SoundTransformException

            Changes the current imported sound to fit the expected format

            **Specified by:**  
            `changeFormat` in interface `FluentClientSoundImported`

            Parameters:  
            `inputStreamInfo` - only the sampleSize and the sampleRate pieces of data will be used

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException`

        -   #### convertIntoSound

                public FluentClientSoundImported convertIntoSound()
                                                           throws SoundTransformException

            **Description copied from interface: `FluentClientWithFile`**

            Shortcut for importToStream ().importToSound () : Conversion from a File to a Sound

            **Specified by:**  
            `convertIntoSound` in interface `FluentClientWithFile`

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if one of the two import fails

        -   #### cutSubSound

                public FluentClientSoundImported cutSubSound(int start,
                                                    int end)
                                                      throws SoundTransformException

            Splice a part of the sound between the sample \#start and the sample \#end

            **Specified by:**  
            `cutSubSound` in interface `FluentClientSoundImported`

            Parameters:  
            `start` - the first sample to cut

            `end` - the last sample to cut

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if the indexes are out of bound

        -   #### exportToClasspathResource

                public FluentClientWithFile exportToClasspathResource(String resource)
                                                               throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Shortcut for exportToStream ().writeToClasspathResource (resource) : Conversion from a Sound to a File

            **Specified by:**  
            `exportToClasspathResource` in interface `FluentClientSoundImported`

            Parameters:  
            `resource` - a resource that can be found in the classpath

            Returns:  
            the client, with a file written

            Throws:  
            `SoundTransformException` - if one of the two operations fails

        -   #### exportToClasspathResourceWithSiblingResource

                public FluentClientWithFile exportToClasspathResourceWithSiblingResource(String resource,
                                                                                String siblingResource)
                                                                                  throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Shortcut for exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource)

            **Specified by:**  
            `exportToClasspathResourceWithSiblingResource` in interface `FluentClientSoundImported`

            Parameters:  
            `resource` - a resource that may or may not exist in the classpath

            `siblingResource` - a resource that can be found in the classpath.

            Returns:  
            the client, with a file written

            Throws:  
            `SoundTransformException` - if one of the two operations fails

        -   #### exportToFile

                public FluentClientWithFile exportToFile(File file1)
                                                  throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Shortcut for exportToStream ().writeToFile (file)

            **Specified by:**  
            `exportToFile` in interface `FluentClientSoundImported`

            Parameters:  
            `file1` - the destination file

            Returns:  
            the client, with a file written

            Throws:  
            `SoundTransformException` - if one of the two operations fails

        -   #### exportToStream

                public FluentClientWithInputStream exportToStream()
                                                           throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Uses the current imported sound and converts it into an InputStream, ready to be written to a file (or to be read again)

            **Specified by:**  
            `exportToStream` in interface `FluentClientSoundImported`

            Returns:  
            the client, with an inputStream

            Throws:  
            `SoundTransformException` - if the metadata format object is invalid, or if the sound cannot be converted

        -   #### extractSound

                public FluentClientSoundImported extractSound()
                                                       throws SoundTransformException

            **Description copied from interface: `FluentClientWithSpectrums`**

            Uses the current available spectrums objects to convert them into a sound (with one or more channels)

            **Specified by:**  
            `extractSound` in interface `FluentClientWithSpectrums`

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if the spectrums are in an invalid format, or if the transform to sound does not work

        -   #### extractSubSound

                public FluentClientSoundImported extractSubSound(int start,
                                                        int end)
                                                          throws SoundTransformException

            Extract a part of the sound between the sample \#start and the sample \#end

            **Specified by:**  
            `extractSubSound` in interface `FluentClientSoundImported`

            Parameters:  
            `start` - the first sample to extract

            `end` - the last sample to extract

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if the indexes are out of bound

        -   #### filterRange

                public FluentClientWithFreqs filterRange(float low,
                                                float high)

            Remove the values between low and high in the loudest freqs array (replace them by 0)

            **Specified by:**  
            `filterRange` in interface `FluentClientWithFreqs`

            Returns:  
            the client, with a loudest frequencies float array

        -   #### findLoudestFrequencies

                public FluentClientWithFreqs findLoudestFrequencies()
                                                             throws SoundTransformException

            Will invoke a soundtransform to find the loudest frequencies of the sound, chronologically
             Caution : the original sound will be lost, and it will be impossible to revert this conversion.
             When shaped into a sound, the new sound will only sounds like the instrument you shaped the freqs with

            **Specified by:**  
            `findLoudestFrequencies` in interface `FluentClientSoundImported`

            Returns:  
            the client, with a loudest frequencies float array

            Throws:  
            `SoundTransformException` - if the convert fails

        -   #### importToSound

                public FluentClientSoundImported importToSound()
                                                        throws SoundTransformException

            **Description copied from interface: `FluentClientWithInputStream`**

            Uses the current input stream object to convert it into a sound (with one or more channels)

            **Specified by:**  
            `importToSound` in interface `FluentClientWithInputStream`

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - the inputStream is invalid, or the convert did not work

        -   #### importToStream

                public FluentClientWithInputStream importToStream()
                                                           throws SoundTransformException

            **Description copied from interface: `FluentClientWithFile`**

            Opens the current file and convert it into an InputStream, ready to be read (or to be written to a file)

            **Specified by:**  
            `importToStream` in interface `FluentClientWithFile`

            Returns:  
            the client, with an inputStream

            Throws:  
            `SoundTransformException` - the current file is not valid, or the conversion did not work

        -   #### loop

                public FluentClientSoundImported loop(int length)
                                               throws SoundTransformException

            Extract a part of the sound between the sample \#start and the sample \#end

            **Specified by:**  
            `loop` in interface `FluentClientSoundImported`

            Parameters:  
            `length` - the number of samples of the result sound

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if the length is not positive

        -   #### mixWith

                public FluentClientSoundImported mixWith(Sound[] sound)
                                                  throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Combines the current sound with another sound. The operation is not reversible

            **Specified by:**  
            `mixWith` in interface `FluentClientSoundImported`

            Parameters:  
            `sound` - the sound to mix the current sound with

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - if the sound is null or if there is a problem with the mix

        -   #### octaveDown

                public FluentClientWithFreqs octaveDown()

            Changes the loudest frequencies array to become one octave lower

            **Specified by:**  
            `octaveDown` in interface `FluentClientWithFreqs`

            Returns:  
            the client, with a loudest frequencies float array

        -   #### octaveUp

                public FluentClientWithFreqs octaveUp()

            Changes the loudest frequencies array to become one octave upper

            **Specified by:**  
            `octaveUp` in interface `FluentClientWithFreqs`

            Returns:  
            the client, with a loudest frequencies float array

        -   #### playIt

                public FluentClient playIt()
                                    throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Plays the current audio data

            **Specified by:**  
            `playIt` in interface `FluentClientSoundImported`

            **Specified by:**  
            `playIt` in interface `FluentClientWithFile`

            **Specified by:**  
            `playIt` in interface `FluentClientWithInputStream`

            **Specified by:**  
            `playIt` in interface `FluentClientWithSpectrums`

            Returns:  
            the client, with a sound

            Throws:  
            `SoundTransformException` - could not play the current audio data

        -   #### replacePart

                public FluentClientWithFreqs replacePart(float[] subFreqs,
                                                int start)

            **Description copied from interface: `FluentClientWithFreqs`**

            Replace some of the values of the loudest freqs array from the “start” index (replace them by the values of subfreqs)

            **Specified by:**  
            `replacePart` in interface `FluentClientWithFreqs`

            Returns:  
            the client, with a loudest frequencies float array

        -   #### shapeIntoSound

                public FluentClientSoundImported shapeIntoSound(String packName,
                                                       String instrumentName,
                                                       InputStreamInfo isi)
                                                         throws SoundTransformException

            **Description copied from interface: `FluentClientWithFreqs`**

            Shapes these loudest frequencies array into a sound and set the converted sound in the pipeline

            **Specified by:**  
            `shapeIntoSound` in interface `FluentClientWithFreqs`

            Parameters:  
            `packName` - reference to an existing imported pack (must be invoked before the shapeIntoSound method by using withAPack)

            `instrumentName` - the name of the instrument that will map the freqs object

            `isi` - the wanted format for the future sound

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - could not call the soundtransform to shape the freqs

        -   #### splitIntoSpectrums

                public FluentClientWithSpectrums splitIntoSpectrums()
                                                             throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Uses the current sound to pick its spectrums and set that as the current data in the pipeline

            **Specified by:**  
            `splitIntoSpectrums` in interface `FluentClientSoundImported`

            Returns:  
            the client, with the spectrums

            Throws:  
            `SoundTransformException` - could not convert the sound into some spectrums

        -   #### stopWithAPack

                public Pack stopWithAPack(String title)

            Stops the client pipeline and returns the pack whose title is in parameter

            **Specified by:**  
            `stopWithAPack` in interface `FluentClientCommon`

            **Specified by:**  
            `stopWithAPack` in interface `FluentClientReady`

            Parameters:  
            `title` - the title of the pack

            Returns:  
            a pack object

        -   #### stopWithFile

                public File stopWithFile()

            **Description copied from interface: `FluentClientWithFile`**

            Stops the client pipeline and returns the obtained file

            **Specified by:**  
            `stopWithFile` in interface `FluentClientWithFile`

            Returns:  
            a file

        -   #### stopWithFreqs

                public float[] stopWithFreqs()

            **Description copied from interface: `FluentClientWithFreqs`**

            Stops the client pipeline and returns the obtained loudest frequencies

            **Specified by:**  
            `stopWithFreqs` in interface `FluentClientWithFreqs`

            Returns:  
            loudest frequencies array

        -   #### stopWithInputStream

                public InputStream stopWithInputStream()

            **Description copied from interface: `FluentClientWithInputStream`**

            Stops the client pipeline and returns the obtained input stream

            **Specified by:**  
            `stopWithInputStream` in interface `FluentClientWithInputStream`

            Returns:  
            an input stream

        -   #### stopWithInputStreamInfo

                public InputStreamInfo stopWithInputStreamInfo()
                                                        throws SoundTransformException

            **Description copied from interface: `FluentClientWithInputStream`**

            Stops the client pipeline and returns the obtained input stream info object

            **Specified by:**  
            `stopWithInputStreamInfo` in interface `FluentClientWithInputStream`

            Returns:  
            an inputStreamInfo object

            Throws:  
            `SoundTransformException` - could not read the inputstreaminfo from the current inputstream

        -   #### stopWithSounds

                public Sound[] stopWithSounds()

            **Description copied from interface: `FluentClientSoundImported`**

            Stops the client pipeline and returns the obtained sound

            **Specified by:**  
            `stopWithSounds` in interface `FluentClientSoundImported`

            Returns:  
            a sound value object

        -   #### stopWithSpectrums

                public List<Spectrum<Serializable>[]> stopWithSpectrums()

            **Description copied from interface: `FluentClientWithSpectrums`**

            Stops the client pipeline and returns the obtained spectrums

            **Specified by:**  
            `stopWithSpectrums` in interface `FluentClientWithSpectrums`

            Returns:  
            a list of spectrums for each channel

        -   #### withAnObserver

                public FluentClientReady withAnObserver(Observer... observers1)

            **Description copied from interface: `FluentClientReady`**

            Tells the client to add an observer that will be notified of different kind of updates from the library. It is ok to call withAnObserver several times.
             If the andAfterStart method is called, the subscribed observers are removed

            **Specified by:**  
            `withAnObserver` in interface `FluentClientReady`

            Parameters:  
            `observers1` - one or more observer(s)

            Returns:  
            the client, ready to start

        -   #### withAPack

                public FluentClient withAPack(String packName,
                                     InputStream jsonStream)
                                       throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Tells the client to work with a pack. Reads the whole inputStream. A pattern must be followed in the jsonStream to enable the import.

            **Specified by:**  
            `withAPack` in interface `FluentClientReady`

            **Specified by:**  
            `withAPack` in interface `FluentClientSoundImported`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithFile`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithFreqs`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithInputStream`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithSpectrums`

            Parameters:  
            `packName` - the name of the pack

            `jsonStream` - the input stream

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - the input stream cannot be read, or the json format is not correct, or some sound files are missing

        -   #### withAPack

                public FluentClient withAPack(String packName,
                                     String jsonContent)
                                       throws SoundTransformException

            **Description copied from interface: `FluentClientSoundImported`**

            Tells the client to work with a pack. Reads the whole string content. A pattern must be followed in the jsonContent to enable the import.

            Here is the format allowed in the file

                {
                  "instrumentName" :
                  {
                    -1 : "/data/mypackage.myapp/unknownFrequencyFile.wav",
                   192 : "/data/mypackage.myapp/knownFrequencyFile.wav",
                   ...
                  },
                  ...
                }

            Do not assign the same frequency for two notes in the same instrument. If several notes must have their frequencies detected by the soundtransform lib, set different negative values (-1, -2, -3, …)

            **Specified by:**  
            `withAPack` in interface `FluentClientReady`

            **Specified by:**  
            `withAPack` in interface `FluentClientSoundImported`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithFile`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithFreqs`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithInputStream`

            **Specified by:**  
            `withAPack` in interface `FluentClientWithSpectrums`

            Parameters:  
            `packName` - the name of the pack

            `jsonContent` - a string containing the definition of the pack

            Returns:  
            the client, with a sound imported

            Throws:  
            `SoundTransformException` - the json content is invalid, the json format is not correct, or some sound files are missing

        -   #### withAudioInputStream

                public FluentClientWithInputStream withAudioInputStream(InputStream ais)

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with an InputStream. It will not be read yet
             The passed inputStream must own a format metadata object. Therefore it must be an AudioInputStream.

            **Specified by:**  
            `withAudioInputStream` in interface `FluentClientReady`

            Parameters:  
            `ais` - the input stream

            Returns:  
            the client, with an input stream

        -   #### withClasspathResource

                public FluentClientWithFile withClasspathResource(String resource)
                                                           throws SoundTransformException

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with a classpath resource. It will be converted in a File

            **Specified by:**  
            `withClasspathResource` in interface `FluentClientReady`

            Parameters:  
            `resource` - a classpath resource that must exist

            Returns:  
            the client, with a file

            Throws:  
            `SoundTransformException` - the classpath resource was not found

        -   #### withFile

                public FluentClientWithFile withFile(File file1)

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with a file. It will not be read yet

            **Specified by:**  
            `withFile` in interface `FluentClientReady`

            Parameters:  
            `file1` - source file

            Returns:  
            the client, with a file

        -   #### withFreqs

                public FluentClientWithFreqs withFreqs(float[] freqs1)

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with a loudest frequencies integer array. It will not be used yet

            **Specified by:**  
            `withFreqs` in interface `FluentClientReady`

            Parameters:  
            `freqs1` - the loudest frequencies float array

            Returns:  
            the client, with a loudest frequencies float array

        -   #### withRawInputStream

                public FluentClientWithInputStream withRawInputStream(InputStream is,
                                                             InputStreamInfo isInfo)
                                                               throws SoundTransformException

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with a byte array InputStream or any readable DataInputStream. It will be read and transformed into an AudioInputStream
             The passed inputStream must not contain any metadata piece of information.

            **Specified by:**  
            `withRawInputStream` in interface `FluentClientReady`

            Parameters:  
            `is` - the input stream

            `isInfo` - the audio format (named “InputStreamInfo”)

            Returns:  
            the client, with an input stream

            Throws:  
            `SoundTransformException` - the input stream cannot be read, or the conversion did not work

        -   #### withSounds

                public FluentClientSoundImported withSounds(Sound[] sounds1)

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with a sound object

            **Specified by:**  
            `withSounds` in interface `FluentClientReady`

            Parameters:  
            `sounds1` - the sound object

            Returns:  
            the client, with an imported sound

        -   #### withSpectrums

                public FluentClientWithSpectrums withSpectrums(List<Spectrum<Serializable>[]> spectrums)

            **Description copied from interface: `FluentClientReady`**

            Tells the client to work first with a spectrum formatted sound.
             The spectrums inside must be in a list (each item must correspond to a channel) The spectrums are ordered in an array in chronological order

            **Specified by:**  
            `withSpectrums` in interface `FluentClientReady`

            Parameters:  
            `spectrums` - the spectrums

            Returns:  
            the client, with the spectrums

        -   #### writeToClasspathResource

                public FluentClientWithFile writeToClasspathResource(String resource)
                                                              throws SoundTransformException

            **Description copied from interface: `FluentClientWithInputStream`**

            Writes the current InputStream in a classpath resource in the same folder as a previously imported classpath resource. Caution : if no classpath resource was imported before, this operation will not work. Use writeToClasspathResourceWithSiblingResource instead

            **Specified by:**  
            `writeToClasspathResource` in interface `FluentClientWithInputStream`

            Parameters:  
            `resource` - a classpath resource.

            Returns:  
            the client, with a file

            Throws:  
            `SoundTransformException` - there is no predefined classpathresource directory, or the file could not be written

        -   #### writeToClasspathResourceWithSiblingResource

                public FluentClientWithFile writeToClasspathResourceWithSiblingResource(String resource,
                                                                               String siblingResource)
                                                                                 throws SoundTransformException

            **Description copied from interface: `FluentClientWithInputStream`**

            Writes the current InputStream in a classpath resource in the same folder as a the sibling resource.

            **Specified by:**  
            `writeToClasspathResourceWithSiblingResource` in interface `FluentClientWithInputStream`

            Parameters:  
            `resource` - a classpath resource that may or may not exist yet

            `siblingResource` - a classpath resource that must exist

            Returns:  
            the client, with a file

            Throws:  
            `SoundTransformException` - no such sibling resource, or the file could not be written

        -   #### writeToFile

                public FluentClientWithFile writeToFile(File file1)
                                                 throws SoundTransformException

            **Description copied from interface: `FluentClientWithInputStream`**

            Writes the current InputStream in a file

            **Specified by:**  
            `writeToFile` in interface `FluentClientWithInputStream`

            Parameters:  
            `file1` - the destination file

            Returns:  
            the client, with a file

            Throws:  
            `SoundTransformException` - The file could not be written
