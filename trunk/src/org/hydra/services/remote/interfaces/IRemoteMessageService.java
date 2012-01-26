package org.hydra.services.remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.hydra.messages.interfaces.IMessage;

public interface IRemoteMessageService extends Remote {
	public Object[] processMessage(IMessage inMessage) throws RemoteException;
}
