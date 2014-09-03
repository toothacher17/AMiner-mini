package SchoolSearch.services.services.userActionLog;

public interface UserActionLogService {
	void actionLog(Integer uid, String userName, String pageName, String info, String ip, Integer type);
}
