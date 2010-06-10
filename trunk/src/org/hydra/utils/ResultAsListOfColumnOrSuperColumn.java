package org.hydra.utils;

import java.util.List;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

public class ResultAsListOfColumnOrSuperColumn extends Result {
	private List<ColumnOrSuperColumn>  columnOrSuperColumn = null;
	
	public void setColumnOrSuperColumn(List<ColumnOrSuperColumn> columnOrSuperColumn) {
		this.columnOrSuperColumn = columnOrSuperColumn;
	}
	public List<ColumnOrSuperColumn> getColumnOrSuperColumn() {
		return columnOrSuperColumn;
	}

}
