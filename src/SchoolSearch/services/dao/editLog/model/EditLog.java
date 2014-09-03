package SchoolSearch.services.dao.editLog.model;
/**
 * @author GCR
 */
import java.sql.Timestamp;

public class EditLog {
	public int id;
	public Integer uid;
	public String table;
	public int column_id;
	public String field;
	public String old_value;
	public String new_value;
	public String ip;
	public int state;
	public Timestamp time; 
}
