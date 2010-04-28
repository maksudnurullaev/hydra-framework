package org.hydra.db.server.abstracts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hydra.db.beans.CFKey;
import org.hydra.db.beans.CFName;
import org.hydra.db.beans.KSName;
import org.hydra.db.beans.CFKey.TYPE;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.handlers.CassandraMessageHandler;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.abstracts.ALogger;

public abstract class ACassandraDescriptorBean extends ALogger {

	private Set<KSName> _keyspaceBeans = new HashSet<KSName>();
	private CassandraAccessorBean accessor;
	private Map<String, String> _locales = new HashMap<String, String>();
	private String _defaultLocale;

	public ACassandraDescriptorBean() {
		super();
	}

	public void setAccessor(CassandraAccessorBean accessor) {
		this.accessor = accessor;
	}

	public CassandraAccessorBean getAccessor() {
		return accessor;
	}

	public void setDefaultLocale(String defaultLocale) {
		this._defaultLocale = defaultLocale;
	}

	public String getDefaultLocale() {
		return _defaultLocale;
	}

	public void setLocales(Map<String, String> locales) {
		this._locales = locales;
	}

	public Map<String, String> getLocales() {
		return _locales;
	}

	public void setKeyspaces(Set<KSName> inKeyspaces) {
		_keyspaceBeans = inKeyspaces;
	}

	public Set<KSName> getKeyspaces() {
		return _keyspaceBeans;
	}

	public KSName getKSName(String inKSName) {
		for(KSName entry:getKeyspaces()){
			if(entry.getName().equals(inKSName))
				return entry;
		}
		return null;
	}

	public boolean checkDescriptions(String ksName, String cfName, String cName) {
		// 1. check ksname
		KSName ksNameBean = getKSName(ksName);
		if(ksNameBean == null) return false;
		
		// 2. check cfname
		CFName cfNameBean = ksNameBean.getCFName(cfName);
		if(cfName == null) return false;
		
		// 3. check cname
		return cfNameBean.getFields().containsKey(cName);
	}

	public String getAccessDescription(String ksName, String cfName, String cName) {
		String formatStrong = Constants.getTemplate("template.html.Strongtext.Text.br", null);
		
		String inputBoxID = ksName + cfName;
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		if(!checkDescriptions(ksName, cfName, cName)){
			return trace + "error.ksname.cfname.cname.not.exist";
		}
		
		if(getCName(ksName, cfName, cName).getType() == TYPE.COLUMNS){
			return String.format(String.format(formatStrong, "Column", "%s.%s['%s']['%s']['%s']"), 
					ksName,
					cfName,
					String.format(Constants.getTemplate("template.html.custom.input.ID.Value", null), inputBoxID, inputBoxVal),
					getCName(ksName, cfName, cName).getType(),
					Constants.makeJSLink(cName, 
							"handler:'%s',what:'%s',kind:'%s',dest:'%s',%s:'%s',%s:'%s'", 
							CassandraMessageHandler._handler_name, // handler
							CassandraMessageHandler._what_select,  // what
							ksName, //kind
							resultDivID, //dest
							CassandraMessageHandler._cfname_key, cfName,
							CassandraMessageHandler._cname_key, cName)
					)
					+
					String.format(Constants.getTemplate("template.html.hr.divId.dots",null), resultDivID);
		}
		
		return String.format(String.format(formatStrong, "Column", "%s.%s['%s']['%s']['%s']"), 
				ksName,
				getKSName(ksName).getLinkTableName(),
				String.format(Constants.getTemplate("template.html.custom.input.ID.Value", null), inputBoxID, inputBoxVal),
				cName,
				Constants.makeJSLink("IDs", 
						"handler:'%s',what:'%s',kind:'%s',cname:'%s',dest:'%s'", 
						"CassandraData",
						ksName,
						cfName,
						cName,
						resultDivID)
				)
				+
				String.format(Constants.getTemplate("template.html.hr.divId.dots",null), resultDivID);
	}


	public CFKey getCName(String ksName, String cfName, String cName) {
		return getKSName(ksName).getCFName(cfName).getFields().get(cName);
	}

	public void setup() {
		if(getAccessor() == null){
			getLog().error("Could not setup Cassandra accessor bean!");
			return;
		}
		getLog().debug("Setup Cassandra accessor");
		getAccessor().setup();
	}

	public String getHTMLReport() {		
		StringBuffer result = new StringBuffer();
		String formatStrong = Constants.getTemplate("template.html.Strongtext.Text.br", null);
		try{
			result.append(String.format(formatStrong,"Cluster name", getAccessor().getClusterName()));
			result.append(String.format(formatStrong,"Ip", getAccessor().getHost()));
			result.append(String.format(formatStrong,"Port", getAccessor().getPort()));
			result.append(String.format(formatStrong,"Version", getAccessor().getProtocolVersion()));
			result.append(String.format(formatStrong,"Keyspaces", getKSNamesJSLinks()));
			result.append(String.format(Constants.getTemplate("template.html.hr.divId.dots",null), KSName._ksname_desc_divId));
		}catch (Exception e) {
			result.append(e.getMessage());
			getLog().error(e.getMessage());
		}			
		return result.toString();
	}

	private String getKSNamesJSLinks() {
		StringBuffer result = new StringBuffer();	
		int counter = 0;
		
		for (KSName entryKSName: _keyspaceBeans) {
			if(getAccessor().getKeyspaces().contains(entryKSName.getName())){
				if(counter++ != 0)
					result.append(", ");
					result.append(Constants.makeJSLink(entryKSName.getName(), 
							"handler:'%s',what:'%s',kind:'%s', dest:'%s'", 
							AdminMessageHandler._handler_name,
							AdminMessageHandler._what_cassandra_ksname_desc,
							entryKSName.getName(),
							KSName._ksname_desc_divId));
					
			}else result.append(String.format("Keyspace(%s) doesn't exist on server!", entryKSName.getName()));
		}
		
		return result.toString();
	}

}