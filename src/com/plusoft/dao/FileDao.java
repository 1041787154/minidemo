package com.plusoft.dao;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.plusoft.util.Convert;
import com.plusoft.util.JSON;
import com.plusoft.util.StringUtil;


public class FileDao  extends BaseDao{
	
	protected String getTableName() {
		return "plus_file";
	}	
	
}
