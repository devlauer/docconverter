package de.elnarion.util.docconverter.spi;

import java.util.Map;
import java.util.Set;

/**
 * Defines the interface for different DocConverterProviders which help to
 * create the concrete DocConverter-Object for conversion.
 */
public interface DocConverterProvider {

	/**
	 * Returns a mapping of all conversions that are supported by the backing
	 * conversion engine.
	 *
	 * @return A map of all possible conversions with the key describing the input
	 *         mimetype and the set describing the mimetypes that these input
	 *         mimetypes can be converted into.
	 */
	Map<String, Set<String>> getSupportedMimeTypeConversions();

	/**
	 * Creates the doc converter.
	 *
	 * @param paramConfigurationParameters the param configuration parameters
	 * @return the doc converter
	 */
	public DocConverter createDocConverter(Map<String, Object> paramConfigurationParameters);

	/**
	 * Checks if is input type is supported.
	 *
	 * @param paramInputType the param input type
	 * @return true, if is input type supported
	 */
	boolean isInputTypeSupported(InputType paramInputType);

}
