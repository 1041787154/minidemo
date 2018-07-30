package com.plusoft.db;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

import com.plusoft.util.Convert;
import com.plusoft.util.StringUtil;

import oracle.sql.CLOB;

public class DataBase {
	
	//mysql
//	static String dbType = "MySql";
//	private static String driver = "com.mysql.jdbc.Driver";
//	private static String url = "jdbc:mysql://localhost/plusoft_test?useUnicode=true&characterEncoding=GBK";
//	private static String user = "root";
//	private static String pwd = "";
	
//	sqlserver
	static String dbType = "SqlServer";
		private static  String driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
		private static  String url="jdbc:sqlserver://localhost:1433;DatabaseName=plusoft_test";
		private static  String user="sa";
		private static  String pwd="wangjia";
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pwd);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	////////////////////////////////////////////////////////////////////////
	
	public Connection conn;	
	protected PreparedStatement stmt;
	
	protected void open(String sql) throws Exception{		
		open(sql, null);
	}
	
	protected void open(String sql, HashMap param) throws Exception{		
		if(conn == null) {
			conn = getConnection();
		}
		stmt = createPreparedStatement(conn, sql, param);
	}
	
	protected void close() throws Exception{
		if(conn != null && conn.getAutoCommit()) {
			conn.close();
			conn = null;
		}
	}
	
	protected Object getFirstValue(HashMap o) {
		for (Object key : o.keySet()) {
    		Object value = o.get(key.toString());
    		return value;
    	}		
		return null;	
	}
	
	//返回第一行第一列
	public Object selectScalar(String sql) throws Exception
    {		
		return selectScalar(sql, null);
    }
	
	public Object selectScalar(String sql, HashMap param) throws Exception
    {		
		HashMap o = selectFirst(sql, param);
		
		return getFirstValue(o);
    }
	
	//返回第一行
	public HashMap selectFirst(String sql) throws Exception
    {		
		return selectFirst(sql, null);
    }
	
	public HashMap selectFirst(String sql, HashMap param) throws Exception
    {		
		ArrayList list = select(sql, param);
		return list.size() == 0 ? null : (HashMap)list.get(0); 
    }
	
	//返回所有行
	public ArrayList select(String sql) throws Exception
	{
		return select(sql, null);
	}
		
	public ArrayList select(String sql, HashMap param) throws Exception
	{
		open(sql, param);
		
		ResultSet rst = stmt.executeQuery();		
		ArrayList list = resultSetToList(rst);
		
		rst.close();
		stmt.close();
			
		close();
			
		return list;
	}	
	
	public ArrayList select(String sql, HashMap param, int pageIndex, int pageSize) throws Exception
    {
        boolean flag = false;
        if (pageIndex != -1)
        {
            if (dbType.equals("MySql"))
            {
                sql += "\nlimit " + pageIndex * pageSize + "," + pageSize;
                flag = true;
            }
            if (dbType.equals("SqlServer"))
            {
                //string s = sql;
                //s = s.Insert(s.IndexOf("select") + 6, " ROW_NUMBER() OVER ( ORDER BY id ) AS rownumber ,");
                //s = "SELECT TOP " + pageSize + " * FROM ( " + s + " ) __ WHERE   rownumber > " + (pageIndex + 1) * pageSize;

                //flag = true;
            }
        }

        //数据库分页（最佳性能）
        if(flag) return select(sql, param);

        //内存分页（临时方案）
        ArrayList dataAll = select(sql, param);
        if (pageIndex == -1) return dataAll;

	    ArrayList data = new ArrayList();
	    int start = pageIndex * pageSize, end = start + pageSize;
	    int total = dataAll.size();
	    
	    for (int i = start, l = end; i < l; i++)
	    {
	    	if(i >= total) break;	    	
	        HashMap record = (HashMap)dataAll.get(i);
	        data.add(record);	        
	    }
	    return data;
    }
	
	public int execute(String sql) throws Exception {
		return execute(sql, null);
	}
	
	public int execute(String sql, HashMap param) throws Exception {		
		open(sql, param);
		
	    int count = stmt.executeUpdate();               
		stmt.close();
		
		close();
		return count;
	}	
	
	///////////////////////////////////////////////////////////////////////////
	
	protected PreparedStatement createPreparedStatement(Connection conn, String sql, HashMap param) throws Exception{
	
		List paramList = new ArrayList();
		
		if(param !=null) {
			HashMap lowerParam = new HashMap();
	    	for (Object key : param.keySet()) {
	    		Object value = param.get(key.toString());
	    		String newKey = key.toString().toLowerCase();
	    		lowerParam.put(newKey, value);
	    	}
	    	
	    	String regex = "@\\w+";
	    	Pattern mPattern = Pattern.compile(regex);
	        Matcher mMatcher = mPattern.matcher(sql);	        
	        
	        while (mMatcher.find()) {
	        	String key = mMatcher.group(0);
	        	//sql = sql.replace(key, "?");
	        	sql = sql.replaceFirst(key, "?");
	        	
	        	key = key.substring(1).toLowerCase();
	        	Object value = lowerParam.get(key);
	        	
	        	HashMap kv = new HashMap();
	        	kv.put("key", key);
	        	kv.put("value", value);
	        	paramList.add(kv);
	        	
	            //System.out.println(key);
	        }
	        //System.out.println(sql);
		}
	    
		PreparedStatement stmt = conn.prepareStatement(sql);
		
	    int index = 1;
	    for(int i=0,l=paramList.size(); i<l; i++) {
	   		HashMap kv = (HashMap)paramList.get(i);
	   		
			Object value = kv.get("value");
			stmt.setObject(index++, value);
		}
		
		return stmt;
	}
	
	public static ArrayList resultSetToList(ResultSet rs) throws Exception{    	
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		ArrayList list = new ArrayList();
		Map rowData;
		while(rs.next()){
	    	rowData = new HashMap(columnCount);
	    	for(int i = 1; i <= columnCount; i++)   {	 	    		
	    		Object v = rs.getObject(i);
	    		
	    		if(v != null && (v.getClass() == java.util.Date.class || v.getClass() == java.sql.Date.class)){
	    			Timestamp ts= rs.getTimestamp(i);
	    			v = new java.util.Date(ts.getTime());
	    		}else if(v != null && v.getClass() == CLOB.class){
	    			v = clob2String((CLOB)v);
	    		}
	    		rowData.put(md.getColumnName(i),   v);
	    	}
	    	list.add(rowData);	    	
		}
		return list;
	} 	
	
	private static String clob2String(CLOB clob) throws Exception {
	    return (clob != null ? clob.getSubString(1, (int) clob.length()) : null);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	public static String createOrderSql(ArrayList sortFields, String namePrefix)
    {
        if (namePrefix == null) namePrefix = "";
        
        String sql = "";
        if (sortFields != null && sortFields.size() > 0)
        {
            for (int i = 0; i < sortFields.size(); i++)
            {
            	HashMap record = (HashMap)sortFields.get(i);
                String sortField = (String)record.get("field");
                String sortOrder = (String)record.get("dir");
                
                if (StringUtil.isNullOrEmpty(sortOrder)) sortOrder = "asc";
                
                if (i == 0)
                {
                    sql += " order by " + namePrefix + sortField + " " + sortOrder;
                }
                else
                {
                    sql += ", " + namePrefix + sortField + " " + sortOrder;
                }
            }
        }
        return sql;
    }
    
	protected static ArrayList getTableColumns(String tableName) {
		String sql = "select * from information_schema.COLUMNS where table_name = '"+tableName+"'"; 
		ArrayList list = null;
		try {
			list = new DataBase().select(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}	
	
	private static HashMap<String, String> insertSqlCache = new HashMap<String, String>();
	private static HashMap<String, String> updateSqlCache = new HashMap<String, String>();
    
    public static String createInsertSql(String tableName)  {
		return createInsertSql(tableName, null);
	}
    
	protected static String createInsertSql(String tableName, HashMap mapping)  {
		String sql = insertSqlCache.get(tableName);
		if(StringUtil.isNullOrEmpty(sql)) {
			HashMap dbMapping = new HashMap();
            if (mapping != null)
            {
                Iterator iter = mapping.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    dbMapping.put(val, key);
                }                
            }
            
			ArrayList columns = getTableColumns(tableName);
			String s1 = "";
			String s2 = "";
			
			boolean flag = true;
			for(int i=0,l=columns.size(); i<l; i++) {
				HashMap column = (HashMap)columns.get(i);
				String name = column.get("COLUMN_NAME").toString();
                
				//自增长字段不能主动插入数据
                if ("auto_increment".equals(column.get("EXTRA")))
                {
                    continue;
                }
                
				if (!flag)
                {
					s1 += ", ";
					s2 += ", ";
				}
				s1 += name;
				//s2 += "@"+name;
                String modelName = Convert.toString(dbMapping.get(name));
                if (StringUtil.isNullOrEmpty(modelName)) modelName = name;

                s2 += "@" + modelName;			
                flag = false;
			}
			sql = "insert into "+tableName+" ("+s1+") values ("+s2+")";		
			
			insertSqlCache.put(tableName, sql);
		}
		return sql;
	}
	
	public static String createUpdateSql(String tableName, String tableIdField)  {
		return createUpdateSql(tableName, tableIdField, null);
	}
	
	protected static String createUpdateSql(String tableName, String tableIdField, HashMap mapping)  {
		String sql = updateSqlCache.get(tableName);
		
		if(StringUtil.isNullOrEmpty(sql)) {
			HashMap dbMapping = new HashMap();
            if (mapping != null)
            {
                Iterator iter = mapping.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    dbMapping.put(val, key);
                }                
            }
            
			ArrayList columns = getTableColumns(tableName);
			String s = "";
			
			boolean flag = true;
			for(int i=0,l=columns.size(); i<l; i++) {
				HashMap column = (HashMap)columns.get(i);
				String name = column.get("COLUMN_NAME").toString();
				
				if(!tableIdField.equals(name)) {
	                if (!flag)
	                {
	                    s += ", ";
	                }
					//s += name +" = @" + name;
	                String modelName = Convert.toString(dbMapping.get(name));
                    if (StringUtil.isNullOrEmpty(modelName)) modelName = name;
                    s += name + " = @" + modelName;
                    
					flag = false;
				}
			}
			sql = "update "+tableName+" set "+s+" where "+tableIdField+"=@id";
			updateSqlCache.put(tableName, sql);
		}
		return sql;
	}
	
//	public static String autoId(HashMap o, String idField) {
//		String id = o.get(idField) == null ? null : o.get(idField).toString();
//		if(id == null) {
//			id = UUID.randomUUID().toString();
//		}
//		o.put(idField, id);
//		return id;
//	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
//	public int update(String tableName, HashMap model) throws Exception {
//		return update(tableName, "id", model);
//	}
//	
//	public int update(String tableName, String idField, HashMap model) throws Exception {
//		String sql = createUpdateSql(tableName, idField);
//		int count = this.execute(sql, model);
//		return count;
//	}
//	
//	public String insert(String tableName, HashMap model) throws Exception {
//		return insert(tableName, "id", model);
//	}
//	
//	public String insert(String tableName, String idField, HashMap model) throws Exception {
//		String id = autoId(model, idField);
//		String sql = createInsertSql(tableName);
//		this.execute(sql, model);
//		return id;
//	}
	
}
