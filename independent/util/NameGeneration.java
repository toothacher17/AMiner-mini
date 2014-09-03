package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;

public class NameGeneration {
	static Map<String,String> fixComplexNamepinyin = new HashMap<String, String>();
	static Set<String> complexLastNames = new HashSet<String>();
	static List<String> patterns = new ArrayList<String>();
	static List<String> patterns2 = new ArrayList<String>();
	static List<String> patterns3 = new ArrayList<String>();
	static List<String> patterns4 = new ArrayList<String>();

	static {
		complexLastNames.addAll(loadNameStringFromFile("complexLastName.txt"));
		complexLastNames.addAll(loadNameStringFromFile("japaneseLastName.txt"));
		
		fixComplexNamepinyin.put("尉迟", "yuchi");
		fixComplexNamepinyin.put("拓跋", "tuoba");
		fixComplexNamepinyin.put("长孙", "zhangsun");
		fixComplexNamepinyin.put("拓拨", "touba");
		fixComplexNamepinyin.put("乐羊", "yueyang");
		fixComplexNamepinyin.put("纥于", "heyu");
		fixComplexNamepinyin.put("陆", "lu");
		
		patterns.add("==lastName== ==firstName==");
		patterns.add("==firstName== ==lastName==");
		
		patterns2.add("==firstName== ==middleName== ==lastName==");
		patterns2.add("==lastName== ==middleName== ==firstName==");
		patterns3.add("==firstName== ==lastName==");
		patterns3.add("==lastName== ==firstName==");
		patterns4.add("==lastName==");
	}
	
