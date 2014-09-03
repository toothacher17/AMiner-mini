package SchoolSearch.services.utils.dataUpdateTools.basic.manipulate;
/**
 * @author CX
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.model.MergedGroup;

public class General {
	//待操作的数据导入
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;

	public static ColumnStructure columnStructureCreator(Integer index, String type){
		ColumnStructure cs = new ColumnStructure();
		if(null != index && null != type && !type.isEmpty()){
			cs.index = index;
			cs.type = type;
		}
		return cs;
	}
	/**
	 * 根据keyListRef作为参考对inputKey进行过滤，,若返回KeyList为空则表示被滤除
	 * @param inputKey，可为null
	 * @param keyListRef 参考List，可为null
	 * @param isVerbose
	 * @return 返回过滤后的key，不会为null，至多为Empty
	 */
	public static String inputKeyFilterString(String inputKey, List<String> keyListRef, boolean isVerbose){
		String inputKeyNew = new String();
		List<String> inputKeyList = new ArrayList<String>();
		List<String> inputKeyListNew = new ArrayList<String>();
		inputKeyList.add(inputKey);
		inputKeyListNew = inputKeyListFilterString(inputKeyList, keyListRef, isVerbose);
		if(!inputKeyListNew.isEmpty()){
			inputKeyNew = inputKeyListNew.get(0);
		}
		return inputKeyNew;
	}
	/**
	 * 根据keyListRef作为参考对inputKeyList进行过滤，,若返回KeyList为空则表示被滤除
	 * @param inputKeyList，可为null
	 * @param keyListRef 参考List，可为null
	 * @param isVerbose
	 * @return List<String>不会为null,至多为Empty
	 */
	public static List<String> inputKeyListFilterString(List<String> inputKeyList, List<String> keyListRef, boolean isVerbose){
		List<String> inputKeyListNew = new ArrayList<String>();
		List<String> keyListRefNew = new ArrayList<String>();
		//所有值转换为小写,同时过滤为null的值
		if(null != keyListRef && !keyListRef.isEmpty() && null != inputKeyList && !inputKeyList.isEmpty()){
			for(String key:inputKeyList){
				if(null != key){
					inputKeyListNew.add(key.toLowerCase().trim());
				}
			}
			for(String key:keyListRef){
				if(null != key){
					keyListRefNew.add(key.toLowerCase().trim());
				}
			}
			// 记录不存在的key
			List<String> errorKeyList = new ArrayList<String>();
			for (String key : inputKeyListNew) {
				if (!keyListRef.contains(key)) {
					if(isVerbose){
						System.out.println("[KeyListFilterInfo]KEY " + key+ " IS NOT EXIST!SKIP IT!");
					}
					errorKeyList.add(key);
				}else{
					if(isVerbose){
						System.out.println("[KeyListFilterInfo]KEY " + key+ " PASSED SUCCESSFULLY!");
					}
				}
			}
			// 去掉不存在的key
			inputKeyListNew.removeAll(errorKeyList);
		}
		return inputKeyListNew;
	}
	/**
	 * 根据keyListRef作为参考对inputKeyIndexList进行过滤,检查Index是否越界，若返回KeyList为空则表示被滤除
	 * @param inputKeyIndexList
	 * @param keyListRef 参考List
	 * @param isVerbose
	 * @return List<String>不会为null,至多为Empty
	 */
	public static List<String> inputKeyListFilterInteger(List<Integer> inputKeyIndexList, List<String> keyListRef, boolean isVerbose){
		List<String> inputKeyList = new ArrayList<String>();
		List<String> inputKeyListNew = new ArrayList<String>();
		List<String> keyListRefNew = new ArrayList<String>();
		if(null != keyListRef && !keyListRef.isEmpty() && null != inputKeyIndexList && !inputKeyIndexList.isEmpty()){
			//滤除keyListRef中为null的值
			for(String key:keyListRef){
				if(null != key){
					keyListRefNew.add(key);
				}
			}
			int size = keyListRefNew.size();
			for(Integer id:inputKeyIndexList){
				if(null != id){
					if(id<size && id >=0){
						inputKeyList.add(keyListRefNew.get(id));
					}else{
						System.out.print("[ErrorInfo]OUT OF RANGE!TRY TO REMOVE INDEX" + id + " OF " + size);
						inputKeyList.clear();
						break;
					}
				}
			}
			inputKeyListNew = inputKeyListFilterString(inputKeyList, keyListRef, isVerbose);
		}
		return inputKeyListNew;
	}
	/**
	 * 直接从数据库构造出general，省去{@link ReadFromDataBase()}
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	public static General getIndependentInstance(String dbName, String tableName){
		return new General(dbName, tableName);
	}
	/**
	 * 生成空的general，常常作为容器用于新表的拼接
	 * @return
	 */
	public static General getIndependentInstance(){
		return new General();
	}
	/**
	 * 常规用法，通过外面传入的keyList, key2Index和dataCache来构造general
	 * @param keyList
	 * @param key2Index
	 * @param dataCache
	 * @return
	 */
	public static General getIndependentInstance(List<String> keyList, Map<String, ColumnStructure> key2Index, List<List<Object>> dataCache){
		return new General(keyList, key2Index, dataCache);
	}
	/**
	 * 直接从数据库构造出general，省去{@link ReadFromDataBase()}
	 * @param keyList
	 * @param key2Index
	 * @param dataCache
	 */
	public General(String dbName, String tableName){
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, tableName);
		rd.getAllData();
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
		//初始化待插入的Cache信息
		this.keyList.addAll(rd.getKeyList());
		
		this.key2Index.putAll(rd.getKey2Index());
		
		//初始化dataCache,值传递
		for(List<Object> objListI:rd.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	/**
	 * 常规用法，通过外面传入的keyList, key2Index和dataCache来构造general
	 * @param keyList
	 * @param key2Index
	 * @param dataCache
	 */
	public General(List<String> keyList, Map<String, ColumnStructure> key2Index, List<List<Object>> dataCache){
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
		//初始化待插入的Cache信息
		for(String key:keyList){
			this.keyList.add(key.toLowerCase().trim());
		}
		for(String key:key2Index.keySet()){
			ColumnStructure cs = new ColumnStructure();
			cs.index = key2Index.get(key).index;
			cs.type = key2Index.get(key).type;
			this.key2Index.put(key.toLowerCase().trim(),cs);
		}
		
		//初始化dataCache,值传递
		for(List<Object> objListI:dataCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	/**
	 * 生成空的general，常常作为容器用于新表的拼接
	 */
	public General(){
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
	}
	/**
	 * 针对dataCache中的每一行进行过滤，滤除所有凡是包含null的行
	 * @param isVerbose
	 */
	public void shrinkRows(boolean isVerbose){
		String shrinkKey = null;
		shrinkRows(shrinkKey, isVerbose);
	}
	/**
	 * 针对dataCache中的每一行进行过滤，滤除所有凡是shrinkKey字段包含null的行
	 * @param shrinkKey
	 * @param isVerbose
	 */
	public void shrinkRows(String shrinkKey, boolean isVerbose){
		List<String> shrinkKeyList = new ArrayList<String>();
		shrinkKeyList.add(shrinkKey);
		shrinkRows(shrinkKeyList, isVerbose);
	}
	/**
	 * 针对dataCache中的每一行进行过滤，滤除所有凡是shrinkKeyList字段包含null的行
	 * @param shrinkKeyList
	 * @param isVerbose
	 */
	public void shrinkRows(List<String> shrinkKeyList, boolean isVerbose){
		List<String> shrinkKeyListNew = new ArrayList<String>();
		shrinkKeyListNew = General.inputKeyListFilterString(shrinkKeyList, keyList, isVerbose);
		if(shrinkKeyListNew.isEmpty()){
			shrinkKeyListNew.addAll(keyList);
			System.out.print("[ShrinkRowsInfo]shrinkKeyList IS EMPTY!USE ALL KEYS TO SHRINK:");
			for(String key:keyList){
				System.out.print(key + " ");
			}
			System.out.println();
		}
		if(!dataCache.isEmpty()){
			int sizePre = dataCache.size();
			//记录待删除的行号
			List<Integer> rowNumList = new ArrayList<Integer>();
			for(int i=0; i<dataCache.size(); i++){
				if(null != dataCache.get(i)){
					boolean isDelete = false;
					for(String key:shrinkKeyListNew){
						Object o = dataCache.get(i).get(key2Index.get(key).index);
						if(null == o || o.equals("")){
							isDelete = true;
							break;
						}
					}
					if(isDelete){
						rowNumList.add(i);
					}
				}else{
					rowNumList.add(i);
				}
			}
			//从小到大排序
			Collections.sort(rowNumList);
			
			//从后往前删除
			int length = rowNumList.size();
			for(int i=length-1; i>=0; i--){
				dataCache.remove(rowNumList.get(i).intValue());
			}
			int sizeAfter = dataCache.size();
			if(isVerbose){
				System.out.println("[ShrinkRowsInfo]SHRINK SUCCESSFULLY!SIZE " + sizePre + " SHRINKS TO " + sizeAfter);
			}
		}else{
			System.out.println("[ShrinkRowsInfo]dataCache IS EMPTY!NO NEED TO SHRINK!DO NOTHING!");
		}
	}
	
	/**
	 * 按顺序依次转换，注意前后keyList大小一致
	 * @param newKeyList
	 */
	public void changeKeyListTo(List<String> newKeyList){
		changeKeyListTo(newKeyList, false);
	}
	/**
	 * 按顺序依次转换，注意前后keyList大小一致
	 * @param newKeyList
	 * @param isVerbose
	 */
	public void changeKeyListTo(List<String> newKeyList, boolean isVerbose){
		if(null !=newKeyList && !newKeyList.isEmpty()){
			List<String> newKeyList2 = General.inputKeyListFilterString(newKeyList, keyList, isVerbose);
			if(keyList.size() == newKeyList2.size()){
				for(int i=0; i<keyList.size(); i++){
					changeKey(keyList.get(i), newKeyList2.get(i), isVerbose);
				}
			}else{
				System.out.println("[ChangeKeyListErrorInfo]SIZE DO NO MATCH:" + keyList.size() + "<-------->" + newKeyList2.size());
			}
		}else{
			System.out.println("[ChangeKeyListErrorInfo]NEW KEY IS EMPTY!");
		}
	}
//	public void append(List<String> keyList, Map<String, ColumnStructure> key2Index, List<List<Object>> dataCach, boolean isVerbose){
//		for(String key:keyList){
//			General gl = General.getIndependentInstance(keyList, key2Index, dataCach);
//			if(gl.extract(key)){
//				String insertKey = gl.getKeyList().get(0);
//				insertColumn(-1, insertKey, gl.getKey2Index().get(key), gl.getObjList(), isVerbose);
//			}else{
//				System.out.println("[AppendErrorInfo]EXTRACT " + key + " FAILED!");
//			}
//		}
//	}
	/**
	 * 改变表头的KeyList名称，注意不改变类型
	 * @param oldKey
	 * @param newKey
	 */
	public void changeKey(String oldKey, String newKey){
		changeKey(oldKey, newKey, false);
	}
	/**
	 * 改变表头的KeyList名称，注意不改变类型
	 * @param oldKey
	 * @param newKey
	 * @param isVerbose
	 */
	public void changeKey(String oldKey, String newKey, boolean isVerbose){
		String oldKey2 = new String();
		oldKey2 = General.inputKeyFilterString(oldKey, keyList, isVerbose);
		if(!oldKey2.isEmpty() && null != newKey && !newKey.isEmpty()){
			ColumnStructure cs = new ColumnStructure();
			cs = key2Index.get(oldKey2);
			//更新keyList
			keyList.set(cs.index, newKey);
			//更新key2Index
			key2Index.remove(oldKey2);
			key2Index.put(newKey, cs);
		}
	}
	public void fuzzyFilterRowsWithCondition(String key, List<Object> value){
		String newKey = General.inputKeyFilterString(key, keyList, true);
		List<Integer> removeIndexList = new ArrayList<Integer>();
		if(!newKey.isEmpty()){
			int index = key2Index.get(newKey).index;
			for(int i=0; i<dataCache.size(); i++){
				Object o = dataCache.get(i).get(index);
				boolean isRemove = true;
				if(null != o && String.class.isInstance(o)){
					if(null != value && !value.isEmpty()){
						for(Object va:value){
							if(va.toString().equalsIgnoreCase(o.toString())){
								isRemove = false;
								break;
							}
						}
					}else{
						System.out.println("[FuzzyFilterRowWithConditionErrorInfo]value IS NULL!DO NOTHING!");
						return;
					}
				}
				if(isRemove){
					removeIndexList.add(i);
				}
			}
			//删除不符合condition的行
			Collections.sort(removeIndexList);
			int size = removeIndexList.size();
			for(int i=size-1; i>=0; i--){
				 dataCache.remove(removeIndexList.get(i).intValue());
			}
		}
	}
	public void filterRowsWithCondition(String key, Object value){
		String newKey = General.inputKeyFilterString(key, keyList, true);
		List<Integer> removeIndexList = new ArrayList<Integer>();
		if(!newKey.isEmpty()){
			int index = key2Index.get(newKey).index;
			for(int i=0; i<dataCache.size(); i++){
				Object o = dataCache.get(i).get(index);
				if(null == value){
					if(null != o){
						removeIndexList.add(i);
					}
				}else{
					if(null == o || !o.equals(value)){
						removeIndexList.add(i);
					}
				}
			}
			//删除不符合condition的行
			int size = removeIndexList.size();
			for(int i=size-1; i>=0; i--){
				 dataCache.remove(removeIndexList.get(i).intValue());
			}
		}
	}
	/**
	 * 将dataCache封装成List<Object>返回，注意此时只能有一列
	 * {@link extract()}与{@link getObjList()}方法结合
	 * @param selectedKey
	 * @return
	 */
	public List<Object> extractObject(String selectedKey){
		List<Object> selectedList = new ArrayList<Object>();
		if(extract(selectedKey)){
			selectedList = getObjList();
		}
		return selectedList;
	}
	/**
	 * 将dataCache封装成List<String>返回，注意此时只能有一列
	 * {@link extract()}与{@link getStringList()}方法结合
	 * @param selectedKey
	 * @return
	 */
	public List<String> extractString(String selectedKey){
		List<String> selectedList = new ArrayList<String>();
		if(extract(selectedKey)){
			selectedList = getStringList();
		}
		return selectedList;
	}
	/**
	 * 抽取selectedKey字段，存储到dataCache里
	 * @param selectedKey
	 * @return
	 */
	public boolean extract(String selectedKey){
		return extract(selectedKey,false);
	}
	/**
	 * 抽取selectedKey字段，存储到dataCache里
	 * @param selectedKey
	 * @param isVerbose
	 * @return
	 */
	public boolean extract(String selectedKey, boolean isVerbose){
		List<String> selectedKeyList = new ArrayList<String>();
		selectedKeyList.add(selectedKey);
		return extract(selectedKeyList, isVerbose);
	}
	/**
	 * 抽取selectedKeys字段，存储到dataCache里
	 * @param selectedKeys
	 * @return
	 */
	public boolean extract(List<String> selectedKeys){
		return extract(selectedKeys,false);
	}
	/**
	 * 抽取selectedKeys字段，存储到dataCache里
	 * @param selectedKeys
	 * @param isVerbose
	 * @return
	 */
	public boolean extract(List<String> selectedKeys, boolean isVerbose){
		boolean result = true;
		List<String> selectedKeysNew = new ArrayList<String>();
		selectedKeysNew = General.inputKeyListFilterString(selectedKeys, keyList, isVerbose);
		if(selectedKeysNew.isEmpty()){
			System.out.println("[ExtractErrorInfo]selectedKeys IS EMPTY!DO NOTHING!");
			result = false;
		}else{
			//找出需要删除的keyList
			List<String> deleteKeyList = new ArrayList<String>();
			for(String key:keyList){
				if(!selectedKeysNew.contains(key)){
					deleteKeyList.add(key);
				}
			}
			result = removeColumnsByKeyList(deleteKeyList, isVerbose);
		}
		return result;
	}
	
	public General fork(String selectedKey, boolean isVerbose){
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(selectedKey,isVerbose);
		return gl;
	}
	public General fork(List<String> selectedKeyList, boolean isVerbose){
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(selectedKeyList,isVerbose);
		return gl;
	}
	public boolean insertColumnAtTailInteger(String keyName, List<Object> objList, boolean isVerbose){
		return insertColumnInteger(-1, keyName, objList, isVerbose);
	}
	public boolean insertColumnAtTailString(String keyName, List<Object> objList, boolean isVerbose){
		return insertColumnString(-1, keyName, objList, isVerbose);
	}
	public boolean insertColumnInteger(int index, String keyName, List<Object> objList, boolean isVerbose){
		ColumnStructure cs = new ColumnStructure();
		cs.type = "int";
		return insertColumn(index, keyName, cs, objList, isVerbose);
	}
	public boolean insertColumnString(int index, String keyName, List<Object> objList, boolean isVerbose){
		ColumnStructure cs = new ColumnStructure();
		cs.type = "char";
		return insertColumn(index, keyName, cs, objList, isVerbose);
	}
	public boolean insertColumnAtTail(String keyName, Object value){
		return insertColumnAtTail(keyName, value, false);
	}
	
	public boolean insertColumnAtTail(String keyName, Object value, boolean isVerbose){
		return insertColumn(-1, keyName, value, isVerbose);
	}
	
	public boolean insertColumn(int index, String keyName, Object value){
		return insertColumn(index, keyName, value,false);
	}
	
	public boolean insertColumn(int index, String keyName, Object value, boolean isVerbose){
		boolean result = true;
		//开始构造新dataObjectList
		List<Object> dataObjecList = new ArrayList<Object>();
		if(String.class.isInstance(value)){
			
		}
		for(int i=0; i<dataCache.size(); i++){
			dataObjecList.add(value);
		}
		//构造ColumnStructure
		ColumnStructure cs =  new ColumnStructure();
		cs.index = index;
		cs.type = String.class.isInstance(value)?"char":"int";
		
		result = insertColumn(index, keyName.toLowerCase(), cs, dataObjecList);
		
		return result;
	}
	
	public boolean insertColumn(int index, String newkey, ColumnStructure newcs, List<Object> newObjList){
		return insertColumn(index, newkey, newcs, newObjList, false);
	}
	
	//在索引号为index处插入一列数据,返回true则插入成功，否则失败
	public boolean insertColumn(int index, String newkey, ColumnStructure newcs, List<Object> newObjList, boolean isVerbose){
		boolean result = true;
		if(!key2Index.containsKey(newkey)){
			int indexNew = 0;
			//先进行index验证
			if(-1 == index){
				indexNew = keyList.size();
			}else if(index <= keyList.size() && index >=0){
				indexNew = index;
			}else{
				System.out.println("[PaddingErrorInfo]INDEX IF OUT OF RANGE!" + index + " OF " + (keyList.size()-1));
				return false;
			}
			
			//进行keyName检测
			if(key2Index.containsKey(newkey)){
				System.out.println("[PaddingErrorInfo]KEY " + newkey + " ALREADY EXIST!");
				return false;
			}
			
			//更新keyList
			keyList.add(indexNew, newkey); 
			//生成临时keyList表
			List<String> keyListTmp = new ArrayList<String>();
			for(String key:key2Index.keySet()){
				keyListTmp.add(key);
			}
			
			for(String key:keyListTmp){
				if(key2Index.get(key).index >=  indexNew){
					ColumnStructure cs = new ColumnStructure();
					cs.index = key2Index.get(key).index + 1;
					cs.type =  key2Index.get(key).type;
					key2Index.remove(key);
					key2Index.put(key, cs);
				}
			}
			newcs.index = indexNew;
			key2Index.put(newkey,newcs);
			
			//更新dataCache
			if(dataCache.isEmpty()){
				for(Object o:newObjList){
					List<Object> objL =  new ArrayList<Object>();
					objL.add(o);
					dataCache.add(objL);
				}
			}else{
				for(int i=0; i<dataCache.size(); i++){
					dataCache.get(i).add(indexNew, newObjList.get(i));
				}
			}
			if(isVerbose){
				System.out.println("[InsertKeyInfo]INSERT " + newkey + " IN INDEX " + indexNew + " SUCCESSFULLY!");
			}
//			}
		}else{
			if(isVerbose){
				System.out.println("[InsertKeyInfo]INSERT " + newkey + " IS NOT EXIST!SKIP THIS KEY!");
			}
			result = false;
		}	
		return result;
	}
	public boolean removeColumn(String key){
		return removeColumn(key, false);
	}
	
	public boolean removeColumn(String key, boolean isVerbose){
		List<String> removeKeyList = new ArrayList<String>();
		removeKeyList.add(key);
		return removeColumnsByKeyList(removeKeyList, isVerbose);
	}
	public boolean removeColumnsByKeyList(List<String> removeKeyList){
		return removeColumnsByKeyList(removeKeyList, false);
	}
	
	//通过removeKeyList链表来删除列，若key不存在则跳过此key
	public boolean removeColumnsByKeyList(List<String> removeKeyList, boolean isVerbose) {
		if(null == removeKeyList || removeKeyList.isEmpty()){
			return true;
		}else{
			List<String> removeKeyListNew = new ArrayList<String>();
			removeKeyListNew = General.inputKeyListFilterString(removeKeyList, keyList, isVerbose);
			boolean result = true;
			if(removeKeyListNew.isEmpty()){
				result = false;
			}else{
				//更新keyList
				keyList.removeAll(removeKeyListNew);
				
				//更新dataCache
				//记录待删除的index数组
				List<Integer> indexList = new ArrayList<Integer>();
				for(String key:removeKeyListNew){
					indexList.add(key2Index.get(key).index);
				}
				//从小到大排序
				Collections.sort(indexList);
				//从后面的的开始删除，不影响index
				for (int i = indexList.size()-1;i>=0;i--) {
					for(List<Object> objList:dataCache){
						objList.remove(indexList.get(i).intValue());
					}
				}
				
				//更新key2Index
				for(String removeKey:removeKeyListNew){
					int ind = key2Index.get(removeKey).index;
					//存储当前key2Index的key链表
					List<String> keyListTmp = new ArrayList<String>();
					for(String key:key2Index.keySet()){	
						keyListTmp.add(key);
					}
					
					//开始更新key2Index
					for(String key:keyListTmp){
						//大于ind表示在其后面，index需要减一更新
						if(key2Index.get(key).index > ind){
							ColumnStructure cs = new ColumnStructure();
							cs.index = key2Index.get(key).index -1;
							cs.type = key2Index.get(key).type;
							//先剔除
							key2Index.remove(key);
							//再加入
							key2Index.put(key,cs);
						}
					}
					key2Index.remove(removeKey);
				}
			}
			return result;
		}
	}
	public boolean removecolumn(int index){
		return removecolumn(index, false);
	}
	public boolean removecolumn(int index, boolean isVerbose){
		List<Integer> indexList = new ArrayList<Integer>();
		indexList.add(index);
		return removeColumnsByIndexList(indexList, isVerbose);
	}
	
	
	public boolean removeColumnsByIndexList(List<Integer> indexList){
		return removeColumnsByIndexList(indexList,false);
	}
	
	public boolean removeColumnsByIndexList(List<Integer> indexList, boolean isVerbose){
		boolean result = true;
		List<String> removeKeyListNew = new ArrayList<String>();
		removeKeyListNew = General.inputKeyListFilterInteger(indexList, keyList, isVerbose);
		if(removeKeyListNew.isEmpty()){
			result = false;
		}else{
			result = removeColumnsByKeyList(removeKeyListNew,isVerbose);
		}
		return result;
	}
	
	
	public boolean replaceInteger(String key, List<Integer> newIntegerList){
		List<Object> newObjList = new ArrayList<Object>();
		for(Integer ind:newIntegerList){
			newObjList.add(ind);
		}
		return replace(key, null, newObjList, false);
	}
	public boolean replaceString(String key, List<String> newStringList){
		List<Object> newObjList = new ArrayList<Object>();
		for(String str:newStringList){
			newObjList.add(str);
		}
		return replace(key, null, newObjList, false);
	}
	public boolean replace(String key, List<Object> newObjList){
		return replace(key, null, newObjList, false);
	}
	public boolean replace(String key, ColumnStructure newcs, List<Object> newObjList){
		return replace(key, newcs, newObjList, false);
	}
	public boolean replace(String key, ColumnStructure newcs, List<Object> newObjList, boolean isVerbose){
		return replace(key, key, newcs, newObjList, isVerbose);
	}
	public boolean replace(String oldKey, String newKey, ColumnStructure newcs, List<Object> newObjList){
		return replace(oldKey, newKey, newcs, newObjList, false);
	}
	public boolean replace(String oldKey, String newKey, ColumnStructure newcs, List<Object> newObjList, boolean isVerbose){
		boolean result = true;
		if(key2Index.containsKey(oldKey)){
			int replaceIndex= key2Index.get(oldKey).index;
			result = replace(replaceIndex, newKey, newcs, newObjList, isVerbose);
		}else{
			System.out.println("[ReplaceInfo]" + oldKey + " IS NOT EXIST!DO NOTHING");
			result =false;
		}
		return result;
	}
	public boolean replace(int replaceIndex, String newKey, List<Object> newObjList){
		return replace(replaceIndex, newKey, null, newObjList, false);
	}
	public boolean replace(int replaceIndex, String newKey, List<Object> newObjList, boolean isVerbose){
		return replace(replaceIndex, newKey, null, newObjList, isVerbose);
	}
	public boolean replace(int replaceIndex, String newKey, ColumnStructure newcs, List<Object> newObjList){
		return replace(replaceIndex, newKey, newcs, newObjList, false);
	}
	public boolean replace(int replaceIndex, String newKey, ColumnStructure newcs, List<Object> newObjList, boolean isVerbose){
		boolean result = true;
		if(replaceIndex<keyList.size() && replaceIndex >=0){
			ColumnStructure cs = new ColumnStructure();
			//newcs为null表示表头不变
			if(null == newcs){
				cs.index = key2Index.get(keyList.get(replaceIndex)).index;
				cs.type = key2Index.get(keyList.get(replaceIndex)).type;
			//否则更新
			}else{
				cs.index = newcs.index;
				cs.type = newcs.type;
			}
			if(removecolumn(replaceIndex, isVerbose)){
				result = insertColumn(replaceIndex, newKey, cs, newObjList, isVerbose);
			}else{
				result = false;
			}
		}else{
			System.out.print("[ErrorInfo]OUT OF RANGE!TRY TO REPLACE INDEX" + replaceIndex + " OF " + keyList.size());
		}
		return result;
	}
	
	/**
	 * 利用oldKey字段和newKey字段作为参考，找出输入oldList对应的输出List，适用于一对一的情况，多对一将会出现诡异的错误
	 * @param oldKey
	 * @param newKey
	 * @param oldList
	 * @param isVerbose
	 * @return
	 */
	public List<Object> reflect(String oldKey, String newKey, List<Object> oldList, boolean isVerbose){
		String oldKey2 = General.inputKeyFilterString(oldKey, keyList, isVerbose);
		String newKey2 = General.inputKeyFilterString(newKey, keyList, isVerbose);
		List<Object> reflectList =  new ArrayList<Object>();
		if(oldKey2.isEmpty() || newKey2.isEmpty()){
			System.out.println("[ReflectErrorInfo]EMTPY!DO NOTHING!");
		}else{
			//先构造HashMap
			Map<Object, Object> reflectMap =  new HashMap<Object, Object>();
			for(List<Object> objList:dataCache){
				reflectMap.put(objList.get(key2Index.get(oldKey2).index), objList.get(key2Index.get(newKey2).index));
			}
			
			for(Object key:oldList){
				if(reflectMap.containsKey(key)){
					reflectList.add(reflectMap.get(key));
				}else{
					reflectList.add(null);
				}
			}
		}
		return reflectList;
	}
	public boolean Merge(MergedGroup mg){
		return Merge(mg, false);
	}
	
	/**
	 * 合并字段
	 * @param mg
	 * @param isVerbose
	 * @return
	 */
	public boolean Merge(MergedGroup mg, boolean isVerbose){
		boolean result = true;
		if(null == mg){
			return false;
		}
		
		//保存最小值索引号，决定合并成一列后数据的位置
		int position = Integer.MAX_VALUE;
		
		//找出最小的索引号，即带插入的位置，同时剔除不存在的key;同时对mergedElements进行过滤
		List<String> mergedElementsTmp =  new ArrayList<String>();
		mergedElementsTmp.addAll(mg.mergedElements);
		for(String key:mergedElementsTmp){
			if(key2Index.containsKey(key)){
				position = key2Index.get(key).index<position?key2Index.get(key).index:position;
			}else{
				if(isVerbose){
					System.out.println("[MergedKeyInfo]" + key + " IS NOT EXIST!SKIP THIS KEY!");
				}
				
				//同时在mg里面priority将其对应的优先级删除
				if(null != mg.priority && !mg.priority.isEmpty()){
					Integer index = mg.mergedElements.indexOf(key);
					mg.priority.remove(index);
					for(int j=0;j<mg.priority.size(); j++){
						if(mg.priority.get(j) > index){
							Integer in  = mg.priority.get(j);
							mg.priority.add(j,in-1);
							mg.priority.remove(j+1);
						}
					}
					mg.mergedElements.remove(key);
				}
			}
		}
		
		//keyList不存在任何一个mergeElement中的元素，直接返回
		if(Integer.MAX_VALUE == position){
			if(isVerbose){
				System.out.print("[MergedKeyInfo]ALL KEYS ");
				for(String key:mg.mergedElements){
					System.out.print(key + " ");
				}
				System.out.println("ARE NOT EXIST, SKIP THIS MERGE GROUP!");
				System.out.println("--------------------------------------------------------------");
			}
		}else{
			//存储合并后的内容
			List<Object> insertedOjbList = new ArrayList<Object>();
			//构造insertedObjList
			for(List<Object> objList:dataCache){
				//若无优先级,则所有字段合并
				if(null == mg.priority || mg.priority.isEmpty()){
					//逐个字段取出来并拼接,默认为字符串
					StringBuilder sb = new StringBuilder();
					for(String key:mg.mergedElements){
						sb.append(objList.get(keyList.indexOf(key)));
					}
					//加入insertedObjList
					try{
					if(!mg.replacementType.equalsIgnoreCase("int")){
						insertedOjbList.add(sb.toString());
					}else{
						insertedOjbList.add(Integer.parseInt(sb.toString()));
					}
					}catch(NumberFormatException e){
						insertedOjbList.add(0);
					}
				}else{
					//存在优先级则依照priority选出第一个不为null的索引index
					int index = Integer.MAX_VALUE;
					
					for(Integer id:mg.priority){
						String key = mg.mergedElements.get(id);
						int ii = key2Index.get(key).index;
						if(null != objList.get(ii)){
							if(String.class.isInstance(objList.get(ii)) && "".equalsIgnoreCase((String)objList.get(ii))){
								continue;
							}else{
								index = ii;
								break;
							}
						}
					}
					
					//全为null的话，合并后的字段也为null
					if(Integer.MAX_VALUE == index){
						Object o = null;
						insertedOjbList.add(o);
					}else{
						insertedOjbList.add(objList.get(index));
					}
					
				}
			}
			
			
			//先删除列，若成功
			if(removeColumnsByKeyList(mg.mergedElements)){
				ColumnStructure cs = new ColumnStructure();
				cs.index = position;
				cs.type = mg.replacementType;
				//插入列，若成功
				if(insertColumn(position, mg.replacementValue, cs, insertedOjbList, isVerbose)){
					//打印结果
					if(isVerbose){
						System.out.print("[MergeKeyInfo]");
						for(String key:mg.mergedElements){
							System.out.print(key + " ");
						}
						System.out.println("MERGE TO " + mg.replacementValue);
						System.out.println("--------------------------------------------------------------");
					}
				}else{
					result = false;
					System.out.print("[MergeKeyInfo]INSERT FAILD!");
				}
			}else{
				System.out.print("[MergeKeyInfo]REMOVE FAILD!");
				result = false;
			}
		}
		return result;
	}
	public boolean MergeBatch(List<MergedGroup> mgList){
		return MergeBatch(mgList, false);
	}
	
	public boolean MergeBatch(List<MergedGroup> mgList, boolean isVerbose){
		boolean result = true;
		for(MergedGroup mg:mgList){
			if(!Merge(mg, isVerbose)){
				result = false;
				break;
			}
		}
		return result;
	}

	
	public void printStructure() {
		if (null != keyList && null != key2Index) {
			System.out.println("------------------Structure--------------");
			for (String key : keyList) {
				System.out.print(key + " ");
				System.out.print(key2Index.get(key).index + " ");
				System.out.print(key2Index.get(key).type + " ");
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

	public List<String> getKeyList() {
		return keyList;
	}

	public Map<String, ColumnStructure> getKey2Index() {
		return key2Index;
	}

	public List<List<Object>> getDataCache() {
		return dataCache;
	}
	public <T> List<T> getWrappedList(Class<T> type) {
		List<T> wrappedList = new ArrayList<T>();
		// 检查type的各字段与现有keyList是否不一致
		Field[] fields = type.getFields();
		// 先生成所有fieldName的List
		List<String> fieldNameList = new ArrayList<String>();
		for (Field field : fields) {
			fieldNameList.add(field.getName().toLowerCase());
		}

		for (String key : keyList) {
			if (!fieldNameList.contains(key)) {
				System.out.println("[WrappedInfo] " + key
						+ " IS NOT IN MODOLE " + type.toString());
				return wrappedList;
			}
		}

		for (List<Object> objList : dataCache) {
			try {
				Constructor<T> constructor = type.getConstructor();
				T t = constructor.newInstance();
				for (Field field : fields) {
					String key = field.getName().toLowerCase();
					if (key2Index.containsKey(key)) {
						field.set(t, objList.get(key2Index.get(key).index));
					}
				}
				wrappedList.add(t);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return wrappedList;
	}
	
	public List<Integer> getIntegerList(){
		List<Integer> intList = new ArrayList<Integer>();
		if(1 == keyList.size()){
			String type = key2Index.get(keyList.get(0)).type;
			for(List<Object> objList:dataCache){
				if(type.equalsIgnoreCase("int")){
					intList.add((Integer)objList.get(0));
				}else{
					intList.add(Integer.valueOf((String)objList.get(0)));
				}
			}
		}else{
			System.out.print("[ErrorInfo]MORE THAN ONE COLUMN!");
			for(String key:keyList){
				System.out.print(key + " ");
			}
			System.out.println();
		}
		return intList;
	}
	
	public List<String> getStringList(){
		List<String> strList = new ArrayList<String>();
		if(1 == keyList.size()){
			String type = key2Index.get(keyList.get(0)).type;
			for(List<Object> objList:dataCache){
				if(!type.equalsIgnoreCase("int")){
					strList.add((String)objList.get(0));
				}else{
					strList.add(((Integer)objList.get(0)).toString());
				}
			}
		}else{
			System.out.print("[ErrorInfo]MORE THAN ONE COLUMN!");
			for(String key:keyList){
				System.out.print(key + " ");
			}
			System.out.println();
		}
		return strList;
	}
	
	public List<Object> getObjList(){
		List<Object> objList = new ArrayList<Object>();
		if(1 == keyList.size()){
			for(List<Object> objL:dataCache){
				objList.add(objL.get(0));
			}
		}else{
			System.out.print("[ErrorInfo]MORE THAN ONE COLUMN!");
			for(String key:keyList){
				System.out.print(key + " ");
			}
			System.out.println();
		}
		return objList;
	}
	
	/**
	 * 暴力搜索，将字段refkey=reValue的行的targetKey字段值改为targetValue，不适用大范围数据修改
	 * @param refKey
	 * @param refValue
	 * @param targetKey
	 * @param tarValue
	 */
	public void changeValue(String refKey, Object refValue, String targetKey, Object tarValue){
		String refKeyChecked = General.inputKeyFilterString(refKey, keyList, true);
		String targetKeyChecked = General.inputKeyFilterString(targetKey, keyList, true);
		if(refKeyChecked.isEmpty() || targetKeyChecked.isEmpty()){
			System.out.println("[ChangeValueError]KEY NOT FOUND!PLEASE CHECK!");
		}else{
			Integer refIndex = key2Index.get(refKey).index;
			Integer tarIndex = key2Index.get(targetKey).index;
			for(List<Object> objList:dataCache){
				if(String.valueOf(objList.get(refIndex)).trim().equalsIgnoreCase(String.valueOf(refValue).trim())){
					System.out.println("[ChangeValueInfo]KEY CHANGED:");
					for(Object obj:objList){
						System.out.print(obj+ " ");
					}
					System.out.println();
					objList.set(tarIndex, tarValue);
					for(Object obj:objList){
						System.out.print(obj+ " ");
					}
					System.out.println();
				}
			}
		} 
	}
}
