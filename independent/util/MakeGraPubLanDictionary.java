package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.utils.T5RegistryHelper;


public class MakeGraPubLanDictionary {
	
	
	GraduatePublicationService grapubService = T5RegistryHelper.getService(GraduatePublicationService.class);
	
	public Map <String, String > getTheFinalMap(){
		Map <String, String > result =  new HashMap<String, String>();
		List<GraduatePublication> allGraduatePublication = grapubService.getAllGraduatePublications();
		for(GraduatePublication grapub : allGraduatePublication){
			if(null!=grapub.keyword_en&&null!=grapub.keyword_cn){
				String englishKeywords = grapub.keyword_en.toLowerCase();
				String chineseKeywords = grapub.keyword_cn.toLowerCase();
				String []englishKeywordsSplited = englishKeywords.split(",|;|，|；");
				String []chineseKeywordsSplited = chineseKeywords.split(",|;|，|；");
				if(null!=englishKeywordsSplited&&null!=chineseKeywordsSplited&&englishKeywordsSplited.length==chineseKeywordsSplited.length){
					for( int i = 0 ; i<englishKeywordsSplited.length;i++ ){
						if (!result.keySet().contains(englishKeywordsSplited[i]) ){
							result.put(englishKeywordsSplited[i],chineseKeywordsSplited[i]);
						}
					}
				}
			}
		}
		return result ;
	}
	
	public static void main(String [] args){
		MakeGraPubLanDictionary makeDic = new MakeGraPubLanDictionary();
		Map<String,String> test = makeDic.getTheFinalMap();
		List<String> englishWords = new ArrayList<String>(test.keySet());
		String test1 = test.get("radar");
		String test2 = test.get("Semantic Annotation".toLowerCase());
		System.out.println (test1);
		System.out.println (test2);
		System.out.println (test.size());
		String FileName = "D:\\Users\\jingyuanliu";
		try{
			File f = new File (FileName,"grapubTranslateMapChinese.txt");
			PrintWriter out = new PrintWriter(new FileWriter(f));
			for(String wordTemp : englishWords){
				out.println(test.get(wordTemp).trim()+"\t\t"+wordTemp.trim());
			}
			out.close();		
		}catch(IOException e){
			System.out.println("there is some io exception problem");
		}
		T5RegistryHelper.shutdown();
	}
	

}







