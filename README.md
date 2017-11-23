BFLog
=====

Simple but efficient logging facility.

Include it
------

BFLog is available through [Maven's Central Repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.binfalse%22%20AND%20a%3A%22BFLog%22).
If you are using Maven you just need to add the following dependency:

	<dependency>
		<groupId>de.binfalse</groupId>
		<artifactId>BFLog</artifactId>
		<version>1.3.4</version>
	</dependency>

That's it. Maven will care about the rest. You can start using it!


Use it
------

To use the logger in your project just call one of the static functions

* [`LOGGER.debug ("debug message", [EXCEPTION])`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/LOGGER.java#L486)
* [`LOGGER.info ("info message", [EXCEPTION])`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/LOGGER.java#L463)
* [`LOGGER.warn ("warning message", [EXCEPTION])`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/LOGGER.java#L509)
* [`LOGGER.error ("error message", [EXCEPTION])`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/LOGGER.java#L440)

The exception parameter is optional, just in case an exception was thrown..

Example:

	import de.binfalse.bflog.LOGGER;
	[...]
	
	public class SomeClass
	{
		[...]
		
		public function someFunction (Object o)
		{
			if (o == null)
			{
				LOGGER.debug ("object o is null");
				o = new RandomObject ();
			}
			
			try
			{
				// do some stuff
				LOGGER.info ("i'll read a file.");
				BufferedReader br = new BufferedReader (new FileReader (new File ("/some/file")));
				LOGGER.debug ("first line: " + br.readLine ());
				br.close ();
			}
			catch (IOException e)
			{
				LOGGER.error ("failed reading file", e);
			}
			
			LOGGER.closeLogger ();
		}
	}



Initialize it
------

There are four log levels:

* `LOGGER.DEBUG`
* `LOGGER.INFO`
* `LOGGER.WARN`
* `LOGGER.ERROR`

By default only warnings and errors are logged to sys::err. If that's sufficient for you

To modify the levels to log call one of the following methods:

	// set min level info -> log info/warn/error
	LOGGER.setMinLevel (LOGGER.INFO);
	// remove level warn -> log info/error
	LOGGER.rmLevel (LOGGER.WARN);
	// add level debug -> log debug/info/error
	LOGGER.addLevel (LOGGER.DEBUG);
	// set explicitly ony warn and debug
	LOGGER.setLevel (LOGGER.DEBUG|LOGGER.WARN);

To also enable logging to sys::out and sys::err call:

	LOGGER.setLogToStdErr (YourBoolean);
	LOGGER.setLogToStdOut (YourBoolean);

Of course it is also possible to log to a file. You first need to define the file before you can start logging to that file. Don't forget to close the logger afterwards:

	// tell the logger which file to use
	LOGGER.setLogFile (new File ("/your/log/file"));
	// enable logging to that file
	LOGGER.setLogToFile (true);

	[do whatever you want]

	LOGGER.closeLogger ();

Advanced Usage
------

For those of you who aren't satisfied with the above options may create an own callback class, that implements the interface [`de.binfalse.bflog.LogCallback`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/LogCallback.java). You need to implement the [`public void logged (int lvl, String msg)`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/LogCallback.java#L54) function, which will be called in case of a log event. The level of that event as well as the log message will be provided as an argument. Just submit your object to get all log messages:

	MyCallBack mcb = new MyCallBack ();
	LOGGER.addCallback (mcb);

This makes it for example possible to send mails in case of errors or do some other freak stuff. An example of such a callback can be found in [`de.binfalse.bflog.samplecallbacks.LogCollector`](https://github.com/binfalse/BFLog/blob/master/src/main/java/de/binfalse/bflog/samplecallbacks/LogCollector.java)
