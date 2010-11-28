package org.hydra.messages.handlers;

import org.hydra.beans.WebApplications;
import org.hydra.beans.StatisticsCollector;
import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.BeansUtils;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Result;

public class AdmReports extends AMessageHandler { // NO_UCD
	

	public IMessage describeHydra(CommonMessage inMessage) {
		Result result = BeansUtils.getWebSessionBean(Constants._beans_statistics_collector);
		if(result.isOk() && result.getObject() instanceof StatisticsCollector){
			StatisticsCollector statisticsCollector = (StatisticsCollector) result.getObject();
			inMessage.setHtmlContent(statisticsCollector.getHtmlReport(inMessage));
			return inMessage;
		}
		getLog().error("Could not find statistics bean object!");
		inMessage.setError("Could not find statistics bean object!");
		return inMessage;
	}
	public IMessage describeApplications(CommonMessage inMessage){
		WebApplications apps = (WebApplications) BeansUtils.getBean(Constants._beans_hydra_applications);
		inMessage.setHtmlContent(apps.getDescription());
		return inMessage;
	}
	
	public IMessage describeServer(CommonMessage inMessage){
		String result = String.format(MessagesManager.getTemplate("template.table.with.class"),
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
}
