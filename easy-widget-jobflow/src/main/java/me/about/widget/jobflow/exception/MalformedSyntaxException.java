package me.about.widget.jobflow.exception;

/**
 * This exception will be thrown if any exception occurred while parsing
 * 
 * @author griever
 *
 */
public class MalformedSyntaxException extends RuntimeException {
	public MalformedSyntaxException(final String msg) {
		super(msg);
	}

	public MalformedSyntaxException(final Throwable cause) {
		super(cause);
	}
}
