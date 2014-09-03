package SchoolSearch.services.services.editLog.impl;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.editLog.EditLogDao;
import SchoolSearch.services.dao.editLog.model.EditLog;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.T5RegistryHelper;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;

/**
 * 
 * @author guanchengran
 *
 */
public class EditLogServiceImpl implements EditLogService{
	

	@Override
	public void log(String table, Integer column_id, String field_name,
			String old_value, String new_value) {
		System.out.println("[Edit]" + table + "\t" + column_id + "\t" + field_name + "\t" + old_value + "\t" + new_value);
		EditLog log = new EditLog();
		if(authenticator.getCurrentUser() != null) {
			log.uid = authenticator.getCurrentUser().id;
		} else {
			log.uid = null;
		}
		
		log.table = table;
		log.column_id = column_id;
		log.field = field_name;
		log.old_value = old_value;
		log.new_value = new_value;
		log.ip = authenticator.getCurrentIp();
		editLogDao.insert(log);
	}

	@Override
	public void rollback(Integer id) {
		editLogDao.rollback(id);
	}

	@Override
	public List<EditLog> getLogsByTableName(String tableName) {
		return editLogDao.getLogsByTableName(tableName);
	}

	@Override
	public List<EditLog> getAllLog() {
		List<EditLog> logList = editLogDao.walkAll();
		return logList;
	}
	
	@Override
	public List<EditLog> getConditionalEditLog(List<Object> list) {
		 ReadFromDataBase rd = new ReadFromDataBase("schoolsearch","editlog");
		 Map<String, Object> map = new HashMap<String, Object>();
		 String[] fieldNames = {"uid","table","column_id","field","state"};
		 for(int i=0; i<fieldNames.length; i++) {
			 if(list.get(i)!=null) {
				 map.put(fieldNames[i], list.get(i));
			 } 
		 }
		 
//		 map.put("uid", list.get(0));
//		 map.put("table", list.get(1));
//		 map.put("column_id", list.get(2));
//		 map.put("field", list.get(3));
//		 map.put("state", list.get(4));
		 return rd.getWithCondition(map, "time", "desc", EditLog.class);
	}
	
	public static void main(String[] args) {
		EditLogService e = T5RegistryHelper.getService(EditLogService.class);
		List<Object> list = new ArrayList<Object>();
		list.add(null);
		list.add("person_profile");
		list.add(4);
		list.add("location");
		list.add(1);
		System.out.println(e.getConditionalEditLog(list).size());
	}

	@Inject
	EditLogDao editLogDao;
	
	@Inject 
	Authenticator authenticator;

	
}
