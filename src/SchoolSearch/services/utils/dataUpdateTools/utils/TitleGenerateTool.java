package SchoolSearch.services.utils.dataUpdateTools.utils;
/**
 * @author CX
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class TitleGenerateTool {
private static TitleGenerateTool instance;

	private String dbName;
	private String tableName;
	
	
	private Map<String, List<String>> titleMap;
	private Map<String, String> titleMapString;
	
	private List<String> keyList; 
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;
	
	public static TitleGenerateTool getInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		if(null == instance)
			instance = new TitleGenerateTool(dbName, tableName, keyList, key2Index, dataCache);
		return instance;
	}
	
	public static TitleGenerateTool getIndependentInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		return new TitleGenerateTool(dbName, tableName, keyList, key2Index, dataCache);
	}
	
	public TitleGenerateTool(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache) {
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
		this.titleMap = new HashMap<String, List<String>>();
		this.titleMapString = new HashMap<String, String>();
		
		
		//初始化待插入的Cache信息
		this.keyList.addAll(keyList);
		this.key2Index.putAll(key2Index);
		//初始化dataCache,值传递
		for(List<Object> objListI:dataCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	
	public void process(){
		process("C:/data/titles.txt");
	}
	public void process(String filePathStr){
		initTitleMap(filePathStr);
		initTitleMapString();
		
		//抽取出nameList数据
		List<Object> nameList = new ArrayList<Object>();
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(Arrays.asList("name"));
		nameList.addAll(gl.getObjList());
		
		//生成titleList数据
		List<Object> titleList = new ArrayList<Object>();
		for(Object o:nameList){
			if(titleMapString.containsKey(o)){
				titleList.add(titleMapString.get(o));
			}else{
				titleList.add(null);
			}
		}
		
		//抽取出idList数据
		List<Object> idList = new ArrayList<Object>();
		gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(Arrays.asList("id"));
		idList.addAll(gl.getObjList());
		
		//利用General构造person_ext表的形式
		gl = General.getIndependentInstance();
		gl.insertColumnAtTailInteger("id", idList, true);
		gl.insertColumnAtTailString("title", titleList, true);
		gl.shrinkRows(true);
		
		
		AntiReduplicationCheck.prepareDB(dbName, tableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
		AntiReduplicationCheck.setReplaceKeyList("title",true);
		
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertAndUpdateWithConstruct(true, "id");
	}
	
	private void initTitleMapString(){
		if(null != titleMap){
			for(String key:titleMap.keySet()){
				StringBuilder sb =  new StringBuilder();
				for(String title:titleMap.get(key)){
					sb.append(title + "\n");
				}
				sb.deleteCharAt(sb.length()-1);
				titleMapString.put(key, sb.toString());
			}
		}
	}
	private void initTitleMap(String filePathStr){
		File file = new File(filePathStr);
		if(file.exists()){
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			String line = null;
			String title = null;
			try {
				while((line = reader.readLine())!=null) {
					line = line.trim();
					if(line.isEmpty()){
						continue;
					}else if(line.matches(".+:")){
						title = line.substring(0, line.length()-1);
						//说明此时line是人名
					}else if(null !=title){
						if(!titleMap.containsKey(line)){
							titleMap.put(line, new ArrayList<String>());
							titleMap.get(line).add(title);
						}else{
							if(!titleMap.get(line).contains(title)){
								titleMap.get(line).add(title);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void printTitleMap(){
		for(String key:titleMap.keySet()){
			System.out.print(key);
			System.out.println(titleMap.get(key));
		}
	}
	public void printTitleMapString(){
		for(String key:titleMapString.keySet()){
			System.out.print(key);
			System.out.println(titleMapString.get(key));
		}
	}
}
