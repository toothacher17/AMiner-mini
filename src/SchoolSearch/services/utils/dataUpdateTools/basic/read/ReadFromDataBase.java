package SchoolSearch.services.utils.dataUpdateTools.basic.read;

/**
 * @author CX
 *
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;
import SchoolSearch.services.utils.dataUpdateTools.basic.TableStructureInfo;
import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.model.TableStructure;

public class ReadFromDataBase {

	private String dbName;
	private String tableName;

	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;

	private static ReadFromDataBase instance;
	static ConnectionPool pool = ConnectionPool.getInstance();

	public static ReadFromDataBase getInstance(String dbName, String tableName) {
		if (null == instance) {
			instance = new ReadFromDataBase(dbName, tableName);
		}
		return instance;
	}
	
	public static ReadFromDataBase getInstance(String tableName) {
		if (null == instance) {
			instance = new ReadFromDataBase(tableName);
		}
		return instance;
	}
	

	public static ReadFromDataBase getIndependentInstance(String dbName,
			String tableName) {
		return new ReadFromDataBase(dbName, tableName);
	}
	
	public static ReadFromDataBase getIndependentInstance(String tableName) {
		return new ReadFromDataBase(tableName);
	}
	
	public ReadFromDataBase(String tableName) {
		keyList = new ArrayList<String>();
		key2Index = new HashMap<String, ColumnStructure>();
		dataCache = new ArrayList<List<Object>>();
		this.dbName = ConsistanceService.get("db.defaultDatabase");
		this.tableName = tableName;

		//初始化表keyList和key2Index
		List<TableStructure> tableStructureList = TableStructureInfo
				.getIndependentInstance(this.dbName, this.tableName)
				.getTableStructureList();
		for (int j = 0; j < tableStructureList.size(); j++) {
			ColumnStructure cs = new ColumnStructure();
			cs.type = tableStructureList.get(j).type;
			cs.index = j;
			keyList.add(tableStructureList.get(j).field.toLowerCase());
			key2Index.put(tableStructureList.get(j).field.toLowerCase(), cs);
			
		}
	}
	
	public ReadFromDataBase(String dbName, String tableName) {
		keyList = new ArrayList<String>();
		key2Index = new HashMap<String, ColumnStructure>();
		dataCache = new ArrayList<List<Object>>();
		this.dbName = dbName;
		this.tableName = tableName;

		//初始化表keyList和key2Index
		List<TableStructure> tableStructureList = TableStructureInfo
				.getIndependentInstance(this.dbName, this.tableName)
				.getTableStructureList();
		for (int j = 0; j < tableStructureList.size(); j++) {
			ColumnStructure cs = new ColumnStructure();
			cs.type = tableStructureList.get(j).type.toLowerCase().trim();
			cs.index = j;
			keyList.add(tableStructureList.get(j).field.toLowerCase());
			key2Index.put(tableStructureList.get(j).field.toLowerCase(), cs);
			
		}
	}
	
	public <T> T getLastRecord(Class<T> type){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM " + dbName + "." + tableName
				+ " ORDER BY id DESC LIMIT 1");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				dataCache.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		if(null != dataCache){
			return getWrappedList(type).get(0);
		}else{
			return null;
		}
	}
	public <T> T getFirstRecord(Class<T> type){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM " + dbName + "." + tableName
				+ " ORDER BY id LIMIT 1");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				dataCache.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		if(null != dataCache){
			return getWrappedList(type).get(0);
		}else{
			return null;
		}
	}
	public Integer getLastRecordId(){
		List<List<Object>> dataCache = new ArrayList<List<Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM " + dbName + "." + tableName
				+ " ORDER BY id DESC LIMIT 1");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				dataCache.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		if(dataCache.isEmpty()){
			return 0;
		}else{
			return (Integer)dataCache.get(0).get(0);
		}
	}
	// 根据startId和endId一次性从数据库fetch一批数据
	private List<List<Object>> walkAllKeys(int startId, int endId) {
		List<List<Object>> dataCache = new ArrayList<List<Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		// 根据keyList决定选择哪些字段
		for (String key : keyList) {
			sql.append(_safeSurroundings(key) + ",");
		}
		sql = sql.deleteCharAt(sql.length() - 1);
		sql.append(" FROM " + dbName + "." + tableName
				+ " WHERE id >= ? AND id <= ? ORDER BY id ");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, startId);
			ps.setInt(2, endId);
			rs = ps.executeQuery();
			while (rs.next()) {
				dataCache.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return dataCache;
	}

	// 根据keyList按startId和endId选择条目，startId不小于0，endId设为-1表示直到结尾
	private void getAllKeys(int startId, int endId, boolean isVerbose) {
		// 一次取500条，大于500条分批取
		int limit = 500;
		int maxId = getLastRecordId();
		// 判断是否超出上界
		if (-1 == endId || endId >maxId) {
			endId = maxId;
		}
		if (-1 == startId || startId >maxId) {
			startId = maxId;
		}
		// 判断是否超出下界
		startId = startId < 1 ? 0 :  startId;
		endId = endId < 1 ? 0 :  endId;
		// 开始id号大于结束id号，则交换
		if (endId < startId) {
			int temp = startId;
			endId = temp;
			startId = endId;
		}
		// 分批取出时存储当前已经存储到的id号
		int pauseId;
		while (true) {
			pauseId = (endId - startId + 1 < limit) ? endId : startId + limit - 1;
			List<List<Object>> walk = walkAllKeys(startId, pauseId);
//			if (null == walk || walk.size() == 0)
//				break;
			if(null != walk){
				dataCache.addAll(walk);
			}
			startId = pauseId + 1;
			if(startId > endId)
				break;
		}
	}
	public void getStructure(boolean isVerbose){
		getWithIdLimited(null, 1, 1, isVerbose);
	}
	public void getAllData(){
		getAllData(false);
	}
	public void getAllData(boolean isVerbose){
		 getWithIdLimited(null, 1, -1, isVerbose);
	}
	public void getAllData(int startId, int endId, boolean isVerbose){
		getWithIdLimited(null,  startId, endId, isVerbose);
	}
	/**
	 * 
	 * @param selectedKeyList the keyList that want to get
	 * 
	 */
	public void getSelectedColumns(List<String> selectedKeyList){
		 getSelectedColumns(selectedKeyList, false);
	}
	
	public void getSelectedColumns(List<String> selectedKeyList, boolean isVerbose){
		getWithIdLimited(selectedKeyList, 1, -1, isVerbose);
	}
	
	/**
	 * 
	 * @param selectedKey the key that want to get
	 * @return List returns the specific class of list
	 */
	public <T> List<T> getSelectedColumn(String selectedKey, Class<T> type){
		return getSelectedColumn(selectedKey, 1, -1, type, false);
	}
	public <T> List<T> getSelectedColumn(String selectedKey, Class<T> type, boolean isVerbose){
		return getSelectedColumn(selectedKey, 1, -1, type, isVerbose);
	}
	public <T> List<T> getSelectedColumn(String selectedKey, int startId, int endId, Class<T> type){
		return getSelectedColumn(selectedKey, startId, endId, type, false);
	}
	public <T> List<T> getSelectedColumn(String selectedKey, int startId, int endId, Class<T> type, boolean isVerbose){
		if(key2Index.containsKey(selectedKey)){
			List<T> resultList = new ArrayList<T>();
			getWithIdLimited(Arrays.asList(selectedKey), startId, endId, isVerbose);
			for(List<Object> objList:dataCache){
				resultList.add((type.cast(objList.get(0))));
			}
			return resultList;
		}else{
			System.out.println("[ErrorInfo]SELECTED KEY " + selectedKey + "NOT FOUND IN THE kyeList!");
			return null;
		}
	}
	
	public void getWithIdLimited(List<String> selectedKeyList, int startId, int endId){
		getWithIdLimited(selectedKeyList, startId, endId, false);
	}
	public void getWithIdLimited(List<String> selectedKeyList, int startId, int endId, boolean isVerbose) {
		getAllKeys(startId, endId, isVerbose);
		//调用general中的方法
		List<String> selectedKeysNew = new ArrayList<String>();
		selectedKeysNew = General.inputKeyListFilterString(selectedKeyList, keyList, isVerbose);
		if(selectedKeysNew.isEmpty()){
			selectedKeysNew.addAll(keyList);
		}
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		if(gl.extract(selectedKeysNew, isVerbose))
		{
			keyList.clear();
			keyList.addAll(gl.getKeyList());
			
			key2Index.clear();
			key2Index.putAll(gl.getKey2Index());
			
			dataCache.clear();
			for(List<Object> objListI:gl.getDataCache()){
				List<Object> objList = new ArrayList<Object>();
				objList.addAll(objListI);
				dataCache.add(objList);
			}
		}
	}
	
	public <T> List<T> getSelectedColumn(String selectedKey, String conditionKey, Object value, Class<T> type){
		Map<String, List<Object>> condition = new HashMap<String, List<Object>>();
		condition.put(conditionKey, Arrays.asList(value));
		getWithCondition(Arrays.asList(selectedKey), condition);
		if(null != dataCache){
			List<T> resultList = new ArrayList<T>();
			for(List<Object> objList:dataCache){
				resultList.add((type.cast(objList.get(0))));
			}
			return resultList;
		}else{
			System.out.println("[ErrorInfo]NOTHING FOUND!");
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
		Map<String, List<Object>> conditionNew = new HashMap<String, List<Object>>();
		if(null != condition){
			for(String key:condition.keySet()){
				if(null != condition.get(key)){
					List<Object> objList = new ArrayList<Object>();
					objList.add(condition.get(key));
					conditionNew.put(key, objList);
				}else{
					conditionNew.put(key, null);
				}
			}
			return getWithCondition(null, conditionNew, orderBy, sequence, type);
		}else{
			System.out.println("[GetWithConditionInfo]Condition IS NULL!DO NOTHING!");
			return null;
		}
	}
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
		getWithCondition(keyList, condition, orderBy, sequence);
		return getWrappedList(type);
	}
	
	public void getWithConditionString(List<String> keyList, Map<String, List<String>> condition) {
		Map<String, List<Object>> conditionNew = new HashMap<String, List<Object>>();
		for(String key:condition.keySet()){
			List<Object> objList =  new ArrayList<Object>();
			if(null != condition.get(key)){
				for(String field:condition.get(key)){
					objList.add(field);
				}
				conditionNew.put(key, objList);
			}else{
				conditionNew.put(key, null);
			}
		}
		getWithCondition(keyList, conditionNew, null, null);
	}
	public void getWithCondition(List<String> keyList, Map<String, List<Object>> condition) {
		getWithCondition(keyList, condition, null, null);
	}
	public void getWithCondition(List<String> keyList, Map<String, List<Object>> condition, String orderBy, String sequence) {
		StringBuilder sql = new StringBuilder();
		// 加上id为了排序和标记
		sql.append("SELECT ");
		// 更新keyList
		if (null != keyList) {
			this.keyList.clear();
			this.keyList.addAll(keyList);
		}
		for (String key : this.keyList) {
			sql.append(_safeSurroundings(key) + ",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" FROM " + dbName + "." + tableName + " WHERE ");
		// 记录condition中key的顺序
		List<String> conditionList = new ArrayList<String>();
		for (String key : condition.keySet()) {
			if(null != condition.get(key)){
				conditionList.add(key);
				sql.append(_safeSurroundings(key) + " IN(");
				for (int i = 0; i < condition.get(key).size(); i++) {
					sql.append("?,");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(")");
			}else{
				sql.append(_safeSurroundings(key) + " IS NULL ");
			}
			sql.append(" AND ");
		}

		sql = sql.delete(sql.length() - 5, sql.length());
		
		if(null != orderBy && this.keyList.contains(orderBy.toLowerCase())){
			sql.append(" ORDER BY " + _safeSurroundings(orderBy));
		}
		if(null != sequence && sequence.equalsIgnoreCase("desc")){
			sql.append(" DESC ");
		}
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			int count = 1;
			ps = conn.prepareStatement(sql.toString());

			for (String key : conditionList) {
				for (Object field : condition.get(key)) {
					if(null != field){
						if(String.class.isInstance(field)) {
							ps.setString(count++, (String)field);
						}else {
							ps.setInt(count++, (Integer)field);
						}
					}else{
						if(String.class.isInstance(field)) {
							ps.setNull(count++, Types.INTEGER);
						}else {
							ps.setNull(count++, Types.VARCHAR);
						}
					}
				}
			}
			System.out.println(ps.toString());
			rs = ps.executeQuery();

			// 构造dataCache
			while (rs.next()) {
				List<Object> result = new ArrayList<Object>();
				result = _construct(rs);
				dataCache.add(result);
			}

			// 更新key2Index
			Map<String, ColumnStructure> key2IndexTmp = new HashMap<String, ColumnStructure>();
			key2IndexTmp.putAll(key2Index);
			key2Index.clear();
			int ii = 0;
			for (String key : this.keyList) {
				ColumnStructure cs = new ColumnStructure();
				cs.index = ii++;
				cs.type = key2IndexTmp.get(key).type;
				key2Index.put(key, cs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
	}
	
	public void getDistinctList(String distinctKey){
		this.keyList.clear();
		this.keyList.add(distinctKey);
		StringBuilder sql = new StringBuilder();
		// 加上id为了排序和标记
		sql.append("SELECT DISTINCT " + _safeSurroundings(distinctKey) + " FROM " + dbName + "." + tableName + " WHERE " + _safeSurroundings(distinctKey) + " IS NOT NULL");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			System.out.println(ps.toString());
			rs = ps.executeQuery();

			// 构造dataCache
			while (rs.next()) {
				List<Object> result = new ArrayList<Object>();
				result = _construct(rs);
				dataCache.add(result);
			}

			// 更新key2Index
			Map<String, ColumnStructure> key2IndexTmp = new HashMap<String, ColumnStructure>();
			key2IndexTmp.putAll(key2Index);
			key2Index.clear();
			int ii = 0;
			for (String key : this.keyList) {
				ColumnStructure cs = new ColumnStructure();
				cs.index = ii++;
				cs.type = key2IndexTmp.get(key).type;
				key2Index.put(key, cs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
	}
	public List<String> getDistinctStringList(String distinctKey){
		getDistinctList(distinctKey);
		return getStringList();
	}
	public void getDistinctWithCondition(String distinctKey, Map<String, List<String>> condition) {
		this.keyList.clear();
		this.keyList.add(distinctKey);
		StringBuilder sql = new StringBuilder();
		// 加上id为了排序和标记
		sql.append("SELECT DISTINCT " + _safeSurroundings(distinctKey) + " FROM " + dbName + "." + tableName + " WHERE ");
		// 记录condition中key的顺序
		List<String> conditionList = new ArrayList<String>();
		for (String key : condition.keySet()) {
			conditionList.add(key);
			sql.append(_safeSurroundings(key) + " IN(");
			for (int i = 0; i < condition.get(key).size(); i++) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") AND ");
		}

		sql = sql.delete(sql.length() - 5, sql.length());

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			int count = 1;
			ps = conn.prepareStatement(sql.toString());

			for (String key : conditionList) {
				String type = key2Index.get(key).type;
				for (String field : condition.get(key)) {
					if (type.equalsIgnoreCase("int")) {
						ps.setInt(count++, Integer.valueOf(field));
					} else {
						ps.setString(count++, field);
					}
				}
			}
			System.out.println(ps.toString());
			rs = ps.executeQuery();

			// 构造dataCache
			while (rs.next()) {
				List<Object> result = new ArrayList<Object>();
				result = _construct(rs);
				dataCache.add(result);
			}

			// 更新key2Index
			Map<String, ColumnStructure> key2IndexTmp = new HashMap<String, ColumnStructure>();
			key2IndexTmp.putAll(key2Index);
			key2Index.clear();
			int ii = 0;
			for (String key : this.keyList) {
				ColumnStructure cs = new ColumnStructure();
				cs.index = ii++;
				cs.type = key2IndexTmp.get(key).type;
				key2Index.put(key, cs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
	}

	private List<Object> _construct(ResultSet rs) throws SQLException {
		List<Object> result = new ArrayList<Object>();
		for (String key : this.keyList) {
			ColumnStructure cs = key2Index.get(key);
			if (cs.type.equalsIgnoreCase("int")) {
				result.add(rs.getInt(key));
			}else if(cs.type.equalsIgnoreCase("timestamp")){
				result.add(rs.getTimestamp(key));
			}else{
				result.add(rs.getString(key));
			}
		}
		return result;
	}

	private String _safeSurroundings(String str) {
		return "`" + str + "`";
	}

	//通过removeKeyList链表来删除列，若key不存在则跳过此key
	/**
	 * remove the relative data according to the removeKeyList, if some key is not exist, skip it
	 * @param removeKeyList the keyList that want to remove
	 * @return  <ul>
	 * 			    <li><b>True</b> if success
	 * 				<li><b>False</b> if failed
	 * 		    </ul>
	 * @see <ul>
	 * 			<li>{@link #getDataCache()} returns the dataCache 
	 * 			<li>{@link #getKeyList()} returns the keyList
	 * 			<li>{@link #getKey2Index()} returns the key2Index
	 * 		</ul>
	 */
	
	public void printCount(){
		if(null != dataCache && 0!= dataCache.size()){
			System.out.println("[PrintInfo]TOTAL READ RECORD NUMBERS:" + dataCache.size());
		}else
		{
			System.out.println("[PrintInfo]TOTAL READ RECORD NUMBERS:" + 0);
		}
	}
	public void printStructure() {
		if (null != keyList && null != key2Index) {
			System.out.println("------------------Structure--------------");
			for (String key : keyList) {
				System.out.print(key + " ");
				System.out.print(key2Index.get(key).index + " ");
				System.out.print(key2Index.get(key).type + " ");
				if (null != dataCache && !dataCache.isEmpty()){
					Object o = dataCache.get(0).get(keyList.indexOf(key));
					if(Integer.class.isInstance(o)){
						System.out.print("Integer ");
					}else if(String.class.isInstance(o)){
						System.out.print("String ");
					}else{
						System.out.print("Unknow Type!");
					}
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]KeyList IS EMPTY!");
		}
	}

	public void printdataCache() {
		if (null != dataCache && !dataCache.isEmpty()) {
			System.out.println("------------------dataCache--------------");
			for (List<Object> objList : dataCache) {
				for (Object str : objList) {
					System.out.print(str);
					System.out.print("   ");
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]dataCache IS EMPTY!");
		}
	}

	public Map<String, ColumnStructure> getKey2Index() {
		return key2Index;
	}

	public List<List<Object>> getDataCache() {
		return dataCache;
	}

	public <T> List<T> getWrappedList(Class<T> type) {
		//调用general方法
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		return general.getWrappedList(type);
	}
	
	public List<Integer> getIntegerList(){
		//调用general方法
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		return general.getIntegerList();
	}
	
	public List<String> getStringList(){
		//调用general方法
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		return general.getStringList();
	}
	
	public List<String> getKeyList() {
		return keyList;
	}
}
