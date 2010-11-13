package org.hydra.pipes.exceptions;


public class RichedMaxCapacityException extends Exception{
	private String _error_message;
	
	public RichedMaxCapacityException(){
		super();
		_error_message = "Pipe riched Maximal Capacity";
	}
	
	public RichedMaxCapacityException(String err){
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
}