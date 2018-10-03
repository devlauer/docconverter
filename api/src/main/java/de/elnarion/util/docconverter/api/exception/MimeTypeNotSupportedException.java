package de.elnarion.util.docconverter.api.exception;

/**
 * The Class MimeTypeNotSupportedException.
 */
public class MimeTypeNotSupportedException extends ConversionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new mime type not supported exception.
	 */
	public MimeTypeNotSupportedException() {
	}

	/**
	 * Instantiates a new mime type not supported exception.
	 *
	 * @param message the message
	 */
	public MimeTypeNotSupportedException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new mime type not supported exception.
	 *
	 * @param cause the cause
	 */
	public MimeTypeNotSupportedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new mime type not supported exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public MimeTypeNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new mime type not supported exception.
	 *
	 * @param message            the message
	 * @param cause              the cause
	 * @param enableSuppression  the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public MimeTypeNotSupportedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
