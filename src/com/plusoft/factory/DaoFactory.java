package com.plusoft.factory;

import com.plusoft.dao.*;

public class DaoFactory {
	
	public static EmployeeDao getEmployeeDao() {
		return new EmployeeDao();
	}
	
	public static DeptDao getDeptDao() {
		return new DeptDao();
	}
	
	public static EduDao getEduDao() {
		return new EduDao();
	}
	
	public static PositionDao getPositionDao() {
		return new PositionDao();
	}
	
	public static FileDao getFileDao() {
		return new FileDao();
	}
	
} 
