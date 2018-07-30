package com.plusoft.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import com.plusoft.db.DataBase;
import com.plusoft.util.Convert;
import com.plusoft.util.StringUtil;;

public class BaseDao {
    
	protected String getTableName() {
		return "";
	}
	protected String getTableIdField() {
		return getIdField();
	}
	protected String getIdField() {
		return "id";
	}	
	protected String getInsertSql() {		
		return createInsertSql(getTableName()); 
	}
	protected String getUpdateSql() {
		return createUpdateSql(getTableName(), getTableIdField()); 
	}
	protected String getDeleteSql() {
		return "delete from " + getTableName() + " where "+getTableIdField()+" = @id";
	}
	protected String getSelectSql() {
		return "select * from " + getTableName();
	}
	protected String getFindByIdSql(String id) {			
		return getSelectSql() +" where "+ getTableIdField()+" = '"+id+"'";
	}
	protected boolean getUpdateByDelete() {
		return true;
	}	
	protected boolean getIdAutoincrement() {
		return false;
	}	
	
	public Object insert(HashMap o) throws Exception {		
		Object id = autoId(o, getIdField());		
    	execute(getInsertSql(), o);
    	
        //如果是自增长id
        if (getIdAutoincrement())
        {
            id = db.selectScalar("select @@IDENTITY");
            //int id = (Integer)db.selectScalar("select @@IDENTITY"));			//sqlserver
            //int id = (Integer)db.selectScalar("select last_insert_id()"));	//mysql
//        	rs = stmt.getGeneratedKeys();  
//          generatedKey = rs.next()? (Serializable) rs.getObject(1) : generatedKey;
        }        
    	
        return id;
	}
	
	public void delete(String id) throws Exception
    {    
        HashMap param = new HashMap();
        param.put("id", id);
        execute(getDeleteSql(), param);
    }
	
	public void update(HashMap o) throws Exception {
		String id = Convert.toString(o.get(getIdField()));
		if(!StringUtil.isNullOrEmpty(id)) {
			HashMap to = findById(id);
			if(to == null) return;
			copyFrom(to, o);
			
			if(getUpdateByDelete() && !getIdAutoincrement()) {
				delete(id);
				insert(to);				
			}else {			
		    	execute(getUpdateSql(), to);
			}
		}
	}	
	
	public HashMap findById(String id) throws Exception
    {
		return selectFirst(getFindByIdSql(id)); 		
    }
	
	public ArrayList findAll() throws Exception
    {		
		return findAll("");
    }
	
	public ArrayList findAll(String sqlSuffix) throws Exception 
    {
	    return findAll(sqlSuffix, -1, 0);
    }
	
	public ArrayList findAll(String sqlSuffix, int pageIndex, int pageSize) throws Exception 
    {
        if (sqlSuffix == null) sqlSuffix = "";
        String sql = getSelectSql() + " "+ sqlSuffix;
	    
	    return selectPage(sql, pageIndex, pageSize);
    }
	
	public int getCount() throws Exception {
		return getCount("");
	}
	
	public int getCount(String where) throws Exception {
		String sql = "select count(1) from "+ getTableName() ;
		if(!StringUtil.isNullOrEmpty(where)) {
			sql += " where " + where;
		}
		return Convert.toInt(db.selectScalar(sql));		
	}	
	
	protected Object modelFrom(HashMap o) {
		return o;
	}
	
	///////////////////////////////////////////////////////////////////////
	
	public DataBase db = new DataBase();
	
	protected void execute(String sql) throws Exception {
		execute(sql, null);
	}
	
	protected void execute(String sql, HashMap param) throws Exception {
		db.execute(sql, param);	
	}
	
	protected ArrayList select(String sql) throws Exception {
		return select(sql, null);
	}
	
	protected ArrayList select(String sql, HashMap param) throws Exception {
		return selectPage(sql, param, -1, 0);
	}
	
	protected HashMap selectFirst(String sql) throws Exception {
		return selectFirst(sql, null);
	}
	
	protected HashMap selectFirst(String sql, HashMap param) throws Exception {
		HashMap o = db.selectFirst(sql, param); 
		if(o != null ) o = (HashMap)modelFrom(o);
		return o;
	}
	
	protected ArrayList selectPage(String sql, int pageIndex, int pageSize) throws Exception {
		return selectPage(sql, null, pageIndex, pageSize);
	}
	
	protected ArrayList selectPage(String sql, HashMap param, int pageIndex, int pageSize) throws Exception {
		ArrayList list = db.select(sql, param, pageIndex, pageSize);
		
		for(int i=0, l=list.size(); i<l; i++) {
			HashMap o = (HashMap)list.get(i);
			if(o != null ) list.set(i, modelFrom(o));
		}
		
		return list;		
	}
	
	protected String autoId(HashMap o, String idField) {
		String id = o.get(idField) == null ? null : o.get(idField).toString();
		if(id == null) {
			id = UUID.randomUUID().toString();
		}
		o.put(idField, id);
		return id;
	}
	
	protected String createOrderSql(ArrayList sortFields, String namePrefix)
    {
		return DataBase.createOrderSql(sortFields, namePrefix);
    }
    
    public static String createInsertSql(String tableName)  {
		return DataBase.createInsertSql(tableName);
	}
	
	protected String createUpdateSql(String tableName, String tableIdField)  {
		return DataBase.createUpdateSql(tableName, tableIdField);
	}
    
	
	protected void copyFrom(HashMap to, HashMap from) {		
        Iterator iter = from.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();  
            to.put(key, val);
        }
	}
	
}


//protected String getByIdSql(String id) {			
//return getSelectSql() +" where "+ aliasPrefixField(getIdField())+" = '"+id+"'";
//}
//
//protected String aliasPrefixField(String field) {
//String aliasName = getAliasTableName();
//return ("".equals(aliasName) ? "" : aliasName+".")+field;
//}
//protected String getAliasTableName() {
//String sql = getSelectSql();
//
//if(!"".equals(sql)) {
//	String tableName = getTableName();
//	int index = sql.indexOf(tableName);
//	sql = sql.substring(index + tableName.length());
//	String[] ss = sql.split(" ");
//	
//	for(int i=0,l=ss.length; i<l; i++) {
//		String s = ss[i];
//		if("".equals(s)) continue;
//		if("left".equals(s)
//			|| "join".equals(s)
//			|| "where".equals(s)
//			|| "order".equals(s)
//		) {
//			break;
//		}
//		return s;
//	}
//}
//
//return "";
//}