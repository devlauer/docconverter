package de.elnarion.util.docconverter.adoc2adoc;

import java.util.List;
import java.util.Map;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.PreprocessorReader;

/**
 * The Class Preprocessor uses the asciidoc extension mechanism to extract all
 * asciidoc file contents as a list of strings.
 */
public class Preprocessor extends org.asciidoctor.extension.Preprocessor {

	private List<String> contentLines;

	/**
	 * Instantiates a new preprocessor.
	 */
	public Preprocessor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new preprocessor.
	 *
	 * @param config the config
	 */
	public Preprocessor(Map<String, Object> config) {
		super(config);
	}

	@Override
	public PreprocessorReader process(Document document, PreprocessorReader reader) {
		if (contentLines == null) {
			contentLines = reader.readLines();
			
		}
		return reader;
	}

	/**
	 * Gets the content lines.
	 *
	 * @return List - the content lines
	 */
	public List<String> getContentLines() {
		return contentLines;
	}

}
