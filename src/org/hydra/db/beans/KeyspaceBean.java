package org.hydra.db.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.abstracts.ALogger;

/**
 * @author M.Nurullayev
 *
 */
public class KeyspaceBean extends ALogger {
	private Map<String, ColumnFamilyBean> _columnFamilies = new HashMap<String, ColumnFamilyBean>();
	
	private String name = null;
	private String linkTableName = null;

	// HTML IDs
	public static final String _ksp_desc_divId = "_ksp_desc_div";

	
	public void setColumnFamilies(Set<ColumnFamilyBean> inColumnFamilies) {
		_columnFamilies.clear();
		for(ColumnFamilyBean entryCF: inColumnFamilies)
			_columnFamilies.put(entryCF.getName(), entryCF);
		
		getLog().debug(String.format("ColumnFamily(%s) added to Keyspace(%s)", _columnFamilies.size(), getName()));		
	}
	
	public void setName(String name) {
		this.name = name;
		getLog().debug("Set keyspace name to: " + getName());
	}
	
	public String getName() {
		return name;
	}
	
	public String getCfHTMLDescription() {
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		int counter = 0;
		String result = String.format(formatStrong, "Keyspace", getName());
		
		String cfLinks = "";
				
		
		for(String keyName: _columnFamilies.keySet()){
			if(counter++ != 0)
				cfLinks += ", ";
			cfLinks += Constants.makeJSLink(keyName,
					"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s'", 
						AdminMessageHandler._handler_name,
						AdminMessageHandler._admin_cf_divId,
						IMessage._data_action, AdminMessageHandler._action_describe_cassandra_cf,
						IMessage._data_cs_ksp, getName(),
						IMessage._data_cs_cf, keyName						
					);
		}
		
		// Append all column family links
		result += String.format(formatStrong, "Column families", cfLinks.toString());
		
		// Append tail div for child elements
		if(counter > 0)
			result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), 
					AdminMessageHandler._admin_cf_divId);
		
		return result.toString();		
	}
	
	public ColumnFamilyBean getColumnFamilyByName(String inCfName){
		if(_columnFamilies.containsKey(inCfName)){
			getLog().warn(String.format("Found column family(%s) in keyspace(%s)",
					inCfName,
					getName()));
			return _columnFamilies.get(inCfName);
		}
		getLog().warn(String.format("Could not find column family(%s) in keyspace(%s)",
				inCfName,
				getName()));
		return null;
	}
	
	public void setLinkTableName(String linkTableName) {
		this.linkTableName = linkTableName;
	}
	
	public String getLinkTableName() {
		return linkTableName;
	}

	public boolean containsColumnFamily(String inColumnFamilyName) {
		if(_columnFamilies != null)
			return _columnFamilies.containsKey(inColumnFamilyName);
		return false;
	}

	
	
}
