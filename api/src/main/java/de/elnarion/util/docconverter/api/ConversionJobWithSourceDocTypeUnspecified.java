package de.elnarion.util.docconverter.api;

import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * Basic interface for all conversionjob objects with a missing input mimetype
 */
public interface ConversionJobWithSourceDocTypeUnspecified {

	/**
	 * Defines the source document type for the given input document.
	 *
	 * @param paramMimetype the mimetype of the input Document
	 * @return The current conversion specification.
	 * @throws ConversionException the conversio
	 */
	ConversionJobWithTargetDocTypeUnspecified fromMimeType(String paramMimetype)throws ConversionException;
}
