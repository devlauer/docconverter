package de.elnarion.util.docconverter.api.exception;

/**
 * The basic exception class for all ConversionExceptions.
 */
public class ConversionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new conversion exception.
	 */
	public ConversionException() {
	}

	/**
	 * Instantiates a new conversion exception.
	 *
	 * @param message the message
	 */
	public ConversionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new conversion exception.
	 *
	 * @param cause the cause
	 */
	public ConversionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new conversion exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new conversion exception.
	 *
	 * @param message            the message
	 * @param cause              the cause
	 * @param enableSuppression  the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
