package SchoolSearch.services.services.userLog.impl;
/**
 * @author GCR
 */
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.userLog.UserLogDao;
import SchoolSearch.services.dao.userLog.model.UserLog;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.userLog.UserLogService;

public class UserLogServiceImpl implements UserLogService{

	@Override
	public void log(Integer uid, String username, String state, String password, String userInfo, int type) {
		UserLog log = new UserLog();
		log.uid = uid;
		log.username = username;
		log.ip = authenticator.getCurrentIp();
		log.state = state;
		log.password = password;
		log.userInfo = userInfo;
		log.type = type;
		userLogDao.insert(log);
	}

	@Override
	public List<UserLog> getAllUserLog() {
		List<UserLog> logList = userLogDao.walkAll();
		return logList;
	}
	
	@Inject
	Authenticator authenticator;
	
	@Inject
	UserLogDao userLogDao;

}
