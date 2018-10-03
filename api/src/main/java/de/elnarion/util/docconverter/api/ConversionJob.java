package de.elnarion.util.docconverter.api;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Future;

import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * Basic interface for all conversionjob objects with an input mimetype and an
 * output mimetype
 */
public interface ConversionJob {

	/**
	 * Convert.
	 *
	 * @return the input stream
	 * @throws ConversionException the conversion exception
	 */
	public Future<List<InputStream>> convert() throws ConversionException;

}
