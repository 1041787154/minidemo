package com.plusoft.dao;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.plusoft.util.Convert;
import com.plusoft.util.JSON;
import com.plusoft.util.StringUtil;


public class DeptDao  extends BaseDao{
	
	protected String getTableName() {
		return "t_department";
	}
	
}
