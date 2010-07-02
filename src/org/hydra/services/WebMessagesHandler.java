package org.hydra.services;

import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.WebContextFactory;
import org.hydra.collectors.MessagesCollector;
import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.abstracts.ALogger;

public class WebMessagesHandler extends ALogger {

	public List<MessageBean> sendMessage(MessageBean inMessage) throws RichedMaxCapacityException{
		
		// -1. Attach session's data
		getLog().debug("WebContextFactory.get(): " + WebContextFactory.get());
		SessionUtils.attachIMessageSessionData(inMessage, WebContextFactory.get());
		
		// 0. Debug part
		if(getLog().isDebugEnabled()){
			getLog().debug("\nMESSAGE BEAN handler: " + inMessage.getData().get(IMessage._data_handler));
			if(inMessage.getData() != null)
				for(String key:inMessage.getData().keySet())
					getLog().debug(String.format("\n\tMESSAGE BEAN's data: %s = %s", key, inMessage.getData().get(key)));
			else
				getLog().debug("\n\tMESSAGE BEAN's data: null");
		}
		
		// 1. Create array for MessageBean's
		getLog().debug("Create array for MessageBean's...");
		List<MessageBean> _result = new ArrayList<MessageBean>();
		
		// 2. Send message to default pipe
		getLog().debug("Send message to default pipe...");
		Result result = BeansUtils.getWebSessionBean(Constants._beans_main_input_pipe);
		if(result.isOk() && result.getObject() instanceof Pipe){
			((Pipe)result.getObject()).setMessage(inMessage);
		}else{
			getLog().fatal("Could not initialize " + Constants._beans_main_input_pipe + " object");
			inMessage.setError("Could not initialize " + Constants._beans_main_input_pipe + " object");
			
			SessionUtils.detachIMessageSessionData(inMessage);
			_result.add(inMessage);
			return _result;
		}
		//Constants.getMainInputPipe().setMessage(inMessage);
		
		// 3. Setup waiting condition values
		long startTime = System.currentTimeMillis();
		
		// 4. Waiting for response
		getLog().debug("START: Waiting for response...");
		result = BeansUtils.getWebSessionBean(Constants._beans_main_message_collector);
		if(result.isOk() && result.getObject() instanceof MessagesCollector){
			MessagesCollector messagesCollector = (MessagesCollector)result.getObject();

			while(!messagesCollector.hasNewMessages(inMessage.getData().get(IMessage._data_sessionId))){
				if(System.currentTimeMillis() - startTime > Constants._max_response_wating_time){
					
					inMessage.setError("Waiting time limit is over...");				
					getLog().error("Waiting time limit is over...");
					
					SessionUtils.detachIMessageSessionData(inMessage);
					_result.add(inMessage);				
					return _result;
				}
				Thread.yield();
			}
			getLog().debug("END: Waiting for response...");
			// 5. If response messages exist	
			IMessage messageBean = null;
			while((messageBean = messagesCollector.getMessage(inMessage.getData().get(IMessage._data_sessionId))) != null){
				
				SessionUtils.detachIMessageSessionData(messageBean);
				_result.add((MessageBean)messageBean);
			}
			return _result;		
		}
		
		SessionUtils.detachIMessageSessionData(inMessage);
		inMessage.setError("Could not initialize " + Constants._beans_main_message_collector + " object");
		_result.add(inMessage);
		
		return _result;
		
	}
	
}
