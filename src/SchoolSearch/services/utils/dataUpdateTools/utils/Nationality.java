package SchoolSearch.services.utils.dataUpdateTools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.IFLE;


import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.utils.Crawler;



public class Nationality implements Runnable{
	
	public static Map<Integer, String> id2Nationality;
	public static List<List<Object>> dataCache;
	public static int currentSize;
	public static int totalSize;
	public static int pointer;
	public static File filePath;
	public static boolean isInit = false;
	
	public Nationality(){
		init();
	}
	
	public static synchronized void init(){
		if(!isInit){
			filePath = new File("C:/Data/nationality.txt");
			id2Nationality = Nationality.loadCache(filePath, true);
			int lastId = (null == Nationality.getTheLastRecordId(filePath))?0:Nationality.getTheLastRecordId(filePath);
			if(null == id2Nationality){
				id2Nationality = new HashMap<Integer, String>();
			}
			ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance("arnet_db", "contact_info");
			rd.getAllData(1, -1, false);
			dataCache = rd.getDataCache();
			
			currentSize = Integer.parseInt(id2Nationality.get(-1));
			totalSize = dataCache.size() + currentSize;
			pointer = 0;
			isInit = true;
		}
	}
	
	public static synchronized int getNextPointer(){
		if(pointer < dataCache.size()-1){
			return ++pointer;
		}else{
			return -1;
		}
	}
	
	public static synchronized void addId2Nationality(Integer id, String nationality){
		id2Nationality.put(id, nationality);
	}
	
	public static String findTheNationality(List<Object> contactInfo){
		//尝试address, index = 8
		String nationality = touchTry(contactInfo.get(8));
		if(null == nationality){
			//尝试university, index = 24
			nationality = touchTry(contactInfo.get(24));
			if(null == nationality){
				//尝试phduniv, index = 14
				nationality = touchTry(contactInfo.get(14));
				if(null == nationality){
					//尝试msuniv, index = 17
					nationality = touchTry(contactInfo.get(17));
					if(null == nationality){
						//尝试bsuniv, index = 20
						nationality = touchTry(contactInfo.get(20));
						if(null == nationality){
							//尝试affiliation,index = 7
							nationality = touchTry(contactInfo.get(7));
						}
					}
				}
			}
		}
		return nationality;
	}
	
	private static String touchTry(Object element){
		if(null == element || element.equals("")){
			return null;
		}
		else{
//			System.out.println(element);
			if(String.class.isInstance(element)){
				return Crawler.getNationality((String)element);
			}else{
				return Crawler.getNationality(element.toString());
			}
		}
	}
	
