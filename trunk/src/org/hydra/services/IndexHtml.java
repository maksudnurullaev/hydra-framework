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
import org.hydra.utils.Utils;

public class IndexHtml extends HttpServlet {
	protected Log _log = LogFactory.getLog("org.hydra.services.IndexHtml");
	final static String index_file = "/index.html";
	final static String err_code = "_e_r_r_o_r_";
	final static String index_with_err = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
			+ "Transitional//EN\">\n"
			+ "<HTML>\n"
			+ "<HEAD><TITLE>ERROR</TITLE></HEAD>\n"
			+ "<BODY>\n"
			+ "<H1>Error: " + err_code + "</H1>\n" + "</BODY></HTML>";
	final static String _header_end_tag = "</head>";
	final static String _body_end_tag = "</body>";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		_log.debug("START process index.html page");
		
		String index_file_path = Utils.getRealPath(index_file);
		PrintWriter out = response.getWriter();
		if (index_file_path == null) {
			_log.error(index_file + " file not found!");
			out.println(index_with_err.replaceFirst(err_code,
					index_file + " file not found!"));
			return;
		}
		
		String url = req.getRequestURL().toString() + "?" + req.getQueryString();
		WebApplication app = SessionUtils.getWebApplication(url);
		CommonMessage msg = new CommonMessage();
		msg.setUrl(url);
		Result inResult = new Result();
		SessionUtils.setWebAppParameters(inResult, msg, app);		
		if(!inResult.isOk()){
			_log.error(index_file + msg.getUrl() + " - not found responsible application!");
			out.println(index_with_err.replaceFirst(err_code,
					index_file + msg.getUrl() + " - not found responsible application!"));
			return;
		}
		_log.debug("Corresponding web application is: " + msg.getData().get("appid"));

		File file = new File(index_file_path);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		StringBuffer sb = new StringBuffer();
		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));

			String nextLine;
			// Read File Line By Line
			while ((nextLine = br.readLine()) != null) {
				if(nextLine.trim().isEmpty()) continue;
				if(nextLine.toLowerCase().endsWith(_header_end_tag)){
					sb.append(getAdditional(msg.getData().get("appid"), "_head"));					
				}
				if(nextLine.toLowerCase().endsWith(_body_end_tag)){
					sb.append(getAdditional(msg.getData().get("appid"), "_body"));					
				}
				sb.append(nextLine);
			}
			out.println(sb.toString());
			// Close the input stream
			dis.close();
		} catch (Exception e) {// Catch exception if any
			_log.error(e.getMessage());
			out.println(index_with_err.replaceFirst(err_code,
					e.getMessage()));
		}
		_log.debug("END process index.html page");	
	}

	private String getAdditional(String inAppId, String htmlEndTagType) {
		_log.debug("Try to insert " + htmlEndTagType + " tag for: " + inAppId);			
		String content = FileUtils.getFromHtmlFile(inAppId, htmlEndTagType);
		if(content == null){
			_log.warn(htmlEndTagType + " not found for: " + inAppId);
			return("");
		}
		else _log.debug(htmlEndTagType + " found for: " + inAppId);
		return("<!-- " + inAppId + " -->" + (content == null?"<!-- additional head elements not found -->":content));
	}

	private static final long serialVersionUID = 1L;

}
