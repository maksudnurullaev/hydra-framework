package org.hydra.db.server;

import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.hydra.db.beans.AccessPath;
import org.hydra.db.beans.Ksp;
import org.hydra.db.beans.Key.SUPER;
import org.hydra.db.server.abstracts.ACassandraAccessor;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;



public class CassandraAccessorBean extends ACassandraAccessor {
	private List<Column> getDBColumns(AccessPath accessPath) {
		List<Column> result = null;
		
        SlicePredicate predicate = new SlicePredicate();
        
//        if(SessionManager.getCassandraServerDescriptor().getType(accessPath) == TYPE.COLUMNS)
//        {
//        	List<byte[]> column_names = new ArrayList<byte[]>();
//        	column_names.add(Constants.string2UTF8Bytes(accessPath.getCName()));
//            predicate.setColumn_names(column_names);
//        }else{
            SliceRange sliceRange = new SliceRange();
//            sliceRange.setStart(new byte[0]);
            sliceRange.setStart(Constants.string2UTF8Bytes(accessPath.getKey()));            
            sliceRange.setFinish(new byte[0]);
            predicate.setSlice_range(sliceRange);        	
//        }
        
        
        ColumnParent parent = new ColumnParent(accessPath.getCf());

        getLog().debug("Borrow client...");
        Cassandra.Client cClient = clientGet();
        try {
			List<ColumnOrSuperColumn> results = cClient.get_slice(
					accessPath.getKsp(), 
					accessPath.getID(),
					parent, 
					predicate, 
					ConsistencyLevel.ONE);
			
			getLog().debug("Get getDBObject results: " + results.size());
			
			if(results.size() == 0){
				getLog().debug("No result!");
				result = null;		
			}else{			
				result = results.get(0).super_column.columns;
			}
			
		} catch (Exception e) {
			getLog().equals(e.getMessage());
			e.printStackTrace();			
			result = null;			
		}finally{
			clientClose(cClient);
			getLog().debug("Borrowed client closed!");
		}
		
		return result;
	}

	public SUPER getType(AccessPath accessPath) {
		return null;
	}

	public List<Column> getColumns(AccessPath accessPath) {
		return getDBColumns(accessPath);
	}

	public String getHTMLReport() {		
		StringBuffer result = new StringBuffer();
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		try{
			result.append(String.format(formatStrong,"Cluster name", getClusterName()));
			result.append(String.format(formatStrong,"Ip", getHost()));
			result.append(String.format(formatStrong,"Port", getPort()));
			result.append(String.format(formatStrong,"Version", getProtocolVersion()));
			result.append(String.format(formatStrong,"Keyspaces", getKSNamesJSLinks()));
			result.append(String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), Ksp._ksp_desc_divId));
		}catch (Exception e) {
			result.append(e.getMessage());
			getLog().error(e.getMessage());
		}			
		return result.toString();
	}	
	
	private String getKSNamesJSLinks() {
		StringBuffer result = new StringBuffer();	
		int counter = 0;
		
		for (Ksp entryKSName: getDescriptor().getKeyspaces()) {
			if(getKeyspaces().contains(entryKSName.getName())){
				if(counter++ != 0)
					result.append(", ");
					result.append(Constants.makeJSLink(entryKSName.getName(), 
							"handler:'%s',dest:'%s',%s:'%s',%s:'%s'", 
							AdminMessageHandler._handler_name,
							Ksp._ksp_desc_divId,
							IMessage._data_what, AdminMessageHandler._what_cassandra_ksp_desc,
							IMessage._data_kind, entryKSName.getName()
							));
					
			}else result.append(String.format("Keyspace(%s) doesn't exist on server!", entryKSName.getName()));
		}
		
		return result.toString();
	}		
	
}
