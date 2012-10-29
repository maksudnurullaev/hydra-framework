package org.hydra.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.interfaces.IMessage;

public final class CaptchaUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.CaptchaUtils");
	
	public static String getCaptchaId(String queryString) {
	   	Matcher m = SessionUtils.pattern.matcher(queryString);
	   	if(m.matches())
	   		return m.group(1);
	   	return null;
	}
	
	
	public static boolean validateIfNeedsCaptcha(IMessage inMessage, WebContext context) {
		String captchaValue = Utils.getMessageDataOrNull(inMessage, Constants._captcha_value);
		if(captchaValue == null) { return(true); }; // nothing to check
		HttpSession session = context.getSession();
		int sessionValue = (Integer) session.getAttribute(Utils.getMessageDataOrNull(inMessage, Constants._appid_key) + Constants._captcha_value);
		try{
			int passedValue = Integer.parseInt(captchaValue);
			_log.debug(String.format("sessionValue(%s), captchaValue(%s)", sessionValue, captchaValue));
			if(passedValue == sessionValue){
				inMessage.getData().put(Constants._captcha_value, Constants._captcha_OK);
				return(true);
			}
		}catch (Exception e){
			_log.error(e.toString());
		}
		return false;
	}

	public static boolean isValidCaptcha(CommonMessage inMessage) {
		String captchaValue = Utils.getMessageDataOrNull(inMessage, Constants._captcha_value);
		if(captchaValue != null){
			if(captchaValue.equalsIgnoreCase(Constants._captcha_OK)){
				return(true);
			}
		}
		makeError4Captcha(inMessage);
		return(false);
	}	
	
	public static void makeError4Captcha(CommonMessage inMessage){
		List<String> err_ids = new ArrayList<String>();
		err_ids.add(Constants._captcha_value);
		inMessage.setHighlightFields(err_ids);
		inMessage.setError(MessagesManager.getText("Captcha.Incorrect", null, Utils.getMessageDataOrNull(inMessage, Constants._locale_key)));
	}
}
