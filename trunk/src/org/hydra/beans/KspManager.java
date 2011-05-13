package org.hydra.beans;

import me.prettyprint.hector.api.Keyspace;

public class KspManager {
	private Keyspace keyspace;
	
	public int getCountOf(String inColumnName){
		return(keyspace.hashCode());
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}
}
