package com.plusoft.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import com.plusoft.factory.*;
import com.plusoft.util.*;
import com.plusoft.dao.*;
import com.plusoft.db.DataBase;

public class AppService extends BaseService{
	
	EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
	DeptDao deptDao = DaoFactory.getDeptDao();
	EduDao eduDao = DaoFactory.getEduDao();
	PositionDao positionDao = DaoFactory.getPositionDao();
	FileDao fileDao = DaoFactory.getFileDao();
	
	public AppService() {
		addTransaction(employeeDao);
		addTransaction(deptDao);
		addTransaction(eduDao);
		addTransaction(positionDao);
		addTransaction(fileDao);
	}
	
	public ArrayList searchEmployees(String key, int pageIndex, int pageSize, ArrayList sortFields) throws Exception {
		return employeeDao.search(key, pageIndex, pageSize, sortFields);
	}
	
	//
	public HashMap searchEmployeesResult(String key, int pageIndex, int pageSize, ArrayList sortFields) throws Exception {
	    ArrayList data = searchEmployees(key, pageIndex, pageSize, sortFields);
	    int total = searchEmployeesTotal(key);
	    
	    HashMap result = new HashMap();
	    result.put("data", data);
	    result.put("total", total);		
	    return result;
	}
	
	public ArrayList searchEmployees(String key, int pageIndex, int pageSize, String sortField, String sortOrder) throws Exception {
		return employeeDao.search(key, pageIndex, pageSize, sortField, sortOrder);		
	}
	
	public int searchEmployeesTotal(String key) throws Exception {
		if(key == null) key = "";
		return employeeDao.getCount("name like '%"+key+"%'");
	}
	
	public HashMap searchEmployeesResult(String key, int pageIndex, int pageSize, String sortField, String sortOrder) throws Exception {
	    ArrayList data = searchEmployees(key, pageIndex, pageSize, sortField, sortOrder);
	    int total = searchEmployeesTotal(key);
	    
	    HashMap result = new HashMap();
	    result.put("data", data);
	    result.put("total", total);		
	    
        //汇总信息：年龄（minAge, maxAge, avgAge）
	    HashMap ageInfo = new DataBase().selectFirst("select min(age) as minAge, max(age) as maxAge, avg(age) as avgAge from t_employee");            
        result.put("minAge", ageInfo.get("minAge"));
        result.put("maxAge", ageInfo.get("maxAge"));
        result.put("avgAge", ageInfo.get("avgAge"));
        
	    return result;
	}
	
	
	
	public void saveEmployees(ArrayList data) throws Exception
	{
		Connection conn = startTransaction();
		try {
			conn.setAutoCommit(false);
			
			for(int i=0,l=data.size(); i<l; i++){
		    	HashMap o = (HashMap)data.get(i);
		  		
				String id = o.get("id") != null ? o.get("id").toString() : "";
		        String state = o.get("_state") != null ? o.get("_state").toString() : "";
		        if(state.equals("added") || id.equals(""))				//新增：id为空，或_state为added
		        {
		            o.put("createtime", new Date());
		            employeeDao.insert(o);
		        }
		        else if (state.equals("removed") || state.equals("deleted"))
		        {	            
		            employeeDao.delete(id);
		        }
		        else if (state.equals("modified") || state.equals(""))	//更新：_state为空，或modified
		        {	            
		            employeeDao.update(o);
		        }
		    }
			
			conn.commit();
		}catch(Exception e) {			
			e.printStackTrace();						
			conn.rollback();
		}finally{				
			closeTransaction();
		}	    
	}
	
	public void removeEmployees(String id) throws Exception
	{
	    Connection conn = startTransaction();
		try {
			conn.setAutoCommit(false);
			
			if (StringUtil.isNullOrEmpty(id)) return;
		    String[] ids = id.split(",");
		    for (int i = 0, l = ids.length; i < l; i++)
		    {
		        String s = ids[i];
		        employeeDao.delete(s);
		    }
			
			conn.commit();
		}catch(Exception e) {			
			e.printStackTrace();						
			conn.rollback();
		}finally{				
			closeTransaction();
		}
		
	}
	
	public HashMap getEmployee(String id) throws Exception {
		return employeeDao.findById(id);
	}
	
	public ArrayList getEmployeesByDeptId(String deptId, int pageIndex, int pageSize) throws Exception {
		return employeeDao.findAllByDetpId(deptId, pageIndex, pageSize);
	}
	
	public int getEmployeesByDeptIdTotal(String deptId) throws Exception {
		return employeeDao.getCount("dept_id ='"+deptId+"'");
	}
	
	public HashMap getEmployeesByDeptIdResult(String deptId, int pageIndex, int pageSize) throws Exception {
	    ArrayList data = getEmployeesByDeptId(deptId, pageIndex, pageSize);
	    int total = getEmployeesByDeptIdTotal(deptId);
	    
	    HashMap result = new HashMap();
	    result.put("data", data);
	    result.put("total", total);		
	    return result;
	}
	
	public ArrayList getDepartments() throws Exception {
		return deptDao.findAll();		
	}
	
	public ArrayList getPositions() throws Exception {
		return positionDao.findAll();		
	}
	
	public ArrayList getPositionsByDeptId(String deptId) throws Exception {
		return positionDao.findAllByDeptId(deptId);
	}
	
	public ArrayList getEducationals() throws Exception {
		return eduDao.findAll();		
	}
	
	public String addFile(HashMap o) throws Exception {
		return (String)fileDao.insert(o);
	}
	
	public void removeFile(String id) throws Exception {
		 fileDao.delete(id);
	}
	
	public void updateFile(HashMap o) throws Exception {
		 fileDao.update(o);
	}
	

}
