package SchoolSearch.services.utils.dataUpdateTools.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;


import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.InsertIntoDataBase;
import SchoolSearch.services.utils.dataUpdateTools.utils.Crawler;
import SchoolSearch.services.utils.dataUpdateTools.utils.IOUtils;
import SchoolSearch.services.utils.dataUpdateTools.utils.NLPUtils;

public class Conference {
	private int size;
	private int[]parent;
//	private int[][]distance;
	private Map<Integer,List<String>> index2Info ;
	private Map<String,String> name2name;
	int find(int index){
		if(parent[index]!=index){
			parent[index] = find(parent[index]);
		}
		return parent[index];
	}
	
	void combination(int i, int j){
		i=find(i);
		j=find(j);
		if(i!=j){
			parent[j]=i;
		}
	}
	
	public void run(){
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance("cache","publication");
		rd.getDistinctList("relation_conferencename");
		System.out.println(rd.getDataCache().size());
		List<String> conference=rd.getStringList();
		size=conference.size();
		parent = new int[size];
//		distance = new int[size][size];
		for(int i=0;i<size;i++){
			parent[i]=i;
		}
		index2Info = new HashMap<Integer, List<String>>();
		name2name = new HashMap<String, String>();
		for(int i=0;i<size;i++){
			String str1=conference.get(i);
			for(int j=0;j<i;j++){
				String str2=conference.get(j);
				int distance;
				if(str1.length()<str2.length()){
					distance=NLPUtils.editDistance(str1,str2);
				}else{
					distance=NLPUtils.editDistance(str2,str1);
				}
				
				if(i!=j && distance<13 && NLPUtils.isChinese(str1)){
					IOUtils.appendToFile("c:/data/demo.txt", distance+"||"+conference.get(i)+"||"+conference.get(j));
					combination(i,j);
					break;
				}else if(i!=j && distance<20 && !NLPUtils.isChinese(str1)){
					IOUtils.appendToFile("c:/data/demo.txt", distance+"||"+conference.get(i)+"||"+conference.get(j));
					combination(i,j);
					break;
				}
			}
		}
		for(int i=0;i<size;i++){
			name2name.put(conference.get(i), conference.get(find(i)));
		}
		for(int i=0;i<size;i++){
			int tmp=find(i);
			if(!index2Info.containsKey(tmp)){
				List<String> strTmp = new ArrayList<String>();
				String str=conference.get(tmp).replaceAll("\\d{4}[年]?", "").replaceAll("\\d{1,2}[th|nd|st|rd]+", "").
						replaceAll("第?.{1,3}届", "").replaceAll("（）", "").replaceAll("’", "").
						replaceAll("第?.{1,3}次", "").replaceAll("首次", "").replaceAll("首届", "").
						replaceAll("Proceedings\\.?\\s?(of)?\\s?(the)?","").
						replaceAll("第?.{1,3}期", "").trim();
				List<MatchResult> match=Crawler.findAll("\\(.*?\\)|（.*?）", str);
				if(!match.isEmpty() && null!=match.get(0)){
					str = str.replaceAll("\\(.*?\\)", "");
					str = str.replaceAll("（.*?）", "");
					String abbr = match.get(0).group(0);
					abbr = abbr.substring(1,abbr.length()-1).replaceAll("'", "").trim();
					strTmp.add(conference.get(tmp));
					strTmp.add(str);
					strTmp.add(abbr);
				}else{
					strTmp.add(conference.get(tmp));
					strTmp.add(str);
					strTmp.add(null);
				}
				index2Info.put(tmp, strTmp);
			}
		}
		List<Object> name =new ArrayList<Object>();
		List<Object> originalname = new ArrayList<Object>();
		List<Object> abbrevation = new ArrayList<Object>();
		for(Integer key:index2Info.keySet()){
			List<String> strList = index2Info.get(key);
			originalname.add(strList.get(0));
			name.add(strList.get(1));
			abbrevation.add(strList.get(2));
		}
		General gl =General.getIndependentInstance();
		gl.insertColumnAtTailString("originalname", originalname, true);
		gl.insertColumnAtTailString("name", name, true);
		gl.insertColumnAtTailString("abbreviation", abbrevation, true);
		InsertIntoDataBase iid = InsertIntoDataBase.getIndependentInstance("cache", "conference", gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		iid.insertBatchRecord();
		
	}
	
	public void populate(){
		General gl = General.getIndependentInstance("cache", "conference");
		
		General gl2 = General.getIndependentInstance("cache", "publication");
		List<Object> conferencename =gl2.fork("relation_conferencename",true).getObjList();
		System.out.println(name2name.size());
		for(int i=0;i<conferencename.size();i++){
			String str = (String)conferencename.get(i);
			if(null!=str && !str.isEmpty() && name2name.containsKey(str)){
				conferencename.set(i, name2name.get(str));
			}else{
				if(null!=str){
					System.out.println(str);
				}
			}
		}
		List<Object> ids =gl.reflect("originalname", "id",conferencename, true);
		gl2.replace("relation_conferencename", ids);
		
		AntiReduplicationCheck.prepareDB("cache", "publication");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
		AntiReduplicationCheck.setReplaceKeyList("relation_conferencename");
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.updateWithConstruct("id");
	}
	public static void main(String[] args) {
		Conference t= new Conference();
		t.run();
		t.populate();
	}
}
