package org.hydra.db.server.abstracts;

import java.util.HashSet;
import java.util.Set;

import org.hydra.db.beans.AccessPath;
import org.hydra.db.beans.Key;
import org.hydra.db.beans.Ksp;
import org.hydra.db.beans.Key.SUPER;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.handlers.CassandraMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraDescriptorBean extends ALogger {

	private Set<Ksp> _keyspaceBeans = new HashSet<Ksp>();
	
	public static final String PATH2COLUMN = "%s.%s['%s']['%s']['%s']";
	public static final String PATH2LINK   = "%s.%s['%s']['%s']";	
	
	public SUPER getSuper(AccessPath accessPath) {
		return getCFKey(accessPath).getSuper();
	}	
	
	public ACassandraDescriptorBean() {
		super();
	}

	public void setKeyspaces(Set<Ksp> inKeyspaces) {
		_keyspaceBeans = inKeyspaces;
	}

	public Set<Ksp> getKeyspaces() {
		return _keyspaceBeans;
	}

	public Ksp getKSName(String inKSName) {
		for(Ksp entry:getKeyspaces()){
			if(entry.getName().equals(inKSName))
				return entry;
		}
		return null;
	}

	public String getAccessDescription(AccessPath inAccessPath){
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		String inputBoxID = inAccessPath.getKsp() + inAccessPath.getCf();
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		
		if(getCFKey(inAccessPath).getSuper() == SUPER.COLUMNS){
			getLog().debug("Create html path for column!");
			return String.format(String.format(formatStrong, "Column", PATH2COLUMN), 
					inAccessPath.getKsp(),
					inAccessPath.getCf(),
					String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
					getCFKey(inAccessPath).getSuper(),
					Constants.makeJSLink(inAccessPath.getKey(), 
							"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value",
							//         1         2   3   4   5   6   7   8   9  10  11    12  
							CassandraMessageHandler._handler_name, // 1
							resultDivID, // 2
							CassandraMessageHandler._action, CassandraMessageHandler._action_select, // 3,4   
							CassandraMessageHandler._ksp_link, inAccessPath.getKsp(),     // 5,6
							CassandraMessageHandler._cf_link, inAccessPath.getCf(),     // 7,8
							CassandraMessageHandler._key_link,  inAccessPath.getKey(),      // 9,10
							CassandraMessageHandler._ID_link, inputBoxID  // 11,12
							)
					)
					+
					String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID);
		}else if(getCFKey(inAccessPath).getSuper() == SUPER.LINKS){
			getLog().debug("Create html path for link!");
			return String.format(String.format(formatStrong, "Column", PATH2LINK), 
					inAccessPath.getKsp(),
					getKSName(inAccessPath.getKsp()).getLinkTableName(),
					String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
					Constants.makeJSLink(inAccessPath.getKey(), 
							"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s',%s:$('%s').value", 
							//         1         2   3   4   5   6   7   8   9  10  11    12  
							CassandraMessageHandler._handler_name, // 1
							resultDivID, // 2
							CassandraMessageHandler._action, CassandraMessageHandler._action_select, // 3,4  
							CassandraMessageHandler._ksp_link, inAccessPath.getKsp(),     // 5,6
							CassandraMessageHandler._cf_link, inAccessPath.getCf(),     // 7,8
							CassandraMessageHandler._key_link, inAccessPath.getKey(),       // 9,10
							CassandraMessageHandler._ID_link, inputBoxID) // 11,12
					)
					+
					String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID);
		}
		getLog().error("UNKNOWN TYPE: " + getCFKey(inAccessPath).getSuper());		
		return "UNKNOWN TYPE: " + getCFKey(inAccessPath).getSuper();
	}
	
	public Key getCFKey(AccessPath inAccessPath){
		return getKSName(inAccessPath.getKsp()).getCFName(inAccessPath.getCf()).getKeys().get(inAccessPath.getKey());
	}





}