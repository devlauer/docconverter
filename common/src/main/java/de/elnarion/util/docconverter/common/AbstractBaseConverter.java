package de.elnarion.util.docconverter.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.elnarion.util.docconverter.api.ConfigurationParameterConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.spi.DocConverter;

/**
 * AbstractBaseConverter acts as base class for all converters.
 */
public abstract class AbstractBaseConverter implements DocConverter {

	private Map<String, Object> configurationParameters;

	protected AbstractBaseConverter(Map<String, Object> paramConfigurationParameters) {
		configurationParameters = paramConfigurationParameters;
	}
	
	/**
	 * Convert streams.
	 *
	 * @param source the source
	 * @param paramSourceMimeType the param source mime type
	 * @param paramTargetMimeType the param target mime type
	 * @return the future
	 * @throws ConversionException the conversion exception
	 */
	@Override
	public Future<List<InputStream>> convertStreams(final List<InputStream> source, final String paramSourceMimeType,
			String paramTargetMimeType) throws ConversionException {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		return exec.submit(new Callable<List<InputStream>>() {
			@Override
			public List<InputStream> call() throws Exception {
				List<InputStream> resultList = new ArrayList<>();
				for (InputStream is : source) {
					resultList.addAll(convertToInputStream(is, paramSourceMimeType));
				}
				return resultList;
			}
		});
	}

	/**
	 * Convert files.
	 *
	 * @param source the source
	 * @param paramSourceMimeType the param source mime type
	 * @param paramTargetMimeType the param target mime type
	 * @return the future
	 * @throws ConversionException the conversion exception
	 */
	@Override
	public Future<List<InputStream>> convertFiles(List<File> source, String paramSourceMimeType,
			String paramTargetMimeType) throws ConversionException {
		try {
			List<InputStream> isList = new ArrayList<>();
			for (File sourceFile : source) {
				isList.add(new FileInputStream(sourceFile));
			}
			return convertStreams(isList, paramSourceMimeType, paramTargetMimeType);
		} catch (FileNotFoundException e) {
			throw new ConversionException("File could not be read. Please check file!");
		}
	}

	protected String getConfiguredCharset() {
		String charsetConfigured = (String) getConfigurationParameters()
				.get(ConfigurationParameterConstants.INPUT_CHARSET_KEY);
		// set default value for charsets
		if (charsetConfigured == null)
			charsetConfigured = "utf-8";
		return charsetConfigured;
	}
	
	/**
	 * Gets the configuration parameters.
	 *
	 * @return the configuration parameters
	 */
	protected Map<String, Object>  getConfigurationParameters(){
		return configurationParameters;
	}

	/**
	 * Convert to input stream.
	 *
	 * @param is the is
	 * @param paramSourceMimeType the param source mime type
	 * @return the input stream
	 * @throws ConversionException the conversion exception
	 */
	protected abstract List<InputStream> convertToInputStream(InputStream is, String paramSourceMimeType)
			throws ConversionException;

}
