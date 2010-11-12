package org.hydra.utils;

import java.util.Map;

import org.hydra.beans.db.ColumnBean;

public class ResultAsMapOfStringAndColumnBean {
	private boolean _result = false;
	private String _resultAsString = null;
	private Map<String, ColumnBean> _mapOfStringAndColumnBean = null;
	
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
	
	public void setMapOfStringAndColumnBean(Map<String, ColumnBean> mapOfStringAndColumnBean) {
		this._mapOfStringAndColumnBean = mapOfStringAndColumnBean;
	}
	
	public Map<String, ColumnBean> getMapOfStringAndColumnBean() {
		return _mapOfStringAndColumnBean;
	}

}
