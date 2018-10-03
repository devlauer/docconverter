package de.elnarion.maven.plugin.docconverter;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DocConverterMojoTest.
 */
public class DocConverterMojoTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	protected void setUp() throws Exception {
		// required for mojo lookups to work
		super.setUp();
	}

	/** {@inheritDoc} */
	@After
	protected void tearDown() throws Exception {
		// required
		super.tearDown();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMojoConvert() throws Exception {
		System.out.println("TEST MOJO");
		File testPom = new File(getBasedir(), "src/test/resources/unit/docconverter/xhtml2pdftest/pom.xml");
		assertNotNull(testPom);
		assertTrue(testPom.exists());

		DocConverterMojo mojo = (DocConverterMojo) lookupMojo("convert", testPom);
		assertNotNull(mojo);

		mojo.execute();
		File testFile = new File(getBasedir(), "target/test-harness/docconverter/xhtml2pdf/test.pdf");
		assertTrue(testFile.exists());
		File testFile2 = new File(getBasedir(), "target/test-harness/docconverter/xhtml2pdf/test2.pdf");
		assertTrue(testFile2.exists());
	}
}
