/**
 * 
 */
package de.binfalse.bflog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Martin Scharm
 *
 */
public class LOGGER
{
	public static final int ERROR = 128;
	public static final int WARN = 32;
	public static final int INFO = 8;
	public static final int DEBUG = 2;
	public static final SimpleDateFormat	dateformat	= new SimpleDateFormat (
		"dd.MM HH:mm:ss");
	
	private static int logLevel = WARN & ERROR;
	private static BufferedWriter logFile;
	private static boolean logToFile = false;
	private static boolean logToStdOut = false;
	private static boolean logToStdErr = true;
	private static boolean logStackTrace = false;
	
	public static boolean setLogFile (String file)
	{
		return setLogFile (new File (file));
	}
	
	public static boolean setLogFile (File file)
	{
		try
		{
			if (!file.exists())
				file.createNewFile();
			
			if (!file.isFile() || !file.canRead() || !file.canWrite())
				return false;
			
			logFile = new BufferedWriter (new FileWriter (file, true));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setLogToFile (boolean log)
	{
		if (logFile == null)
			return false;
		
		logToFile = log;
		return true;
	}
	
	public static void setLogStackTrace (boolean log)
	{
		logStackTrace = log;
	}

	public static void setLogToStdErr (boolean log)
	{
		logToStdErr = log;
	}

	public static void setLogToStdOut (boolean log)
	{
		logToStdOut = log;
	}
	
	public static void addLevel (int level)
	{
		logLevel = logLevel|level;
	}
	public static void rmLevel (int level)
	{
		logLevel = logLevel&~level;
	}
	public static boolean hasLevel (int level)
	{
		return (logLevel&level) == level;
	}
	public static boolean isInfoEnabled ()
	{
		return hasLevel (INFO);
	}
	public static boolean isDebugEnabled ()
	{
		return hasLevel (DEBUG);
	}
	public static boolean isErrorEnabled ()
	{
		return hasLevel (ERROR);
	}
	public static boolean isWarnEnabled ()
	{
		return hasLevel (WARN);
	}
	
	private static void publish (String line)
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
	}
	
	/*public static boolean setLogFile (String file)
	{
		return setLogFile (new File (file));
	}
	
	public static boolean setLogFile (File file)
	{
		if (!file.isFile() || !file.canWrite())
			return false;
		
		try {
			logFile = new BufferedWriter (new FileWriter (file));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}*/
	
	
	private static void log (int level, String msg)
	{
		publish (preMsg (level, Thread.currentThread().getStackTrace()[3]) + msg);
	}
	
	private static void log (int level, String msg, Exception e)
	{
		publish (preMsg (level, Thread.currentThread().getStackTrace()[3]) + msg + " (throwing "+e.getClass().getName()+": " + e.getMessage() + ")");
		
		if (logStackTrace)
		{
			publish ("\t" + e.getClass().getName()+": " + e.getMessage());
			StackTraceElement [] ste = e.getStackTrace();
			for (StackTraceElement el : ste)
				publish ("\t\tat " + el.getClassName() + "." + el.getMethodName() + "("+el.getFileName()+":"+el.getLineNumber()+")");
		}
	}
	
	public static void error (String msg, Exception e)
	{
		if ((logLevel & ERROR) > 0)
			log (ERROR, msg, e);
	}
	
	public static void error (String msg)
	{
		if ((logLevel & ERROR) > 0)
			log (ERROR, msg);
	}
	
	public static void info  (String msg, Exception e)
	{
		if ((logLevel & INFO) > 0)
			log (INFO, msg, e);
	}
	
	public static void info (String msg)
	{
		if ((logLevel & INFO) > 0)
			log (INFO, msg);
	}
	
	public static void debug  (String msg, Exception e)
	{
		if ((logLevel & DEBUG) > 0)
			log (DEBUG, msg, e);
	}
	
	public static void debug (String msg)
	{
		if ((logLevel & DEBUG) > 0)
			log (DEBUG, msg);
	}
	
	public static void warn  (String msg, Exception e)
	{
		if ((logLevel & WARN) > 0)
			log (WARN, msg, e);
	}
	
	public static void warn (String msg)
	{
		if ((logLevel & WARN) > 0)
			log (WARN, msg);
	}
	
	private static String preMsg (int level, StackTraceElement ste)
	{
		return dateformat.format (new Date ()) + " "+ levelString (level) + " " + ste.getClassName () + "@" + ste.getLineNumber() + ": ";
	}
	
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
