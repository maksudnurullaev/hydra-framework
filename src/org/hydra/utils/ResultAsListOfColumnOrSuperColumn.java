package org.hydra.utils;

import java.util.List;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

public class ResultAsListOfColumnOrSuperColumn {
	private boolean _result = false;
	private String _resultAsString = null;
	private List<ColumnOrSuperColumn>  columnOrSuperColumn = null;
	
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
	public void setColumnOrSuperColumn(List<ColumnOrSuperColumn> columnOrSuperColumn) {
		this.columnOrSuperColumn = columnOrSuperColumn;
	}
	public List<ColumnOrSuperColumn> getColumnOrSuperColumn() {
		return columnOrSuperColumn;
	}

}
