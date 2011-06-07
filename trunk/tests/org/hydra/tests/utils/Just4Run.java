package org.hydra.tests.utils;

import org.hydra.utils.DBUtils;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

/**
* A simple example showing what it takes to page over results using
* get_range_slices.
*
* To run this example from maven:
* mvn -e exec:java -Dexec.mainClass="com.riptano.cassandra.hector.example.PaginateGetRangeSlices"
*
* @author zznate
*
*/
public class Just4Run {
    
    private static StringSerializer stringSerializer = StringSerializer.get();
    
    public static void main(String[] args) throws Exception {

        Cluster cluster = HFactory.getOrCreateCluster("Hydra Cluster", "localhost:9160");

        Keyspace keyspaceOperator = HFactory.createKeyspace("HydraUz", cluster);
                
        try {
//            Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, stringSerializer);
//            mutator.addDeletion("ROOTtest1", "Tag");
//            //Mutator 
//            MutationResult result = mutator.execute();
//            System.out.println(result.toString());

//            for (int i = 0; i < 20; i++) {
//                mutator.addInsertion("fake_key_" + i, "User", HFactory.createStringColumn("fake_column_0", "fake_value_0_" + i))
//                .addInsertion("fake_key_" + i, "User", HFactory.createStringColumn("fake_column_1", "fake_value_1_" + i))
//                .addInsertion("fake_key_" + i, "User", HFactory.createStringColumn("fake_column_2", "fake_value_2_" + i));
//            }
//            mutator.execute();
//            
            RangeSlicesQuery<String, String, String> rangeSlicesQuery =
                HFactory.createRangeSlicesQuery(keyspaceOperator, stringSerializer, stringSerializer, stringSerializer);
            rangeSlicesQuery.setColumnFamily("User");
            rangeSlicesQuery.setKeys("", "");
            rangeSlicesQuery.setRange("", "", false, 3);
            
            rangeSlicesQuery.setRowCount(11);
            QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
            OrderedRows<String, String, String> orderedRows = result.get();

//            Row<String,String,String> lastRow = orderedRows.peekLast();

            System.out.println("Contents of rows: ");
            for (Row<String, String, String> r : orderedRows) {
                System.out.println(" key:" + r.getKey());
                ColumnSlice<String, String> cs = r.getColumnSlice();
                if(cs.getColumns().size() > 0){
	                for(HColumn<String, String> hc:cs.getColumns()){
	                	System.out.println("\tname: " + hc.getName());
	                	System.out.println("\tvalue: " + hc.getValue());
	                }
                }else{
                    System.out.println("\t NO_COLUMNS!");                	
                }
//				ColumnQuery<String, String, String> columnQuery = HFactory.createStringColumnQuery(keyspaceOperator);
//				columnQuery.setColumnFamily("Text").setKey(r.getKey()).setName("name");
//                QueryResult<HColumn<String, String>> result1 = columnQuery.execute();  
//                System.out.println(" value:" + result1.get());
            }
            
//            System.out.println("Should have 11 rows: " + orderedRows.getCount());
//            
//            rangeSlicesQuery.setKeys(lastRow.getKey(), "");
//            orderedRows = rangeSlicesQuery.execute().get();
//            
//            System.out.println("2nd page Contents of rows: \n");
//            for (Row<String, String, String> row : orderedRows) {
//                System.out.println(" " + row);
//            }
            
        } catch (HectorException he) {
            he.printStackTrace();
        }
        cluster.getConnectionManager().shutdown();
    }
}