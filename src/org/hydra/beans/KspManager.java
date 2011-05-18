package org.hydra.beans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;
import me.prettyprint.cassandra.service.ThriftCluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KspManager {
	private static final Log _log = LogFactory.getLog("org.hydra.beans.KspManager");
	
	private ThriftCluster cluster;
	private Set<String> cfNames = new HashSet<String>();
	
	public void initApp(String inAppId) {
		// 1.
		_log.debug("check keyspace for: " + inAppId);
		if(!containsKeyspace(inAppId)){
			_log.warn("... not found, create keyspace: " + inAppId);
			KeyspaceDefinition ksdef = HFactory.createKeyspaceDefinition(inAppId);
			_log.warn("... keyspace created: " + cluster.addKeyspace(ksdef ));			
		} else{
			_log.debug("... ok");						
		}
		// 2.
		for(String cfName:cfNames){
			_log.debug(String.format("check CF(%s) for: %s", cfName, inAppId));
			if(!containsCf(inAppId, cfName)){
				_log.warn("... not found, create CF: " + cfName);
				ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(inAppId, cfName, ComparatorType.UTF8TYPE);
				_log.debug("... ok");						
				cluster.addColumnFamily(cfd);
			}
		}
	}

	private boolean containsCf(String inAppId, String cfName) {
		KeyspaceDefinition kspDef = cluster.describeKeyspace(inAppId);
		List<ColumnFamilyDefinition> cfDefs = kspDef.getCfDefs();
		if(cfDefs == null || cfDefs.size() == 0) return false;
	
		for(ColumnFamilyDefinition cfd: cfDefs){
			if(cfd.getName() != null && cfd.getName().compareToIgnoreCase(cfName) == 0)
				return true;
		}
		return false;
	}

	private boolean containsKeyspace(String inAppId) {
		for(KeyspaceDefinition kspDef: cluster.describeKeyspaces())
			if(kspDef.getName().compareToIgnoreCase(inAppId) == 0)
				return true;
		return false;
	}

	public void setCluster(ThriftCluster cluster) {
		_log.debug("Set cluster: " + cluster.getClusterName());
		this.cluster = cluster;
	}

	public void setCfNames(Set<String> cfNames) {
		this.cfNames = cfNames;
	}

	public Keyspace getKeyspace(String inKeyspace) {
		_log.debug("Get keyspace: " + inKeyspace);
		 return HFactory.createKeyspace(inKeyspace, cluster);
	}

	public SimpleCassandraDao getSimpleCassandraDao(
			String inKeyspace,
			String inColumnFamily) {
		_log.debug(String.format("Get SimpleCassandraDao for KSP(%s) and CF(%s)", inKeyspace, inColumnFamily));
		Keyspace ksp = getKeyspace(inKeyspace);
		if(ksp != null){
			SimpleCassandraDao result = new SimpleCassandraDao();
			result.setKeyspace(ksp);
			result.setColumnFamilyName(inColumnFamily);
			return(result);
		}
		_log.error(String.format("Could not get SimpleCassandraDao for KSP(%s) and CF(%s)", inKeyspace, inColumnFamily));
		return null;
	}
}
