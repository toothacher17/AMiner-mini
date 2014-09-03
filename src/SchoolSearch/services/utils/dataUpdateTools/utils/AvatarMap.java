package SchoolSearch.services.utils.dataUpdateTools.utils;
/**
 * @author CX
 */
import java.util.HashMap;
import java.util.Map;


public class AvatarMap {
	static String path = "C:/Data/SchoolSearch/person/avatar/";
	
	static Map<String, String> index_id2Avatar = new HashMap<String, String>();
	static {
		
		
		
		
		index_id2Avatar.put("0101", "rwxy-zgyywx/");
		index_id2Avatar.put("0102", "rwxy-wgyywxx/");
		index_id2Avatar.put("0103", "rwxy-ls/");
		index_id2Avatar.put("0104", "rwxy-zx/");
		
		index_id2Avatar.put("0201", "jxgcxy-jxgcx/");
		index_id2Avatar.put("0202", "jxgcxy-jmyqx/");
		index_id2Avatar.put("0203", "jxgcxy-rngcx/");
		index_id2Avatar.put("0204", "jxgcxy-qcgcx/");
		index_id2Avatar.put("0205", "jxgcxy-gygcx/");
		index_id2Avatar.put("0206", "jxgcxy-jcgyxlzx/");
		
		
		index_id2Avatar.put("0301", "hthkxy-gclx/");
		index_id2Avatar.put("0302", "hthkxy-hkyhgc/");
		
		index_id2Avatar.put("0401", "shkxy-shxx/");
		index_id2Avatar.put("0402", "shkxy-zzxx/");
		index_id2Avatar.put("0403", "shkxy-gjgxxx/");
		index_id2Avatar.put("0404", "shkxy-xlxx/");
//		index_id2Avatar.put("0405", "");
		index_id2Avatar.put("0406", "shkxy-kxyshyjs/");
		
		index_id2Avatar.put("0500", "fxy/");
		index_id2Avatar.put("0600", "xwycby/");
//		index_id2Avatar.put("0700","/");
		
		index_id2Avatar.put("0801","jzxy-jzx/");
		index_id2Avatar.put("0802","jzxy-csghx/");
		index_id2Avatar.put("0803","jzxy-jgxx/");
		index_id2Avatar.put("0804","jzxy-jzjskxx/");
		
		index_id2Avatar.put("0901", "tmslxy-tmgcsx/");
		index_id2Avatar.put("0902", "tmslxy-slsdgc/");
		index_id2Avatar.put("0903", "tmslxy-jsgl/");
		
		index_id2Avatar.put("1000","hjxy/");
		
		index_id2Avatar.put("1101", "jjglxy-kj/");
		index_id2Avatar.put("1102", "jjglxy-jj/");
		index_id2Avatar.put("1103", "jjglxy-jr/");
		index_id2Avatar.put("1104", "jjglxy-cxcyyzl/");
		index_id2Avatar.put("1105", "jjglxy-ldlyzzgl");
		index_id2Avatar.put("1106", "jjglxy-glkxygc/");
		index_id2Avatar.put("1106", "jjglxy-scyx/");
		
		index_id2Avatar.put("1300","mkszyxy/");
		
		index_id2Avatar.put("1401","jcyxx/");
		index_id2Avatar.put("1402","swyxgcx/");
		index_id2Avatar.put("1403","yxx/");
		index_id2Avatar.put("1404","lcyxx/");
//		index_id2Avatar.put("1405","/");
		
		index_id2Avatar.put("1600", "gcwlx/");
		index_id2Avatar.put("1700", "hxgcx/");
		index_id2Avatar.put("1900", "gdyjy/");
		index_id2Avatar.put("1800", "hnhxnyjsyjy/");
		
		index_id2Avatar.put("2000", "zpyyysxyjzx/");
		index_id2Avatar.put("2100", "jyyjy/");
		index_id2Avatar.put("2200", "jcxxyjy/");
		
		index_id2Avatar.put("2301", "xxkxyjsxy-dzgcx/");
		index_id2Avatar.put("2302", "xxkxjsxy-jsjkxyjsx/");
		index_id2Avatar.put("2303", "xxkxjsxy-zdhx/");
		index_id2Avatar.put("2304", "");
		index_id2Avatar.put("2305", "xxkxjsxy-wdzxyjs/");
		index_id2Avatar.put("2306", "xxkxjsxy-rjxy/");
		index_id2Avatar.put("2307", "xxkxjsxy-xxjsyjy/");
		
		index_id2Avatar.put("2401", "lxy-sxkxx/");
		index_id2Avatar.put("2402", "lxy-wlx/");
		index_id2Avatar.put("2403", "lxy-dqxtkxyjzx/");
		index_id2Avatar.put("2404", "lxy-hxx/");
		
		index_id2Avatar.put("2501", "msxy-gysjx/");
		index_id2Avatar.put("2502", "msxy-gymsx/");
		index_id2Avatar.put("2503", "msxy-hjyssjx/");
		index_id2Avatar.put("2504", "msxy-hhx/");
		index_id2Avatar.put("2505", "msxy-dsx/");
		index_id2Avatar.put("2506", "msxy-rzfzyssjx/");
		index_id2Avatar.put("2507", "msxy-sjcdesjx/");
		index_id2Avatar.put("2508", "msxy-tcyssjx/");
		index_id2Avatar.put("2509", "msxy-xxyssjx/");
		index_id2Avatar.put("2510", "msxy-ysslx/");
		
		index_id2Avatar.put("2600", "clxy/");
		
		index_id2Avatar.put("2700", "szyjsy-hykxyjsxy/");
		index_id2Avatar.put("2701", "szyjsy-nyyhjxb/");
		index_id2Avatar.put("2702", "szyjsy-shkxyglxb/");
		index_id2Avatar.put("2703", "szyjsy-smyjkxb/");
		index_id2Avatar.put("2704", "szyjsy-wlyjtxb/");
		index_id2Avatar.put("2705", "szyjsy-xxkxyjsxb/");
		index_id2Avatar.put("2706", "szyjsy-xjzzxb/");
		
		index_id2Avatar.put("2800", "tyb/");
		index_id2Avatar.put("2900", "ysjyzx/");
		
		
		index_id2Avatar.put("3100", "rsnyzx/");
		index_id2Avatar.put("3200","smkxxy/");
	}
	

	public static String getPath(String key) {
			return path + index_id2Avatar.get(key);
	}


	public static String getPath() {
		return path;
	}


	public static void setPath(String path) {
		AvatarMap.path = path;
	}
	
}

