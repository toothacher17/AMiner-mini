package SchoolSearch.services.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {
	

	
	public static List<Field> getFiledList(Class<?> type, String[] fieldNameArrary) {
		List<Field> result = new ArrayList<Field>();
		for(String f : fieldNameArrary) {
			try {
				result.add(type.getDeclaredField(f));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	
}
