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
	
	
	public static boolean validateCaptcha(IMessage inMessage, WebContext context) {
		try{
			HttpSession session = context.getSession();
			int sessionValue = (Integer) session.getAttribute(inMessage.getData().get("appid") + Constants._captcha_value);
			if(inMessage.getData().containsKey(Constants._captcha_value)){
				String captchaValue = inMessage.getData().get(Constants._captcha_value);
				int passedValue = Integer.parseInt(captchaValue);
				_log.warn(String.format("sessionValue(%s), captchaValue(%s)", sessionValue, captchaValue));
				if(passedValue == sessionValue){
					inMessage.getData().put(Constants._captcha_value, Constants._captcha_OK);
					return(true);
				}
			}
		}catch (Exception e){
			_log.error(e.getMessage());
		}
		return false;
	}

	public static boolean validateCaptcha(CommonMessage inMessage) {
		if(inMessage.getData().containsKey(Constants._captcha_value)){
			String value = inMessage.getData().get(Constants._captcha_value);
			if(value.equalsIgnoreCase(Constants._captcha_OK)){
				return(true);
			}
		}
		makeCaptchaNotVerifiedMessage(inMessage);
		return(false);
	}	
	
	public static void makeCaptchaNotVerifiedMessage(CommonMessage inMessage){
		List<String> err_ids = new ArrayList<String>();
		err_ids.add(Constants._captcha_value);
		inMessage.setHighlightFields(err_ids);
		inMessage.setError(MessagesManager.getText("Captcha.Incorrect", null, inMessage.getData().get("locale")));
	}
}
