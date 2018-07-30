package com.plusoft.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import com.plusoft.factory.*;
import com.plusoft.util.*;
import com.plusoft.dao.*;
import com.plusoft.db.DataBase;

public class BaseService {
	
    List<BaseDao> daoList = new ArrayList<BaseDao>();

    protected void addTransaction(BaseDao dao)
    {
    	daoList.add(dao);
    }
	
	Connection conn;
	protected Connection startTransaction() {
		conn = DataBase.getConnection();	
		for(BaseDao dao : daoList) {
			dao.db.conn = conn;
		}
		return conn;
	}
	
	protected void closeTransaction() throws SQLException {
		if(conn != null) {			
			conn.close();
			conn = null;
			for(BaseDao dao : daoList) {
				dao.db.conn = null;
			}
		}
	}	

}
