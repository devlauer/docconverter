package de.elnarion.util.doconverter.adoc2adoc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.elnarion.util.docconverter.adoc2adoc.Adoc2AdocDocConverter;
import de.elnarion.util.docconverter.api.MimeTypeConstants;
import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * The Class Adoc2AdocDocConverterIT.
 */
class Adoc2AdocDocConverterIT {

	/**
	 * Test adoc 2 adoc conversion.
	 *
	 * @throws ConversionException  the conversion exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the execution exception
	 * @throws IOException
	 */
	@Test
	void testAdoc2AdocConversion()
			throws ConversionException, InterruptedException, ExecutionException, IOException {
		Map<String, Object> config = new HashMap<>();
		config.put(Adoc2AdocDocConverter.REGEXP_REMAIN_INCLUDE_STATEMENT, ".*include\\:\\:\\.\\/.*\\[\\].*");
		Adoc2AdocDocConverter converter = new Adoc2AdocDocConverter(config);
		File source = new File("src/test/resources/processfolder/test1.adoc"); 
		List<File> sourceFiles = new ArrayList<>();
		sourceFiles.add(source);
		Future<List<InputStream>> result = converter.convertFiles(sourceFiles, MimeTypeConstants.TEXT_ASCIIDOC,
				MimeTypeConstants.TEXT_ASCIIDOC);
		InputStream resultIS = result.get().iterator().next();
		assertNotNull(resultIS);
		String resultString = IOUtils.toString(resultIS, "utf-8");
		assertTrue(resultString.contains("include::./test2.adoc[]"));
		assertTrue(!resultString.contains("include::../includefolder/include.adoc[]"));
		assertTrue(!resultString.contains("This is an example of an asciidoc file with a tag inside"));
	}

}
