package de.elnarion.util.docconverter.pdf2jpg;

import java.util.Map;
import java.util.Set;

import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.DocConverterProvider;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class HTML2PDFConverterProvider creates/provides new
 * HTML2PDFDocConverter-objects.
 */
public class PDF2JPGConverterProvider implements DocConverterProvider {

	@Override
	public DocConverter createDocConverter(Map<String, Object> paramConfigurationParameters) {
		return new PDF2JPGConverter(paramConfigurationParameters);
	}

	@Override
	public Map<String, Set<String>> getSupportedMimeTypeConversions() {
		return PDF2JPGConverter.getSupportedConversion();
	}

	@Override
	public boolean isInputTypeSupported(InputType paramInputType) {
		return PDF2JPGConverter.isInputTypeSupported(paramInputType);
	}

}
