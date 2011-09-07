package org.hydra.utils;

public class Result {
	private boolean _result = false;
	private String _resultErrAsString = null;
	private Object object = null;
	
	public void setResult(boolean _result) {
		this._result = _result;
	}
	public boolean isOk() {
		return _result;
	}
	public void setErrorString(String _resultAsString) {
		this._resultErrAsString = _resultAsString;
		_result = false;
	}
	
	public String getResult() {
		return _resultErrAsString;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Object getObject() {
		return object;
	}
}
