package de.elnarion.util.docconverter.api;

import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * Basic interface for all conversionjob objects with a input mimetype specified
 * but with a missing output mimetype.
 */
public interface ConversionJobWithTargetDocTypeUnspecified {

	/**
	 * Defines the source document type for the given input document.
	 *
	 * @param paramTargetMimetype the param target mimetype
	 * @return The current conversion specification.
	 * @throws ConversionException the conversion exception
	 */
	ConversionJob toMimeType(String paramTargetMimetype)throws ConversionException;
}
