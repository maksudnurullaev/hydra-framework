package org.hydra.messages.handlers.intefaces;

import org.hydra.messages.interfaces.IMessage;

/**
 * All incoming messages should be defined with:
 * <ul>
 * <li><strong>WHO</strong> will handle message</li>
 * <li><strong>WHAT KIND</strong> of object should be handled</li>
 * <li>and <strong>DESTINATION</strong> where to send back result</li>
 * </ul>
 * Message handler is <strong>WHO</strong>.
 * And also message should have Message.getData().get("type") 
 * as <strong>WHAT</strong>, Message.getData().get("key") as <strong>KIND</strong>
 * and Message.getData().get("dest") as <strong>DESTINATION</strong>
 * @author M.Nurullayev
 *
 */
public interface IMessageHandler {
	public IMessage handleMessage(IMessage inMessage);
	public boolean isValidMessage(IMessage inMessage, String ...keys);
}
