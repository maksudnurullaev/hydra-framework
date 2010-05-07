package org.hydra.db.server;

import org.hydra.db.beans.ColumnBean;
import org.hydra.messages.handlers.AdminMessageHandler;
import org.hydra.messages.interfaces.IMessage;
import org.hydra.utils.SessionManager;

public class CassandraRealPath {
	public String ksp;
	public String cf;
	public String key;
	public String col;
	
	public CassandraRealPath(String inKsp, String inCf, String inKey, String inCol){
		ColumnBean columnBean = 
			SessionManager.getCassandraDescriptor().getColumnBean(inKsp, inCf, inCol);
		
		ksp = inKsp;
		key = inKey;
		
		if(columnBean.getSuper().equals(ColumnBean.SUPER_COLUMN)){
			cf = inCf;
			col = ColumnBean.SUPER_COLUMN;
		} else if(columnBean.getSuper().equals(ColumnBean.SUPER_LINK)){
			col = inCol;
			cf = SessionManager.getCassandraDescriptor().getKeyspace(inKsp).getLinkTableName();
		}
		
	}

	public CassandraRealPath(IMessage inMessage) {
		ColumnBean columnBean = 
			SessionManager.getCassandraDescriptor().getColumnBean(
					inMessage.getData().get(IMessage._data_cs_ksp), 
					inMessage.getData().get(IMessage._data_cs_cf), 
					inMessage.getData().get(IMessage._data_cs_col));	

		ksp = inMessage.getData().get(IMessage._data_cs_ksp);
		key = inMessage.getData().get(IMessage._data_cs_key);
		
		if(columnBean.getSuper().equals(ColumnBean.SUPER_COLUMN)){
			cf = inMessage.getData().get(IMessage._data_cs_cf);
			col = ColumnBean.SUPER_COLUMN;
		} else if(columnBean.getSuper().equals(ColumnBean.SUPER_LINK)){
			col = inMessage.getData().get(IMessage._data_cs_col);
			cf = SessionManager.getCassandraDescriptor().getKeyspace(
					inMessage.getData().get(IMessage._data_cs_ksp)
					).getLinkTableName();
		}
		
	}
}
