package org.hydra.utils;

import java.util.ArrayList;
import java.util.List;

import org.hydra.messages.interfaces.IMessage;

public final class Roles {
	public static final String USER_ADMINISTRATOR = "User.Administrator";
	
	public static boolean isUserHasRole(String inRole, IMessage inMessage) {
		if(inRole == null) return(false);
		System.out.println("Role request: " + inRole);
		if(inMessage.getUrl().startsWith("http://127.0.0.1")) { 
			return(inRole.equals(USER_ADMINISTRATOR)); 
		} else {
			Roles.getUserRoles(inMessage);
			List<String> roles = Roles.getUserRoles(inMessage);
			for(String role: roles){
				if(role.toLowerCase() == inRole){
					return(true);
				}
			}
		}		
		return(false);
	}

	public static List<String> getUserRoles(IMessage inMessage) {
		ArrayList<String> roles = new ArrayList<String>();
		String roles_as_string = Utils.getMessageDataOrNull(inMessage, Constants._roles_key);
		String user = Utils.getMessageDataOrNull(inMessage, Constants._userid_key);
		
		// user NOT logged in
		if(user == null || user.isEmpty()){
			roles.add("NonRegistered");
			return(roles);
		}
		// ESLE
		roles.add("Registered");
		if(roles_as_string != null && !roles_as_string.isEmpty()){
			String[] temp_roles = roles_as_string.split(",");
			for(String role:temp_roles)
				roles.add(role);
		}	
		return(roles);
	}
}
