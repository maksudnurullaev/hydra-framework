package org.hydra.messages.handlers;

import java.util.Map;

import org.hydra.beans.db.ColumnBean;
import org.hydra.beans.db.ColumnFamilyBean;
import org.hydra.db.server.CassandraAccessorBean;
import org.hydra.db.server.CassandraDescriptorBean;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Utils;

public class AdmCassandra extends AMessageHandler { // NO_UCD
	static final String _destination = "_adm._subdiv";

	public IMessage getCFDescription(CommonMessage inMessage) {
		if(!testParameters(	inMessage, Constants._data_key)) return inMessage;			
		String cfName = inMessage.getData().get(Constants._data_key);
		
		CassandraDescriptorBean descriptor = BeansUtils.getDescriptor();
		ColumnFamilyBean cfBean = descriptor.getColumnFamilyByName(cfName);
			
		if(cfBean == null){
			inMessage.setError(trace + "Could not find Cf: " + inMessage.getData().get(Constants._data_cs_cf));
			return inMessage;	
		}
		
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		String result = String.format(formatStrong, "Column family", cfBean.getName());
//				+ "&nbsp;" 
//				+ Utils.wrap2HTMLTag("sup", DBUtils.getJSLinkShowAllColumns(kspBean, cfBean)));
		
		String resultLinks = "";		
		int counter = 0;
		for(Map.Entry<String, ColumnBean> entryCFKey: cfBean.columns.entrySet()){
			if(counter++ != 0)
				resultLinks += ", ";
			resultLinks += Utils.makeJSLink(entryCFKey.getKey(), 
					String.format("handler:'%s'", this.getClass().getSimpleName()),
					String.format("dest:'%s'", _destination),
					String.format("action:'%s'", "describeColumn"),
					String.format("key:'%s'", cfBean.getName()),
					String.format("col:'%s'", entryCFKey.getKey())
				);									
		}		
		result += String.format(formatStrong, "Columns", resultLinks);
		
		String tableFormat = "<table>" 
			+ "<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>"
			+ "<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>"
			+ "</table";
		// Append tail div for child elements
		if(counter > 0)
			result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), "_adm_cas_cf_col");

		inMessage.setHtmlContent(result);
		return inMessage;
	}		
/*	
	public IMessage describeColumn(IMessage inMessage){
		String keyspaceName = inMessage.getData().get(Constants._data_cs_ksp);
		String columnFamilyName = inMessage.getData().get(Constants._data_cs_cf); 
		String columnName = inMessage.getData().get(Constants._data_cs_col); 
		
		String pathStr = String.format("%s--->%s", keyspaceName, columnFamilyName);
		CassandraVirtualPath path = new CassandraVirtualPath(DBUtils.getDescriptor(), pathStr);
		
		if(!path.isValid()){
			inMessage.setError("Path to DB objects is valid: " + pathStr);
			return inMessage;
		}
		
		Result result = path._cfBean.getColumn(columnName);
		if(!result.isOk() || !(result.getObject() instanceof ColumnBean)){
				inMessage.setError("Could not find cassandra column description!");
				return inMessage;
		}
		
		ColumnBean column = (ColumnBean)result.getObject();
		
		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		
		String inputBoxID = keyspaceName + columnFamilyName;
		String inputBoxVal = inputBoxID + "ID";
		String resultDivID = inputBoxID + "Div";
		
		inMessage.setHtmlContent(String.format(String.format(formatStrong, "Column", "%s.%s['%s']['%s']"), 
				keyspaceName,
				columnFamilyName,
				String.format(MessagesManager.getTemplate("template.html.custom.input.ID.Value"), inputBoxID, inputBoxVal),
					Utils.makeJSLink(column.getName(), 
							String.format("handler:'%s'", this.getClass().getSimpleName()),
							String.format("dest:'%s'", resultDivID),
							String.format("action:'%s'", "cs_select_super_column"),
							String.format("cs_ksp:'%s'", keyspaceName),
							String.format("cs_cf:'%s'", columnFamilyName),
							String.format("cs_key:$('%s').value", inputBoxID))
				)
				+
				String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), resultDivID)
		);		
		return inMessage;
	}	
*/
}
