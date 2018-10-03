package de.elnarion.util.docconverter.html2pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import de.elnarion.util.docconverter.api.ConfigurationParameterConstants;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class HTML2PDFConverter.
 */
public class HTML2PDFConverter implements DocConverter {

	private static Map<String, Set<String>> supportedMimetypes = null;

	private Map<String, Object> configurationParameters;

	/**
	 * Instantiates a new HTML 2 PDF converter.
	 *
	 * @param paramConfigurationParameters the param configuration parameters
	 */
	public HTML2PDFConverter(Map<String, Object> paramConfigurationParameters) {
		configurationParameters = paramConfigurationParameters;
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
			targetMimetypes.add(MimeTypeConstants.APPLICATION_PDF);
			supportedMimetypes.put(MimeTypeConstants.TEXT_HTML, targetMimetypes);
			supportedMimetypes.put(MimeTypeConstants.APPLICATION_XHTML, targetMimetypes);
		}
		return supportedMimetypes;
	}

	@Override
	public Future<List<InputStream>> convertStreams(final List<InputStream> source, final String paramSourceMimeType,
			String paramTargetMimeType) throws ConversionException {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		return exec.submit(new Callable<List<InputStream>>() {
			@Override
			public List<InputStream> call() throws Exception {
				List<InputStream> resultList = new ArrayList<>();
				for (InputStream is : source) {
					resultList.add(convertToInputStream(is, paramSourceMimeType));
				}
				return resultList;
			}
		});
	}

	/**
	 * Convert to output stream.
	 *
	 * @param source              the source
	 * @param paramSourceMimeType the param source mime type
	 * @return the output stream
	 * @throws ConversionException the conversion exception
	 */
	private InputStream convertToInputStream(InputStream source, String paramSourceMimeType)
			throws ConversionException {

		if (MimeTypeConstants.APPLICATION_XHTML.equals(paramSourceMimeType)) {
			return convertHTMLToPDFInputStream(source);
		} else if (MimeTypeConstants.TEXT_HTML.equals(paramSourceMimeType)) {
			return convertXHTMLToPDFInputStream(source);
		}
		return null;
	}

	private InputStream convertHTMLToPDFInputStream(InputStream paramSource) throws ConversionException {
		Document document;
		try {
			document = Jsoup.parse(paramSource, getConfiguredCharset(), "");
		} catch (IOException e) {
			throw new ConversionException(
					"Conversion is not possible due to an IOException. Error message is " + e.getMessage(), e);
		}
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		String xhtmlString = document.html();
		ByteArrayInputStream bais = new ByteArrayInputStream(xhtmlString.getBytes());
		return convertXHTMLToPDFInputStream(bais);
	}

	private InputStream convertXHTMLToPDFInputStream(InputStream paramSource) throws ConversionException {
		String xhtmlString = null;
		try {
			xhtmlString = IOUtils.toString(paramSource, getConfiguredCharset());
		} catch (IOException e) {
			throw new ConversionException(
					"Conversion is not possible due to an IOException. Error message is " + e.getMessage(), e);
		}
		ITextRenderer renderer = new ITextRenderer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// we need to create the target PDF
		// we'll create one page per input string, but we call layout for the first
		renderer.setDocumentFromString(xhtmlString);
		renderer.layout();
		renderer.createPDF(baos, false);
		renderer.finishPDF();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private String getConfiguredCharset() {
		String charsetConfigured = (String) configurationParameters
				.get(ConfigurationParameterConstants.INPUT_CHARSET_KEY);
		// set default value for charsets
		if (charsetConfigured == null)
			charsetConfigured = "utf-8";
		return charsetConfigured;
	}

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
			return true;
		default:
			return false;
		}
	}

}
