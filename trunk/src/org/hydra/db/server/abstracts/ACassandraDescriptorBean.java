package org.hydra.db.server.abstracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.db.beans.ColumnBean;
import org.hydra.db.beans.KeyspaceBean;
import org.hydra.db.server.CassandraVirtualPath;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.SessionManager;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraDescriptorBean extends ALogger {

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