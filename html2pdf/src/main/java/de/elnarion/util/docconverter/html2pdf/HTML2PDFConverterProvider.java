package de.elnarion.util.docconverter.html2pdf;

import java.util.Map;
import java.util.Set;

import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.DocConverterProvider;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class HTML2PDFConverterProvider creates/provides new
 * HTML2PDFDocConverter-objects.
 */
public class HTML2PDFConverterProvider implements DocConverterProvider {

	@Override
	public DocConverter createDocConverter(Map<String, Object> paramConfigurationParameters) {
		return new HTML2PDFConverter(paramConfigurationParameters);
	}

	@Override
	public Map<String, Set<String>> getSupportedMimeTypeConversions() {
		return HTML2PDFConverter.getSupportedConversion();
	}

	@Override
	public boolean isInputTypeSupported(InputType paramInputType) {

		return HTML2PDFConverter.isInputTypeSupported(paramInputType);
	}

}
