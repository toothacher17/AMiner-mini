package SchoolSearch.services.services.userActionLog.impl;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.userActionLog.UserActionLogDao;
import SchoolSearch.services.dao.userActionLog.model.UserActionLog;
import SchoolSearch.services.services.userActionLog.UserActionLogService;

public class UserActionLogServiceImpl implements UserActionLogService{

	@Override
	public void actionLog(Integer uid, String userName, String pageName,
			String info, String ip, Integer type) {
		UserActionLog log = new UserActionLog();
		log.uid = uid;
		log.userName = userName;
		log.pageName = pageName;
		log.info = info;
		log.ip = ip;
		log.type = type;
		userActionLogDao.insert(log);
		System.out.println("[use action log] user sucessfully visit page");
	}
	
	
	@Inject
	UserActionLogDao userActionLogDao;

}
