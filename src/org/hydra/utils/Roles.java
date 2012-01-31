package org.hydra.utils;

import org.hydra.messages.CommonMessage;
import org.hydra.utils.ErrorUtils.ERROR_CODES;

public final class Roles {
	public static final int USER_ADMINISTRATOR = 9;
	public static final int USER_EDITOR = 2;
	public static final int USER_PUBLISHER = 1;
	public static final int USER_REGISTERED = 0;
	
	public static boolean roleNotLessThen(int inRoleLevel, CommonMessage inMessage) {
		//TODO REMOVE IF NEES FULL ACCESS!
		//if(inApplicationID != null) return true;
		String appId = inMessage.getData().get("_appid");
		String userId = inMessage.getData().get("_user");
		if(appId == null || appId.length() == 0) return false;
		if(userId == null || userId.length() == 0) return false;
		
		if(userId.startsWith("+++")) return true; // super user
	
		int roleLevel = -1;
		StringWrapper sWrapper = new StringWrapper();
		ERROR_CODES err = DBUtils.getValue(appId, "User", userId, "tag", sWrapper);
		if(err == ERROR_CODES.NO_ERROR && !sWrapper.getString().isEmpty()){
			roleLevel = 0; // just registered user level
			String rolesStr = sWrapper.getString();
			if(rolesStr != null && rolesStr.length() > 0){
				if(rolesStr.contains("User.Administrator")){
					roleLevel = USER_ADMINISTRATOR;
				}else if(rolesStr.contains("User.Publisher")){
					roleLevel = USER_PUBLISHER;
				}else if(rolesStr.contains("User.Editor")){
					roleLevel = USER_EDITOR;
				}else if(rolesStr.contains("User")){
					roleLevel = USER_REGISTERED;
				}
			}
		}
		return(roleLevel >= inRoleLevel);
	}
}
