package com.tips48.rushMe.util;

import com.tips48.rushMe.RushMe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMLogging extends Thread {

    private static final Logger logger = Logger.getLogger("Minecraft");
    
    private static BufferedWriter writer;

    private static BufferedWriter debugWriter;
    private static boolean debug;
    
    private static List<String> toRun = new ArrayList<String>();
    private static List<String> debugToRun = new ArrayList<String>();
    
    private static final DateFormat df = new SimpleDateFormat("HH:mm:ss");
    
    @Override
    public void run() {
	for (String string : toRun) {
		if (writer != null) {
		    try {
			writer.write(df
				.format(System.currentTimeMillis()) + string);
			writer.newLine();
			writer.flush();
		    } catch (Exception e) {
			RMLogging.log(e, "Could not write " + string
				+ " to the log file!");
		    }
		    toRun.remove(string);
		}
	}
	for (String string : debugToRun) {
	    if (debugWriter != null) {
		try {
		    debugWriter.write(df.format(System
			    .currentTimeMillis())
			    + string);
		    debugWriter.newLine();
		    debugWriter.flush();
		} catch (Exception e) {
		    log(e, "Could not write " + string + " to the debug file!");
		}
		debugToRun.remove(string);
	    }
	}
	if (debugToRun.isEmpty() && toRun.isEmpty()) {
	    try {
	    Thread.sleep(60000);
	    } catch (Exception e) {
		RMLogging.log(e, "Writer thread was interupted!");
	    }
	}
    }

    public static synchronized void shutdown() {
	try {
	    if (writer != null) {
		writer.flush();
		writer.close();
		writer = null;
	    }
	    if (debugWriter != null) {
		debugWriter.flush();
		debugWriter.close();
		debugWriter = null;
	    }
	} catch (Exception e) {
	    log(e, "Error shutting down the logger!");
	}
    }

    public static synchronized void setFile(String name) {
	if (writer != null) {
	    try {
		writer.close();
	    } catch (Exception e) {
		RMLogging.log(e, "Failed to close the writer!");
	    }
	}
	if (name == null) {
	    writer = null;
	    return;
	}
	FileWriter fw;
	try {
	    fw = new FileWriter(name, true);
	} catch (Exception e) {
	    RMLogging.log(e, "Failed to find the file " + name + "!");
	    return;
	}
	writer = new BufferedWriter(fw);
    }

    public static synchronized void log(Level level, String message) {
	logger.log(level, RushMe.getPrefix() + " " + message);
	toRun.add(message);
    }

    public static synchronized void log(Exception e, String reason) {
	RMLogging.log(Level.SEVERE, reason);
	RMLogging.log(Level.SEVERE, "ERROR MESSAGE:");
	RMLogging.log(Level.SEVERE, e.getMessage());
    }

    public static synchronized boolean isDebug() {
	return debug;
    }

    public static synchronized void setDebug(boolean d) {
	debug = d;
    }

    public static synchronized void debugLog(Level level, String message) {
	if (isDebug()) {
	    logger.log(level, RushMe.getPrefix() + "[DEBUG] " + message);
	    debugToRun.add(message);
	}
    }

    public static synchronized void setDebugFile(String name) {
	if (debugWriter != null) {
	    try {
		debugWriter.close();
	    } catch (Exception e) {
		log(e, "Failed to close the writer!");
	    }
	}
	if (name == null) {
	    debugWriter = null;
	    return;
	}
	FileWriter fw;
	try {
	    fw = new FileWriter(name, true);
	} catch (Exception e) {
	    log(e, "Failed to find the file " + name + "!");
	    return;
	}
	debugWriter = new BufferedWriter(fw);
    }

}
