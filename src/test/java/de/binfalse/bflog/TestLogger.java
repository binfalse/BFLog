package de.binfalse.bflog;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;


public class TestLogger extends LOGGER
{
	public static class SysIO
	{
		public ByteArrayOutputStream sysOut, sysErr;
		PrintStream orgErr;
		PrintStream orgOut;
		public SysIO ()
		{
			reset ();
		}
		
		public void reset ()
		{
			stopIoCapture ();
			 sysErr = new ByteArrayOutputStream ();
			 sysOut = new ByteArrayOutputStream ();
		}
		
		public ByteArrayOutputStream getSysErr ()
		{
			return sysErr;
		}
		
		public ByteArrayOutputStream getSysOut ()
		{
			return sysOut;
		}
		
		public void startIoCapture ()
		{
			orgErr = System.err;
			orgOut = System.out;
			System.setErr (new PrintStream(sysErr));
			System.setOut (new PrintStream(sysOut));
			
		}
		
		public void stopIoCapture ()
		{
			System.setErr (orgErr);
			System.setOut (orgOut);
		}
	}
	
	@Test
	public void logToFile ()
	{
		File testLogFile = null;
		try
		{
			testLogFile = File.createTempFile ("BFlogTest", "test");
			testLogFile.deleteOnExit ();
		}
		catch (IOException e)
		{
			System.err.println ("temp file creation failed -> cannot perform test -> skipping");
			e.printStackTrace();
			return;
		}

		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		LOGGER.setLogFile (testLogFile);
		LOGGER.setLogToFile (true);
		LOGGER.setMinLevel (LOGGER.DEBUG);
		LOGGER.warn ("test message");
		LOGGER.closeLogger ();
		
		assertTrue ("logger didn't write to logfile", testLogFile.length () > 0);
		
	}
	
	@Test
	public void testStreams ()
	{
		LOGGER.setLogToFile (false);
		SysIO sio = new SysIO ();
		LOGGER.setMinLevel (LOGGER.DEBUG);
		
		// don't print anything

		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		
		sio.startIoCapture ();
		LOGGER.warn ("test message");
		sio.stopIoCapture ();

		assertTrue ("logger shouldn't log to sys out", sio.getSysOut ().toString ().isEmpty ());
		assertTrue ("logger shouldn't log to sys err", sio.getSysErr ().toString ().isEmpty ());
		
		sio.reset ();

		// print to sys out
		LOGGER.setLogToStdOut (true);
		
		sio.startIoCapture ();
		LOGGER.warn ("test message");
		sio.stopIoCapture ();

		assertFalse ("logger should log to sys out!", sio.getSysOut ().toString ().isEmpty ());
		assertTrue ("logger shouldn't log to sys err", sio.getSysErr ().toString ().isEmpty ());
		
		sio.reset ();

		// print to sys out and sys err
		LOGGER.setLogToStdErr (true);
		
		sio.startIoCapture ();
		LOGGER.warn ("test message");
		sio.stopIoCapture ();

		assertFalse ("logger should log to sys out", sio.getSysOut ().toString ().isEmpty ());
		assertFalse ("logger should log to sys err", sio.getSysErr ().toString ().isEmpty ());
		
		sio.reset ();

		// print to sys out and sys err
		LOGGER.setLogToStdOut (false);
		
		sio.startIoCapture ();
		LOGGER.warn ("test message");
		sio.stopIoCapture ();

		assertTrue ("logger shouldn't log to sys out", sio.getSysOut ().toString ().isEmpty ());
		assertFalse ("logger should log to sys err", sio.getSysErr ().toString ().isEmpty ());
		
	}

	@Test
	public void testLevels()
	{
		// set to "log everything"
		LOGGER.setMinLevel (LOGGER.DEBUG);
		assertEquals ("setting min level failed.", logLevel, LOGGER.DEBUG | LOGGER.INFO | LOGGER.WARN | LOGGER.ERROR);
		assertTrue ("setting min level failed.", 
			LOGGER.hasLevel (LOGGER.DEBUG) && 
			LOGGER.hasLevel (LOGGER.INFO) && 
			LOGGER.hasLevel (LOGGER.WARN) && 
			LOGGER.hasLevel (LOGGER.ERROR));
		
		// log everything but warnings
		LOGGER.rmLevel (LOGGER.WARN);
		assertEquals ("removing a level failed", logLevel, LOGGER.DEBUG | LOGGER.INFO | LOGGER.ERROR);
		assertTrue ("removing a level failed", 
			LOGGER.hasLevel (LOGGER.DEBUG) && 
			LOGGER.hasLevel (LOGGER.INFO) && 
			!LOGGER.hasLevel (LOGGER.WARN) && 
			LOGGER.hasLevel (LOGGER.ERROR));
		
		// log only errors
		LOGGER.setMinLevel (LOGGER.ERROR);
		assertEquals ("setting min level failed. ", logLevel, LOGGER.ERROR);
		assertTrue ("setting min level failed. ", 
			!LOGGER.hasLevel (LOGGER.DEBUG) && 
			!LOGGER.hasLevel (LOGGER.INFO) && 
			!LOGGER.hasLevel (LOGGER.WARN) && 
			LOGGER.hasLevel (LOGGER.ERROR));
		
		
		// log only info
		LOGGER.setLevel (LOGGER.INFO);
		assertEquals ("setting a level failed", logLevel, LOGGER.INFO);
		assertTrue ("setting a level failed", 
			!LOGGER.hasLevel (LOGGER.DEBUG) && 
			LOGGER.hasLevel (LOGGER.INFO) && 
			!LOGGER.hasLevel (LOGGER.WARN) && 
			!LOGGER.hasLevel (LOGGER.ERROR));
		
		
		// still log just info 
		LOGGER.addLevel (LOGGER.INFO);
		assertEquals ("adding an existing level failed", logLevel, LOGGER.INFO);
		assertTrue ("adding an existing level failed", 
			!LOGGER.hasLevel (LOGGER.DEBUG) && 
			LOGGER.hasLevel (LOGGER.INFO) && 
			!LOGGER.hasLevel (LOGGER.WARN) && 
			!LOGGER.hasLevel (LOGGER.ERROR));
		
		
		// log info and error
		LOGGER.addLevel (LOGGER.ERROR);
		assertEquals ("adding another level failed", logLevel, LOGGER.INFO | LOGGER.ERROR);
		assertTrue ("adding another level failed", 
			!LOGGER.hasLevel (LOGGER.DEBUG) && 
			LOGGER.hasLevel (LOGGER.INFO) && 
			!LOGGER.hasLevel (LOGGER.WARN) && 
			LOGGER.hasLevel (LOGGER.ERROR));
		
		
		// log warn and error
		LOGGER.setMinLevel (LOGGER.WARN);
		assertEquals ("setting min level failed. ", logLevel, LOGGER.WARN | LOGGER.ERROR);
		assertTrue ("setting min level failed. ", 
			!LOGGER.hasLevel (LOGGER.DEBUG) && 
			!LOGGER.hasLevel (LOGGER.INFO) && 
			LOGGER.hasLevel (LOGGER.WARN) && 
			LOGGER.hasLevel (LOGGER.ERROR));
		
	}
	
	
}
