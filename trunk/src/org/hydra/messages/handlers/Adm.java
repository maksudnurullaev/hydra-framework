package org.hydra.messages.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hydra.beans.WebApplications;
import org.hydra.beans.StatisticsCollector;
import org.hydra.deployers.Deployer;
import org.hydra.managers.MessagesManager;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.Result;
import org.hydra.utils.Utils;

public class Adm extends AMessageHandler { // NO_UCD	

	public static IMessage getHydraStatistics(CommonMessage inMessage) {
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._beans_statistics_collector);
		if(result.isOk() && result.getObject() instanceof StatisticsCollector){
			StatisticsCollector statisticsCollector = (StatisticsCollector) result.getObject();
			inMessage.setHtmlContent(statisticsCollector.getHtmlReport(inMessage));
			return inMessage;
		}
		inMessage.setError("Could not find statistics bean object!");
		return inMessage;
	}

	public static IMessage getApplicationsPathDefinitions(CommonMessage inMessage){
		Result result = new Result();
		BeansUtils.getWebContextBean(result, Constants._bean_hydra_web_applications);
		
		if(result.isOk() && result.getObject() instanceof WebApplications){
			WebApplications apps = (WebApplications) result.getObject();
			inMessage.setHtmlContent(apps.getDescription());
		}else{
			inMessage.setError("Could not find web applications!");
		}
		
		return inMessage;
	}	

	public static IMessage getApplications(CommonMessage inMessage){
		
		List<String> links = new ArrayList<String>();
		String htmlContent = Deployer.deployContent("[[System|Applications|All|html]]",inMessage, links);
		inMessage.setHtmlContent(htmlContent);
		inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));
		
		inMessage.setHtmlContents("admin.action.2", htmlContent);
		
		return inMessage;
	}		

	public static IMessage getCassandraConfiguration(CommonMessage inMessage){
		String result = Utils.T("template.table.with.class",
				"table.name.value",
				String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Server", inMessage._web_context.getServletContext().getServerInfo())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Protocol", inMessage._web_context.getHttpServletRequest().getProtocol())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Server Name", inMessage._web_context.getHttpServletRequest().getServerName())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Server Port", inMessage._web_context.getHttpServletRequest().getServerPort())
				+ String.format("<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>", 
						"Web Applicication ID", inMessage._web_application.getId())
				);
		inMessage.setHtmlContent(result);
		return inMessage;
	}
	// NO_UCD	
	public static IMessage getApp(CommonMessage inMessage) {
		if(!testData(inMessage, "key")) return inMessage;
		
		String appId = inMessage.getData().get("key");
		
		if(!appId.isEmpty()){
			List<String> links = new ArrayList<String>();
			String content = String.format("[[System|Application|%s|html]]", appId);
			String htmlContent = Deployer.deployContent(content,inMessage, links);
			inMessage.setHtmlContent(htmlContent);
			inMessage.setHtmlContents("editLinks", Utils.formatEditLinks(links));									
		} else {
			inMessage.setHtmlContent("...");
		}
		return inMessage;
	}

}