	private static Set<String> loadNameStringFromFile(String fileName) {
		InputStream stream = NameGeneration.class.getResourceAsStream("/transfer/" + fileName);
		Set<String> result = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		try {
			while((line = reader.readLine())!= null) {
				result.add(line.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null!=reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static int getNameType(String name) {
		boolean allChinese = true;
		boolean allEnglish = true;
		for(int i=0; i<name.length(); i++) {
			UnicodeBlock uncodeBlock = Character.UnicodeBlock.of(name.charAt(i));
//			System.out.println("[NameGeneration]\t" + name.charAt(i) + "\t" + uncodeBlock.toString());
			if(uncodeBlock.equals(Character.UnicodeBlock.BASIC_LATIN)) {
				allChinese = false;
			} else if(uncodeBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || //
					uncodeBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) || //
					uncodeBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B) || //
					uncodeBlock.equals(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) //
					) {
				allEnglish = false;
			} else if(uncodeBlock.equals(Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) //
					) {
				
			} else {
				System.err.println("[NameGeneration][UnexpectedCharsetCode]\t" + name.charAt(i) + "\t" + uncodeBlock.toString());
			}
		}
		if(allChinese){
			return 0;
		}			
		else if(allEnglish) {
			return 1;
		}
		else {
			return 2;
		}
	}
	
	private static String[] splitNameChinese(String name) {
		if(name.length() == 1) {
			return new String[]{name};
		}
		if(name.length()>2) {
			String possibleLastName = name.substring(0, 2);
			if(complexLastNames.contains(possibleLastName)) {
				return new String[]{possibleLastName, name.substring(2, name.length())};
			}
		}
		return new String[]{name.substring(0, 1), name.substring(1, name.length())};
	}
	
	private static String[] splitNameEnglish(String name) {
		/**
		 * JohnSmith
		 * John E. Smith
		 * John E. S.
		 * John Eddie Smith
		 */
		List<Integer> upperCaseLocation = new ArrayList<Integer>();
		for(int i=0; i<name.length(); i++) {
			if(Character.isUpperCase(name.charAt(i)) == true) {
				upperCaseLocation.add(i);
			}
		}
		if (name.split("\\s+").length == 1 && upperCaseLocation.size() <= 1) {
			return new String[] {name};
		} else {
			if(upperCaseLocation.size() == 2) {
				int firstNameLocation = upperCaseLocation.get(1);
				return new String[]{name.substring(0, firstNameLocation).trim(), name.substring(firstNameLocation, name.length()).trim()};
			} else {
				int firstNameLocation = upperCaseLocation.get(1);
				int lastNameLocation = upperCaseLocation.get(upperCaseLocation.size()-1);
				return new String[]{name.substring(0, firstNameLocation).trim(), name.substring(firstNameLocation, lastNameLocation).trim(), name.substring(lastNameLocation, name.length()).trim()};
			}
		}
	}
	
	private static List<String> formEnglishString(String chineseNameString) {
		char[] charArray = chineseNameString.toCharArray();
		List<Set<String>> charEnglishList = new ArrayList<Set<String>>();
		for(char c : charArray) {
			Set<String> pinyinSet = new HashSet<String>();
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
			if(pinyinArray.length == 0) {
				System.out.println("[pinyin not exist]" + c);
			}
			for(String pinyin : pinyinArray) {
				if(null == pinyin) {
					System.out.println("[pinyin not exist]" + c);
				}
				
				pinyin = pinyin.toLowerCase().replaceAll("u:", "v").toString();
				if(pinyin.matches("[a-z]+\\d")) {
					pinyin = pinyin.substring(0, pinyin.length()-1);
				}
				char fl = pinyin.charAt(0);
				char upperCase = Character.toUpperCase(fl);
				pinyin = upperCase + pinyin.substring(1);
				pinyinSet.add(pinyin);
			}
			charEnglishList.add(pinyinSet);
		}
		return generateNamesWithListSet(charEnglishList, 0, null);
	}
	
	private static List<String> generateNamesWithListSet(List<Set<String>> dataSource, int index, List<String> halfResult) {
		Set<String> currentSet = dataSource.get(index);
		List<String> result = new ArrayList<String>();
		if(index == 0) {
			result.addAll(currentSet);
		} else {
			for(String half : halfResult) {
				for(String current : currentSet) {
					result.add(half + current);
				}
			}
		}
		if(null!=halfResult)
			halfResult.clear();
		if(index == dataSource.size()-1) {
			return result;
		} else {
			return generateNamesWithListSet(dataSource, index+1, result);
		}
	}
	
	private static List<String> generateShortNameList(List<String> NameList, Boolean open) {
		
		if(open == true) {

			List<String> ShortNameList = new ArrayList<String>();
			
			for(String name : NameList) {
				List<Integer> upperCaseLocation = new ArrayList<Integer>();
				for(int i=0; i<name.length(); i++) {
					if(Character.isUpperCase(name.charAt(i)) == true) {
						upperCaseLocation.add(i);
					}
				}
				if(upperCaseLocation.size() <= 1) {
					ShortNameList.add(name.substring(0, 1).toUpperCase());
					ShortNameList.add(name.substring(0, 1).toUpperCase() + ".");
				} else {
					int Location0 = upperCaseLocation.get(0);
					char upperCase0 = name.charAt(Location0);
					int Location = upperCaseLocation.get(1);
					char upperCase = name.charAt(Location);
					char lowerCase = Character.toLowerCase(upperCase);
					ShortNameList.add(name.substring(0, Location) + "-" +name.substring(Location, name.length()));
					ShortNameList.add(name.substring(0, Location) + lowerCase + name.substring(Location + 1, name.length()));
					ShortNameList.add(name.substring(0, Location) + "-" + lowerCase + name.substring(Location + 1, name.length()));
					ShortNameList.add(upperCase0 + "" + upperCase);
					ShortNameList.add(upperCase0 + ". " + upperCase + ".");
					ShortNameList.add(upperCase0 + "." + upperCase + ".");
					ShortNameList.add(upperCase0 + ".-" + upperCase + ".");
					ShortNameList.add(name.substring(0, Location) + " " + upperCase + ".");
				}
			}
			
			return ShortNameList;
		} else
			return new ArrayList<String>();
		
	}
	
	private static List<String> formWithPattern_Chinese(List<String> firstNameList, List<String> lastNameList, List<String> pattern, Boolean open) {
		
		List<String> formWithPattern_Chinese = new ArrayList<String>();
		
		List<String> lastShortNameList = generateShortNameList(lastNameList, open);
		lastNameList.addAll(lastShortNameList);
		List<String> firstShortNameList = generateShortNameList(firstNameList, open);
		firstNameList.addAll(firstShortNameList);
		for(String p : pattern) {
			for(String fn : firstNameList) {
				for(String ln : lastNameList) {
					String s = p.replaceAll("==firstName==", fn).replaceAll("==lastName==", ln);
					formWithPattern_Chinese.add(s);
				}
			}
		}
		
		return formWithPattern_Chinese;
	}

	
	private static List<String> formWithPattern2_English(
			List<String> firstNameList, List<String> middleNameList, List<String> lastNameList,
			List<String> pattern) {

		List<String> formWithPattern_English = new ArrayList<String>();
		
		for (String p : pattern) {
			for (String fn : firstNameList) {
				for (String mn : middleNameList) {
					for (String ln : lastNameList) {
						String s = p.replaceAll("==firstName==", fn)
								.replaceAll("==middleName==", mn)
								.replaceAll("==lastName==", ln);
						formWithPattern_English.add(s);
					}
				}
			}
		}

		return formWithPattern_English;
	}
	
	private static List<String> formWithPattern3_English(
			List<String> firstNameList, List<String> lastNameList,
			List<String> pattern) {

		List<String> formWithPattern_English = new ArrayList<String>();

		for (String p : pattern) {
			for (String fn : firstNameList) {
				for (String ln : lastNameList) {
					String s = p.replaceAll("==firstName==", fn).replaceAll(
							"==lastName==", ln);
					formWithPattern_English.add(s);
				}
			}
		}

		return formWithPattern_English;
	}

	private static List<String> formWithPattern4_English(
			List<String> lastNameList, List<String> pattern) {

		List<String> formWithPattern_English = new ArrayList<String>();

		for (String p : pattern) {
			for (String ln : lastNameList) {
				String s = p.replaceAll("==lastName==", ln);
				formWithPattern_English.add(s);
			}
		}
		return formWithPattern_English;
	}
	
	public static List<Object> generateEnglishNamesObject(Object name){
		String nameString = name.toString();
		List<Object> result = new ArrayList<Object>();
		List<String> resultString = new ArrayList<String>();
		resultString = generateEnglishNames(nameString, true);
		for(String key:resultString){
			result.add(key);
		}
		return result;
	}
	
	public static List<String> generateEnglishNames(String name, boolean simplify) {
		
		List<String> result = new ArrayList<String>();
		
		int nameType = getNameType(name);
		List<String> firstNameList = new ArrayList<String>();
		List<String> middleNameList = new ArrayList<String>();
		List<String> lastNameList = new ArrayList<String>();
		
		if(nameType == 0) {
			String[] splitNames = splitNameChinese(name);
			if(splitNames.length == 1) {
				String lastName = splitNames[0];
				lastNameList.addAll(formEnglishString(lastName));
				result = formWithPattern4_English(lastNameList, patterns4);
			} else {
				String lastName = splitNames[0];
				String firstName = splitNames[1];
				firstNameList.addAll(formEnglishString(firstName));
				if(fixComplexNamepinyin.containsKey(lastName))
					lastNameList.add(fixComplexNamepinyin.get(lastName));
				else
					lastNameList.addAll(formEnglishString(lastName));
			
				result = formWithPattern_Chinese(firstNameList, lastNameList, patterns, simplify);
			}
		}
		
		if(nameType == 1) {
			String[] splitNames = splitNameEnglish(name);
//			System.out.println(splitNames.length);
			if(splitNames.length == 1) {
				String lastName = splitNames[0];
				lastNameList.add(lastName);
				result = formWithPattern4_English(lastNameList, patterns4);
			}
			if(splitNames.length == 2) {
				String lastName = splitNames[1];
				String firstName = splitNames[0];
				firstNameList.add(firstName);
				lastNameList.add(lastName);
				result = formWithPattern3_English(firstNameList, lastNameList, patterns3);
			} else if(splitNames.length == 3) {
				String lastName = splitNames[2];
				String middleName = splitNames[1];
				String firstName = splitNames[0];
				firstNameList.add(firstName);
				middleNameList.add(middleName);
				lastNameList.add(lastName);
				result = formWithPattern2_English(firstNameList, middleNameList, lastNameList, patterns2);
			}	
		}
		
		if(nameType == 2) {
			return new ArrayList<String>();
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
//		List<String> testNames = new ArrayList<String>();
//		for(String cn : complexLastNames) {
//			testNames.add(cn);
//		}
//		testNames.add("司马光");
//		testNames.add("欧阳峰");
//		testNames.add("管琤然");
//		testNames.add("尉迟敬得");
//		testNames.add("Felix Guan");
//		testNames.add("Alex N. Y. Tasu");
//		testNames.add("JohnE.Y.Smith");
//		testNames.add("Ken J. Brown");
		
//		for(String name :testNames) {
//			List<String> formEnglishString = formEnglishString(name);
//			if(formEnglishString.size() == 1) 
//				continue;
//			String[] splitNameEnglish = splitNameEnglish(name);
//			String[] splitNameChinese = splitNameChinese(name);
//			System.out.println(name);
//			for(String n : splitNameEnglish) {
//				System.out.print(n + "|");
//			}
//			System.out.println();
//		}
		
//		
		List<String> names = generateEnglishNames("管", false);
		System.out.println(names.size());
		for(String name : names) {
			System.out.print(name + "|");
		}
//		
		
//		List<String> nameList = new ArrayList<String>();
//		nameList.add("ChengRan");
//		nameList.add("Chao");
//		List<String> shortNameList = generateShortNameList(nameList);
//		for(String name : shortNameList) {
//			System.out.print(name + "|");
//		}
		
//		getNameType("James Harden");
	}
}


