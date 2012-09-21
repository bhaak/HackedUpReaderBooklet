package com.github.bhaak;

import java.io.IOException;
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
	
	public HackedUpReaderBooklet() {
		logger.info("HackedUpReaderBooklet");
	}
	public void start(URI contentURI) {
		logger.info("start("+contentURI+")");
		try {
			Runtime.getRuntime().exec(hackedupreader);
		} catch (IOException e) {
			logger.error(e.toString(), e);
		}
		super.start(contentURI);
	}

	public void create(BookletContext bookletContext) {
		logger.info("create("+bookletContext+")");
		super.create(bookletContext);
	}

	public void destroy() {
		logger.info("destroy()");
		super.destroy();
	}

	public String getName() {
		logger.info("getName()");
		return super.getName();
	}

	public void stop() {
		logger.info("stop()");
		super.stop();
	}
	public ChromeHeaderRequest getChromeHeaderRequest() {
		logger.info("getChromeHeaderRequest()");
		return super.getChromeHeaderRequest();
	}
	public void setChromeHeaderRequest(ChromeHeaderRequest chromeHeaderRequest) {
		logger.info("setChromeHeaderRequest("+chromeHeaderRequest+")");
		super.setChromeHeaderRequest(chromeHeaderRequest);
	}
	public void syncHeaderRequest() throws ChromeException {
		logger.info("syncHeaderRequest()");
		super.syncHeaderRequest();
	}
}
