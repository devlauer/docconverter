package de.elnarion.util.docconverter.spi;

import java.util.Map;
import java.util.Set;

/**
 * The Interface DocConverterManagerInterface.
 */
public interface DocConverterManagerInterface {

	/**
	 * Gets the doc converter provider map.
	 *
	 * @return Map - the doc converter provider map
	 */
	public Map<String, Map<DocConverterProvider, Set<String>>> getDocConverterProviderMap();

	/**
	 * Gets the doc converter provider target mapping for source mime type.
	 *
	 * @param paramSourceMimeType the param source mime type
	 * @return Map - the doc converter provider target mapping for source mime type
	 */
	public Map<DocConverterProvider, Set<String>> getDocConverterProviderTargetMappingForSourceMimeType(
			String paramSourceMimeType);

}
