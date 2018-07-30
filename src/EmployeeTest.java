import java.util.ArrayList;
import java.util.HashMap;

import com.plusoft.dao.*;

public class EmployeeTest {
	
	public static void main(String[] args) throws Exception {
		
		
		EmployeeDao dao = new EmployeeDao();
		
		String key = "";
		int i = dao.getCount("name like '%"+key+"%'");	
		System.out.print(i);
		
//		ArrayList list =  dao.getAll();
//		
//		System.out.println(list);
		
		//dao.delete("b3d3d2b2-460a-470e-a33c-aff1c8d8a652");
		
//		HashMap o = new HashMap();
//		o.put("id", "ceb1ab63-c0c6-40fb-a481-65ed70b9e2a2");
//		o.put("name", "张伟2");
//		
//		dao.update(o);
		
//		HashMap a = dao.getById("ceb1ab63-c0c6-40fb-a481-65ed70b9e2a2");
//		System.out.println(a);
//		
//		ArrayList list = dao.search("", 0, 10, "", "");
//		System.out.println(list.size());
		
//		DeptDao deptDao = new DeptDao();
//		
//		HashMap o = new HashMap();
//		o.put("name", "研发技术部");
//		
//		deptDao.insert(o);
//		
//		ArrayList list = deptDao.getAll();
//		System.out.println(list);
		
		
//		String Str = new String("heollo");
//
//        System.out.println(Str.replaceFirst("o", "T"));
		
	}
}
