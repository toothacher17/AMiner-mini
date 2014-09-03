package SchoolSearch.services.services.editLog;

import java.util.List;

import SchoolSearch.services.dao.editLog.model.EditLog;

/**
 * 
 * @author guanchengran
 *
 */
public interface EditLogService {
	void log(String table, Integer column_id, String field_name, String old_value, String new_value);
	
	void rollback(Integer id);
	
	List<EditLog> getAllLog();
	
	List<EditLog> getLogsByTableName(String tableName);
	
	List<EditLog> getConditionalEditLog(List<Object> list);
}
