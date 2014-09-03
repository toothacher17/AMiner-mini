package SchoolSearch.services;

import java.io.InputStream;
import java.util.Properties;

public class ConsistanceService {
//	static Map<String, String> consistenceMap = new HashMap<String, String>();
	static String[] replaceKeys = {"build.base"};
	
	static Properties properties = null;
	static {
		properties = new Properties();
		InputStream in = null;
		try {
			ClassLoader loader = ConsistanceService.class.getClassLoader();
			in = loader.getResourceAsStream("resource.properties");
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key) {
		String value = (String) properties.get(key);
//		System.out.println("1 " + key + "\t" + value);
		for(String rk : replaceKeys) {
			if(value.contains("${" + rk + "}")){
				String regex = "\\$\\{" + rk.replaceAll("\\.", "\\\\.") + "\\}";
//				System.out.println("[regex]\t" + regex + "\t" + value);
				String replacement = get(rk).replaceAll("\\\\", "\\\\\\\\");
				
				value = value.replaceAll(regex, replacement);
//				value = value.replaceAll(regex, "-----------------");
			}
		}
//		System.out.println("2 " + key + "\t" + value);
		return value;
	}
	
}
