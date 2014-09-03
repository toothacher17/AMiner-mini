package SchoolSearch.services.services.userEditLog.impl;

import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.dao.SchooltestUsereditDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestUseredit;
import SchoolSearch.services.services.userEditLog.UserEditLogService;

public class UserEditLogServiceImpl implements UserEditLogService {

	@Inject
	SchooltestUsereditDAO usereditDao;
	
	
	@Override
	public void insert(SchooltestUseredit obj) {
		// TODO Auto-generated method stub
		usereditDao.insert(obj);
		
	}

	@Override
	public void insertMultiple(List<SchooltestUseredit> objList) {
		// TODO Auto-generated method stub
		usereditDao.insertMultiple(objList);
	}

//	@Override
//	public void singleEidt(Integer uid, String userName, String userType, String ip, String componentName, Integer personId, String filed, String original, String newvalue) {
//		// TODO Auto-generated method stub
//		String time = String.valueOf(System.currentTimeMillis());
//		SchooltestUseredit obj = new SchooltestUseredit();
////		obj.fuzhi
////		dao.chazhi
////		
//		
//		
//		
//		
//	}
//
//
//	@Override
//	public void multipleEdit(Integer uid, String userName, String userType, String ip, String componentName, Integer personId, Map<String, String> field2orignial, Map<String, String> field2new) {
//		// TODO Auto-generated method stub
//		for(String fieldname : field2orignial.keySet()) {
//			String time = String.valueOf(System.currentTimeMillis());
//			SchooltestUseredit obj = new SchooltestUseredit();
////			obj.fuzhi
////			dao.chazhi
//		}
//	}


	
	
	
}
