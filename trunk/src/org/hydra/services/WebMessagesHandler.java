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
import org.hydra.utils.SessionManager;
import org.hydra.utils.abstracts.ALogger;

public class WebMessagesHandler extends ALogger {

	public List<MessageBean> sendMessage(MessageBean inMessage) throws RichedMaxCapacityException{
		inMessage.setSessionID(Constants.getCurrentSessionID());
		
		// -1. Attach session's data
		SessionManager.attachSessionData(inMessage, WebContextFactory.get().getSession());
		
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
		Result result = SessionManager.getBean(Constants._beans_main_input_pipe);
		if(result.isOk() && result.getObject() instanceof Pipe){
			((Pipe)result.getObject()).setMessage(inMessage);
		}else{
			getLog().fatal("Could not initialize " + Constants._beans_main_input_pipe + " object");
			inMessage.setError("Could not initialize " + Constants._beans_main_input_pipe + " object");
			
			SessionManager.detachSessionData(inMessage);
			_result.add(inMessage);
			return _result;
		}
		//Constants.getMainInputPipe().setMessage(inMessage);
		
		// 3. Setup waiting condition values
		long startTime = System.currentTimeMillis();
		
		// 4. Waiting for response
		getLog().debug("START: Waiting for response...");
		result = SessionManager.getBean(Constants._beans_main_message_collector);
		if(result.isOk() && result.getObject() instanceof MessagesCollector){
			MessagesCollector messagesCollector = (MessagesCollector)result.getObject();

			while(!messagesCollector.hasNewMessages(Constants.getCurrentSessionID())){
				if(System.currentTimeMillis() - startTime > Constants._max_response_wating_time){
					
					inMessage.setError("Waiting time limit is over...");				
					getLog().error("Waiting time limit is over...");
					
					SessionManager.detachSessionData(inMessage);
					_result.add(inMessage);				
					return _result;
				}
				Thread.yield();
			}
			getLog().debug("END: Waiting for response...");
			// 5. If response messages exist	
			IMessage messageBean = null;
			while((messageBean = messagesCollector.getMessage(Constants.getCurrentSessionID())) != null){
				
				SessionManager.detachSessionData(messageBean);
				_result.add((MessageBean)messageBean);
			}
			return _result;		
		}
		
		SessionManager.detachSessionData(inMessage);
		inMessage.setError("Could not initialize " + Constants._beans_main_message_collector + " object");
		_result.add(inMessage);
		
		return _result;
		
	}
	
}
