package net.collaud.fablab.exceptions;

/**
 *
 * @author gaetan
 */
public class FablabException extends BusinessException{

	public FablabException(String message, Throwable cause) {
		super(message, cause);
	}

	public FablabException(String message) {
		super(message);
	}
	
}
