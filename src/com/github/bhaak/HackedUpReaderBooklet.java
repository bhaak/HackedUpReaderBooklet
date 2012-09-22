package com.github.bhaak;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.booklet.ChromeException;
import com.amazon.kindle.booklet.ChromeHeaderRequest;
import com.amazon.ebook.util.log.Log;

/**
 * A Booklet for starting HackedUpReader.
 *  
 * @author Patric Mueller &lt;bhaak@gmx.net&gt;
 */
public class HackedUpReaderBooklet extends AbstractBooklet {

	private static final Log logger = Log.getInstance("HackedUpReaderBooklet");
	private static final String hackedupreader = "/mnt/us/hackedupreader/bin/cr3";
	
	private Process hackedupreaderProcess; 
	
	public HackedUpReaderBooklet() {
		logger.info("HackedUpReaderBooklet");
	}
	public void start(URI contentURI) {
		logger.info("start("+contentURI+")");
		try {
			hackedupreaderProcess = Runtime.getRuntime().exec(hackedupreader);
		} catch (IOException e) {
			logger.error(e.toString(), e);
		}
		super.start(contentURI);
	}


	public void stop() {
		logger.info("stop()");
		
		if (hackedupreaderProcess != null) {
			try {
				killQuitProcess(hackedupreaderProcess);
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		
		super.stop();
	}
	
	/** 
	 * Send a QUIT signal to a process.
	 * 
	 * See http://stackoverflow.com/questions/2950338/how-can-i-kill-a-linux-process-in-java-with-sigkill-process-destroy-does-sigte#answer-2951193
	 */
	private void killQuitProcess(Process process)
			throws InterruptedException, IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
	    logger.info(process.getClass().getName());
	    if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
	        Class cl = process.getClass();
	        Field field = cl.getDeclaredField("pid");
	        field.setAccessible(true);
	        Object pidObject = field.get(process);
	        
	        Runtime.getRuntime().exec("kill -QUIT " + pidObject).waitFor();
	    } else {
	        throw new IllegalArgumentException("Needs to be a UNIXProcess");
	    }
	}
}
