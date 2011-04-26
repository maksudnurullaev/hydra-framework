package org.hydra.messages.handlers;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;

import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Result;

public class DBRequest extends AMessageHandler{ // NO_UCD

	public IMessage updateText(CommonMessage inMessage){
		if(!testParameters(	inMessage, "value")) return inMessage;
		
		String dbCfText = "cf" + inMessage._web_application.getId()	+ "Text";
		
		_log.debug("Update action for:" + dbCfText);
		_log.debug("... key:" + inMessage.getData().get("dest"));
		_log.debug("... locale:" + inMessage.getData().get("dest"));
		_log.debug("... value length:" + inMessage.getData().get("value").length());
		
		Result result = new Result();
		BeansUtils.getWebContextBean(result , dbCfText);
		if(!result.isOk()){
			inMessage.setError(result.getResult());
			return inMessage;
		}
		
		SimpleCassandraDao dbCf = (SimpleCassandraDao) result.getObject();
		
		dbCf.insert(inMessage.getData().get("dest"), inMessage._locale, inMessage.getData().get("value"));
		
		inMessage.setHtmlContent(dbCf.get(inMessage.getData().get("dest"), inMessage._locale));
		
		return inMessage;
	}
	
}
