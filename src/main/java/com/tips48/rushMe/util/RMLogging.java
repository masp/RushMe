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

    private final Logger logger = Logger.getLogger("Minecraft");

    private BufferedWriter writer;

    private BufferedWriter debugWriter;
    private boolean debug;

    private List<String> toRun = new ArrayList<String>();
    private List<String> debugToRun = new ArrayList<String>();

    private final DateFormat df = new SimpleDateFormat("HH:mm:ss");

    public RMLogging() {
	super("RushMe Logging Thread");
    }

    @Override
    public void run() {
	while (!this.isInterrupted()) {
	    for (String string : toRun) {
		if (writer != null) {
		    try {
			writer.write(df.format(System.currentTimeMillis())
				+ string);
			writer.newLine();
			writer.flush();
		    } catch (Exception e) {
			this.log(e, "Could not write " + string
				+ " to the log file!");
		    }
		    toRun.remove(string);
		}
	    }
	    for (String string : debugToRun) {
		if (debugWriter != null) {
		    try {
			debugWriter.write(df.format(System.currentTimeMillis())
				+ string);
			debugWriter.newLine();
			debugWriter.flush();
		    } catch (Exception e) {
			log(e, "Could not write " + string
				+ " to the debug file!");
		    }
		    debugToRun.remove(string);
		}
	    }
	    try {
		Thread.sleep(1000);
	    } catch (Exception e) {
		this.log(e, "Writer thread was interupted!");
	    }
	}
    }

    public synchronized void shutdown() {
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

    public synchronized void setFile(String name) {
	if (writer != null) {
	    try {
		writer.close();
	    } catch (Exception e) {
		this.log(e, "Failed to close the writer!");
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
	    this.log(e, "Failed to find the file " + name + "!");
	    return;
	}
	writer = new BufferedWriter(fw);
    }

    public synchronized void log(Level level, String message) {
	logger.log(level, RushMe.getPrefix() + " " + message);
	toRun.add(message);
    }

    public synchronized void log(Exception e, String reason) {
	this.log(Level.SEVERE, reason);
	this.log(Level.SEVERE, "ERROR MESSAGE:");
	this.log(Level.SEVERE, e.getMessage());
    }

    public synchronized boolean isDebug() {
	return debug;
    }

    public synchronized void setDebug(boolean d) {
	debug = d;
    }

    public synchronized void debugLog(Level level, String message) {
	if (isDebug()) {
	    logger.log(level, RushMe.getPrefix() + "[DEBUG] " + message);
	    debugToRun.add(message);
	}
    }

    public synchronized void setDebugFile(String name) {
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
