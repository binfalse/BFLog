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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


/**
 * LOGGER is a tiny and simple logging framework.
 * Following options are available to initialize the LOGGER:
 * 
 * <pre>
 * // set log level
 * LOGGER.setMinLevel (LOGGER.WARN);
 * //write to std::out?
 * LOGGER.setLogToStdOut (true);
 * //write to std::err? 
 * LOGGER.setLogToStdErr (true);
 * //write to a file?
 * LOGGER.setLogFile (new File ("/my/log/file"));
 * LOGGER.setLogToFile (true);
 * </pre>
 * 
 * If you don't initialize the LOGGER it defaults to printing to only warning and error messages sdt::err. Of course all these methods can be used from wherever you want.
 * 
 * To log a message just call one of the following functions:
 * 
 * <table>
 * 	<tbody>
 * 	<tr>
 * 		<th>LOGGER.debug ("message", [EXCEPTION])</th>
 * 		<td>log a debug message</td>
 * 	</tr>
 * 	<tr>
 * 		<th>LOGGER.info ("message", [EXCEPTION])</th>
 * 		<td>log an info message</td>
 * 	</tr>
 * 	<tr>
 * 		<th>LOGGER.warn ("message", [EXCEPTION])</th>
 * 		<td>log a warn message</td>
 * 	</tr>
 * 	<tr>
 * 		<th>LOGGER.error ("message", [EXCEPTION])</th>
 * 		<td>log an error message</td>
 * 	</tr>
 * 	</tbody>
 * </table>
 * 
 * Thereby, the exception parameter is optional, just in case an exception was thrown.
 * 
 * To close the LOGGER just call <code>LOGGER.closeLogger ()</code> in order to clean up and close the file stream.
 * 
 * 
 * @author Martin Scharm
 *
 */
public class LOGGER
{
	
	/** The Constant ERROR. */
	public static final int ERROR = 8;
	
	/** The Constant WARN. */
	public static final int WARN = 4;
	
	/** The Constant INFO. */
	public static final int INFO = 2;
	
	/** The Constant DEBUG. */
	public static final int DEBUG = 1;
	
	/** The Constant date format that is used in log messages. Defaults to <code>dd.MM HH:mm:ss</code>, e.g. <code>27.10 18:55:49</code> */
	public static final SimpleDateFormat	dateformat	= new SimpleDateFormat (
		"dd.MM HH:mm:ss");
	
	/** The os independent new line char */
	public static final String NEWLINE = System.getProperty("line.separator");
	
	/** The log level, defaults to warning and error messages. */
	protected static int logLevel = WARN | ERROR;
	
	/** The file where we will log our messages. */
	private static File logFileFile;
	
	/** The corresponding stream to the log file. */
	private static BufferedWriter logFile;
	
	/** Should we log to file? */
	private static boolean logToFile = false;

	/** Should we log to std::out? */
	private static boolean logToStdOut = false;

	/** Should we log to std::err? */
	private static boolean logToStdErr = true;

	/** Should we log the stack trace? */
	private static boolean logStackTrace = false;
	
	/** The log call backs. */
	private static Vector<LogCallback> callBacks = new Vector<LogCallback> ();
	
	/**
	 * Define the file to write the log to. Does not start logging to that file, call <code>LOGGER.setLogToFile (true)</code> to start writing to log file. Same as <code>LOGGER.setLogFile (new File (fileName))</code>
	 *
	 * @param fileName the log file
	 * @return true, if sets the log file
	 */
	public static boolean setLogFile (String fileName)
	{
		return setLogFile (new File (fileName));
	}
	
