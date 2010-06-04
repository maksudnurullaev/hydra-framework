package org.hydra.tests.utils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class Just4Run {

	public static final String UTF8 = "UTF8";

	public static void main(String[] args) throws UnsupportedEncodingException,
			InvalidRequestException, UnavailableException, TimedOutException,
			TException, NotFoundException {
		TTransport tr = new TSocket("localhost", 9160);
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);

		try {
			if (tr.isOpen())
				tr.close();

			tr.open();

			String keyspace = "KSMainTEST";
			String columnFamily = "Users";
			String keyUserID = "COLUMNS";

			// insert data

			ColumnPath colPathName = new ColumnPath(columnFamily);
			colPathName.setColumn("fullName".getBytes(UTF8));

			// read single column
			// read entire row
			SlicePredicate predicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange();
			sliceRange.setStart(new byte[0]);
			sliceRange.setFinish(new byte[0]);
			predicate.setSlice_range(sliceRange);

			System.out.println("\nrow:");
			ColumnParent parent = new ColumnParent(columnFamily);
			List<ColumnOrSuperColumn> results = client.get_slice(keyspace,
					keyUserID, parent, predicate, ConsistencyLevel.ONE);
			for (ColumnOrSuperColumn result : results) {
				Column column = result.column;
				System.out.println(new String(column.name, UTF8) + " -> "
						+ new String(column.value, UTF8));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tr.close();
		}
	}
}
