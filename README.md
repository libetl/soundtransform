# soundtransform
[![Build Status](https://travis-ci.org/libetl/soundtransform.svg?branch=master)](https://travis-ci.org/libetl/soundtransform) [![Coverage Status](https://coveralls.io/repos/libetl/soundtransform/badge.svg)](https://coveralls.io/r/libetl/soundtransform) [![API](https://img.shields.io/badge/API-8%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=8) [![Licence](https://img.shields.io/badge/licence-Apache%20Licence%20%282.0%29-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
================

Android & Pure Java library to shape a voice with an instrument.

**Table of Contents** 
- [How to use the library](#how-to-use-the-library)
- [FluentClient](#fluentclient)
	- [FluentClient samples](#fluentclient-samples)
	- [FluentClient Javadoc](#fluentclient-javadoc)
		- [1. static init](#1-static-init)
		- [2. start of the flow](#2-start-of-the-flow)
		- [3. operations](#3-operations)
		- [4. method flow stops](#4-method-flow-stops)
- [SoundTransformation classes](#soundtransformation-classes)
	- [Built-in SoundTransformation](#built-in-soundtransformation)
		- [Time domain transforms](#time-domain-transforms)
		- [Frequency domain transforms](#frequency-domain-transforms)

## How to use the library
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
* Make sure you have access to the FluentClient class in your project (try the autocompletion feature of your IDE if you have one)
* Read the below documentation about the FluentClient facility
* Have a look at the available SoundTransformation classes
* Use the lib by yourself

## FluentClient
The FluentClient service provider interface is a simple class to give a shortcut to all the features of the lib without walking in the nested classes.

It helps you to proceed to the correct actions at each step, giving you the right programming interface during the pipeline.

To use it, it is only needed to chain the methods invocation. it will always start with a ```FluentClient.start()```, can end with a stop method and can contains an ```andAfterStart``` call to chain two processes in the same instruction of code.

### FluentClient samples
```java

//Set the default Slf4J logger and the log threshold as "WARNING" (the only output will be the warning and error logs)
FluentClient.setDefaultObserversValue (new Slf4jObserver (LogLevel.WARN));

//Apply a 8-bit transform on a wav and then export it to a wav
FluentClient.start ().withClasspathResource ("foo.wav").convertIntoSound ().apply (new EightBitsSoundTransformation (25)).exportToClasspathResource ("bar.wav");

//Shape a wav with an instrument and then export it to a wav
FluentClient.start ().withAPack ("default", packInputStream).withClasspathResource ("foo.wav").convertIntoSound ().findLoudestFrequencies ().shapeIntoSound ("default", "simple_piano", fi).exportToClasspathResource ("bar.wav");

//Play three times the same data, as a File, then as a sound, then as an inputStream
FluentClient.start ().withClasspathResource ("foo.wav").playIt ().convertIntoSound ().playIt ().exportToStream ().playIt ();
 
//Transform a sound into a an array of spectrums
FluentClient.start ().withSounds (sounds).splitIntoSpectrums ().stopWithSpectrums ();

//Transform a lowfi wav file into a cd format wavfile
final FormatInfo fi = new FormatInfo (2, 44100.0);
FluentClient.start ().withClasspathResource ("lowfi.wav").convertIntoSound ().changeFormat (fi).exportToClasspathResource ("hifi.wav");

//Mix of two sounds using two threads for the file-to-sound conversion
FluentClient.start ().inParallel (
    // operations
    FluentClientOperation.prepare ().convertIntoSound ().build (),
    // timeout in seconds
    5,
    // classpath resources
    "foo.wav", "bar.wav")
                         .mixAllInOneSound ().exportToClasspathResourceWithSiblingResource ("targetResource.wav", "existingSoundInSameDirectory.wav");
```

Please have a look at the many different actions that you can ask to the FluentClient in this [JUnit Test](src/test/java/org/toilelibre/libe/soundtransform/FluentClientTest.java)

### FluentClient Javadoc

Four steps can be identified when using the FluentClient SPI :

 1. static init (optional) : the observers subscribe to the future invocations of the FluentClient
 2. start of the flow : a call to `start ()`, followed by one or more calls to a `with... ()` method
 3. operations (optional) : several chained method calls to transform the data in a "one-lined" way
 4. method flow stops (optional) : one call to `stopWith... ()` to get the currently stored data

#### 1. static init

#####   FluentClient.setDefaultObserversValue

```java
static void setDefaultObserversValue (Observer... defaultObservers)
```


Set the passed observers as the default value when a FluentClient is started

It can be useful if you are going to use the FluentClient several times but you want to declare the subscribed observers only once
 
Parameters:  
`defaultObservers` - one or more observer(s)

#### 2. start of the flow

#####   FluentClient.start (*only way to start a FluentClient*)

```java
static FluentClientReady start ()
```


Startup the client

Returns:  
the client, ready to start

#####   FluentClientReady.withAnObserver (before another with.. method)

```java
FluentClientReady withAnObserver (Observer... observers)
```


Tells the client to add an observer that will be notified of different kind of updates from the library. It is ok to call withAnObserver several times.
 If the andAfterStart method is called, the subscribed observers are removed

Parameters:  
`observers` - one or more observer (s)

Returns:  
the client, ready to start

#####   FluentClientReady.inParallel 

```java
<T extends FluentClientCommon> FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, T... clients) throws SoundTransformException
```


Runs asynchronously the same operations on a varargs of started FluentClients 

Parameters:  
`op` - a list of operation to apply

`timeoutInSeconds` - a timeout value. After that, the operation will be stopped, even if it is still processing. 
                     You can choose Integer.MAX_VALUE as a value if you are convinced that it will finish.
                     
`clients` - a list of started FluentClients

Returns:  
the client, with a list of clients inside holding a value each

Throws:  
`SoundTransformException` - can happen if there was a problem during the flow, or if the threads were interrupted

#####   FluentClientReady.inParallel 

```java
FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, Sound []... sounds) throws SoundTransformException
```


Alias for the inParallel method using a list of sounds

Parameters:  
`op` - a list of operation to apply

`timeoutInSeconds` - a timeout value. After that, the operation will be stopped, even if it is still processing. 
                     You can choose Integer.MAX_VALUE as a value if you are convinced that it will finish.
                     
`sounds` - a list of Sounds (each Sound object is a sound channel)

Returns:  
the client, with a list of clients inside holding a value each

Throws:  
`SoundTransformException` - can happen if there was a problem during the flow, or if the threads were interrupted

#####   FluentClientReady.inParallel 

```java
FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, InputStream... inputStreams) throws SoundTransformException
```


Alias for the inParallel method using a list of inputStreams

Parameters:  
`op` - a list of operation to apply

`timeoutInSeconds` - a timeout value. After that, the operation will be stopped, even if it is still processing. 
                     You can choose Integer.MAX_VALUE as a value if you are convinced that it will finish.
                     
`inputStreams` - a list of inputStreams

Returns:  
the client, with a list of clients inside holding a value each

Throws:  
`SoundTransformException` - can happen if there was a problem during the flow, or if the threads were interrupted

#####   FluentClient.inParallel 

```java
FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, File... files) throws SoundTransformException
```


Alias for the inParallel method using a list of files

Parameters:  
`op` - a list of operation to apply

`timeoutInSeconds` - a timeout value. After that, the operation will be stopped, even if it is still processing. 
                     You can choose Integer.MAX_VALUE as a value if you are convinced that it will finish.
                     
`files` - a list of files

Returns:  
the client, with a list of clients inside holding a value each

Throws:  
`SoundTransformException` - can happen if there was a problem during the flow, or if the threads were interrupted

#####   FluentClientReady.inParallel 

```java
FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, float []... freqs) throws SoundTransformException
```


Alias for the inParallel method using a list of freqs

Parameters:  
`op` - a list of operation to apply

`timeoutInSeconds` - a timeout value. After that, the operation will be stopped, even if it is still processing. 
                     You can choose Integer.MAX_VALUE as a value if you are convinced that it will finish.
                     
`freqs` - loudest freqs arrays

Returns:  
the client, with a list of clients inside holding a value each

Throws:  
`SoundTransformException` - can happen if there was a problem during the flow, or if the threads were interrupted

#####   FluentClientReady.inParallel 

```java
FluentClientWithParallelizedClients inParallel (FluentClientOperation op, int timeoutInSeconds, String... classpathResources) throws SoundTransformException
```


Alias for the inParallel method using a list of classpathResources

Parameters:  
`op` - a list of operation to apply

`timeoutInSeconds` - a timeout value. After that, the operation will be stopped, even if it is still processing. 
                     You can choose Integer.MAX_VALUE as a value if you are convinced that it will finish.
                     
`classpathResources` - a list of classpathResources

Returns:  
the client, with a list of clients inside holding a value each

Throws:  
`SoundTransformException` - can happen if there was a problem during the flow, or if the threads were interrupted

#####   FluentClientReady.withAMixedSound 

```java
FluentClientSoundImported withAMixedSound (Sound []... sounds) throws SoundTransformException;
```


Tells the client to use the sounds passed in parameter by mixing them all into one

Parameters:  
`sounds` - a var-arg value of arrays of sounds (each value inside the arrays is a sound channel)

Returns:  
the client, with an imported sound

Throws:  
`SoundTransformException` - the sound files are invalid



#####   FluentClientReady.withAPack (Json InputStream) (before another with.. method)

```java
FluentClientReady withAPack (String packName, InputStream jsonStream) throws SoundTransformException
```


Tells the client to work with a pack. Reads the whole inputStream. A pattern must be followed in the jsonStream to enable the import.

Parameters:  
`packName` - the name of the pack

`jsonStream` - the input stream

Returns:  
the client, ready to start

Throws:  
`SoundTransformException` - the input stream cannot be read, or the json format is not correct, or some sound files are missing

#####   FluentClientReady.withAPack (Android only) (before another with.. method)

```java
FluentClientReady withAPack  (String packName, Object context, Class<?> rClass, int packJsonId) throws SoundTransformException
```


Tells the client to work with a pack. Uses the context object to find the resource from the R object passed in parameter

Parameters:  
`packName`   - the name of the pack

`context`    - the Android context (should be an instance of `android.content.Context`, but left as Object so the FluentClient
               can be used in a non-android project)

`rClass`     - R.raw.getClass () (either from soundtransform or from your pack) should be passed in parameter

`packJsonId` - the id value of your json pack file (should be a field inside R.raw)

Returns:  
the client, ready to start

Throws:  
`SoundTransformException` - the input stream cannot be read, or the json format is not correct, or some sound files are missing

#####   FluentClientReady.withAPack (Json String) (before another with.. method)

```java
FluentClientReady withAPack (String packName, String jsonContent) throws SoundTransformException
```
 

Tells the client to work with a pack. Reads the whole string content. A pattern must be followed in the jsonContent to enable the import.

Here is the format allowed in the file
```javascript
{
  "instrumentName" :
  {
    {"name" : "unknownDetailsFile"},
    {"name" : "knownDetailsFile.wav",
     "frequency": 192.0,
     "attack": 0,
     "decay": 300,
     "sustain": 500,
     "release": 14732},
   ...
  },
  ...
}
```
If a note (one of the records inside the `instrumentName` structure) does not own any detail, it will be obtained by digging
 in the file samples, and can take a really long time. It is advisable to fill in the details in each note.

Parameters:  
`packName` - the name of the pack

`jsonContent` - a string containing the definition of the pack

Returns:  
the client, ready to start

Throws:  
`SoundTransformException` - the json content is invalid, the json format is not correct, or some sound files are missing

#####   FluentClientReady.withAudioInputStream (just after start)

```java
FluentClientWithInputStream withAudioInputStream (InputStream ais)
```


Tells the client to work first with an InputStream. It will not be read yet
 The passed inputStream must own a format metadata object. Therefore it must be an AudioInputStream

Parameters:  
`ais` - the input stream

Returns:  
the client, with an input stream

#####   FluentClientReady.withClasspathResource (just after start)

```java
FluentClientWithFile withClasspathResource (String resource) throws SoundTransformException
```


Tells the client to work first with a classpath resource. It will be converted in a File

Parameters:  
`resource` - a classpath resource that must exist

Returns:  
the client, with a file

Throws:  
`SoundTransformException` - the classpath resource was not found

#####   FluentClientReady.withFile (just after start)

```java
FluentClientWithFile withFile (File file)
```


Tells the client to work first with a file. It will not be read yet

Parameters:  
`file` - source file

Returns:  
the client, with a file

#####   FluentClientReady.withFreqs (just after start)

```java
FluentClientWithFreqs withFreqs (float [] freqs)
```


Tells the client to work first with a loudest frequencies integer array. It will not be used yet

Parameters:  
`freqs` - the loudest frequencies float array

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientReady.withLimitedTimeRecordedInputStream (just after start)

```java
FluentClientWithInputStream withLimitedTimeRecordedInputStream (StreamInfo streamInfo) throws SoundTransformException
```


Tells the client to work first to open the microphone and to record a sound
The result will be of an InputStream type.
The recording time will be the one passed in the streamInfo

Parameters:  
`streamInfo` - the stream info

Returns:  
the client, with an input stream

Throws:  
`SoundTransformException` - the mic could not be read, the recorder could not start, or the buffer did not record anything

#####   FluentClientReady.withRawInputStream (just after start)

```java
FluentClientWithInputStream withRawInputStream (InputStream is, StreamInfo isInfo) throws SoundTransformException
```


Tells the client to work first with a byte array InputStream or any readable DataInputStream. It will be read and transformed into an AudioInputStream
 The passed inputStream must not contain any metadata piece of information.

Parameters:  
`is` - the input stream

`isInfo` - the stream info

Returns:  
the client, with an input stream

Throws:  
`SoundTransformException` - the input stream cannot be read, or the conversion did not work

#####   FluentClientReady.withRecordedInputStream (just after start)

```java
FluentClientWithInputStream withRecordedInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException
```


Tells the client to work first to open the microphone and to record a sound
The result will be of an InputStream type.
The frameLength in the streamInfo will be ignored

 /!\ : blocking method, the `stop.notify` method must be called in another thread.

Parameters:  
`streamInfo` - the future input stream info

`stop` - the method notify must be called to stop the recording

Returns:  
the client, with an input stream

Throws:  
`SoundTransformException` - the mic could not be read, the recorder could not start, or the buffer did not record anything

#####   FluentClientReady.withSounds (just after start)

```java
FluentClientSoundImported withSounds (Sound [] sounds)
```


Tells the client to work first with a sound object

Parameters:  
`sounds` - the sound object

Returns:  
the client, with an imported sound

#####   FluentClientReady.withSpectrums (just after start)

```java
FluentClientWithSpectrums withSpectrums (List<Spectrum<Serializable> []> spectrums)
```


Tells the client to work first with a spectrum formatted sound.
 The spectrums inside must be in a list (each item must correspond to a channel) The spectrums are ordered in an array in chronological order

Parameters:  
`spectrums` - the spectrums

Returns:  
the client, with the spectrums

#### 3. operations

#####   FluentClientWithFreqs.adjust

```java
FluentClientWithFreqs adjust ()
```

Adjust the loudest freqs array to match exactly the piano notes frequencies

Returns:  
the client, with a loudest frequencies float array

#####   FluentClient*.andAfterStart

```java
FluentClientReady andAfterStart ()
```

Start over the client : reset the state and the value objects nested in the client

Returns:  
the client, ready to start

#####   FluentClientSoundImported.append

```java
FluentClientSoundImported append (Sound [] sounds) throws SoundTransformException
```

Append the sound passed in parameter to the current sound stored in the client

Parameters:  
`sounds` - the sound to append the current sound to

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the sound is null or if there is a problem with the appending please ensure that both sounds have the same number of channels

#####   FluentClientSoundImported.apply

```java
FluentClientSoundImported apply (SoundTransformation st) throws SoundTransformException
```

Apply one transform and continue with the result sound

Parameters:  
`st` - the SoundTransformation to apply

Returns:  
the client with a sound imported

Throws:  
`SoundTransformException` - if the transform does not work

#####   FluentClientSoundImported.changeFormat

```java
FluentClientSoundImported changeFormat (FormatInfo formatInfo) throws SoundTransformException
```

Changes the current imported sound to fit the expected format

Parameters:  
`formatInfo` - the new expected format

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException`

#####   FluentClientWithFreqs.compress

```java
FluentClientWithFreqs compress (float factor)
```


Compresses the loudest freq array (speedup or slowdown).
When shaped into a sound, the result will have a different tempo than the original sound but will keep the same pitch

Parameters:  
`factor` - the factor parameter quantifies how much the stretch or shrink will be. (i.e if factor = 0.5, then the result 
           will be twice as long than the original)

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientWithFile.convertIntoSound

```java
FluentClientSoundImported convertIntoSound () throws SoundTransformException
```


Shortcut for importToStream ().importToSound () : Conversion from a File to a Sound

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if one of the two import fails

#####   FluentClientSoundImported.cutSubSound

```java
FluentClientSoundImported cutSubSound (int start, int end) throws SoundTransformException
```
  

Splice a part of the sound between the sample \#start and the sample \#end

Parameters:  
`start` - the first sample to cut

`end` - the last sample to cut

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the indexes are out of bound

#####   FluentClientSoundImported.exportToClasspathResource

```java
FluentClientWithFile exportToClasspathResource (String resource) throws SoundTransformException
```


Shortcut for exportToStream ().writeToClasspathResource (resource) : Conversion from a Sound to a File

Parameters:  
`resource` - a resource that can be found in the classpath

Returns:  
the client, with a file written

Throws:  
`SoundTransformException` - if one of the two operations fails

#####   FluentClientSoundImported.exportToClasspathResourceWithSiblingResource

```java
FluentClientWithFile exportToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException
```


Shortcut for exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource)

Parameters:  
`resource` - a resource that may or may not exist in the classpath

`siblingResource` - a resource that can be found in the classpath.

Returns:  
the client, with a file written

Throws:  
`SoundTransformException` - if one of the two operations fails

#####   FluentClientSoundImported.exportToFile

```java
FluentClientWithFile exportToFile (File file)   throws SoundTransformException
```


Shortcut for exportToStream ().writeToFile (file)

Parameters:  
`file` - the destination file

Returns:  
the client, with a file written

Throws:  
`SoundTransformException` - if one of the two operations fails

#####   FluentClientSoundImported.exportToStream

```java
FluentClientWithInputStream exportToStream () throws SoundTransformException
```


Uses the current imported sound and converts it into an InputStream, ready to be written to a file (or to be read again)

Returns:  
the client, with an inputStream

Throws:  
`SoundTransformException` - if the metadata format object is invalid, or if the sound cannot be converted

#####   FluentClientWithSpectrums.extractSound

```java
FluentClientSoundImported extractSound () throws SoundTransformException
```


Uses the current available spectrums objects to convert them into a sound (with one or more channels)

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the spectrums are in an invalid format, or if the transform to sound does not work

#####   FluentClientSoundImported.extractSubSound

```java
FluentClientSoundImported extractSubSound (int start, int end) throws SoundTransformException
```


Extract a part of the sound between the sample \#start and the sample \#end

Parameters:  
`start` - the first sample to extract

`end` - the last sample to extract

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the indexes are out of bound

#####   FluentClientWithFreqs.filterRange

```java
FluentClientWithFreqs filterRange (float low, float high)
```


Remove the values between low and high in the loudest freqs array (replace them by 0)

Parameters:  
`low` - low frequency (first one to avoid)

`high` - high frequency (last one to avoid)

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientSoundImported.findLoudestFrequencies

```java
FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException
```


Will invoke a soundtransform to find the loudest frequencies of the sound, chronologically
 Caution : the original sound will be lost, and it will be impossible to revert this conversion.
 When shaped into a sound, the new sound will only sounds like the instrument you shaped the freqs with

Returns:  
the client, with a loudest frequencies float array

Throws:  
`SoundTransformException` - if the convert fails

#####   FluentClientWithInputStream.importToSound

```java
FluentClientSoundImported importToSound () throws SoundTransformException
```


Uses the current input stream object to convert it into a sound (with one or more channels)

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - the inputStream is invalid, or the convert did not work

#####   FluentClientWithFile.importToStream

```java
FluentClientWithInputStream importToStream () throws SoundTransformException
```


Opens the current file and convert it into an InputStream, ready to be read (or to be written to a file)

Returns:  
the client, with an inputStream

Throws:  
`SoundTransformException` - the current file is not valid, or the conversion did not work

#####   FluentClientWithFreqs.insertPart

```java
FluentClientWithFreqs insertPart (float [] subFreqs, int start)
```


Add some new values in the loudest freqs array from the "start" index (add the values of subfreqs)

Parameters:  
`subFreqs` - loudest freqs array to insert

`start` - index where to start the insert

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientSoundImported.loop

```java
FluentClientSoundImported loop (int length) throws SoundTransformException
```


Extract a part of the sound between the sample \#start and the sample \#end

Parameters:  
`length` - the number of samples of the result sound

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the length is not positive

#####   FluentClientSoundImported.mixWith

```java
FluentClientSoundImported mixWith (Sound [] sound) throws SoundTransformException
```


Combines the current sound with another sound. The operation is not reversible

Parameters:  
`sound` - the sound to mix the current sound with

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the sound is null or if there is a problem with the mix

#####   FluentClientWithParallelizedClients.mixAllInOneSound

```java
FluentClientSoundImported mixAllInOneSound () throws SoundTransformException
```


Uses the sounds inside the nested clients to mix them all and to produce a single sound

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - if the nested clients are not in the Sound imported state

#####   FluentClientWithFreqs.octaveDown

```java
FluentClientWithFreqs octaveDown ()
```


Changes the loudest frequencies array to become one octave lower

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientWithFreqs.octaveUp

```java
FluentClientWithFreqs octaveUp ()
```

Changes the loudest frequencies array to become one octave upper

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientSoundImported.playIt or FluentClientWithFile.playIt or FluentClientWithInputStream.playIt or FluentClientWithSpectrums.playIt

```java
<T> playIt () throws SoundTransformException
```


Plays the current audio data

Returns:  
the client, with the current data

Throws:  
`SoundTransformException` - could not play the current audio data

#####   FluentClientWithFreqs.replacePart

```java
FluentClientWithFreqs replacePart (float [] subFreqs, int start)
```


Replace some of the values of the loudest freqs array from the "start" index (replace them by the values of subfreqs)

Parameters:  
`subFreqs` - replacement loudest freqs array

`start` - index where to start the replacement

Returns:  
the client, with a loudest frequencies float array

#####   FluentClientWithFreqs.shapeIntoSound

```java
FluentClientSoundImported shapeIntoSound (String packName, String instrumentName, FormatInfo formatInfo) throws SoundTransformException
```


Shapes these loudest frequencies array into a sound and set the converted sound in the pipeline

Parameters:  
`packName` - reference to an existing imported pack (must be invoked before the shapeIntoSound method by using withAPack)

`instrumentName` - the name of the instrument that will map the freqs object

`formatInfo` - the wanted format for the future sound

Returns:  
the client, with a sound imported

Throws:  
`SoundTransformException` - could not call the soundtransform to shape the freqs

#####   FluentClientSoundImported.splitIntoSpectrums

```java
FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException
```


Uses the current sound to pick its spectrums and set that as the current data in the pipeline

Returns:  
the client, with the spectrums

Throws:  
`SoundTransformException` - could not convert the sound into some spectrums

#####   FluentClientWithInputStream.writeToClasspathResource

```java
FluentClientWithFile writeToClasspathResource (String resource) throws SoundTransformException
```


Writes the current InputStream in a classpath resource in the same folder as a previously imported classpath resource. Caution : if no classpath resource was imported before, this operation will not work. Use writeToClasspathResourceWithSiblingResource instead

Parameters:  
`resource` - a classpath resource.

Returns:  
the client, with a file

Throws:  
`SoundTransformException` - there is no predefined classpathresource directory, or the file could not be written

#####   FluentClientWithInputStream.writeToClasspathResourceWithSiblingResource

```java
FluentClientWithFile writeToClasspathResourceWithSiblingResource (String resource, String siblingResource) throws SoundTransformException
```


Writes the current InputStream in a classpath resource in the same folder as a the sibling resource.

Parameters:  
`resource` - a classpath resource that may or may not exist yet

`siblingResource` - a classpath resource that must exist

Returns:  
the client, with a file

Throws:  
`SoundTransformException` - no such sibling resource, or the file could not be written

#####   FluentClientWithInputStream.writeToFile

```java
FluentClientWithFile writeToFile (File file) throws SoundTransformException
```


Writes the current InputStream in a file

Parameters:  
`file` - the destination file

Returns:  
the client, with a file

Throws:  
`SoundTransformException` - The file could not be written

#### 4. method flow stops

#####   FluentClient*.stopWithAPack

```java
Pack stopWithAPack (String title)
```

Stops the client pipeline and returns the pack whose title is in parameter

Parameters:  
`title` - the title of the pack

Returns:  
a pack object

#####   FluentClientWithFile.stopWithFile

```java
File stopWithFile ()
```

Stops the client pipeline and returns the obtained file

Returns:  
a file

#####   FluentClientWithFreqs.stopWithFreqs

```java
float [] stopWithFreqs ()
```

Stops the client pipeline and returns the obtained loudest frequencies

Returns:  
loudest frequencies array

#####   FluentClientWithInputStream.stopWithInputStream

```java
InputStream stopWithInputStream ()
```

Stops the client pipeline and returns the obtained input stream

Returns:  
an input stream

#####   FluentClient*.stopWithObservers

```java
Observer [] stopWithObservers ();
```

Stops the client pipeline and returns the currently subscribed observers

Returns:  
the observers

#####   FluentClientWithParallelizedClients.stopWithResults

```java
<T> T [] stopWithResults (Class<T> resultClass)
```

Stops the client pipeline and get all the values inside each nested client

Parameters:  
`resultClass` - You have to specify what type of result you expect.
                the value can be one of this list : (Sound.class, InputStream.class, File.class, String.class, float [].class)

Returns:  
an array of results

#####   FluentClientSoundImported.stopWithSounds

```java
Sound [] stopWithSounds ()
```

Stops the client pipeline and returns the obtained sound

Returns:  
a sound value object

#####   FluentClientWithSpectrums.stopWithSpectrums

```java
List<Spectrum<Serializable> []> stopWithSpectrums ()
```


Stops the client pipeline and returns the obtained spectrums

Returns:  
a list of spectrums for each channel

#####   FluentClientWithInputStream.stopWithStreamInfo

```java
StreamInfo stopWithStreamInfo () throws SoundTransformException
```


Stops the client pipeline and returns the obtained stream info object

Returns:  
a streamInfo object

Throws:  
`SoundTransformException` - could not read the StreamInfo from the current inputstream

## SoundTransformation classes
The SoundTransformation classes are a family of classes whose role is to process something on a single sound channel. 

The signature will contain this transform method for every class :
```java
public Sound transform (Sound input) throws SoundTransformException;
```

The simplest way to process a transform is to use the `FluentClientSoundImported.apply` method.

Calling directly the transform method can do the trick, but don't forget to call it on each channel with exactly the same parameters, and to pass the list of your observers.

Some of these classes only iterate over the samples once, changing some samples with a formula (for example EightBightSoundTransformation).
Some others convert first the sound in the frequency domain before processing it (it uses a spectrum as input), like EqualizerSoundTransformation.

### Built-in SoundTransformation
#### Time domain transforms
##### CutSoundTransformation
##### EightBitsSoundTransformation
##### FadeSoundTransformation
##### InsertPartSoundTransformation
##### LinearRegressionSoundTransformation
##### LoopSoundTransformation
##### MixSoundTransformation
##### NormalizeSoundTransformation
##### PitchSoundTransformation
##### ReplacePartSoundTransformation
##### SubSoundExtractSoundTransformation
#### Frequency domain transforms
##### CepstrumSoundTransformation
####### `public class CepstrumSoundTransformation<T extends Serializable> extends SimpleFrequencySoundTransformation<T> implements PeakFindSoundTransformation<T>`

Transforms a sound into a list of cepstrums (log modulus of the spectrums). Useful to get the f0 values of a sound (loudest freqs array). 

The obtained Spectrum are not really spectrums. They consist of a graph a quefrencies (and not frequencies).<br/> The peak can represent the f0 (if the FormatInfo of the input sound is adequate), but it is not faithful everytime.<br/> This method can detected false values.

 * **Parameters:** `<T>` — The kind of object held inside a spectrum.

 `public CepstrumSoundTransformation ()`

Constructor with default values. The cepstrums will not be kept when using the getCepstrums method

 `public CepstrumSoundTransformation (final boolean note1)`

Constructor with default values. The cepstrums will not be kept when using the getCepstrums method and the cepstrum will be made once, using the whole sound

 * **Parameters:** `note1` — if true, the loudest freqs array will contain a single element

 `public CepstrumSoundTransformation (final double step1)`

The cepstrums will not be kept when using the getCepstrums method (increasing the value will speed the transform but will be less precise)

 * **Parameters:** `step1` — the iteration step

 `public CepstrumSoundTransformation (final double step1, final boolean note1)`

The cepstrums will not be kept when using the getCepstrums method (increasing the value will speed the transform but will be less precise) and the cepstrum will be made once, using the whole sound

 * **Parameters:**
   * `step1` — the iteration step
   * `note1` — if true, the loudest freqs array will contain a single element

 `public CepstrumSoundTransformation (final double step1, final boolean keepCepstrums1, final boolean note1)`

Constructor will every parameter specified This can cause a big memory leak if not used with care. Be vigilant. (increasing the value will speed the transform but will be less precise) and the cepstrum will be made once, using the whole sound

 * **Parameters:**
   * `keepCepstrums1` — if true, the cepstrums will all be saved after each call to the method transform
   * `step1` — the iteration step
   * `note1` — if true, the loudest freqs array will contain a single element

##### EqualizerSoundTransformation
##### GaussianEqualizerSoundTransformation
##### PeakFindWithHPSSoundTransformation
##### ShapeSoundTransformation
##### SimpleFrequencySoundTransformation
##### SlowdownSoundTransformation
##### SpeedUpSoundTransformation
