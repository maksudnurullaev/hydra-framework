package org.hydra.tests.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hydra.utils.Constants;


public class Just4Run
{
    public static void main(String[] args) throws TTransportException, UnsupportedEncodingException
    {
    	TTransport transport =  new TSocket("localhost", 9160);
    	TProtocol protocol = new TBinaryProtocol(transport);
    	Cassandra.Client client = new Cassandra.Client(protocol);
    	// Open connection
    	transport.open();
    	// Main
    	
    	Map<String, List<ColumnOrSuperColumn>> data = new HashMap<String, List<ColumnOrSuperColumn>>();
        
    	List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();

    	// Create the email column.
    	ColumnOrSuperColumn ce = new ColumnOrSuperColumn();
    	ce.setColumn(new Column("email".getBytes("utf-8"), "ronald (at) sodeso.nl".getBytes("utf-8"), Constants.getDate().getTime()));
    	columns.add(ce);

    	// Create the country column.
    	ColumnOrSuperColumn cc = new ColumnOrSuperColumn();
    	cc.setColumn(new Column("country".getBytes("utf-8"), "Netherlands, The".getBytes("utf-8"), Constants.getDate().getTime()));
    	columns.add(cc);

    	// Create the registeredSince column.
    	ColumnOrSuperColumn cr = new ColumnOrSuperColumn();
    	cr.setColumn(new Column("registeredSince".getBytes("utf-8"), "01/01/2010".getBytes("utf-8"), Constants.getDate().getTime()));
    	columns.add(cr);

    	data.put("Authors", columns);
    	       
    	try {
			client.batch_insert("Blog", "Ronald Mathies", data, ConsistencyLevel.ANY);
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimedOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	
    	// Close connection
    	transport.flush();
    	transport.close();
    }

}