	/**
	 * Define the file to write the logs to. Does not start logging to that file, call <code>LOGGER.setLogToFile (true)</code> to start writing to log file.
	 *
	 * @param file the log file
	 * @return true, if the log file is read- and writeable
	 */
	public static boolean setLogFile (File file)
	{
		try
		{
			if (!file.exists())
				file.createNewFile();
			
			if (!file.isFile() || !file.canRead() || !file.canWrite())
				return false;
			
			logFileFile = file;
		}
		catch (IOException e)
		{
			error ("cannot set logfile", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Open the log file.
	 *
	 * @return true, if log file successfully opened.
	 */
	private static boolean openLogFile ()
	{
		try
		{
			logFile = new BufferedWriter (new FileWriter (logFileFile, true));
		}
		catch (IOException e)
		{
			error ("error opening log file: " + logFileFile.getAbsolutePath (), e);
			return false;
		}
		return true;
	}
	
	/**
	 * Close log file.
	 *
	 * @return true, if log file closed successfully
	 */
	private static boolean closeLogFile ()
	{
		if (logFile == null)
			return true;
		
		try
		{
			logFile.flush ();
			logFile.close ();
		}
		catch (IOException e)
		{
			error ("error closing logfile", e);
			return false;
		}
		
		logFile = null;
		return true;
	}
	
	/**
	 * Close the logger. Cleans up and closes the log file.
	 */
	public static void closeLogger ()
	{
		closeLogFile ();
		logToStdErr = false;
		logToStdOut = false;
		logLevel = 0;
	}

	/**
	 * Should we write to the log file? Only successful if log file was defined previously. (call <code>LOGGER.setLogFile (file)</code>)
	 *
	 * @param log should we log to file?
	 * @return true, log file defined and stream open successfully
	 */
	public static boolean setLogToFile (boolean log)
	{
		if (logFileFile == null)
			return false;
		
		logToFile = log;
		if (logToFile)
			return openLogFile ();
		else
			return closeLogFile ();
	}
	
	/**
	 * Should we log stack traces? If you pass <code>true</code> you'll find a stacktrace after the log message which passed an exception, otherwise (default) only the exception class and the corresponding message will be logged.
	 *
	 * @param log should we log stack traces?
	 */
	public static void setLogStackTrace (boolean log)
	{
		logStackTrace = log;
	}

	/**
	 * Should we log to std::err? default: <code>true</code>
	 *
	 * @param log should we log to std:err?
	 */
	public static void setLogToStdErr (boolean log)
	{
		logToStdErr = log;
	}

	/**
	 * Should we log to std::out? default: <code>false</code>
	 *
	 * @param log the log to std out
	 */
	public static void setLogToStdOut (boolean log)
	{
		logToStdOut = log;
	}

	/**
	 * Sets the min log level. Available log levels in order: <code>LOGGER.DEBUG</code>, <code>LOGGER.INFO</code>, <code>LOGGER.WARN</code>, <code>LOGGER.ERROR</code>. By default the min level is <code>LOGGER.WARN</code>. Thus, <code>LOGGER.setMinLevel (LOGGER.INFO)</code> enables logging for <code>INFO</code>, <code>WARN</code>, and <code>ERROR</code> messages.
	 *
	 * @param level the min level
	 */
	public static void setMinLevel (int level)
	{
		logLevel = 0;
		int lvl = 1;
		for (int i = 0; i < 4; i++)
		{
			if (lvl >= level)
				logLevel = logLevel|lvl;
			lvl *= 2;
		}
	}
	
	/**
	 * Sets the exclusive level. Thus, <code>LOGGER.setLevel (LOGGER.INFO)</code> enables logging only for <code>INFO</code> messages.
	 *
	 * @param level the level
	 */
	public static void setLevel (int level)
	{
		logLevel = level;
	}
	
	/**
	 * Adds a log level. Thus, if previously only <code>INFO</code> was enabled, <code>LOGGER.addLevel (LOGGER.ERROR)</code> enables logging only for <code>INFO</code> and <code>ERROR</code> messages.
	 *
	 * @param level the level
	 */
	public static void addLevel (int level)
	{
		logLevel = logLevel|level;
	}
	
	/**
	 * Removes a level. Thus, if previously <code>INFO</code> and <code>ERROR</code> was enabled, <code>LOGGER.rmLevel (LOGGER.INFO)</code> enables logging only for <code>ERROR</code> messages.
	 *
	 * @param level the level
	 */
	public static void rmLevel (int level)
	{
		logLevel = logLevel&~level;
	}
	
	/**
	 * Checks for a log level.
	 *
	 * @param level the level
	 * @return true, if level is enabled
	 */
	public static boolean hasLevel (int level)
	{
		return (logLevel&level) == level;
	}
	
	/**
	 * Checks if info is enabled.
	 *
	 * @return true, if info logging is enabled
	 */
	public static boolean isInfoEnabled ()
	{
		return hasLevel (INFO);
	}
	
	/**
	 * Checks if debug is enabled.
	 *
	 * @return true, if debug logging is enabled
	 */
	public static boolean isDebugEnabled ()
	{
		return hasLevel (DEBUG);
	}
	
	/**
	 * Checks if error is enabled.
	 *
	 * @return true, if error logging is enabled
	 */
	public static boolean isErrorEnabled ()
	{
		return hasLevel (ERROR);
	}
	
	/**
	 * Checks if warn is enabled.
	 *
	 * @return true, if warn logging is enabled
	 */
	public static boolean isWarnEnabled ()
	{
		return hasLevel (WARN);
	}
	
	/**
	 * Publish a message.
	 *
	 * @param line the line to log
	 */
	private static void publish (int level, String line)
	{
		if (logToStdOut)
			System.out.println (line);
		if (logToStdErr)
			System.err.println(line);
		if (logToFile && logFile != null)
		{
			try {
				logFile.write(line);
				logFile.newLine();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		for (LogCallback lcb : callBacks)
			lcb.logged (level, line);
	}
	
	
	/**
	 * Log a message.
	 *
	 * @param level the level
	 * @param msg the msg
	 */
	private static void log (int level, String... msg)
	{
		StringBuilder sb = preMsg (level, Thread.currentThread().getStackTrace()[3]);
		for (String m : msg)
			sb.append (m);
		publish (level, sb.toString ());
	}
	
	/**
	 * Log a message.
	 *
	 * @param level the level
	 * @param e the thrown exception
	 * @param msg the msg
	 */
	private static void log (int level, Exception e, String... msg)
	{
		StringBuilder sb = preMsg (level, Thread.currentThread().getStackTrace()[3]);
		for (String m : msg)
			sb.append (m);
		
		sb.append (" (throwing ").append (e.getClass().getName())
			.append (": ").append (e.getMessage()).append (")");
		
		if (logStackTrace)
		{
			sb.append (NEWLINE).append ("\t").append (e.getClass()
				.getName()).append (": ").append (e.getMessage());
			StackTraceElement [] ste = e.getStackTrace();
			for (StackTraceElement el : ste)
				sb.append (NEWLINE).append ("\t\tat ")
				.append (el.getClassName()).append (".").append (el.getMethodName())
				.append ("(").append (el.getFileName()).append (":")
				.append (el.getLineNumber()).append (")");
		}
		
		publish (level, sb.toString ());
	}
	
	/**
	 * Log an error.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 * @deprecated use {@link error(Exception e, String... msg)} instead
	 */
	@Deprecated
	public static void error (String msg, Exception e)
	{
		if ((logLevel & ERROR) > 0)
			log (ERROR, e, msg);
	}
	
	/**
	 * Log an info message.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 * @deprecated use {@link info(Exception e, String... msg)} instead
	 */
	@Deprecated
	public static void info  (String msg, Exception e)
	{
		if ((logLevel & INFO) > 0)
			log (INFO, e, msg);
	}
	
	/**
	 * Log a debug message.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 * @deprecated use {@link debug(Exception e, String... msg)} instead
	 */
	@Deprecated
	public static void debug  (String msg, Exception e)
	{
		if ((logLevel & DEBUG) > 0)
			log (DEBUG, e, msg);
	}
	
	/**
	 * Log a warning message.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 * @deprecated use {@link warn(Exception e, String... msg)} instead
	 */
	@Deprecated
	public static void warn  (String msg, Exception e)
	{
		if ((logLevel & WARN) > 0)
			log (WARN, e, msg);
	}
	
	/**
	 * Log an error.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 */
	public static void error (Exception e, String... msg)
	{
		if ((logLevel & ERROR) > 0)
			log (ERROR, e, msg);
	}
	
	/**
	 * Log an info message.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 */
	public static void info  (Exception e, String... msg)
	{
		if ((logLevel & INFO) > 0)
			log (INFO, e, msg);
	}
	
	/**
	 * Log a debug message.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 */
	public static void debug  (Exception e, String... msg)
	{
		if ((logLevel & DEBUG) > 0)
			log (DEBUG, e, msg);
	}
	
	/**
	 * Log a warning message.
	 *
	 * @param msg the msg
	 * @param e the thrown exception
	 */
	public static void warn  (Exception e, String... msg)
	{
		if ((logLevel & WARN) > 0)
			log (WARN, e, msg);
	}
	
	/**
	 * Log an error.
	 *
	 * @param msg the msg
	 */
	public static void error (String... msg)
	{
		if ((logLevel & ERROR) > 0)
		{
			log (ERROR, msg);
		}
	}
	
	/**
	 * Log an info message.
	 *
	 * @param msg the msg
	 */
	public static void info (String... msg)
	{
		if ((logLevel & INFO) > 0)
			log (INFO, msg);
	}
	
	/**
	 * Log a debug message.
	 *
	 * @param msg the msg
	 */
	public static void debug (String... msg)
	{
		if ((logLevel & DEBUG) > 0)
			log (DEBUG, msg);
	}
	
	/**
	 * Log a warning message.
	 *
	 * @param msg the msg
	 */
	public static void warn (String... msg)
	{
		if ((logLevel & WARN) > 0)
			log (WARN, msg);
	}
	
	public static void addCallback (LogCallback callBack)
	{
		callBacks.add (callBack);
	}
	
	public static void rmCallBack (LogCallback callBack)
	{
		callBacks.remove (callBack);
	}
	
	/**
	 * Generate the preamble of a message.
	 *
	 * @param level the level
	 * @param ste the stack trace element calling the LOGGER
	 * @return the preamble
	 */
	private static StringBuilder preMsg (int level, StackTraceElement ste)
	{
		return new StringBuilder (dateformat.format (new Date ())).append (" ")
			.append (levelString (level)).append (" ").append (ste.getClassName ())
			.append ("@").append (ste.getLineNumber()).append (": ");
	}
	
	/**
	 * Generate the string representation of the current log level.
	 *
	 * @param level the level
	 * @return the string representation
	 */
	private static final String levelString (int level)
	{
		switch (level)
		{
			case ERROR:
				return "ERROR";
			case WARN:
				return "WARNS";
			case DEBUG:
				return "DEBUG";
			case INFO:
				return "INFOS";
			default:
				return "UNKWN";
		}
	}
}
