package de.elnarion.util.docconverter.adoc2adoc;

import java.util.Map;
import java.util.Set;

import de.elnarion.util.docconverter.spi.DocConverter;
import de.elnarion.util.docconverter.spi.DocConverterProvider;
import de.elnarion.util.docconverter.spi.InputType;

/**
 * The Class Adoc2AdocDocConverterProvider.
 */
public class Adoc2AdocDocConverterProvider implements DocConverterProvider {

	@Override
	public Map<String, Set<String>> getSupportedMimeTypeConversions() {
		return Adoc2AdocDocConverter.getSupportedConversion();
	}

	@Override
	public DocConverter createDocConverter(Map<String, Object> paramConfigurationParameters) {
		return new Adoc2AdocDocConverter(paramConfigurationParameters);
	}

	@Override
	public boolean isInputTypeSupported(InputType paramInputType) {
		return Adoc2AdocDocConverter.isInputTypeSupported(paramInputType);
	}

}
