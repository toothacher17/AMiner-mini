package SchoolSearch.services.utils.dataUpdateTools.basic;
/**
 * @author CX
 */
import java.util.List;
import java.util.Map;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.InsertIntoDataBase;
import SchoolSearch.services.utils.dataUpdateTools.utils.DataUpdateConstants;

public class CommonDao {
	private static CommonDao instance;
	String dbName = ConsistanceService.get("db.defaultDatabase");
	public static CommonDao getInstance(){
		if(null == instance){
			instance = new CommonDao();
		}
		return instance;
	}
	
	public static CommonDao getIndependentInstance(String dbName) {
		return new CommonDao(dbName);
	}

	private CommonDao() {
	}

	private CommonDao(String dbName) {
		this.dbName = dbName;
	}
	
	public <T> void insertWithouId(T t){
		if(null != t){
			String tableName = DataUpdateConstants.getTableName(t.getClass());
			if(null != tableName){
				InsertIntoDataBase iidb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, t, false);
				iidb.insertBatchRecord();
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + t.getClass().toString() + " NOT FOUND!");
			}
		}else{
			System.out.println("[InsertWithoutIdErrorInfo]INPUT PARAMETER IS NULL!" );
		}
	}
	
	public <T> void insertWithId(T t){
		if(null != t){
			String tableName = DataUpdateConstants.getTableName(t.getClass());
			if(null != tableName){
				InsertIntoDataBase iidb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, t, true);
				iidb.insertBatchRecord();
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + t.getClass().toString() + " NOT FOUND!");
			}
		}else{
			System.out.println("[InsertWithIdErrorInfo]INPUT PARAMETER IS NULL!" );
		}
	}
	public <T> void insertBatchWithouId(List<T> tList){
		if(null != tList && !tList.isEmpty()){
			String tableName = DataUpdateConstants.getTableName(tList.get(0).getClass());
			if(null != tableName){
				InsertIntoDataBase iidb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, tList, false);
				iidb.insertBatchRecord();
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + tList.get(0).getClass().toString() + " NOT FOUND!");
			}
		}else{
			System.out.println("[InsertBatchWithouIdErrorInfo]INPUT PARAMETER IS NULL!" );
		}
	}
	public <T> void insertBatchWithId(List<T> tList){
		if(null != tList && !tList.isEmpty()){
			String tableName = DataUpdateConstants.getTableName(tList.get(0).getClass());
			if(null != tableName){
				InsertIntoDataBase iidb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, tList, true);
				iidb.insertBatchRecord();
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + tList.get(0).getClass().toString() + " NOT FOUND!");
			}
		}else{
			System.out.println("[InsertBatchWithIdErrorInfo]INPUT PARAMETER IS NULL!" );
		}
	}
	public <T> List<T> walkAll(Class<T> type){
		if(null != type){
			String tableName = DataUpdateConstants.getTableName(type);
			if(null != tableName){
				ReadFromDataBase rd =  ReadFromDataBase.getIndependentInstance(dbName, tableName);
				rd.getAllData();
				return rd.getWrappedList(type);
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + type.getClass().toString() + " NOT FOUND!");
				return null;
			}
		}else{
			System.out.println("[WalkAllErrorInfo]TYPE IS EMPTY!");
			return null;
		}
	}
	public <T> T getFirstRecordOrderById(Class<T> type){
		if(null != type){
			String tableName = DataUpdateConstants.getTableName(type);
			if(null != tableName){
				ReadFromDataBase rd =  ReadFromDataBase.getIndependentInstance(dbName, tableName);
				return rd.getFirstRecord(type);
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + type.getClass().toString() + " NOT FOUND!");
				return null;
			}
		}else{
			System.out.println("[WalkAllErrorInfo]TYPE IS EMPTY!");
			return null;
		}
	}
	public <T> T getLastRecordOrderById(Class<T> type){
		if(null != type){
			String tableName = DataUpdateConstants.getTableName(type);
			if(null != tableName){
				ReadFromDataBase rd =  ReadFromDataBase.getIndependentInstance(dbName, tableName);
				return rd.getLastRecord(type);
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + type.getClass().toString() + " NOT FOUND!");
				return null;
			}
		}else{
			System.out.println("[WalkAllErrorInfo]TYPE IS EMPTY!");
			return null;
		}
	}
	/**
	 * 根据筛选条件condition, 按某键值orderBy排序, 升降序sequence,已经返回的类型type来进行数据库操作
	 * @param condition
	 * @param orderBy
	 * @param sequence
	 * @param type
	 * @return
	 */
	public <T> List<T> getWithCondition(Map<String, Object> condition, String orderBy, String sequence, Class<T> type){
		if(null != type){
			String tableName = DataUpdateConstants.getTableName(type);
			if(null != tableName){
				ReadFromDataBase rd =  ReadFromDataBase.getIndependentInstance(dbName, tableName);
				return rd.getWithCondition(condition, orderBy, sequence, type);
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + type.getClass().toString() + " NOT FOUND!");
				return null;
			}
		}else{
			System.out.println("[WalkAllErrorInfo]TYPE IS EMPTY!");
			return null;
		}
	}
	@Deprecated
	/**
	 * 根据筛选条件condition, 按某键值orderBy排序, 升降序sequence,已经返回的类型type来进行数据库操作，返回特定的键值keyList
	 * @param keyList
	 * @param condition
	 * @param orderBy
	 * @param sequence
	 * @param type
	 * @return
	 */
	public <T> List<T> getWithCondition(List<String> keyList, Map<String, List<Object>> condition, String orderBy, String sequence, Class<T> type){
		if(null != type){
			String tableName = DataUpdateConstants.getTableName(type);
			if(null != tableName){
				ReadFromDataBase rd =  ReadFromDataBase.getIndependentInstance(dbName, tableName);
				return rd.getWithCondition(keyList, condition, orderBy, sequence, type);
			}else{
				System.out.println("[InsertErrorInfo]CLASS " + type.getClass().toString() + " NOT FOUND!");
				return null;
			}
		}else{
			System.out.println("[WalkAllErrorInfo]TYPE IS EMPTY!");
			return null;
		}
	}
	public static void main(String[] args) {
//		CommonDao cd = CommonDao.getInstance("newdb");
//		Person p1 =cd.getFirstRecordOrderById(Person.class);
//		Person p2 = cd.getLastRecordOrderById(Person.class);
//		System.out.println(p1.id);
//		System.out.println(p2.id);
//
//		Person p1 = new Person();
//		p1.id = 10002;
//		p1.name = "chenxi";
//		p1.name_alias = "xiaoA";
//		Person p2 = new Person();
//		p2.id = 10001;
//		p2.name = "chenxi2";
//		p2.name_alias = "xiaoA";
//		List<Person> pList = new ArrayList<Person>();
//		pList.add(p1);
//		pList.add(p2);
//		cd.insertBatchWithId(pList);
	}
}
 