package de.elnarion.util.docconverter.api.exception;

/**
 * The Class EmptyMimeTypeException.
 */
public class EmptyMimeTypeException extends ConversionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new empty mime type exception.
	 */
	public EmptyMimeTypeException() {
	}

	/**
	 * Instantiates a new empty mime type exception.
	 *
	 * @param message the message
	 */
	public EmptyMimeTypeException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new empty mime type exception.
	 *
	 * @param cause the cause
	 */
	public EmptyMimeTypeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new empty mime type exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public EmptyMimeTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new empty mime type exception.
	 *
	 * @param message            the message
	 * @param cause              the cause
	 * @param enableSuppression  the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public EmptyMimeTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
