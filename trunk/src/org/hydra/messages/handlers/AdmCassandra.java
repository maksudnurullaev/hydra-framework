package org.hydra.messages.handlers;

import java.util.Map;

import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Utils;

public class AdmCassandra extends AMessageHandler { // NO_UCD
	static final String sub_div = "_adm._subdiv";
	public IMessage getCFDescription(CommonMessage inMessage) {
		// test parameters
		if(!testParameters(	inMessage, "key")) return inMessage;			
		String cfName = inMessage.getData().get("key");
		/*
		CassandraDescriptorBean descriptor = BeansUtils.getDescriptor();
		// get & check bean
		ColumnFamilyBean cfBean = descriptor.getColumnFamilyByName(cfName);
		if(cfBean.columns == null && cfBean.columns.size() == 0) {
			inMessage.setError("No Columns!");
			return inMessage;
		}
		// create link for columns
		String resultColumns = "";		
		for(Map.Entry<String, ColumnBean> entryCFKey: cfBean.columns.entrySet()){
			if(!resultColumns.isEmpty()) resultColumns += ", ";
			resultColumns += Utils.makeJSLink(entryCFKey.getKey(), 
					String.format("handler:'%s'", this.getClass().getSimpleName()),
					String.format("dest:'%s'", sub_div),
					String.format("action:'%s'", "describeColumn"),
					String.format("key:'%s'", cfBean.getName()),
					String.format("col:'%s'", entryCFKey.getKey())
				);									
		}		
		// create link for CF's links
		String resultLinks = "";		
		for(Map.Entry<String, ColumnBean> entryCFKey: cfBean.columns.entrySet()){
			if(!resultLinks.isEmpty()) resultLinks += ", ";
			resultLinks += Utils.makeJSLink(entryCFKey.getKey(), 
					String.format("handler:'%s'", this.getClass().getSimpleName()),
					String.format("dest:'%s'", sub_div),
					String.format("action:'%s'", "describeColumn"),
					String.format("key:'%s'", cfBean.getName()),
					String.format("col:'%s'", entryCFKey.getKey())
				);									
		}		
		// format results
		String tableFormat = "<table>" 
			+ "<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>"
			+ "<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>"
			+ "<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>"
			+ "</table";
		String result = String.format(tableFormat, 
				"Column family", cfBean.getName(),
				"Columns", resultColumns);
		// Append tail div for child elements
		result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), sub_div);
		// finish
		inMessage.setHtmlContent(result);
		*/
		inMessage.setHtmlContent("IMessage org.hydra.messages.handlers.AdmCassandra.getCFDescription");
		return inMessage;
	}
}
