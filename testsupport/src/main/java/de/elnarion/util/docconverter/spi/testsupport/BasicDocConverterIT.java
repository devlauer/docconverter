package de.elnarion.util.docconverter.spi.testsupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class provides different helper methods for integration test checks
 */
public abstract class BasicDocConverterIT {

	protected void checkPDFBytes(byte[] paramPDFBytes) {
		assertNotNull(paramPDFBytes);
		assertTrue(paramPDFBytes.length > 5);

		// check for pdf header
		assertEquals(0x25, paramPDFBytes[0]); // %
		assertEquals(0x50, paramPDFBytes[1]); // P
		assertEquals(0x44, paramPDFBytes[2]); // D
		assertEquals(0x46, paramPDFBytes[3]); // F
		assertEquals(0x2D, paramPDFBytes[4]); // -
	}

	protected void checkJPEGBytes(byte[] paramJPEGBytes) {
		assertNotNull(paramJPEGBytes);
		assertTrue(paramJPEGBytes.length > 3);

		byte[] jpegStartBytes = new byte[] { (byte) 255, (byte) 216, (byte) 255 };
		// check for jpeg header
		assertEquals(jpegStartBytes[0], paramJPEGBytes[0]);
		assertEquals(jpegStartBytes[1], paramJPEGBytes[1]);
	}

}
