package de.elnarion.util.docconverter.html2pdf;

import com.lowagie.text.DocumentException;
import de.elnarion.util.docconverter.api.ConfigurationParameterConstants;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.common.AbstractBaseConverter;
import de.elnarion.util.docconverter.spi.InputType;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The Class HTML2PDFConverter.
 */
public class HTML2PDFConverter extends AbstractBaseConverter {

    private static Map<String, Set<String>> supportedMimetypes = null;


    /**
     * Instantiates a new HTML 2 PDF converter.
     *
     * @param paramConfigurationParameters the param configuration parameters
     */
    public HTML2PDFConverter(Map<String, Object> paramConfigurationParameters) {
        super(paramConfigurationParameters);
    }

    /**
     * Returns a mapping of all conversions that are supported by the backing
     * conversion engine.
     *
     * @return A map of all possible conversions with the key describing the input
     * mimetypes and the set describing the mimetypes that these input
     * mimetypes can be converted into.
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
            resultStreams.add(convertXHTMLToPDFInputStream(source));
        } else if (MimeTypeConstants.TEXT_HTML.equals(paramSourceMimeType)) {
            resultStreams.add(convertHTMLToPDFInputStream(source));
        }
        return resultStreams;
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
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xhtmlString.getBytes());
        return convertXHTMLToPDFInputStream(byteArrayInputStream);
    }

    private InputStream convertXHTMLToPDFInputStream(InputStream paramSource) throws ConversionException {
        String xhtmlString;
        try {
            xhtmlString = IOUtils.toString(paramSource, getConfiguredCharset());
        } catch (IOException e) {
            throw new ConversionException(
                    "Conversion is not possible due to an IOException. Error message is " + e.getMessage(), e);
        }
        ITextRenderer renderer = new ITextRenderer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // we need to create the target PDF
        // we'll create one page per input string, but we call layout for the first
        if (getConfigurationParameters() != null
                && getConfigurationParameters().containsKey(ConfigurationParameterConstants.BASE_DIRECTORY_URL)) {
            renderer.setDocumentFromString(xhtmlString,
                    (String) getConfigurationParameters().get(ConfigurationParameterConstants.BASE_DIRECTORY_URL));
        } else {
            renderer.setDocumentFromString(xhtmlString);
        }
        renderer.layout();
        try {
            renderer.createPDF(byteArrayOutputStream, false);
            renderer.finishPDF();
        } catch (DocumentException e) {
            throw new ConversionException(e.getMessage(), e);
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
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
