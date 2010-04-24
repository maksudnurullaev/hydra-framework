package org.hydra.utils;

public class Result {
	private boolean _result = false;
	private String _resultAsString = null;
	private Object object = null;
	
	public void setResult(boolean _result) {
		this._result = _result;
	}
	public boolean isOk() {
		return _result;
	}
	public void setResult(String _resultAsString) {
		this._resultAsString = _resultAsString;
	}
	
	public String getResult() {
		return _resultAsString;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Object getObject() {
		return object;
	}
}
