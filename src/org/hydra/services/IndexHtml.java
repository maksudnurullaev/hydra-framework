package org.hydra.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hydra.messages.CommonMessage;
import org.hydra.utils.FileUtils;
import org.hydra.utils.Result;
import org.hydra.utils.SessionUtils;

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
	final static String _header_tag = "</HEAD>";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		_log.debug("START process index.html page");
		
		String index_file_path = getServletContext().getRealPath(index_file);
		PrintWriter out = response.getWriter();
		if (index_file_path == null) {
			_log.error(index_file + " file not found!");
			out.println(index_with_err.replaceFirst(err_code,
					index_file + " file not found!"));
			return;
		}
		
		CommonMessage msg = new CommonMessage();
		msg.setUrl(req.getRequestURL().toString() + "?" + req.getQueryString());
		Result inResult = new Result();
		SessionUtils.setWebAppParameters(inResult,msg, req.getServletContext());		
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

			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				sb.append(updateIfHtmlHead(strLine, msg.getData().get("appid"), req.getServletContext()));
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

	private String updateIfHtmlHead(String strLine, String inAppId, ServletContext inServletContext) {
		if(strLine == null || strLine.trim().isEmpty()){
			return "";
		}
		String strLine2 = strLine.toLowerCase();
		if(strLine2.endsWith(_header_tag)
				|| strLine2.endsWith(_header_tag.toLowerCase())){
			_log.debug("Try to insert head.html before </head> tag for: " + inAppId);			
			String header = FileUtils.getFromHtmlFile(inAppId, "head", inServletContext);
			if(header == null) _log.warn("Additional head.html file for application not found!");
			else _log.debug("head.html file found for: " + inAppId);
			return("<!-- " + inAppId + " -->" + (header == null?"<!-- additional head elements not found -->":header) + strLine);
		}	
		return(strLine);
	}

	private static final long serialVersionUID = 1L;

}
