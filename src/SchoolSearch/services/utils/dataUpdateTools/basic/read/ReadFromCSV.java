package SchoolSearch.services.utils.dataUpdateTools.basic.read;

/**
 * @author CX
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.utils.CSVUtil;

public class ReadFromCSV {
	private static ReadFromCSV instance;

	// 从CSV读入的数据
	private List<String[]> CSVCache;

	// 数据库表的field链表
	private List<String> keyList;

	// 数据库表的field字段
	private Map<String, ColumnStructure> key2Index;

	// 将CSV数据转换为中间Java数据结构
	private List<List<Object>> dataCache;

	// CSV文件绝对路径
	private String filePathStr;

	public static ReadFromCSV getInstance(String filePath) {
		if (null == instance) {
			instance = new ReadFromCSV(filePath);
		}
		return instance;
	}

	public static ReadFromCSV getIndependentInstance(String filePath) {
		return new ReadFromCSV(filePath);
	}

	public ReadFromCSV(String filePath) {
		filePathStr = filePath;
		CSVCache = new ArrayList<String[]>();
		keyList = new ArrayList<String>();
		key2Index = new HashMap<String, ColumnStructure>();
		dataCache = new ArrayList<List<Object>>();

	}

	public void load(){
		// 构造CSVCache
		try {
			loadCSV();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 构造keyList
		initKeyList();

		// 构造key2Index
		initKey2Index();

		// 构造dataCache
		initDataCache();
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

	private void loadCSV() throws Exception {
		File filePath = new File(filePathStr);
		if (filePath.exists()) {
			CSVCache.addAll(CSVUtil.getInstance().readCsv(filePath.toString()));
		} else {
			System.out.println("[ReadCSVFileInfo] " + filePath + " IS NOT EXIST!");
		}
	}

	private void initKeyList() {
		for (String key : Arrays.asList(CSVCache.get(0))) {
			keyList.add(key.toLowerCase().trim());
		}
	}

	private void initKey2Index() {
		int i = 0;
		for (String key : keyList) {
			ColumnStructure cs = new ColumnStructure();
			cs.index = i++;
			cs.type = "char";
			key2Index.put(key, cs);
		}
	}

	private void initDataCache() {
		for (int i = 1; i < CSVCache.size(); i++) {
			List<Object> objList = new ArrayList<Object>();
			for (int c = 0; c < CSVCache.get(i).length; c++) {
				if(null == CSVCache.get(i)[c] || CSVCache.get(i)[c].isEmpty()){
					Object o = null;
					objList.add(o);
				}else{
					objList.add(CSVCache.get(i)[c]);
				}
			}
			dataCache.add(objList);
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

	public void printCount(){
		for(List<Object> objList:dataCache){
			System.out.println(objList.size());
		}
	}
}
