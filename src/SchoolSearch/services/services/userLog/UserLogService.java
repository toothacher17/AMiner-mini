package SchoolSearch.services.services.userLog;

import java.util.List;

import SchoolSearch.services.dao.userLog.model.UserLog;

/**
 * 
 * @author guanchengran
 *
 */
public interface UserLogService {
	void log(Integer uid, String username, String state, String password, String userInfo, int type);
	List<UserLog> getAllUserLog();
}
