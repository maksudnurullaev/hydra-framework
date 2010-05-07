package org.hydra.tests.threadpool;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.collectors.MessagesCollector;
import org.hydra.collectors.StatisticsCollector;
import org.hydra.executors.Executor;
import org.hydra.messages.MessageBean;
import org.hydra.messages.handlers.MessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.processors.Processor;
import org.hydra.processors.exceptions.NullProcessorException;
import org.hydra.processors.interfaces.IProcessor.ProcessorStatus;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Test;

public class TestThreadPool {
	Log _log = LogFactory.getLog(this.getClass());

	Processor _processor11, _processor12, _processor13;

	Pipe _main_inPipe;
	Pipe _outPipe;

	MessageBean _message1, _message2;

	StatisticsCollector _statisticsCollector;	
	MessagesCollector _messagesCollector;
	
	@Test
	public void test_processors_and_messages() {
		_statisticsCollector = new StatisticsCollector();
		_messagesCollector = new MessagesCollector();

		_processor11 = new Processor();
		_processor11.setName("Processor#1.1");
		_processor11.setStatisticsCollector(_statisticsCollector);
		_processor11.setMessageCollector(_messagesCollector);

		_processor12 = new Processor();
		_processor12.setName("Processor#1.2");
		_processor12.setStatisticsCollector(_statisticsCollector);
		_processor12.setMessageCollector(_messagesCollector);
		
		_processor13 = new Processor();
		_processor13.setName("Processor#1.3");
		_processor13.setStatisticsCollector(_statisticsCollector);		
		_processor13.setMessageCollector(_messagesCollector);

		
		_main_inPipe = new Pipe();
		_main_inPipe.setName("Pipe1");
		_main_inPipe.setStatisticsCollector(_statisticsCollector);

		_outPipe = new Pipe();
		_outPipe.setName("Pipe2");
		_outPipe.setStatisticsCollector(_statisticsCollector);
		
		_processor11.setExecutor(Executor.getInstance());
		_processor12.setExecutor(Executor.getInstance());
		_processor13.setExecutor(Executor.getInstance());
		
		try {
			_main_inPipe.setProcessor(_processor11);
			_main_inPipe.setProcessor(_processor12);
			_main_inPipe.setProcessor(_processor13);
						
		} catch (NullProcessorException e1) {
			e1.printStackTrace();
		}

		_processor11.setOutPipe(_outPipe);
		_processor12.setOutPipe(_outPipe);		
		_processor13.setOutPipe(_outPipe);		

		int messageCount = 10000;

		try {
			MessageBean message = null;
			for (int i = 0; i < messageCount; i++) {
				message = new MessageBean();
				message.setData(new HashMap<String, String>());
				message.getData().put(IMessage._data_handler,"Message");
				message.getData().put(MessageHandler._action, IMessage._data_what_html_content);
				message.getData().put(MessageHandler._key, "home.context");
				message.getData().put(IMessage._data_sessionId, String.format("Test Message #%d", i));
				_main_inPipe.setMessage(message);
			}
		} catch (RichedMaxCapacityException e) {
			e.printStackTrace();
		}
		
		while(_processor11.getState() != ProcessorStatus.WAITING
				|| _processor12.getState() != ProcessorStatus.WAITING
				|| _processor13.getState() != ProcessorStatus.WAITING){
			// System.out.println("WAITING...");
			Thread.yield();
			
		}
		
		Assert.assertTrue(messageCount == (
				_statisticsCollector.getMessagesTotal4(_processor11.getName())
				+ _statisticsCollector.getMessagesTotal4(_processor12.getName())
				+ _statisticsCollector.getMessagesTotal4(_processor13.getName())));
		
		System.out.println(_statisticsCollector.getTxtReport());
	}
}
