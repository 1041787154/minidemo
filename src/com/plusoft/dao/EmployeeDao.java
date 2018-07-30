package com.plusoft.dao;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.plusoft.db.DataBase;
import com.plusoft.util.Convert;
import com.plusoft.util.JSON;
import com.plusoft.util.StringUtil;


public class EmployeeDao  extends BaseDao{
	
	protected String getTableName() {
		return "t_employee";
	}
	
	protected String getSelectSql() {
		return "select a.*, b.name dept_name, c.name position_name, d.name educational_name \n"
				 +"from t_employee a \n"
				 +"left join t_department b \n"
				 +"on a.dept_id = b.id \n"
				 +"left join t_position c \n"
				 +"on a.position = c.id \n"
				 +"left join t_educational d \n"
				 +"on a.educational = d.id \n";
	}
	
	protected String getFindByIdSql(String id) {
		return getSelectSql() +" where a.id = '"+id+"'";
	}	
	
	public ArrayList findAllByDetpId (String deptId, int pageIndex, int pageSize) throws Exception
	{
	    String sql = getSelectSql() + "where a.dept_id = '" + deptId + "'";
	    return selectPage(sql, pageIndex, pageSize);
	}	
	
	public ArrayList search(String key, int pageIndex, int pageSize, String sortField, String sortOrder) throws Exception
    {
        ArrayList sortFields = new ArrayList();        
        if (!StringUtil.isNullOrEmpty(sortField)) {        	        
	        HashMap p = new HashMap();
	        p.put("field", sortField);
	        p.put("dir", sortOrder);
	        sortFields.add(p);
        }
        
        return search(key, pageIndex, pageSize, sortFields);
    }	
	
	public ArrayList search(String key, int pageIndex, int pageSize, ArrayList sortFields) throws Exception
    {
    	if(key == null) key = "";
    	
    	String sql = getSelectSql() +"where a.name like '%" + key + "%' \n";
    	
    	if (sortFields != null && sortFields.size() > 0)
        {
            sql += createOrderSql(sortFields, "a.");
        }
        else
        {
            sql += " order by a.createtime desc";
        }
    	
    	return selectPage(sql, pageIndex, pageSize);
    }
	
}
