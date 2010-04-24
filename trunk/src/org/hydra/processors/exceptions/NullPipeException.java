package org.hydra.processors.exceptions;

public class NullPipeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String _error_message;
	
	public NullPipeException(){
		super();
		_error_message = "Pipe is null!";
	}
	
	public NullPipeException(String err){
		super(err);
		_error_message = err;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return _error_message;
	}	
	
	
}
