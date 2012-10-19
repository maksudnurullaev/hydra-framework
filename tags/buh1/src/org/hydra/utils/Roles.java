package org.hydra.utils;

import java.util.List;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

public final class Roles {
	public static final String USER_ADMINISTRATOR = "User.Administrator";
	public static final String USER_EDITOR        = "User.Editor";
	public static final String USER_PUBLISHER     = "User.Publisher";
	public static final String USER_REGISTERED    = "User";
	
	public static boolean isUserHasRole(String inRole, IMessage inMessage) {
		if(inRole == null) return(false);
		if(inMessage.getUrl().startsWith("http://127.0.0.1")) { return(true); }
		else {
			SessionUtils.getSessionRoles(inMessage);
			List<String> roles = SessionUtils.getSessionRoles(inMessage);
			for(String role: roles){
				if(role.toLowerCase() == inRole){
					return(true);
				}
			}
		}		
		return(false);
	}
}
