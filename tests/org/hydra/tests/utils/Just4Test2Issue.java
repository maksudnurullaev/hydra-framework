package org.hydra.tests.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.hydra.utils.DBUtils;

public class Just4Test2Issue {

        public static final String UTF8 = "UTF8";

        public static void main(String[] args) throws UnsupportedEncodingException,
                        InvalidRequestException, UnavailableException, TimedOutException,
                        TException, NotFoundException {
                TTransport tr = new TSocket("localhost", 9160);
                TProtocol proto = new TBinaryProtocol(tr);
                Cassandra.Client client = new Cassandra.Client(proto);
                tr.open();

                String keyspace = "KSMainTEST";
                String columnFamily = "Articles";
                String keyID = "COLUMNS";
                // String superID1 = "11";

                // insert data
                

                // setup super column
                
				List<Mutation> listOfMutation = new ArrayList<Mutation>();
                for (int i = 0; i < 5; i++) {
                	List<Column> listOfCol = new ArrayList<Column>();
                    Mutation mutation = new Mutation();
                    SuperColumn super_column = new SuperColumn((keyID+i).getBytes(UTF8), listOfCol);                    
                    ColumnOrSuperColumn column_or_supercolumn1 = new ColumnOrSuperColumn();
                    
                    column_or_supercolumn1.setSuper_column(super_column);
    				mutation.setColumn_or_supercolumn(column_or_supercolumn1);                	
                    listOfCol.add(new Column("testname".getBytes(UTF8), "testvalue".getBytes(UTF8), DBUtils.getCassandraTimestamp()));
                    
    				listOfMutation.add(mutation);
				}
                
                
                
				Map<String, Map<String, List<Mutation>>> mutation_map = new HashMap<String, Map<String, List<Mutation>>>();			            
				Map<String, List<Mutation>> mutation_map_sub = new HashMap<String, List<Mutation>>();
				
				mutation_map_sub.put(columnFamily, listOfMutation);
				mutation_map.put(keyID, mutation_map_sub);
				try{
					// insert
					client.batch_mutate(keyspace, mutation_map, ConsistencyLevel.ONE);
	                
	                // read entire row
	                SlicePredicate predicate = new SlicePredicate();
	                SliceRange sliceRange = new SliceRange();
	                sliceRange.setStart(new byte[0]);
	                sliceRange.setFinish(new byte[0]);
	                predicate.setSlice_range(sliceRange);
	
	                ColumnParent parent = new ColumnParent(columnFamily);
	                // print after insertion
	                List<ColumnOrSuperColumn> results = client.get_slice(keyspace,
	                                keyID, parent, predicate, ConsistencyLevel.ONE);
	                System.out.println(" === after insertion === ");
	                if(results.size() != 0){	                
		                for (ColumnOrSuperColumn result : results) {
		                        SuperColumn sColumn = result.super_column;
		                        System.out.println(new String(sColumn.name, UTF8) + " -> after insertion!");
		                }
	                }else{
                        System.out.println("No records!!!");	                	
	                }
	                
					// delete
	                ColumnPath columnPath = new ColumnPath(columnFamily);
	                client.remove(keyspace, keyID, columnPath, DBUtils.getCassandraTimestamp(), ConsistencyLevel.ONE);
	                
	                System.out.println("COUNT: " + client.get_count(keyspace,
                            keyID, parent, ConsistencyLevel.ONE));
	                
	                // print after deletion
	                results = client.get_slice(keyspace,
	                                keyID, parent, predicate, ConsistencyLevel.ONE);
	                System.out.println(" === after deletion === ");	                
	                if(results.size() != 0){	                
		                for (ColumnOrSuperColumn result : results) {
		                        SuperColumn sColumn = result.super_column;
		                        System.out.println(new String(sColumn.name, UTF8) + " -> after insertion!");
		                }
	                }else{
                        System.out.println("No records!!!");	                	
	                }	                
	                
				}catch(Exception e){
					System.out.println(e);
				}finally{
					tr.close();
				}
        }
}
