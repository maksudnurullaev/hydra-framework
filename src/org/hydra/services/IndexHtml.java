package org.hydra.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.beans.WebApplication;
import org.hydra.messages.CommonMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;

public class IndexHtml extends HttpServlet {
	protected Log _log = LogFactory.getLog("org.hydra.services.IndexHtml");
	final static String index_file_str = "/index.html";
	final static String err_code = "_e_r_r_o_r_";
	final static String index_with_err = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
			+ "Transitional//EN\">\n"
			+ "<HTML>\n"
			+ "<HEAD><TITLE>ERROR</TITLE></HEAD>\n"
			+ "<BODY>\n"
			+ "<H1>Error: " + err_code + "</H1>\n" + "</BODY></HTML>";
	final static String _header_end_tag = "</head>";
	final static String _body_end_tag = "</body>";
	final static String _mobile_version_comment = "<!-- mobile.version -->";
	final static String _general_version_comment = "<!-- general.version -->";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		_log.debug("START process index.html page");

		
		// === check for mobile browser ===
		boolean mobile_browser = SessionUtils.isMobileBrowser(req);
		
		// === set output writer
		PrintWriter out = response.getWriter();
		
		// === set real path to index.html file
		File file_index = FileUtils.getRealFile(index_file_str);
		if (file_index == null || (!file_index.exists())) {
			_log.error(index_file_str + " file not found!");
			out.println(index_with_err.replaceFirst(err_code,
					index_file_str + " file not found!"));
			return;
		}		
		
		// === get responsible application
		String url = req.getRequestURL().toString() + "?" + req.getQueryString();
		WebApplication app = SessionUtils.getWebApplication(url);
		if(app == null){
			String err_string = " not found responsible application!";
			_log.error(err_string);
			out.println(index_with_err.replaceFirst(err_code, err_string));
			return;
		}
		// === set session value for browser type
		if(mobile_browser){
			SessionUtils.setSessionData(req.getSession(), "browser" , app.getId(), "mobile");
		}else{
			SessionUtils.setSessionData(req.getSession(), "browser" , app.getId(), "general");			
		}
		
		CommonMessage msg = new CommonMessage(req.getSession().getId());
		msg.setUrl(url);
		Result inResult = new Result();
		SessionUtils.setWebAppParameters(inResult, msg, app);
		_log.debug("Corresponding web application is: " + msg.getData().get("appid"));

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		StringBuffer sb = new StringBuffer();
		try {
			fis = new FileInputStream(file_index);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));

			String nextLine;
			// Read File Line By Line
			while ((nextLine = br.readLine()) != null) {
				if(nextLine.trim().isEmpty()) continue;
				String lowCaseNextLine = nextLine.toLowerCase();
				if(lowCaseNextLine.contains(_header_end_tag)){
					sb.append(addHtmlTagFiles(msg.getData().get("appid"), "_head", mobile_browser));					
				}
				if(lowCaseNextLine.contains(_body_end_tag)){
					sb.append(addHtmlTagFiles(msg.getData().get("appid"), "_body", mobile_browser));					
				}
				if(lowCaseNextLine.contains(_general_version_comment)){
					 if(!mobile_browser){
						sb.append(nextLine);						 
					 }
				} else if(lowCaseNextLine.contains(_mobile_version_comment)){
					if(mobile_browser){
						sb.append(nextLine);						
					}
				} else {
					sb.append(nextLine);											
				}
			}
			out.println(sb.toString());
			// Close the input stream
			dis.close();
		} catch (Exception e) {// Catch exception if any
			_log.error(e.getMessage());
			out.println(index_with_err.replaceFirst(err_code, e.getMessage()));
		}
		_log.debug("END process index.html page");	
	}

	private String addHtmlTagFiles(String inAppId, String htmlTag, boolean isMobile) {
		_log.debug("Try to insert " + htmlTag + " tag for: " + inAppId);
		
		if(isMobile){ 
			String fileName = htmlTag + ".mobile";
			if(FileUtils.isExistAppHtmlFile(inAppId, fileName)){
				htmlTag = fileName ;
			}else{
				_log.warn(String.format("Mobile version of html(%s) templates not exist for: %s", fileName, inAppId));				
			}
		}
		
		String content = FileUtils.getHtmlFromFile(inAppId, htmlTag);
		if(content == null){
			_log.warn(htmlTag + " not found for: " + inAppId);
			return("");
		}
		else _log.debug(htmlTag + " found for: " + inAppId);
		return("<!-- " + inAppId + " -->" + (content == null?"<!-- additional head elements not found -->":content));
	}

	private static final long serialVersionUID = 1L;

}
