package SchoolSearch.services.monitor.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

import SchoolSearch.services.dao.schooltest.dao.SchooltestUsereditDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestUseredit;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.userActionLog.UserActionLogService;
import SchoolSearch.services.services.userEditLog.UserEditLogService;
import SchoolSearch.services.services.userEditLog.impl.UserEditLogServiceImpl;

public class MonitorServiceImpl implements MonitorService {
	@Inject
	Authenticator authenticator;

	@Inject
	RequestGlobals request;

	@Inject
	UserActionLogService userActionLog;

	@Inject
	UserEditLogService userEditService;

	@Override
	public void visitPage(String pageName, String info) {
		String username = null;
		User currentUser = authenticator.getCurrentUser();
		if (null != currentUser)
			username = currentUser.username;
		System.out.println(String.format("[visit page]\t[%s]\t[%s]\t[user : %s]\t[%s]\t[%s]", pageName, info, username, authenticator.getCurrentIp(), new Timestamp(System.currentTimeMillis()).toString()));
		System.out.println(String.format("[visit session]\t[%s]", request.getHTTPServletRequest().getSession().getId()));
		Integer userType = 0;
		if (null != authenticator.getCurrentUser()) {
			if (authenticator.getCurrentUser().userType.equals("admin")) {
				userType = 1;
			}
			userActionLog.actionLog(authenticator.getCurrentUser().id, authenticator.getCurrentUser().username, pageName, info, authenticator.getCurrentIp(), userType);
		}else {
			userActionLog.actionLog(null, null, pageName, info, authenticator.getCurrentIp(), null);
		}

	}

	@Override
	public void singleEditAction(String componentName, String field, String original, String newValue, Integer personId) {
		// TODO Auto-generated method stub
		if (null != authenticator.getCurrentUser()) {
			User currentUser = authenticator.getCurrentUser();
			Integer uid = currentUser.id;
			String userName = currentUser.username;
			String userType = currentUser.userType;
			String ip = authenticator.getCurrentIp();
//			String time = String.valueOf(System.currentTimeMillis());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			SchooltestUseredit temp = new SchooltestUseredit(0, uid, userName, componentName, personId, field, original, newValue, ip, time, userType);
			userEditService.insert(temp);
		}
	}


	@Override
	public void multipleEditionAction(String componentName, Object original, Object newObj, List<Field> fieldList) throws IllegalArgumentException, IllegalAccessException {
		if(null != authenticator.getCurrentUser()) {
			SchooltestPersonProfile personprofile = new SchooltestPersonProfile();
			personprofile = (SchooltestPersonProfile) original;
			User currentUser = authenticator.getCurrentUser();
			Integer uid = currentUser.id;
			String userName = currentUser.username;
			String userType = currentUser.userType;
			String ip = authenticator.getCurrentIp();
			List<SchooltestUseredit> resultList = new ArrayList<SchooltestUseredit>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
//			String time = String.valueOf(System.currentTimeMillis());
			for (Field f : fieldList) {
				f.setAccessible(true);
				Object originalValue = f.get(original);
				Object newValue = f.get(newObj);
				Class<?> fieldType = f.getType();
				SchooltestUseredit temp = null;
				if(null == originalValue) {
					if(null != newValue) {
						temp = new SchooltestUseredit(0, uid, userName, componentName, personprofile.getId(), f.toString(), null, newValue.toString(), ip, time, userType);
					}
				} else {
					if (null == newValue) {
						temp = new SchooltestUseredit(0, uid, userName, componentName, personprofile.getId(), f.toString(), originalValue.toString(), null, ip, time, userType);
					} else {
						if (!fieldType.cast(originalValue).equals(fieldType.cast(newValue))) {
							temp = new SchooltestUseredit(0, uid, userName, componentName, personprofile.getId(), f.toString(), originalValue.toString(), newValue.toString(), ip, time, userType);
						}
					}
				}
				if(null != temp) {
					resultList.add(temp);
				}
			}
			userEditService.insertMultiple(resultList);
			
		}
	}
	

}
