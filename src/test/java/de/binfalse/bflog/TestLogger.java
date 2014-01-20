/**
 * Copyright (c) 2007-2014 Martin Scharm -- <software@binfalse.de>
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the
 * disclaimer below) provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * 
 * * Neither the name of <Owner Organization> nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 * 
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
 * GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.binfalse.bflog;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

import de.binfalse.bflog.samplecallbacks.LogCollector;


/**
 * Test the LOGGER.
 */
public class TestLogger extends LOGGER
{
	
	/**
	 * The Class SysIO to capture std::out and std::err.
	 */
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
	
	/**
	 * Test the log-to-file mechanism.
	 */
	@Test
	public void logToFile ()
	{
		// open log file
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
		
		// disable all other log stuff
		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		// enable log file
		LOGGER.setLogFile (testLogFile);
		LOGGER.setLogToFile (true);
		// set min = debug
		LOGGER.setMinLevel (LOGGER.DEBUG);
		// log a message
		LOGGER.warn ("test message");
		// close the logger to finish the file stream
		LOGGER.closeLogger ();
		
		// make sure we've written something to the file.
		assertTrue ("logger didn't write to logfile", testLogFile.length () > 0);
		
	}
	
	/**
	 * Test call backs.
	 */
	@Test
	public void testCallBacks ()
	{
		// stop logging
		LOGGER.setLogToStdErr (false);
		LOGGER.setLogToStdOut (false);
		LOGGER.setLogToFile (false);

		// create a log collector
		LogCollector lc = new LogCollector ();
		LOGGER.addCallback (lc);
		
		// temporary class, just to log some stuff
		class Temporary
		{
			public void logIt (int i)
			{
				LOGGER.debug ("test debug message " + i);
				LOGGER.info ("test info message " + i);
				LOGGER.warn ("test warn message " + i);
				LOGGER.error ("test error message " + i);
			}
		}
		Temporary t = new Temporary ();

		// log different messages in different levels
		LOGGER.setMinLevel (LOGGER.DEBUG);
		t.logIt (1);

		LOGGER.rmLevel (LOGGER.WARN);
		t.logIt (2);

		LOGGER.addLevel (LOGGER.INFO);
		t.logIt (3);

		LOGGER.setLevel (LOGGER.WARN);
		t.logIt (4);
		
		LOGGER.setMinLevel (LOGGER.INFO);
		t.logIt (5);
		
		LOGGER.addLevel (LOGGER.DEBUG);
		t.logIt (5);

		// check wether the logged results are as expected
		assertEquals ("unexpected number of debug messages logged: " + lc.getDebugs (), 4, lc.getDebugs ().size ());
		assertEquals ("unexpected number of info messages logged: " + lc.getInfos (), 5, lc.getInfos ().size ());
		assertEquals ("unexpected number of warn messages logged: " + lc.getWarnings (), 4, lc.getWarnings ().size ());
		assertEquals ("unexpected number of error messages logged: " + lc.getErrors (), 5, lc.getErrors ().size ());
	}
	
	/**
	 * Test IO streams.
	 */
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

		// print to not sys out and sys err
		LOGGER.setLogToStdOut (false);
		
		sio.startIoCapture ();
		LOGGER.warn ("test message");
		sio.stopIoCapture ();

		assertTrue ("logger shouldn't log to sys out", sio.getSysOut ().toString ().isEmpty ());
		assertFalse ("logger should log to sys err", sio.getSysErr ().toString ().isEmpty ());
		
	}

	/**
	 * Test levels.
	 */
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
