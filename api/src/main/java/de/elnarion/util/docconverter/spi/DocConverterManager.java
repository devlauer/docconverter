package de.elnarion.util.docconverter.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Manages different {@link DocConverterProvider} objects and their supported
 * MIME-Type Mappings.
 */
public class DocConverterManager implements DocConverterManagerInterface {

	private ServiceLoader<DocConverterProvider> docConverterServiceLoader;
	private Map<String, Map<DocConverterProvider, Set<String>>> docConverterProviderMap = new HashMap<>();

	private DocConverterManager(ClassLoader paramClassloader) {
		if (paramClassloader != null)
			docConverterServiceLoader = ServiceLoader.load(DocConverterProvider.class, paramClassloader);
		else
			docConverterServiceLoader = ServiceLoader.load(DocConverterProvider.class);

	}

	/**
	 * Gets the single instance of DocConverterManager.
	 *
	 * @return single instance of DocConverterManager
	 */
	public static DocConverterManager getInstance() {
		return getInstance(null);
	}

	/**
	 * Gets an instance of DocConverterManager.
	 *
	 * @param paramClassLoader the param class loader
	 * @return instance of DocConverterManager
	 */
	public static DocConverterManager getInstance(ClassLoader paramClassLoader) {
		DocConverterManager docConverterManager = new DocConverterManager(paramClassLoader);
		docConverterManager.reload();
		return docConverterManager;

	}

	/**
	 * Reload all {@link DocConverterProvider}
	 */
	public void reload() {
		docConverterServiceLoader.reload();
		Map<String, Map<DocConverterProvider, Set<String>>> currentDocConverterProviderMap = new HashMap<>();
		for (DocConverterProvider provider : docConverterServiceLoader) {
			Map<String, Set<String>> supportedMimetypesByDocProvider = provider.getSupportedMimeTypeConversions();
			if (supportedMimetypesByDocProvider != null) {
				Set<String> sourceMimeTypeSet = supportedMimetypesByDocProvider.keySet();
				for (String sourceMimeType : sourceMimeTypeSet) {
					Map<DocConverterProvider, Set<String>> providerTargetMapping = currentDocConverterProviderMap.get(sourceMimeType);
					if(providerTargetMapping==null)
					{
						providerTargetMapping = new HashMap<>();
						currentDocConverterProviderMap.put(sourceMimeType, providerTargetMapping);
					}
					providerTargetMapping.put(provider, supportedMimetypesByDocProvider.get(sourceMimeType));
				}
			}
		}
		synchronized (docConverterProviderMap) {
			docConverterProviderMap.clear();
			docConverterProviderMap.putAll(currentDocConverterProviderMap);
		}
	}

	/**
	 * Gets the doc converter provider map.
	 *
	 * @return Map - the doc converter provider map
	 */
	public Map<String, Map<DocConverterProvider, Set<String>>> getDocConverterProviderMap() {
		return docConverterProviderMap;
	}

	/**
	 * Gets the doc converter provider target mapping for source mime type.
	 *
	 * @param paramSourceMimeType the param source mime type
	 * @return Map - the doc converter provider target mapping for source mime type
	 */
	public Map<DocConverterProvider, Set<String>> getDocConverterProviderTargetMappingForSourceMimeType(
			String paramSourceMimeType) {
		if (paramSourceMimeType != null)
			return docConverterProviderMap.get(paramSourceMimeType);
		return null;
	}

}
