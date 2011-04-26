package org.hydra.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.hydra.beans.WebApplications;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;

public final class SessionUtils {
	private static Log _log = LogFactory.getLog("org.hydra.utils.SessionUtils");

	/**
	 * Attache session data (locale, userId and etc.)
	 * 
	 * @param inMessage
	 * @param inSession
	 */
	public static Result attachSessionData(Result result,
			CommonMessage inMessage, WebContext inWebContext) {
		// 1. set web context
		inMessage._web_context = WebContextFactory.get();
		if (inMessage._web_context == null) {
			result.setResult("Could not find web context!");
			result.setResult(false);
			return result;
		}
		// 2. set web application
		setWebApplication(result, inMessage, inMessage._web_context);
		if (!result.isOk())
			return result;
		// 3. set session id
		inMessage._session_id = inMessage._web_context.getSession().getId();
		// 4. set locale
		getSessionData(result, inMessage);

		_log.debug("Web Application id: " + inMessage._web_application.getId());
		_log.debug("Web Application locale: " + inMessage._locale);

		return result;
	}

	public static void setLocaleID(Result result, CommonMessage inMessage) {
		if (inMessage == null || inMessage._web_application == null) {
			result.setResult("Could not define LocalID");
			result.setResult(false);
			return;
		}
		result.setObject(inMessage._web_application.getId()
				+ Constants._data_locale);
		result.setResult(true);
	}

	private static void generateSessionDataKey(Result result,
			CommonMessage commonMessage, String key) {
		if (commonMessage._web_application == null || key == null) {
			result.setObject("Could not generate Session Data Key!");
			_log.warn("Could not generate Session Data Key!");
			result.setResult(false);
			return;
		}
		result.setObject(commonMessage._web_application.getId() + key);
		result.setResult(true);
	};

	private static void getSessionData(Result result,
			CommonMessage commonMessage) {
		generateSessionDataKey(result, commonMessage, Constants._data_locale);
		if (!result.isOk())
			return;

		setLocaleID(result, commonMessage);

		String localeKey = commonMessage._web_application.getId()
				+ Constants._data_locale;
		String localeValue = (String) commonMessage._web_context.getSession()
				.getAttribute(localeKey);
		if (localeValue == null)
			localeValue = MessagesManager.getTextManager().getDefaultLocale();

		commonMessage._locale = localeValue;

		result.setResult(true);
	};

	public static void setWebApplication(Result result,
			CommonMessage inMessage, WebContext webContext) {
		if (inMessage == null || webContext == null) {
			result.setResult("CommonMessage or WebContext equal NULL!");
			result.setResult(false);
			return;
		}

		String urlPrefix = webContext.getHttpServletRequest().getScheme()
				+ "://" + webContext.getHttpServletRequest().getServerName();

		BeansUtils.getWebContextBean(result,
				Constants._beans_hydra_applications);
		if (!result.isOk() || !(result.getObject() instanceof WebApplications))
			return;

		WebApplications webApplications = (WebApplications) result.getObject();
		inMessage._web_application = webApplications
				.getValidApplication(urlPrefix);

		if (inMessage._web_application == null) {
			result.setResult("Could not initialize WebApplication object!");
			result.setResult(false);
		} else
			result.setResult(true);
	};

	public static void setSessionData(
			Result inResult,
			CommonMessage inCommonMessage,
			String inKey,
			Object inValue) {

		if (inCommonMessage == null
					|| inCommonMessage._web_context == null
					|| inCommonMessage._web_application == null
					|| inKey == null
					|| inValue == null) {
			inResult.setResult(false);
			inResult.setResult("Invalid session!");
		} else {
			// try {
			inCommonMessage._web_context.getSession().setAttribute(
					inCommonMessage._web_application.getId() + inKey,
					inValue);
			inResult.setResult(true);
			// } catch (Exception e) {
			// inResult.setResult(e.getMessage());
			// inResult.setResult(false);
			// }
		}
	};

}
