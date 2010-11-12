package org.hydra.tests.utils;

import java.lang.reflect.Method;

import org.hydra.messages.MessageBean;
import org.hydra.messages.interfaces.IMessage;


public class Just4Run {

	
	public static void main(String[] args) {
		String methodName = "SessionInfo2";
		MessageBean message = new MessageBean();
		try {
			Class<?> c = Class.forName("org.hydra.messages.handlers.RunTestsMessageHandler");
			Class<?> parameterTypes[] = new Class[1];
			parameterTypes[0] = IMessage.class;
			Method m = c.getMethod(methodName, parameterTypes);
			Object result = m.invoke(c.newInstance(), message);
			if(result instanceof IMessage){
				System.out.println("Method called properly!");
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");	
	}
}