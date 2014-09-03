package SchoolSearch.services.utils.dataUpdateTools.utils;
/**
 * @author CX
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class AvatarTransferTool {
	private static AvatarTransferTool instance;
	
	//数据库表的field链表
	private List<String> keyList; 
	
	//数据库表的field字段
	private Map<String, ColumnStructure> key2Index;
	
	//将CSV数据转换为中间Java数据结构
	private List<List<Object>> dataCache;
	
	public static AvatarTransferTool getInstance(List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		if(null == instance)
			instance = new AvatarTransferTool(keyList, key2Index, dataCache);
		return instance;
	}
	
	public static AvatarTransferTool getIndependentInstance(List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		return new AvatarTransferTool(keyList, key2Index, dataCache);
	}
	
	public AvatarTransferTool(List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache) {
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
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
	
	public void convert(boolean isVerbose){
		convert("C:/Data/SchoolSearch/person/avatar/","C:/Data/SchoolSearch/convertedAvatar/", isVerbose);
	}
	public void convert(String sourceDir, String targetDir, boolean isVerbose){
		if(null != sourceDir && null != targetDir){
			File sourceDirf = new File(sourceDir);
			File targetDirf = new File(targetDir + System.currentTimeMillis());
			if(!targetDirf.exists()){
				targetDirf.mkdirs();
			}
			if(sourceDirf.isDirectory()){
				AvatarMap.setPath(sourceDir);
				General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
				gl.extract(Arrays.asList("id","index_id","no"));
				this.keyList.clear();
				this.key2Index.clear();
				this.dataCache.clear();
				
				//初始化待插入的Cache信息
				this.keyList.addAll(gl.getKeyList());
				this.key2Index.putAll(gl.getKey2Index());
				//初始化dataCache,值传递
				for(List<Object> objListI:gl.getDataCache()){
					List<Object> objList = new ArrayList<Object>();
					objList.addAll(objListI);
					this.dataCache.add(objList);
				}
				
				int total = 0;
				int find = 0;
				for(List<Object> objList:dataCache){
					total++;
					String id = objList.get(key2Index.get("id").index).toString();
					String index_id = (String)objList.get(key2Index.get("index_id").index);
					String no = (String)objList.get(key2Index.get("no").index);
					String path = AvatarMap.getPath(index_id);
					File file = new File(path);
					if(file.exists() && file.isDirectory()){
						File[] fileList = file.listFiles();
						String pattern = "[^0-9]*" + no + "[^0-9]*";
						boolean isFind = false;
						for(File f:fileList){
							if(f.toString().matches(pattern)){
								if(isVerbose){
									System.out.println(f.toString() + "<----------->" + id + " " + index_id + " " + no);
								}
								find++;
								isFind = true;
								File targetFile = new File(targetDirf + File.separator + id + ".jpg");
								try {
									FileCopy.copyFile(f, targetFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							}
						}
						if(!isFind && isVerbose){
//						if(!isFind){
							System.out.println(index_id + pattern + id + "COULD NOT FOUND!");
						}
					}
				}
				System.out.println("----------------------------------------------");
				System.out.println("[AvatarTransferToolInfo]TOTAL PHOTO:" + total);
				System.out.println("[AvatarTransferToolInfo]MATCH PHOTO:" + find);
				if(0 != total){
					System.out.println("[AvatarTransferToolInfo]PERCENTAGE:" + (100*find/total) + "%");
				}
			}
		}else{
			System.out.println("[AvatarTransferToolInfo]EITHER SourceDir OR targetDir IS NULL!");
			System.out.println("[AvatarTransferToolInfo]SourceDir:" + sourceDir);
			System.out.println("[AvatarTransferToolInfo]TargetDir" + targetDir);
		}
	}
	public static void main(String[] args) {
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance("cache","faculty_tblnew");
		rd.getAllData();
		AvatarTransferTool avt = AvatarTransferTool.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
		avt.convert(false);
	}
}
