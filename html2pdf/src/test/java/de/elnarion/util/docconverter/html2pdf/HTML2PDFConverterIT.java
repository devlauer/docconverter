package de.elnarion.util.docconverter.html2pdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.elnarion.util.docconverter.api.ConversionJobFactory;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.spi.testsupport.BasicDocConverterIT;

/**
 * The integration test class for HTML2PDFConverter.
 */
public class HTML2PDFConverterIT extends BasicDocConverterIT {

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
		HTML2PDFConverter converter = new HTML2PDFConverter(new HashMap<String,Object>());
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.xhtml");
		List<InputStream> inputList = new ArrayList<>();
		inputList.add(input);
		Future<List<InputStream>> result = converter.convertStreams(inputList, MimeTypeConstants.APPLICATION_XHTML,
				MimeTypeConstants.APPLICATION_PDF);
		InputStream os = result.get().iterator().next();
		checkPDFBytes(IOUtils.toByteArray(os));
	}

	/**
	 * Test convert HTML with a sample html file which is not xhtml compliant.
	 *
	 * @throws ConversionException  the conversion exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws IOException
	 */
	@Test
	public void testConvertHTML() throws ConversionException, InterruptedException, ExecutionException, IOException {
		HTML2PDFConverter converter = new HTML2PDFConverter(new HashMap<String,Object>());
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("test.html");
		List<InputStream> inputList = new ArrayList<>();
		inputList.add(input);
		Future<List<InputStream>> result = converter.convertStreams(inputList, MimeTypeConstants.TEXT_HTML,
				MimeTypeConstants.APPLICATION_PDF);
		InputStream os = result.get().iterator().next();
		checkPDFBytes(IOUtils.toByteArray(os));
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
				.toMimeType(MimeTypeConstants.APPLICATION_PDF).convert();
		InputStream os = result.get().iterator().next();
		checkPDFBytes(IOUtils.toByteArray(os));
	}

}
