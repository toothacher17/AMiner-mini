package SchoolSearch.services.dao.userLog.model;
/**
 * @author GCR
 */
import java.sql.Timestamp;

public class UserLog {
	public int id;
	public Integer uid;
	public String username;
	public String ip;
	public String state;
	public String password;
	public String userInfo;
	public int type;
	public Timestamp time;
}
