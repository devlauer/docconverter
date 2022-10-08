package de.elnarion.maven.plugin.docconverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.elnarion.util.docconverter.api.ConfigurationParameterConstants;
import de.elnarion.util.docconverter.api.ConversionJobFactory;
import de.elnarion.util.docconverter.api.ConversionJobWithInputUnspecified;
import de.elnarion.util.docconverter.api.exception.ConversionException;

/**
 * The Class DocConverterMojo.
 */
@Mojo(name = "convert", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class DocConverterMojo extends AbstractMojo {

	private static final String CONFIGSETTING = "configsetting";

	/** The Constant PREFIX. */
	static final String PREFIX = "docconverter.";

	/** The encoding. */
	@Parameter(defaultValue = "${project.build.sourceEncoding}")
	private String encoding;

	/** The source directory. */
	@Parameter(property = PREFIX + "sourceDirectory", defaultValue = "${basedir}/src/main/doc", required = false)
	private File sourceDirectory;

	/** The source document. */
	@Parameter(property = PREFIX + "sourceDocument", required = false)
	private File sourceDocument = null;

	/** The source document extensions. */
	@Parameter(property = PREFIX + "sourceDocumentExtensions", required = false)
	private List<String> sourceDocumentExtensions;

	/** The output directory. */
	@Parameter(property = PREFIX
			+ "outputDirectory", defaultValue = "${project.build.directory}/target/generated-docs", required = false)
	private File outputDirectory;

	/** The source mime type. */
	@Parameter(property = PREFIX + "sourceMimeType", required = true)
	private String sourceMimeType;

	/** The target mime type. */
	@Parameter(property = PREFIX + "targetMimeType", required = true)
	private String targetMimeType;

	/** The target mime type. */
	@Parameter(property = PREFIX + "outputFileending", required = true)
	private String outputFileending;

	/** The conversion parameters. */
	@Parameter(property = PREFIX + "conversionParameters", required = false)
	private Map<String, Object> conversionParameters;

	/** The base path. */
	private String basePath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Starting conversion from " + sourceMimeType + " to " + targetMimeType);
		basePath = sourceDirectory.getPath();
		try {
			if (sourceDocument != null) {
				Future<List<InputStream>> result = convertFile(sourceDocument);
				writeResult(sourceDocument, result);
			} else {
				convertSourceDirectory();
			}
		} catch (ConversionException e) {
			String message = "Failed to execute conversion.";
			getLog().error(message, e);
			throw new MojoFailureException(message);
		}
	}

	/**
	 * Write result.
	 *
	 * @param sourceDocument2 the source document 2
	 * @param result          the result
	 * @throws MojoFailureException the mojo failure exception
	 */
	private void writeResult(File sourceDocument2, Future<List<InputStream>> result) throws MojoFailureException {
		String path = sourceDocument2.getParent();
		getLog().debug("Writing result of " + sourceDocument2.getAbsolutePath());
		String filePath = sourceDocument2.getPath();
		String filename = filePath.substring(path.length() + 1, filePath.length());
		String filenameWithoutExtension = filename.lastIndexOf('.') > 0
				? filename.substring(0, filename.lastIndexOf('.'))
				: filename;
		String relativePath = path.substring(basePath.length(), path.length());
		File targetDirectory = new File(outputDirectory.getPath() + relativePath);
		if (getOutputFileending() != null) {
			setOutputFileending(
					getOutputFileending().startsWith(".") ? getOutputFileending() : "." + getOutputFileending());
		}
		String outputFilename = (getOutputFileending() != null ? filenameWithoutExtension + getOutputFileending()
				: filename);

		if (targetDirectory.exists() || targetDirectory.mkdirs()) {
			try {
				List<InputStream> inList = result.get();
				String counterString = "";
				int counter = 0;
				for (InputStream in : inList) {
					File targetFile = new File(
							targetDirectory.getPath() + File.separator + counterString + outputFilename);
					getLog().info(
							"Writing result of " + sourceDocument2.getName() + " to " + targetFile.getAbsolutePath());
					counter++;
					counterString = "" + counter;
					try (FileOutputStream targetStream = new FileOutputStream(targetFile)) {
						IOUtils.copy(in, targetStream);
					}
				}
			} catch (InterruptedException e) {
				getLog().warn("Thread has been interrupted.", e);
				Thread.currentThread().interrupt();
			} catch (ExecutionException | IOException e) {
				throw new MojoFailureException("Conversion failed for file " + filePath, e);
			}

		} else {
			throw new MojoFailureException(
					"Plugin was not able to create target folder " + targetDirectory.getAbsolutePath());
		}
	}

	/**
	 * Convert source directory.
	 *
	 * @throws MojoFailureException the mojo failure exception
	 * @throws ConversionException  the conversion exception
	 */
	private void convertSourceDirectory() throws MojoFailureException, ConversionException {
		if (sourceDirectory == null || !sourceDirectory.isDirectory()) {
			throw new MojoFailureException("Source directory is not a directory");
		}
		Map<File, Future<List<InputStream>>> conversionMap = new HashMap<>();
		convertDirectory(sourceDirectory, conversionMap);
		getLog().debug("Size conversion Map" + conversionMap.size());
		Set<Entry<File, Future<List<InputStream>>>> conversionEntries = conversionMap.entrySet();
		for (Entry<File, Future<List<InputStream>>> entry : conversionEntries) {
			getLog().debug("Writing entry result");
			writeResult(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Convert directory.
	 *
	 * @param paramDirectory     the param directory
	 * @param paramConversionMap the param conversion map
	 * @throws ConversionException  the conversion exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	private void convertDirectory(File paramDirectory, Map<File, Future<List<InputStream>>> paramConversionMap)
			throws ConversionException, MojoFailureException {
		File[] directoryFiles = paramDirectory.listFiles();
		for (File directoryFile : directoryFiles) {
			if (directoryFile.isDirectory()) {
				convertDirectory(directoryFile, paramConversionMap);
			} else {
				String fileName = directoryFile.getName();
				String fileExtension = fileName.contains(".")
						? fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length())
						: null;

				if (sourceDocumentExtensions == null || sourceDocumentExtensions.isEmpty()
						|| sourceDocumentExtensions.contains(fileExtension)) {
					getLog().info("Starting conversion of " + directoryFile.getAbsolutePath());
					Future<List<InputStream>> result = convertFile(directoryFile);
					paramConversionMap.put(directoryFile, result);
				}
			}
		}
	}

	/**
	 * Convert file.
	 *
	 * @param paramSourceDocument the param source document
	 * @return the future
	 * @throws ConversionException the conversion exception
	 */
	private Future<List<InputStream>> convertFile(File paramSourceDocument) throws ConversionException {
		getLog().debug(CONFIGSETTING);
		ConversionJobWithInputUnspecified conversionJob = null;
		Map<String, Object> config = new HashMap<>();
		if (conversionParameters != null) {
			getLog().debug("configsetting all");
			config.putAll(conversionParameters);
		}
		getLog().debug("configsetting update");
		if (config.containsKey(ConfigurationParameterConstants.BASE_DIRECTORY_URL)) {
			String directory = (String) config.get(ConfigurationParameterConstants.BASE_DIRECTORY_URL);
			getLog().debug(CONFIGSETTING + directory);
			config.put(ConfigurationParameterConstants.BASE_DIRECTORY_URL, directory.replace("\\\\", "/"));
			getLog().debug(
					"Fixed base directory URL to " + config.get(ConfigurationParameterConstants.BASE_DIRECTORY_URL));
		} else if (paramSourceDocument != null) {
			try {
				getLog().debug(CONFIGSETTING + paramSourceDocument.getParentFile().toURI().toURL());
				config.put(ConfigurationParameterConstants.BASE_DIRECTORY_URL,
						paramSourceDocument.getParentFile().toURI().toURL().toString());
			} catch (MalformedURLException e) {
				getLog().error(e);
			}
		}
		conversionJob = ConversionJobFactory.getInstance().createEmptyConversionJob(config);

		List<File> conversionSourceFiles = new ArrayList<>();
		conversionSourceFiles.add(paramSourceDocument);
		return conversionJob.fromFiles(conversionSourceFiles).fromMimeType(sourceMimeType).toMimeType(targetMimeType)
				.convert();
	}

	/**
	 * Gets the encoding.
	 *
	 * @return String - the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the encoding.
	 *
	 * @param encoding the encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Gets the source directory.
	 *
	 * @return File - the source directory
	 */
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	/**
	 * Sets the source directory.
	 *
	 * @param sourceDirectory the source directory
	 */
	public void setSourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	/**
	 * Gets the output directory.
	 *
	 * @return File - the output directory
	 */
	public File getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * Sets the output directory.
	 *
	 * @param outputDirectory the output directory
	 */
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	/**
	 * Gets the source mime type.
	 *
	 * @return String - the source mime type
	 */
	public String getSourceMimeType() {
		return sourceMimeType;
	}

	/**
	 * Sets the source mime type.
	 *
	 * @param sourceMimeType the source mime type
	 */
	public void setSourceMimeType(String sourceMimeType) {
		this.sourceMimeType = sourceMimeType;
	}

	/**
	 * Gets the target mime type.
	 *
	 * @return String - the target mime type
	 */
	public String getTargetMimeType() {
		return targetMimeType;
	}

	/**
	 * Sets the target mime type.
	 *
	 * @param targetMimeType the target mime type
	 */
	public void setTargetMimeType(String targetMimeType) {
		this.targetMimeType = targetMimeType;
	}

	/**
	 * Gets the source document.
	 *
	 * @return File - the source document
	 */
	public File getSourceDocument() {
		return sourceDocument;
	}

	/**
	 * Sets the source document.
	 *
	 * @param sourceDocument the source document
	 */
	public void setSourceDocument(File sourceDocument) {
		this.sourceDocument = sourceDocument;
	}

	/**
	 * Gets the source document extensions.
	 *
	 * @return List - the source document extensions
	 */
	public List<String> getSourceDocumentExtensions() {
		return sourceDocumentExtensions;
	}

	/**
	 * Sets the source document extensions.
	 *
	 * @param sourceDocumentExtensions the source document extensions
	 */
	public void setSourceDocumentExtensions(List<String> sourceDocumentExtensions) {
		this.sourceDocumentExtensions = sourceDocumentExtensions;
	}

	/**
	 * Gets the output fileending.
	 *
	 * @return String - the output fileending
	 */
	public String getOutputFileending() {
		return outputFileending;
	}

	/**
	 * Sets the output fileending.
	 *
	 * @param outputFileending the output fileending
	 */
	public void setOutputFileending(String outputFileending) {
		this.outputFileending = outputFileending;
	}

	/**
	 * Gets the conversion parameters.
	 *
	 * @return Map - the conversion parameters
	 */
	public Map<String, Object> getConversionParameters() {
		return conversionParameters;
	}

	/**
	 * Sets the conversion parameters.
	 *
	 * @param conversionParameters the conversion parameters
	 */
	public void setConversionParameters(Map<String, Object> conversionParameters) {
		this.conversionParameters = conversionParameters;
	}

}
