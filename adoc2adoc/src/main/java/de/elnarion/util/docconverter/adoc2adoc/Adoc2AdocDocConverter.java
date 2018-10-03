package de.elnarion.util.docconverter.adoc2adoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.extension.ExtensionGroup;

import de.elnarion.util.docconverter.api.ConfigurationParameterConstants;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class Adoc2AdocDocConverter.
 */
public class Adoc2AdocDocConverter implements DocConverter {

	private static final String INCLUDE = "include::";
	private static Map<String, Set<String>> supportedMimetypes = null;
	/**
	 * The Constant REGEXP_REMAIN_INCLUDE_STATEMENT can be used to configure a
	 * regular expression which is used to decide whether an include statement
	 * should remain.
	 */
	public static final String REGEXP_REMAIN_INCLUDE_STATEMENT = "adoc2adoc.remain_include_statement_regexp";

	Map<String, Object> configurationParameters;

	/**
	 * Instantiates a new adoc 2 adoc doc converter.
	 *
	 * @param paramConfigurationParameters the param configuration parameters
	 */
	public Adoc2AdocDocConverter(Map<String, Object> paramConfigurationParameters) {
		configurationParameters = paramConfigurationParameters;
	}

	@Override
	public Future<List<InputStream>> convertStreams(List<InputStream> source, String paramSourceMimeType,
			String paramTargetMimeType) throws ConversionException {
		throw new ConversionException("InputStreams are not supported by this DocConverter.");
	}

	/**
	 * Returns a mapping of all conversions that are supported by the backing
	 * conversion engine.
	 *
	 * @return A map of all possible conversions with the key describing the input
	 *         mimetypes and the set describing the mimetypes that these input
	 *         mimetypes can be converted into.
	 */
	public static synchronized Map<String, Set<String>> getSupportedConversion() {
		if (supportedMimetypes == null) {
			supportedMimetypes = new HashMap<>();
			Set<String> targetMimetypes = new HashSet<>();
			targetMimetypes.add(MimeTypeConstants.TEXT_ASCIIDOC);
			supportedMimetypes.put(MimeTypeConstants.TEXT_ASCIIDOC, targetMimetypes);
		}
		return supportedMimetypes;
	}

	/**
	 * Checks if is input type is supported.
	 *
	 * @param paramInputType the param input type
	 * @return true, if is input type supported
	 */
	public static boolean isInputTypeSupported(InputType paramInputType) {
		switch (paramInputType) {
		case FILE:
			return true;
		case INPUTSTREAM:
		default:
			return false;
		}
	}

	@Override
	public Future<List<InputStream>> convertFiles(final List<File> source, final String paramSourceMimeType,
			String paramTargetMimeType) throws ConversionException {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		return exec.submit(new Callable<List<InputStream>>() {
			@Override
			public List<InputStream> call() throws Exception {
				return convertToInputStreams(source);
			}
		});
	}

	private List<InputStream> convertToInputStreams(List<File> source)
			throws IOException {
		List<InputStream> inputStreams = new ArrayList<>();
		Asciidoctor asciidoctor = Factory.create();
		ExtensionGroup group = asciidoctor.createGroup();
		Preprocessor preprocessor = new Preprocessor();
		group.preprocessor(preprocessor);
		Options options = new Options();
		options.setToFile(false);
		options.setSafe(SafeMode.UNSAFE);
		group.register();
		for (File file : source) {
			inputStreams.add(processSingleFile(file, asciidoctor, group, preprocessor, options));
		}
		return inputStreams;
	}

	private InputStream processSingleFile(File source, Asciidoctor asciidoctor, ExtensionGroup group,
			Preprocessor preprocessor, Options options) throws IOException {
		options.setBaseDir(source.getParentFile().getAbsolutePath());
		try(FileInputStream fis= new FileInputStream(source))
		{
		List<String> content = IOUtils.readLines(fis, getConfiguredCharset());
		StringBuilder contentBuilder = new StringBuilder();
		for (String contentline : content) {
			if (contentline.contains(INCLUDE) && shouldIncludeStatementRemain(contentline))
				contentline = contentline.replace(INCLUDE, "include##");
			contentBuilder.append(contentline);
			contentBuilder.append("\r\n");
		}
		asciidoctor.convert(contentBuilder.toString(), options);
		group.unregister();
		List<String> lines = preprocessor.getContentLines();
		StringBuilder newContentBuilder = new StringBuilder();
		for (String line : lines) {
			line = line.replace("include##", INCLUDE);
			newContentBuilder.append(line);
			newContentBuilder.append(System.lineSeparator());
		}
		
		return IOUtils.toInputStream(newContentBuilder.toString(), getConfiguredCharset());
		}
	}

	private boolean shouldIncludeStatementRemain(String paramContentLine) {
		if (configurationParameters.containsKey(REGEXP_REMAIN_INCLUDE_STATEMENT))
			return paramContentLine.matches((String) configurationParameters.get(REGEXP_REMAIN_INCLUDE_STATEMENT));
		return false;
	}

	private String getConfiguredCharset() {
		String charsetConfigured = (String) configurationParameters
				.get(ConfigurationParameterConstants.INPUT_CHARSET_KEY);
		// set default value for charsets
		if (charsetConfigured == null)
			charsetConfigured = "utf-8";
		return charsetConfigured;
	}
}
