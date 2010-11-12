package org.hydra.db.server.abstracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.beans.db.KeyspaceBean;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraDescriptorBean extends ALogger {

	/**
	 * In future we should use keyspaces to separate each applications data
	 */
	private Map<String, KeyspaceBean> _keyspaces = new HashMap<String, KeyspaceBean>();
	private KeyspaceBean rootKeyspace = null;
	
	public boolean containsKeyspace(String keyspaceName) {
		return _keyspaces.containsKey(keyspaceName);
	}	
	
	public ACassandraDescriptorBean() {
		super();
	}

	public void setKeyspaces(Set<KeyspaceBean> inKeyspaces) {
		_keyspaces.clear();
		for(KeyspaceBean entryKsp: inKeyspaces)
			_keyspaces.put(entryKsp.getName(), entryKsp);
	}

	public KeyspaceBean getKeyspace(String inKeyspaceName) {
		if(_keyspaces.containsKey(inKeyspaceName)){
			getLog().debug("Get keyspace: " + inKeyspaceName);
			return _keyspaces.get(inKeyspaceName);
		}
		getLog().warn("Could not find keyspace: " + inKeyspaceName);
		return null;
	}

	public void setRootKeyspace(KeyspaceBean rootKeyspace) {
		this.rootKeyspace = rootKeyspace;
	}

	public KeyspaceBean getRootKeyspace() {
		return rootKeyspace;
	}
	




}