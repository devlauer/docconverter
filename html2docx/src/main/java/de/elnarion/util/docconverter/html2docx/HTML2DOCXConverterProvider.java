package de.elnarion.util.docconverter.html2docx;

import java.util.Map;
import java.util.Set;

import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.DocConverterProvider;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class HTML2DOCXConverterProvider creates/provides new
 * HTML2PDFDocConverter-objects.
 */
public class HTML2DOCXConverterProvider implements DocConverterProvider {

	@Override
	public DocConverter createDocConverter(Map<String, Object> paramConfigurationParameters) {
		return new HTML2DOCXConverter(paramConfigurationParameters);
	}

	@Override
	public Map<String, Set<String>> getSupportedMimeTypeConversions() {
		return HTML2DOCXConverter.getSupportedConversion();
	}

	@Override
	public boolean isInputTypeSupported(InputType paramInputType) {

		return HTML2DOCXConverter.isInputTypeSupported(paramInputType);
	}

}
