package de.elnarion.util.docconverter.pdf2jpg;


import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.pdfbox.io.IOUtils;
import org.junit.Test;

import de.elnarion.util.docconverter.api.ConversionJobFactory;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;
import de.elnarion.util.docconverter.spi.testsupport.BasicDocConverterIT;

/**
 * The Class PDF2JPGConverterIT.
 */
public class PDF2JPGConverterIT extends BasicDocConverterIT {

	/**
	 * Test pdf 2 jpg conversion.
	 *
	 * @throws ConversionException  the conversion exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	@Test
	public void testPdf2JpgConversion()
			throws ConversionException, InterruptedException, ExecutionException, IOException {
		PDF2JPGConverter converter = new PDF2JPGConverter(new HashMap<String,Object>());
		List<InputStream> inputStreams = new ArrayList<>();
		inputStreams.add(this.getClass().getClassLoader().getResourceAsStream("dummy.pdf"));
		Future<List<InputStream>> result = converter.convertStreams(inputStreams, MimeTypeConstants.APPLICATION_PDF,
				MimeTypeConstants.IMAGE_JPEG);
		assertNotNull(result);
		List<InputStream> resultInputStreamList = result.get();
		InputStream ris = resultInputStreamList.iterator().next();
		assertNotNull(ris);
		byte[] bytes = IOUtils.toByteArray(ris);
		checkJPEGBytes(bytes);
	}

	/**
	 * Test convert pdf 2 jpg via SPI.
	 *
	 * @throws ConversionException  the conversion exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	@Test
	public void testConvertPdf2JpgViaSPI()
			throws ConversionException, InterruptedException, ExecutionException, IOException {
		List<InputStream> inputList = new ArrayList<>();
		inputList.add(this.getClass().getClassLoader().getResourceAsStream("dummy.pdf"));
		Future<List<InputStream>> result = ConversionJobFactory.getInstance().createEmptyConversionJob()
				.fromStreams(inputList).fromMimeType(MimeTypeConstants.APPLICATION_PDF)
				.toMimeType(MimeTypeConstants.IMAGE_JPEG).convert();
		List<InputStream> resultInputStreamList = result.get();
		InputStream ris = resultInputStreamList.iterator().next();
		assertNotNull(ris);
		byte[] bytes = IOUtils.toByteArray(ris);
		checkJPEGBytes(bytes);
	}

}
