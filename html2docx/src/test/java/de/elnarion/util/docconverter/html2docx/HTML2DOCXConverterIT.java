package de.elnarion.util.docconverter.html2docx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.Test;

import de.elnarion.util.docconverter.api.ConversionJobFactory;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.spi.testsupport.BasicDocConverterIT;

/**
 * The integration test class for HTML2DOCXConverter.
 */
public class HTML2DOCXConverterIT extends BasicDocConverterIT {

	/**
	 * Test convert XHTML with a sample xhtml file.
	 *
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws ConversionException  the conversion exception
	 * @throws IOException
	 */
	@Test
	public void testConvertXHTML() throws InterruptedException, ExecutionException, ConversionException, IOException {
		HTML2DOCXConverter converter = new HTML2DOCXConverter(new HashMap<String, Object>());
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.xhtml");
		List<InputStream> inputList = new ArrayList<>();
		inputList.add(input);
		Future<List<InputStream>> result = converter.convertStreams(inputList, MimeTypeConstants.APPLICATION_XHTML,
				MimeTypeConstants.APPLICATION_DOCX);
		InputStream os = result.get().iterator().next();
		checkDocxBytes(IOUtils.toByteArray(os));
	}

	/**
	 * Test convert HTML with a sample html file which is not xhtml compliant.
	 *
	 * @throws ConversionException  the conversion exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	@Test
	public void testConvertHTML() throws ConversionException, InterruptedException, ExecutionException, IOException {
		HTML2DOCXConverter converter = new HTML2DOCXConverter(new HashMap<String, Object>());
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.html");
		List<InputStream> inputList = new ArrayList<>();
		inputList.add(input);
		Future<List<InputStream>> result = converter.convertStreams(inputList, MimeTypeConstants.TEXT_HTML,
				MimeTypeConstants.APPLICATION_DOCX);
		InputStream os = result.get().iterator().next();
		checkDocxBytes(IOUtils.toByteArray(os));
	}

	/**
	 * Test convert XHTML via SPI.
	 *
	 * @throws ConversionException  the conversion exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	@Test
	public void testConvertXHTMLViaSPI()
			throws ConversionException, InterruptedException, ExecutionException, IOException {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.xhtml");
		List<InputStream> inputList = new ArrayList<>();
		inputList.add(input);
		Future<List<InputStream>> result = ConversionJobFactory.getInstance().createEmptyConversionJob()
				.fromStreams(inputList).fromMimeType(MimeTypeConstants.APPLICATION_XHTML)
				.toMimeType(MimeTypeConstants.APPLICATION_DOCX).convert();
		InputStream os = result.get().iterator().next();
		checkDocxBytes(IOUtils.toByteArray(os));
	}

	protected void checkDocxBytes(byte[] paramDocxBytes) throws IOException {
		assertNotNull(paramDocxBytes);
		assertTrue(paramDocxBytes.length > 5);
		XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(paramDocxBytes));
		assertNotNull(document);
		XWPFParagraph paragraph = document.getLastParagraph();
		assertNotNull(paragraph);
	}
}
