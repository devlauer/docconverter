package de.elnarion.util.docconverter.pdf2jpg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.common.AbstractBaseConverter;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class PDF2JPGConverter.
 */
public class PDF2JPGConverter extends AbstractBaseConverter {

	private static Map<String, Set<String>> supportedMimetypes = null;

	/**
	 * Instantiates a new PDF 2 JPG converter.
	 *
	 * @param paramConfigurationParameters the param configuration parameters
	 */
	public PDF2JPGConverter(Map<String, Object> paramConfigurationParameters) {
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
			targetMimetypes.add(MimeTypeConstants.IMAGE_JPEG);
			supportedMimetypes.put(MimeTypeConstants.APPLICATION_PDF, targetMimetypes);
		}
		return supportedMimetypes;
	}


	protected List<InputStream> convertToInputStream(InputStream source,String paramSourceMimeType) throws ConversionException {
		ByteArrayOutputStream baos;
		List<InputStream> resultList = new ArrayList<>();
		try {
			System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
			PDDocument document = Loader.loadPDF(IOUtils.toByteArray(source));
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				baos = new ByteArrayOutputStream();
				BufferedImage bim;
				bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				ImageIOUtil.writeImage(bim, "jpg", baos, 300);
				resultList.add(new ByteArrayInputStream(baos.toByteArray()));
			}
			document.close();
		} catch (IOException e) {
			throw new ConversionException("Conversion failed because of an io exception." + e.getMessage(), e);
		}
		return resultList;
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
