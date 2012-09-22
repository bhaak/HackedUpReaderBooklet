package com.github.bhaak;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.ebook.util.log.Log;

/**
 * A Booklet for starting HackedUpReader.
 *
 * @author Patric Mueller &lt;bhaak@gmx.net&gt;
 */
public class HackedUpReaderBooklet extends AbstractBooklet {

	private final Log logger = Log.getInstance("HackedUpReaderBooklet");
	private final String hackedupreader = "/mnt/us/hackedupreader/bin/cr3";
	
	private Process hackedupreaderProcess; 
	
	public HackedUpReaderBooklet() {
		logger.info("HackedUpReaderBooklet");
	}
	public void start(URI contentURI) {
		logger.info("start("+contentURI+")");
		
		// Start HackedUpReader
		try {
			hackedupreaderProcess = Runtime.getRuntime().exec(hackedupreader);
		} catch (IOException e) {
			logger.error(e.toString(), e);
		}

		Thread thread = new HackedUpReaderWaitThread();
		thread.start();

		super.start(contentURI);
	}


	public void stop() {
		logger.info("stop()");
		
		// Stop HackedUpReader
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
		throws InterruptedException, IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
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
	
	/** This thread waits for HackedUpReader to finish and then sends
	 * a BACKWARD lipc event. 
	 */
	class HackedUpReaderWaitThread extends Thread {
		public void run() {
			try {
				// wait for HackedUpReader to finish
				hackedupreaderProcess.waitFor();

				// sent BACKWARD lipc event if HackedUpReader finished normally
				Runtime.getRuntime().exec("lipc-set-prop com.lab126.appmgrd backward 0");
			} catch (IOException e) {
				logger.error(e.toString(), e);
			} catch (InterruptedException e) {
				logger.error(e.toString(), e);
			}
		}
	}
}
