package de.elnarion.util.docconverter.api;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * The Interface ConversionJobWithInputUnspecified.
 */
public interface ConversionJobWithInputUnspecified {

	/**
	 * From.
	 *
	 * @param paramInputstream the param inputstream
	 * @return the conversion job with source doc type unspecified
	 * @throws ConversionException the conversion exception
	 */
	ConversionJobWithSourceDocTypeUnspecified fromStreams(List<InputStream> paramInputstream) throws ConversionException;

	/**
	 * From.
	 *
	 * @param paramFile the param file
	 * @return the conversion job with source doc type unspecified
	 * @throws ConversionException the conversion exception
	 */
	ConversionJobWithSourceDocTypeUnspecified fromFiles(List<File> paramFile) throws ConversionException;
}
