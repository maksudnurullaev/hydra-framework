package org.hydra.tests.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.UnavailableException;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.hydra.utils.ResultAsListOfColumnOrSuperColumn;

public class Just4Test3 {

	public static void main(String[] args) throws UnsupportedEncodingException,
			InvalidRequestException, UnavailableException, TimedOutException,
			TException, NotFoundException {
        TTransport tr = new TSocket("localhost", 9160);
        TProtocol proto = new TBinaryProtocol(tr);
        Cassandra.Client client = new Cassandra.Client(proto);
        tr.open();
		
		ResultAsListOfColumnOrSuperColumn result =
			new ResultAsListOfColumnOrSuperColumn();		
		
		String ksp = null;
		String key = null;
		ColumnParent cf = null;
		// SlicePredicate predicate = null;
		ConsistencyLevel cLevel = null;
		
		ksp = "KSMainTEST";
		cf = new ColumnParent("Users");
		key = "COLUMNS";
		// predicate = DBUtils.getSlicePredicate4Col(new byte[0]);
		cLevel = ConsistencyLevel.ONE;		
		
		SlicePredicate predicate = new SlicePredicate();
		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(new byte[0]);
		sliceRange.setFinish(new byte[0]);
		predicate.setSlice_range(sliceRange);

		try {
			List<ColumnOrSuperColumn> cols = client.get_slice(ksp, key, cf, predicate, cLevel);
			result.setColumnOrSuperColumn(cols);
			result.setResult(true);
		} catch (Exception e) {
			result.setResult(false);
			System.out.println(e.toString());
			result.setResult(e.toString());
		} finally {
	        tr.close();
		}
		
        if(result.isOk())
        	System.out.println("\nCount of ColumnOrSuperColumn:" + result.getColumnOrSuperColumn().size());
        
        
		
	}
}
