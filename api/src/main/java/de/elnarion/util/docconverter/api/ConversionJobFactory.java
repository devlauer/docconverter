package de.elnarion.util.docconverter.api;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.api.exception.EmptyMimeTypeException;
import de.elnarion.util.docconverter.api.exception.InputUnavailableException;
import de.elnarion.util.docconverter.api.exception.MimeTypeNotSupportedException;
import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.DocConverterManager;
import de.elnarion.util.docconverter.spi.DocConverterManagerInterface;
import de.elnarion.util.docconverter.spi.DocConverterProvider;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * A factory for creating ConversionJob objects.
 */
public class ConversionJobFactory {

	private static ConversionJobFactory conversionJobFactory;

	protected ConversionJobFactory() {

	}

	/**
	 * Gets the single instance of ConversionJobFactory.
	 *
	 * @return single instance of ConversionJobFactory
	 */
	public static synchronized ConversionJobFactory getInstance() {
		if (conversionJobFactory == null)
			conversionJobFactory = new ConversionJobFactory();
		return conversionJobFactory;
	}

	/**
	 * The Class ConversionJobImpl.
	 */
	private class ConversionJobImpl implements ConversionJob, ConversionJobWithSourceDocTypeUnspecified,
			ConversionJobWithTargetDocTypeUnspecified, ConversionJobWithInputUnspecified {
		private String sourceMimeType;
		private String targetMimeType;
		private DocConverterManagerInterface docConverterManager;
		private List<InputStream> sourceInputStreams;
		private List<File> sourceFiles;
		private Map<DocConverterProvider, Set<String>> docConverterProviderTargetMapping;
		private DocConverter docConverter;
		private Map<String, Object> configurationParameters;

		/**
		 * Instantiates a new conversion job impl.
		 *
		 * @param paramDocConverterManager       the param doc converter manager
		 * @param paramDocConverterConfiguration the param doc converter configuration
		 */
		private ConversionJobImpl(DocConverterManagerInterface paramDocConverterManager,
				Map<String, Object> paramDocConverterConfiguration) {
			docConverterManager = paramDocConverterManager;
			configurationParameters = paramDocConverterConfiguration;
		}

		@Override
		public ConversionJob toMimeType(String paramTargetMimetype) throws ConversionException {
			if (paramTargetMimetype == null)
				throw new EmptyMimeTypeException("A null value as target MIME-Type is not allowed");
			boolean fileInput = true;
			if (sourceFiles == null)
				fileInput = false;
			Set<DocConverterProvider> docConverterProviders = docConverterProviderTargetMapping.keySet();
			for (DocConverterProvider docConverterProvider : docConverterProviders) {
				Set<String> targetMimeTypes = docConverterProviderTargetMapping.get(docConverterProvider);
				if (targetMimeTypes.contains(paramTargetMimetype)
						&& ((!fileInput && docConverterProvider.isInputTypeSupported(InputType.INPUTSTREAM))
								|| (fileInput && docConverterProvider.isInputTypeSupported(InputType.FILE)))) {
					docConverter = docConverterProvider.createDocConverter(configurationParameters);
					break;
				}
			}
			if (docConverter == null)
				throw new MimeTypeNotSupportedException("The specified target MIME-Type " + paramTargetMimetype
						+ "is not supported for source MIME-Type" + sourceMimeType);
			targetMimeType = paramTargetMimetype;
			return this;
		}

		@Override
		public ConversionJobWithTargetDocTypeUnspecified fromMimeType(String paramMimetype) throws ConversionException {
			if (paramMimetype == null)
				throw new EmptyMimeTypeException("No source MIME-Type specified!");
			docConverterProviderTargetMapping = docConverterManager
					.getDocConverterProviderTargetMappingForSourceMimeType(paramMimetype);
			if (docConverterProviderTargetMapping == null || docConverterProviderTargetMapping.isEmpty())
				throw new MimeTypeNotSupportedException(
						"No document converter found for the specified MIME-Type " + paramMimetype);
			sourceMimeType = paramMimetype;
			return this;
		}

		@Override
		public ConversionJobWithSourceDocTypeUnspecified fromStreams(List<InputStream> paramInputstreams)
				throws ConversionException {
			if (paramInputstreams == null)
				throw new InputUnavailableException("Null is not allowed as input parameter.");
			sourceInputStreams = paramInputstreams;
			return this;
		}

		@Override
		public ConversionJobWithSourceDocTypeUnspecified fromFiles(List<File> paramFiles) throws ConversionException {
			if (paramFiles == null)
				throw new InputUnavailableException("Null is not allowed as input parameter.");
			sourceFiles = paramFiles;
			for (File file : paramFiles)
				if (!file.exists())
					throw new InputUnavailableException("The specified file could not be found!");
			return this;
		}

		@Override
		public Future<List<InputStream>> convert() throws ConversionException {
			if (sourceInputStreams != null) {
				return docConverter.convertStreams(sourceInputStreams, sourceMimeType, targetMimeType);

			} else {
				return docConverter.convertFiles(sourceFiles, sourceMimeType, targetMimeType);
			}
		}

	}

	/**
	 * Creates a new empty ConversionJob object.
	 *
	 * @return the conversion job with source doc type unspecified
	 */
	public ConversionJobWithInputUnspecified createEmptyConversionJob() {
		return new ConversionJobImpl(DocConverterManager.getInstance(), new HashMap<>());
	}

	/**
	 * Creates a new ConversionJob object.
	 *
	 * @param paramConfigParameters the param config parameters
	 * @return the conversion job with input unspecified
	 */
	public ConversionJobWithInputUnspecified createEmptyConversionJob(Map<String, Object> paramConfigParameters) {
		return new ConversionJobImpl(DocConverterManager.getInstance(), paramConfigParameters);
	}

	/**
	 * Creates a new empty ConversionJob object.
	 *
	 * @param paramClassLoader the param class loader
	 * @return the conversion job with source doc type unspecified
	 */
	public ConversionJobWithInputUnspecified createEmptyConversionJob(ClassLoader paramClassLoader) {
		return new ConversionJobImpl(DocConverterManager.getInstance(paramClassLoader), new HashMap<>());
	}

	/**
	 * Creates a new ConversionJob object.
	 *
	 * @param paramConfigParameters the param config parameters
	 * @param paramClassLoader      the param class loader
	 * @return the conversion job with input unspecified
	 */
	public ConversionJobWithInputUnspecified createEmptyConversionJob(Map<String, Object> paramConfigParameters,ClassLoader paramClassLoader) {
		return new ConversionJobImpl(DocConverterManager.getInstance(paramClassLoader), paramConfigParameters);
	}
	
}
