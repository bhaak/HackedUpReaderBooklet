package com.github.bhaak;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JTextArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class HackedUpReaderKindlet extends AbstractKindlet {

	private static final Logger logger = Logger.getLogger(HackedUpReaderKindlet.class);
	private final String hackedupreader = "/mnt/us/hackedupreader/bin/cr3";

	private Process hackedupreaderProcess;
	private HackedUpReaderWaitThread thread = null;

	JButton status = new JButton("!");
	JTextArea stacktrace = new JTextArea();

	public void create(KindletContext context) {
		
		Properties p = new Properties();
		try {
			p.load(this.getClass().getResource("log4j.properties").openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		PropertyConfigurator.configure(p);
		
		logger.info("create()");

		// set up button
		Container root = context.getRootContainer();
		root.setLayout(new BorderLayout());
		root.add(status, BorderLayout.CENTER);
		status.setText("loading HackedUpReader ...");

		try {
			logger.info("Starting HackedUpReader");
			// Start HackedUpReader
			hackedupreaderProcess = Runtime.getRuntime().exec(hackedupreader);
			logger.info("Started HackedUpReader "+hackedupreaderProcess);
			thread = new HackedUpReaderWaitThread();
			thread.start();
		} catch (Throwable t) {
			// remove all UI components of root window
			root.removeAll();

			// put generic error message at top
			status.setText("Error while loading HackedUpReader");
			root.add(status, BorderLayout.PAGE_START);

			// transform stacktrace into string
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			writer.flush();
			String stacktraceString = writer.toString();

			// show stacktrace in the middle of the screen
			JTextArea stacktrace = new JTextArea(stacktraceString);
			logger.info(stacktrace.getFont());
			logger.info(stacktrace.getFont().getName());
			logger.info(""+stacktrace.getFont().getSize());
			stacktrace.setFont(new Font(stacktrace.getFont().getName(), Font.PLAIN, 6));
			root.add(stacktrace, BorderLayout.CENTER);
			logger.error(t.getMessage(), t);
		}
	}

	public void start() {
		logger.info("start()");
		super.start();
	}

	public void stop() {
		logger.info("stop()");
		super.stop();
	}

	public void destroy() {
		logger.info("destroy()");
		// Stop HackedUpReader
		if (hackedupreaderProcess != null) {
			try {
				thread.interrupt();
				killQuitProcess(hackedupreaderProcess);
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		super.destroy();
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

			Runtime.getRuntime().exec(new String[]{"/bin/kill", "-QUIT", pidObject.toString()}).waitFor();
		} else {
			throw new IllegalArgumentException("Needs to be a UNIXProcess");
		}
	}

	/** This thread waits for HackedUpReader to finish and then sends
	 * a BACKWARD lipc event. 
	 */
	class HackedUpReaderWaitThread extends Thread {
		public void start() {
			logger.info("HackedUpReaderWaitThread started");
			super.start();
		}
		public void run() {
			try {
				logger.info("HackedUpReaderWaitThread run");

				// wait for HackedUpReader to finish
				hackedupreaderProcess.waitFor();
				logger.info("HackedUpReader stopped");

				// sent BACKWARD lipc event if HackedUpReader finished normally
				Runtime.getRuntime().exec(new String[]{"/usr/bin/lipc-set-prop", "com.lab126.appmgrd", "backward", "0"});
			} catch (IOException e) {
				logger.error(e.toString(), e);
			} catch (InterruptedException e) {
				// Thread got interrupted, don't send lipc event
				logger.warn(e.toString());
			}
		}
	}
}
