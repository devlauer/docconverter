package de.elnarion.util.docconverter.spi;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Future;

import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * A converter that allows the conversion of documents from one mimetype to
 * another mimetype.
 * 
 */
public interface DocConverter {

	/**
	 * Converts a source that is represented as a {@link InputStream}. The input
	 * stream will be closed after the conversion is complete of if the conversion
	 * failed.
	 *
	 * @param source              The conversion input as an input stream.
	 * @param paramSourceMimeType the param source mime type
	 * @param paramTargetMimeType the param target mime type
	 * @return The conversion result.
	 * @throws ConversionException the conversion exception
	 */
	Future<List<InputStream>> convertStreams(List<InputStream> source, String paramSourceMimeType, String paramTargetMimeType)
			throws ConversionException;

	/**
	 * Converts a source that is represented as a {@link File}. The input stream
	 * will be closed after the conversion is complete of if the conversion failed.
	 *
	 * @param source              The conversion input as an input stream.
	 * @param paramSourceMimeType the param source mime type
	 * @param paramTargetMimeType the param target mime type
	 * @return The conversion result.
	 * @throws ConversionException the conversion exception
	 */
	Future<List<InputStream>> convertFiles(List<File> source, String paramSourceMimeType, String paramTargetMimeType)
			throws ConversionException;

}
