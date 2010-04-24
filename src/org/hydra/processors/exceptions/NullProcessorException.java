package org.hydra.processors.exceptions;

public class NullProcessorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String _error_message;
	
	public NullProcessorException(){
		super();
		_error_message = "Processor is null!";
	}
	
	public NullProcessorException(String err){
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
