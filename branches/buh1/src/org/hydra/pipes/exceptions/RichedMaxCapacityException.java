package org.hydra.pipes.exceptions;


public class RichedMaxCapacityException extends Exception{
	private String _error_message;
	
	public RichedMaxCapacityException(){
		super();
		_error_message = "Pipe riched Maximal Capacity";
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