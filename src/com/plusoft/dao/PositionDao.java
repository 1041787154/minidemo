package com.plusoft.dao;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.plusoft.util.Convert;
import com.plusoft.util.JSON;
import com.plusoft.util.StringUtil;


public class PositionDao  extends BaseDao{
	
	protected String getTableName() {
		return "t_position";
	}
	
	public ArrayList findAllByDeptId(String deptId) throws Exception
	{
		String sql = "select * from t_position where dept_id = '" + deptId + "'";
		return select(sql);
	}
	
}
