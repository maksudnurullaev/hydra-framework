package org.hydra.db.beans;

import java.util.HashSet;
import java.util.Set;

import org.hydra.messages.handlers.CFNameMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.abstracts.ALogger;

public class Ksp extends ALogger {
	private Set<Cf> _columnFamilies = new HashSet<Cf>();
	
	private String name = null;
	private String linkTableName = null;

	// HTML IDs
	public static final String _ksp_desc_divId = "_ksp_desc_div";

	
	public void setColumnFamilies(Set<Cf> inCFNames) {
		_columnFamilies = inCFNames;
		
		getLog().debug(String.format("%s CFNames added to KSName(%s)", _columnFamilies.size(), getName()));		
	}
	
	public Set<Cf> getColumnFamilies() {
		return _columnFamilies;
	}
	
	public void setName(String name) {
		this.name = name;
		getLog().debug("Set KSName name to: " + getName());
	}
	
	public String getName() {
		return name;
	}
	
	public String getCFNamesDescriptionHTML() {
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		int counter = 0;
		String result = String.format(formatStrong, "KSName", getName());
		
		String cfNamesLinks = "";
				
		
		for(Cf entryCFName: _columnFamilies){
			if(counter++ != 0)
				cfNamesLinks += ", ";
			cfNamesLinks += Constants.makeJSLink(entryCFName.getName(),
					"handler:'%s',dest:'%s',%s:'%s',%s:'%s'", 
						CFNameMessageHandler._handler_name,
						CFNameMessageHandler._cf_desc_divId,
						IMessage._data_what, getName(),
						IMessage._data_kind, entryCFName.getName()						
					);
		}
		
		// Append all CFName links
		result += String.format(formatStrong, "CFNames", cfNamesLinks.toString());
		
		// Append tail div for child elements
		if(counter > 0)
			result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), CFNameMessageHandler._cf_desc_divId);
		
		return result.toString();		
	}
	
	public Cf getCFName(String inCFName){
		for(Cf entry:_columnFamilies){
			if(entry.getName().equalsIgnoreCase(inCFName))
				return entry;
		}
		return null;
	}
	
	public void setLinkTableName(String linkTableName) {
		this.linkTableName = linkTableName;
	}
	public String getLinkTableName() {
		return linkTableName;
	}
	
	
}
