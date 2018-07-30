
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.plusoft.db.DataBase;
import com.plusoft.service.*;

public class Test {

	public static void main(String[] args) throws Exception {
		
		String tableName = "t_employee";
		
		//ArrayList list = getDBTable(tableName);		
		//System.out.print(list);
		
		String insertSql = createInsertSql(tableName);
		System.out.println(insertSql);
		
		String updateSql = createUpdateSql(tableName, "id");
		System.out.println(updateSql);
		
	}
	
	public static ArrayList getDBTable(String tableName) throws Exception {
		String sql = "select * from information_schema.COLUMNS where table_name = '"+tableName+"'"; 
		Connection conn = DataBase.getConnection();
		
		ResultSet rs = conn.createStatement().executeQuery(sql);
		
		ArrayList list = DataBase.resultSetToList(rs);
		return list;
	}
	
//	String sql = 
	//"insert into t_employee (id, loginname, name, age, married, gender, birthday, country, city, dept_id, position, createtime, salary, educational, school, email, remarks)"
//  + " values(@id, @loginname, @name, @age, @married, @gender, @birthday, @country, @city, @dept_id, @position, @createtime, @salary, @educational, @school, @email, @remarks)";

	
	public static String createInsertSql(String tableName) throws Exception {
		ArrayList list = getDBTable(tableName);
		String s1 = "";
		String s2 = "";
		
		for(int i=0,l=list.size(); i<l; i++) {
			HashMap column = (HashMap)list.get(i);
			String name = column.get("COLUMN_NAME").toString();
			if(i!=0) {
				s1 += ", ";
				s2 += ", ";
			}
			s1 += name;
			s2 += "@"+name;
		}
		String sql = "insert into "+tableName+" ("+s1+") values ("+s2+")";
		
		return sql;
	}
	
	public static String createUpdateSql(String tableName, String idField) throws Exception {
		ArrayList list = getDBTable(tableName);
		String s = "";
		
		for(int i=0,l=list.size(); i<l; i++) {
			HashMap column = (HashMap)list.get(i);
			String name = column.get("COLUMN_NAME").toString();
			if(i!=0) {
				s += ", ";
			}
			s += name +" = @" + name;
		}
		String sql = "update "+tableName+" set "+s+" where "+idField+"=@id";
		
		return sql;
	}

}