	public static String readLastLine(File file, String charset) throws IOException {   
		if (!file.exists() || file.isDirectory() || !file.canRead()) {   
			return null;   
		}   
		RandomAccessFile raf = null;   
		try {   
			raf = new RandomAccessFile(file, "r");   
			long len = raf.length();   
			if (len == 0L) {   
				return "";   
			} else {   
				long pos = len - 1;   
				while (pos > 0) {   
					pos--;   
					raf.seek(pos);   
					if (raf.readByte() == '\n') {   
						break;   
					}   
				}   
				if (pos == 0) {   
					raf.seek(0);   
				}   
				byte[] bytes = new byte[(int) (len - pos)];   
				raf.read(bytes);   
				if (charset == null) {   
					return new String(bytes);   
				} else {   
					return new String(bytes, charset);   
				}   
			}   
		} catch (FileNotFoundException e) {   
		} finally {   
			if (raf != null) {   
				try {   
					raf.close();   
				} catch (Exception e2) {   
				}   
			}   
		}   
		return null;   
	}  
	
	
	public static Integer getTheLastRecordId(File filePath){
		try {
			String lastLine = Nationality.readLastLine(filePath, "utf-8");
			String[] strings = lastLine.split("\\|\\|");
			if(null != strings && 2 == strings.length){
				return Integer.parseInt(strings[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<Integer,String> loadCache(File filePath, boolean isNeedSize){
		if(!filePath.exists())
			try {
				filePath.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		Map<Integer, String> id2Nationality = new HashMap<Integer, String>();
		Integer lineCounts = 0;
		BufferedReader reader = null;
		try {
			String temp = null;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			while((temp = reader.readLine()) != null){
				lineCounts++;
				String [] data = temp.split("\\|\\|");
				if(2 == data.length){
					Integer id = Integer.parseInt(data[0]);
					if(!id2Nationality.containsKey(id)){
						id2Nationality.put(id, data[1]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != reader){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(isNeedSize){
			//把总行数也装入map, key为-1
			id2Nationality.put(-1, lineCounts.toString());
		}
		return id2Nationality;
	}
	
	
	//由于支持断点进行，因此每更新一条记录需要3次数据库交互
	public static void updateDB(File filePath){
		//准备参考表
		System.out.println("STEP 1:读入参考表.....");
		Long t1 = System.currentTimeMillis();
//		DBPersistence dbp = DBPersistence.getIndependentInstance("c:/data/na_person/");
//		General gl = General.getIndependentInstance(dbp.getKeyList(), dbp.getKey2Index(), dbp.getDataCache());
		General gl = General.getIndependentInstance("arnet_db", "na_person");
		Long t2 = System.currentTimeMillis();
		System.out.println((t2-t1)/1000 + "s");
		//准备转换待数据
		System.out.println("STEP 2:准备待转换的数据...");
		Map<Integer, String> id2Nationality = Nationality.loadCache(filePath, false);
		List<Object> idOldList = new ArrayList<Object>();
		List<Object> nationalityList = new ArrayList<Object>();
		for(Integer key:id2Nationality.keySet()){
			idOldList.add(key);
			nationalityList.add(id2Nationality.get(key));
		}
		
		//开始转换数据
		System.out.println("STEP 3:开始转换数据...");
		List<Object> idNewList = new ArrayList<Object>();
		idNewList = gl.reflect("contact_id", "id", idOldList, true);
		
		//拼接新的dataCache
		System.out.println("STEP 4:拼接新的dataCache...");
		gl = General.getIndependentInstance();
		gl.insertColumnAtTailInteger("id", idNewList, true);
		gl.insertColumnAtTailString("nationality", nationalityList, true);
		gl.shrinkRows(true);
		gl.printStructure();
		gl.printdataCache();
		
		//开始更新至数据库
		System.out.println("STEP 5:更新至数据库");
		AntiReduplicationCheck.prepareDB("arnet_db", "na_person_ext");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
		AntiReduplicationCheck.setReplaceKeyList("nationality"); 
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.updateWithConstruct("id");
	}
	
	public static void main(String[] args) {
//		for(int i=0;i<20;i++){
//			Nationality na = new Nationality();
//			Thread th = new Thread(na);
//			th.start();
//		}
		Nationality.updateDB(new File("C:/data/nationality.txt"));
	}

	@Override
	public void run() {
		int pointer = 0;
		while(-1 != (pointer = getNextPointer())){
			List<Object> objList = dataCache.get(pointer);
			Integer id = (Integer)objList.get(0);
			//检查是否已经存在
			if(!id2Nationality.containsKey(id)){
				String nationality = Nationality.findTheNationality(objList);
				//能查询到国籍
				if(null != nationality){
					addId2Nationality(id, nationality);
					IOUtils.appendToFile(filePath, id.toString() + "||" + nationality);
					//查询不到
				}else{
					System.out.println("Find Nothing!----->" + objList.get(0));
				}
				//已经存在
			}else{
				System.out.println(id + " ALREADY EXIST!SKIP IT!");
			}
			System.out.println("[PROCESS]" + (float)(++currentSize)/(float)totalSize*100 +"%");
		}
	}
}
 