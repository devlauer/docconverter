package de.elnarion.util.docconverter.api.exception;

/**
 * The Class InputUnavailableException.
 */
public class InputUnavailableException extends ConversionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new input unavailable exception.
	 */
	public InputUnavailableException() {
	}

	/**
	 * Instantiates a new input unavailable exception.
	 *
	 * @param message the message
	 */
	public InputUnavailableException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new input unavailable exception.
	 *
	 * @param cause the cause
	 */
	public InputUnavailableException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new input unavailable exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public InputUnavailableException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new input unavailable exception.
	 *
	 * @param message            the message
	 * @param cause              the cause
	 * @param enableSuppression  the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public InputUnavailableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
