package org.hydra.utils;

import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

public final class Roles {
	public static final String USER_ADMINISTRATOR = "User.Administrator";
	public static final String USER_EDITOR        = "User.Editor";
	public static final String USER_PUBLISHER     = "User.Publisher";
	public static final String USER_REGISTERED    = "User";
	
	public static boolean isUserHasRole(String inRole, IMessage inMessage) {
		if(inRole == null) return(false);
		String appId = inMessage.getData().get("_appid");
		String userId = inMessage.getData().get("_userid");		
		String rolesStr = null;
		if(inMessage.getUrl().startsWith("http://127.0.0.1")) { rolesStr = USER_ADMINISTRATOR; }
		else {
			StringWrapper sWrapper = new StringWrapper();
			ERROR_CODES err = DBUtils.getValue(appId, "User", userId, "tag", sWrapper);
			if(err == ERROR_CODES.NO_ERROR && !sWrapper.getString().isEmpty()){
				rolesStr = sWrapper.getString();
			}
		}
		if(rolesStr != null && rolesStr.length() > 0){
			return(rolesStr.toLowerCase().contains(inRole.toLowerCase()));
		}		
		return(false);
	}
}
