package org.hydra.messages.handlers;

import org.hydra.messages.CommonMessage;
import org.hydra.messages.handlers.abstracts.AMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.Constants;
import org.hydra.utils.MessagesManager;
import org.hydra.utils.Utils;

public class Administration extends AMessageHandler { // NO_UCD
	static final String _destination = "_adm._subdiv";

	public IMessage describeReports(CommonMessage inMessage) {
		String result = Utils.makeJSLink("Hydra",
				String.format("handler:'%s'", "AdmReports"),
				String.format("action:'%s'", "describeHydra"),
				String.format("dest:'%s'", _destination));
		result += " &bull; "
				+ Utils.makeJSLink(MessagesManager.getText("text.Applications",
						null, inMessage.getData().get(Constants._data_locale)),
						String.format("handler:'%s'", "AdmReports"), String
								.format("action:'%s'", "describeApplications"),
						String.format("dest:'%s'", _destination));
		result += " &bull; "
				+ Utils.makeJSLink(MessagesManager.getText("text.Server", null,
						inMessage.getData().get(Constants._data_locale)),
						String.format("handler:'%s'", "AdmReports"), String
								.format("action:'%s'", "describeServer"),
						String.format("dest:'%s'", _destination));
		result += String.format(
				MessagesManager.getTemplate("template.html.hr.divId.dots"),
				_destination);
		inMessage.setHtmlContent(result);

		return inMessage;
	}

	public IMessage describeCassandra(CommonMessage inMessage) {
		/*
		CassandraAccessorBean cassandraAccessorBean = BeansUtils.getAccessor();
		if (!cassandraAccessorBean.isValid()) {
			inMessage.setError("Could not access to cassandra!");
			return inMessage;
		}
		String result = String
				.format(MessagesManager
						.getTemplate("template.table.with.class"),
						"table.name.value",
						String.format(
								"<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>",
								"Cluster Name",
								cassandraAccessorBean.getClusterName())
								+ String.format(
										"<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>",
										"Host", cassandraAccessorBean.getHost())
								+ String.format(
										"<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>",
										"Post", cassandraAccessorBean.getPort())
								+ String.format(
										"<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>",
										"Protovol Version",
										cassandraAccessorBean
												.getProtocolVersion())
								+ String.format(
										"<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>",
										"Keyspaces",
										getKeyspaces(cassandraAccessorBean))
								+ String.format(
										"<tr><td class='tr'><u>%s</u>:</td><td>%s</td></tr>",
										"Column Families",
										getColumnFamiliesJSLinks(cassandraAccessorBean)));
		result += String.format(
				MessagesManager.getTemplate("template.html.hr.divId.dots"),
				_destination);
		inMessage.setHtmlContent(result);
		*/
		inMessage.setHtmlContent("IMessage org.hydra.messages.handlers.Administration.describeCassandra");
		return inMessage;
	}

//	private String getColumnFamiliesJSLinks(
//			CassandraAccessorBean cassandraAccessorBean) {
//		Set<String> cfNamesSet = cassandraAccessorBean.getDescriptor()
//				.getColFamilies().keySet();
//		String result = "";
//		for (String cfName : cfNamesSet) {
//			if (result.length() > 0)
//				result += ", ";
//			result += Utils.makeJSLink(
//					cfName,
//					String.format("handler:'%s'",
//							AdmCassandra.class.getSimpleName()),
//					String.format("dest:'%s'", _destination),
//					String.format("action:'%s'", "getCFDescription"),
//					String.format("key:'%s'", cfName));
//		}
//		return result;
//	}

//	private String getKeyspaces(CassandraAccessorBean inAccessor) {
//		StringBuffer result = new StringBuffer();
//		int counter = 0;
//
//		for (KsDef keyspaceName : inAccessor.getServerKeyspaces()) {
//			if (counter++ != 0)
//				result.append(", ");
//			result.append(keyspaceName);
//		}
//		return result.toString();
//	}
}
