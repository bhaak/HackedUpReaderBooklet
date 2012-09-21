package com.github.bhaak;

import java.net.URI;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.booklet.ChromeException;
import com.amazon.kindle.booklet.ChromeHeaderRequest;
import com.amazon.ebook.util.log.Log;

public class HackedUpReaderBooklet extends AbstractBooklet {

	private static final Log logger = Log.getInstance("HackedUpReaderBooklet");
	
	public HackedUpReaderBooklet() {
		logger.info("HackedUpReaderBooklet");
	}
	public void start(URI contentURI) {
		logger.info("start("+contentURI+")");
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
