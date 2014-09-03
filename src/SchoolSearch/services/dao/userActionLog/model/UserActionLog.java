package SchoolSearch.services.dao.userActionLog.model;

import java.sql.Timestamp;

/**
 * 
 * @author guanchengran
 *
 */
public class UserActionLog {
	public Integer id;
	public Integer uid;
	public String userName;
	public String pageName;
	public String info;
	public String ip;
	public Integer type;
	public Timestamp time;
}
