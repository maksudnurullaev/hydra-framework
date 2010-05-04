package org.hydra.messages.handlers;

import java.util.Map;

import org.hydra.db.beans.Key;
import org.hydra.db.beans.Cf;
import org.hydra.db.beans.Ksp;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.spring.AppContext;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.SessionManager;

public class CFNameMessageHandler extends AMessageHandler {
	public static final String _handler_name = "CFNameMessage";
	public static final String _what_desc = "desc";

	// HTML IDs
	public static final String _cf_desc_divId = "_cf_desc_div";
	public static final String _cname_desc_divId = "_cname_desc_div";

	@Override
	public IMessage handleMessage(IMessage inMessage) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";
		
		// - Test incoming message
		if(!isValidMessage(inMessage)) return inMessage;
		
		String cfName = inMessage.getData().get(IMessage._data_kind); // cfName
		String ksName = inMessage.getData().get(IMessage._data_what); // ksName

		Ksp ksNameBean = SessionManager.getCassandraServerDescriptor().getKSName(ksName);
				
		if(ksNameBean != null){
			Cf cfNameBean = ksNameBean.getCFName(cfName);
			if(cfNameBean != null){
				inMessage.setHtmlContent(getCNamesDescHtml(inMessage, cfNameBean, ksNameBean));
			}else inMessage.setError(trace + "Could not find CFName: " + cfName);
			
		}else inMessage.setError(trace + "Could not find KSName: " + ksName);
		
		return inMessage;				
	}

	private String getCNamesDescHtml(IMessage inMessage, Cf inCFNameBean, Ksp ksNameBean) {
		// Spring Debug Mode
		if(AppContext.isDebugMode()){
			trace = Constants.trace(this, Thread.currentThread().getStackTrace());
		} else trace = "";

		String formatStrong = MessagesManager.getTemplate("template.html.Strongtext.Text.br");
		int counter = 0;
		
		String result = String.format(formatStrong, "CFName", inCFNameBean.getName());
		
		String resultLinks = "";
		for(Map.Entry<String, Key> entryCFKey: inCFNameBean.getKeys().entrySet()){
			if(counter++ != 0)
				resultLinks += ", ";
			resultLinks += Constants.makeJSLink(entryCFKey.getKey(),
					"handler:'%s',dest:'%s',%s:'%s',%s:'%s',%s:'%s',%s:'%s'",
					CassandraMessageHandler._handler_name,
					CFNameMessageHandler._cname_desc_divId,
					CassandraMessageHandler._action, CassandraMessageHandler._action_describe,
					CassandraMessageHandler._ksp_link, ksNameBean.getName(),
					CassandraMessageHandler._cf_link, inCFNameBean.getName(),
					CassandraMessageHandler._key_link, entryCFKey.getKey()
				);
						
		}		
		result += String.format(formatStrong, "CNames", resultLinks);
		
		// Append tail div for child elements
		if(counter > 0)
			result += String.format(MessagesManager.getTemplate("template.html.hr.divId.dots"), _cname_desc_divId);

		
		return result;
	}

}
