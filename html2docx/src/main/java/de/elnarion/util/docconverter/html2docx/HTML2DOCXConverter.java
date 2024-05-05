package de.elnarion.util.docconverter.html2docx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.elnarion.util.docconverter.api.ConfigurationParameterConstants;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.common.AbstractBaseConverter;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class HTML2DOCXConverter.
 */
public class HTML2DOCXConverter extends AbstractBaseConverter {

	private static Map<String, Set<String>> supportedMimetypes = null;


	private static final Logger LOGGER = Logger.getLogger(HTML2DOCXConverter.class.getName());

	/**
	 * Instantiates a new HTML 2 PDF converter.
	 *
	 * @param paramConfigurationParameters the param configuration parameters
	 */
	public HTML2DOCXConverter(Map<String, Object> paramConfigurationParameters) {
		super(paramConfigurationParameters);
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
			targetMimetypes.add(MimeTypeConstants.APPLICATION_DOCX);
			supportedMimetypes.put(MimeTypeConstants.TEXT_HTML, targetMimetypes);
			supportedMimetypes.put(MimeTypeConstants.APPLICATION_XHTML, targetMimetypes);
		}
		return supportedMimetypes;
	}


	/**
	 * Convert to output stream.
	 *
	 * @param source              the source
	 * @param paramSourceMimeType the param source mime type
	 * @return the output stream
	 * @throws ConversionException the conversion exception
	 */
	protected List<InputStream> convertToInputStream(InputStream source, String paramSourceMimeType)
			throws ConversionException {
		List<InputStream> resultStreams = new ArrayList<>();
		if (MimeTypeConstants.APPLICATION_XHTML.equals(paramSourceMimeType)) {
			resultStreams.add(convertXHTMLToDocxInputStream(source));
		} else if (MimeTypeConstants.TEXT_HTML.equals(paramSourceMimeType)) {
			resultStreams.add(convertHTMLToDocxInputStream(source));
		}
		return resultStreams;
	}

	private InputStream convertHTMLToDocxInputStream(InputStream paramSource) throws ConversionException {
		Document document;
		try {
			document = Jsoup.parse(paramSource, getConfiguredCharset(), "");
		} catch (IOException e) {
			throw new ConversionException(
					"Conversion is not possible due to an IOException. Error message is " + e.getMessage(), e);
		}
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		String xhtmlString = document.html();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xhtmlString.getBytes());
		return convertXHTMLToDocxInputStream(byteArrayInputStream);
	}

	private InputStream convertXHTMLToDocxInputStream(InputStream paramSource) throws ConversionException {
		String xhtmlString;
		try {
			xhtmlString = IOUtils.toString(paramSource, getConfiguredCharset());
		} catch (IOException e) {
			throw new ConversionException(
					"Conversion is not possible due to an IOException. Error message is " + e.getMessage(), e);
		}

		// Setup font mapping
		RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
		rfonts.setAscii("Century Gothic");
		XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
		try {

			// Create an empty docx package
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

			NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
			wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
			ndp.unmarshalDefaultNumbering();

			// Convert the XHTML, and add it into the empty docx we made
			XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);

			xhtmlImporter.setHyperlinkStyle("Hyperlink");

			// we need to create the target PDF
			// we'll create one page per input string, but we call layout for the first
			if (getConfigurationParameters() != null
					&& getConfigurationParameters().containsKey(ConfigurationParameterConstants.BASE_DIRECTORY_URL)) {
				wordMLPackage.getMainDocumentPart().getContent().addAll(xhtmlImporter.convert(xhtmlString,
						(String) getConfigurationParameters().get(ConfigurationParameterConstants.BASE_DIRECTORY_URL)));
			} else {
				String baseUrl = Paths.get("").toFile().toURI().toURL().toString();
				wordMLPackage.getMainDocumentPart().getContent().addAll(xhtmlImporter.convert(xhtmlString, baseUrl));
			}

			if (LOGGER.isLoggable(Level.FINEST)) {
				LOGGER.finest(
						XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
			}

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			wordMLPackage.save(byteArrayOutputStream);

			return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		} catch (Docx4JException | MalformedURLException | JAXBException e) {
			throw new ConversionException(
					"Conversion is not possible due to an internal exception. Error message is " + e.getMessage(), e);
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
			case INPUTSTREAM:
			return true;
		default:
			return false;
		}
	}

}
