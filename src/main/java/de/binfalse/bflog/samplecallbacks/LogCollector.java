/**
 * Copyright (c) 2007-2015 Martin Scharm -- <software@binfalse.de>
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
package de.binfalse.bflog.samplecallbacks;

import java.util.Vector;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bflog.LogCallback;


/**
 * LogCollector collects all messages. Just initialize it at pass the object
 * to the LOGGER:
 * 
 * <pre>
 * LogCollector lc = new LogCollector ();
 * LOGGER.addCallback (lc);
 * </pre>
 * 
 * When all logging has finished you can get the accumulated messages using:
 * 
 * <pre>
 * Vector<String> debugMessages = lc.getDebugs ();
 * Vector<String> infoMessages = lc.getInfos ();
 * Vector<String> warnings = lc.getWarnings ();
 * Vector<String> errors = lc.getErrors ();
 * </pre>
 * 
 * @author martin scharm
 *
 */
public class LogCollector
implements LogCallback
{
	
	/** The vectors. */
	private Vector<String>	errors, warns, infos, debugs;
	
	
	/**
	 * Instantiates a new log collector.
	 */
	public LogCollector ()
	{
		errors = new Vector<String> ();
		warns = new Vector<String> ();
		infos = new Vector<String> ();
		debugs = new Vector<String> ();
	}
	
	
	/**
	 * Gets the errors.
	 * 
	 * @return the errors
	 */
	public Vector<String> getErrors ()
	{
		return errors;
	}
	
	
	/**
	 * Gets the warnings.
	 * 
	 * @return the warnings
	 */
	public Vector<String> getWarnings ()
	{
		return warns;
	}
	
	
	/**
	 * Gets the infos.
	 * 
	 * @return the infos
	 */
	public Vector<String> getInfos ()
	{
		return infos;
	}
	
	
	/**
	 * Gets the debugs.
	 * 
	 * @return the debugs
	 */
	public Vector<String> getDebugs ()
	{
		return debugs;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.binfalse.bflog.LogCallback#logged(int, java.lang.String)
	 */
	public void logged (int lvl, String msg)
	{
		switch (lvl)
		{
			case LOGGER.ERROR:
				errors.add (msg);
				break;
			case LOGGER.WARN:
				warns.add (msg);
				break;
			case LOGGER.INFO:
				infos.add (msg);
				break;
			case LOGGER.DEBUG:
				debugs.add (msg);
				break;
			default:
				LOGGER.error ("don't know how to handle an error of type: " + lvl);
		}
	}

}
