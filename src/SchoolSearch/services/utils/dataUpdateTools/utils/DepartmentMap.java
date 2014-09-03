package SchoolSearch.services.utils.dataUpdateTools.utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class DepartmentMap {
	static Map<Object, List<Object>> department2Abbreviation = new HashMap<Object, List<Object>>();
	static Map<Object, List<Object>> abbrevitation2department = new HashMap<Object, List<Object>>();
	
	static{
		department2Abbreviation.put(1101, Lists.asList("accounting",new Object[]{"sem"}));
		department2Abbreviation.put(1104, Lists.asList("bsp",new Object[]{"sem"}));
		department2Abbreviation.put(2302, Lists.asList("cs",new Object[]{},new Object[]{}));
		department2Abbreviation.put(1102, Lists.asList("economics",new Object[]{"sem"}));
		department2Abbreviation.put(1103, Lists.asList("finance",new Object[]{"sem"}));
		department2Abbreviation.put(1105, Lists.asList("hso",new Object[]{"sem"}));
		department2Abbreviation.put(2100, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0101, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0102, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0103, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0104, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0401, Lists.asList("hss",new Object[]{"sem"}));
		department2Abbreviation.put(0402, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0405, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0406, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0407, Lists.asList("hss",new Object[]{}));
		department2Abbreviation.put(0403, Lists.asList("internationalrelationships",new Object[]{"hss"}));
		department2Abbreviation.put(1107, Lists.asList("marketing",new Object[]{"sem"}));
		department2Abbreviation.put(1106, Lists.asList("mse",new Object[]{"sem"}));
		department2Abbreviation.put(0402, Lists.asList("politicalscience",new Object[]{}));
		department2Abbreviation.put(0404, Lists.asList("psychology",new Object[]{"hss"}));
		
		abbrevitation2department.put("ee",Lists.asList("2301",new Object[]{}));
		abbrevitation2department.put("automation",Lists.asList("2303",new Object[]{}));
		abbrevitation2department.put("microelectronics",Lists.asList("2305",new Object[]{}));
		abbrevitation2department.put("software",Lists.asList("2306",new Object[]{}));
		abbrevitation2department.put("riit",Lists.asList("2307",new Object[]{}));
		abbrevitation2department.put("me",Lists.asList("0201",new Object[]{}));
		abbrevitation2department.put("pim",Lists.asList("0202",new Object[]{}));
		abbrevitation2department.put("thermal",Lists.asList("0203",new Object[]{}));
		abbrevitation2department.put("automotive",Lists.asList("0204",new Object[]{}));
		abbrevitation2department.put("industry",Lists.asList("0205",new Object[]{}));
		abbrevitation2department.put("otherme",Lists.asList("0206",new Object[]{}));
	}
	
	public static List<Object> getAbbreviation(Object department){
		if(String.class.isInstance(department)){
			department = Integer.valueOf((String)department);
		}
		if(null != department2Abbreviation.get(department)){
			return department2Abbreviation.get(department);
		}else{
			return null;
		}
	}
	
	public static List<Object> getDepartment(Object abbreviation){
		if(null != abbrevitation2department.get(abbreviation)){
			return abbrevitation2department.get(abbreviation);
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(DepartmentMap.getDepartment("pim"));
	}
}
