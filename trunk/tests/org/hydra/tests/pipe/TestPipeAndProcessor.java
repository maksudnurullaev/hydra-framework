package org.hydra.tests.pipe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.collectors.StatisticsCollector;
import org.hydra.messages.MessageBean;
import org.hydra.pipes.Pipe;
import org.hydra.pipes.exceptions.RichedMaxCapacityException;
import org.hydra.processors.Processor;
import org.hydra.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPipeAndProcessor{

	Log _log = LogFactory.getLog(this.getClass());

	Processor _processor1, _processor2, _processor3;
	
	StatisticsCollector _statisticsCollector;
	
	Pipe _inPipe;
	Pipe _outPipe;
	
	MessageBean _message1, _message2;


	@Before
	public void initialize() {
		
		_statisticsCollector = new StatisticsCollector();
		
		_processor1 = new Processor();
		_processor1.setName("Simple Processor#1");
		_processor1.setStatisticsCollector(_statisticsCollector);
		
		_processor2 = new Processor();
		_processor2.setName("Simple Processor#2");
		_processor2.setStatisticsCollector(_statisticsCollector);

		_processor3 = new Processor();
		_processor3.setName("Simple Processor#3");
		_processor3.setStatisticsCollector(_statisticsCollector);
		
		_inPipe = new Pipe();
		_inPipe.setName("Test inPipe");
		_inPipe.setStatisticsCollector(_statisticsCollector);

		_outPipe = new Pipe();
		_outPipe.setName("Test outPipe");
		_outPipe.setStatisticsCollector(_statisticsCollector);

		_message1 = new MessageBean();
		_message1.setSessionID("Test Message1");
		
		_message2 = new MessageBean();
		_message2.setSessionID("Test Message2");
	}

	@Test
	public void test_common() {
		_processor1.setInPipe(_inPipe);
		_processor1.setOutPipe(_outPipe);

		Assert.assertSame(_inPipe, _processor1.getInPipe());
		Assert.assertNotSame(_outPipe, _processor1.getInPipe());

		Assert.assertSame(_outPipe, _processor1.getOutpipe());
		Assert.assertNotSame(_inPipe, _processor1.getOutpipe());
		
		// Test default values
		Assert.assertTrue(_inPipe.getMaxCapacity() == Constants._unlimited);
		Assert.assertTrue(_outPipe.getMaxCapacity() == Constants._unlimited);
		
		Assert.assertTrue(_processor1.getState() == Processor.ProcessorStatus.WAITING);
		Assert.assertTrue(_processor2.getState() == Processor.ProcessorStatus.WAITING);
		Assert.assertTrue(_processor3.getState() == Processor.ProcessorStatus.WAITING);
	}

	@Test
	public void test_simple_add_pipe() {
		try {
			_inPipe.setMessage(_message1);
		} catch (RichedMaxCapacityException e) {
			_log.error("Could not add a message to pipe!");
			Assert.assertTrue(false);
		}
		Assert.assertSame(_message1, _inPipe.getMessage());
	}

	@Test
	public void test_simple_pipe_functionality() {
		try {
			_inPipe.setMessage(_message1);
		} catch (RichedMaxCapacityException e) {
			_log.error("Could not add a message to pipe!");
			Assert.assertTrue(false);
		}
		Assert.assertTrue(_inPipe.getMessagesCount() == 1);
		Assert.assertSame(_message1, _inPipe.getMessage());
		Assert.assertTrue(_inPipe.getMessagesCount() == 0);
	}

	@Test
	public void test_simple_pipe_functionality2() {
		Assert.assertTrue(_inPipe.getMessagesCount() == 0);
		try {
			_inPipe.setMessage(_message1);
			_inPipe.setMessage(_message2);
		} catch (RichedMaxCapacityException e) {
			_log.error("Could not add a message to pipe!");
			Assert.assertTrue(false);
		}
		Assert.assertTrue(_inPipe.getMessagesCount() == 2);
		Assert.assertSame(_message1, _inPipe.getMessage());
		Assert.assertTrue(_inPipe.getMessagesCount() == 1);
		Assert.assertSame(_message2, _inPipe.getMessage());
		Assert.assertTrue(_inPipe.getMessagesCount() == 0);
		
	}

	@Test(expected = RichedMaxCapacityException.class)
	public void test_pipe_with_exeption() throws RichedMaxCapacityException {
		int messageCount = 10;
		int counter = 0;
		MessageBean message = null;
		_inPipe.setMaxCapacity(messageCount);
		// Test with (messageCount x 2) messages
		for (int i = 0; i < (messageCount * 2); i++) {
			counter++;
			message = new MessageBean();
			message.setSessionID(String.format("Message #%d", i));
			_inPipe.setMessage(message);
		}
	}

}
