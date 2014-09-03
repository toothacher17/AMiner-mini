package SchoolSearch.services.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.search.PhraseParser;

public class TranslationService {
	static String targetFilePath = ConsistanceService.get("translate.filePath");
	static Map<String, String> map;
	static HashSet<String> englishSet;
	static {
		System.out.println("[loading translation]");
		long t0 = System.currentTimeMillis();
		
		Map<String, String> dictionary = new HashMap<String, String>();
		HashSet<String> set = new HashSet<String>();
		
		File file = new File(targetFilePath);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while((tempString = reader.readLine())!=null) {
				String[] split = tempString.split("\t\t");
				String lowerCase0 = split[0].toLowerCase();
				String lowerCase1 = split[1].toLowerCase();
				if(lowerCase0.compareTo(lowerCase1) == 0) {
					continue;
				}
				dictionary.put(lowerCase0, lowerCase1);
				dictionary.put(lowerCase1, lowerCase0);
				set.add(lowerCase1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
//		for(String keyword : dictionary.keySet()) {
//			System.out.println(keyword + "||" + dictionary.get(keyword));
//		}
		System.out.println("[translation loaded] in " + (System.currentTimeMillis() - t0) + " ms" );
		map = dictionary;
		englishSet = set;
	}
	
	public static String translate(String word) {
		return map.get(word.toLowerCase());
	}
	
	public static HashSet<String> returnSet() {
		return englishSet;
	}
	
	public static void main(String[] args) {
		System.out.println(TranslationService.translate("data mining"));
		System.out.println(TranslationService.translate("web service"));
		System.out.println(TranslationService.translate("DATA MINING"));
		System.out.println(TranslationService.translate("情绪的解析"));
	}
}
