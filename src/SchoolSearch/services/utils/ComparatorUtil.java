package SchoolSearch.services.utils;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import SchoolSearch.services.dao.course.model.Course;
import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;

public class ComparatorUtil {
	static Map<String, Integer> titleOrderMap = new HashMap<String, Integer>();
	static {
		int titleOrderCount = 0;
		titleOrderMap.put("中国科学院院士", ++titleOrderCount);
		titleOrderMap.put("中国工程院院士", ++titleOrderCount);
		titleOrderMap.put("“长江学者奖励计划”特聘教授", ++titleOrderCount);
		titleOrderMap.put("“长江学者奖励计划”讲座教授", ++titleOrderCount);
		titleOrderMap.put("何梁何利基金科学与技术进步奖获得者", ++titleOrderCount);
		titleOrderMap.put("国家级教学名师奖获得者", ++titleOrderCount);
		titleOrderMap.put("国家杰出青年科学基金获得者", ++titleOrderCount);
		titleOrderMap.put("国家“百千万人才工程”入选者", ++titleOrderCount);
		titleOrderMap.put("国家自然科学基金委创新研究群体学术带头人", ++titleOrderCount);
		titleOrderMap.put("清华大学“学术新人奖”获得者", ++titleOrderCount);

	}
	public static Comparator<String> titleComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {

			Integer v1 = titleOrderMap.get(o1);
			Integer v2 = titleOrderMap.get(o2);

			if (null == v1)
				v1 = Integer.MAX_VALUE;
			if (null == v2)
				v2 = Integer.MAX_VALUE;

			if (v1 != v2)
				return v1.compareTo(v2);
			else
				return o1.compareTo(o2);
		}
	};

	public static class MapComparator<T, K extends Comparable<K>> implements
			Comparator<T> {

		private Map<T, K> valueMap;

		public MapComparator(Map<T, K> valueMap) {
			super();
			this.valueMap = valueMap;
		}

		@Override
		public int compare(T o1, T o2) {
			return valueMap.get(o1).compareTo(valueMap.get(o2));
		}
	}

	public static class GeneralComparator<T> implements Comparator<T> {
		Class<T> type;
		String fieldName;
		public GeneralComparator(Class<T> type, String fieldName) {
			super();
			this.type = type;
			this.fieldName = fieldName;
		}
		@SuppressWarnings("unchecked")
		public int compare(T o1, T o2) {
			try {
				Field declaredField = type.getDeclaredField(fieldName);
				declaredField.setAccessible(true);
				Class<?> fieldType = declaredField.getType();
				if(Comparable.class.isAssignableFrom(fieldType)) {
					@SuppressWarnings("rawtypes")
					Comparable v1 = (Comparable) declaredField.get(o1);
					@SuppressWarnings("rawtypes")
					Comparable v2 = (Comparable) declaredField.get(o2);
					return v1.compareTo(v2);
				} else {
					throw new Exception("field not comparable");
				}
				
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		};
		 
	}
	
	public static Comparator<SchooltestCourse> semesterComparator = new Comparator<SchooltestCourse>() {
		@Override
		public int compare(SchooltestCourse o1, SchooltestCourse o2) {
			return o1.getSemesterId().compareTo(o2.getSemesterId());
		}
	};
}
