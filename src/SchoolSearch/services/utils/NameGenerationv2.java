package SchoolSearch.services.utils;

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

public class NameGenerationv2 {
	public static enum ExpandOption {
		Expand, NotExpand, EliminateExpand
	};

	static Map<String, String> fixComplexNamepinyin = new HashMap<String, String>();
	static Set<String> complexLastNames = new HashSet<String>();

	static List<List<String>> namePattern = new ArrayList<List<String>>();

	static List<String[]> flawPinyinPairArray = new ArrayList<String[]>();

	static {
		complexLastNames.addAll(loadNameStringFromFile("complexLastName.txt"));
		complexLastNames.addAll(loadNameStringFromFile("japaneseLastName.txt"));

		fixComplexNamepinyin.put("尉迟", "YuChi");
		fixComplexNamepinyin.put("拓跋", "TuoBa");
		fixComplexNamepinyin.put("长孙", "ZhangSun");
		fixComplexNamepinyin.put("拓拨", "TouBa");
		fixComplexNamepinyin.put("乐羊", "YueYang");
		fixComplexNamepinyin.put("纥于", "HeYu");
		fixComplexNamepinyin.put("陆", "Lu");

		List<String> patterns1 = new ArrayList<String>();
		List<String> patterns2 = new ArrayList<String>();

		patterns1.add("==lastName== ==firstName==");
		patterns1.add("==firstName== ==lastName==");

		patterns2.add("==firstName== ==middleName== ==lastName==");
		// patterns2.add("==lastName== ==middleName== ==firstName==");

		namePattern.add(patterns1);
		namePattern.add(patterns2);

	}

	private static Set<String> loadNameStringFromFile(String fileName) {
		InputStream stream = NameGenerationv2.class.getResourceAsStream("/transfer/" + fileName);
		Set<String> result = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				result.add(line.toString());
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
		return result;
	}

	public static int getNameType(String name) {
		boolean allChinese = true;
		boolean allEnglish = true;
		for (int i = 0; i < name.length(); i++) {
			UnicodeBlock uncodeBlock = Character.UnicodeBlock.of(name.charAt(i));
			// System.out.println("[NameGeneration]\t" + name.charAt(i) + "\t" +
			// uncodeBlock.toString());
			if (uncodeBlock.equals(Character.UnicodeBlock.BASIC_LATIN) || //
					uncodeBlock.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) || //
					uncodeBlock.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) || //
					uncodeBlock.equals(Character.UnicodeBlock.GREEK) || //
					uncodeBlock.equals(Character.UnicodeBlock.LATIN_EXTENDED_B)) {
				allChinese = false;
			} else if (uncodeBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || //
					uncodeBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) || //
					uncodeBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B) || //
					uncodeBlock.equals(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) || //
					uncodeBlock.equals(Character.UnicodeBlock.GENERAL_PUNCTUATION) //
			) {
				allEnglish = false;
			} else if (uncodeBlock.equals(Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) //
			) {

			} else {
				System.err.println("[NameGeneration][UnexpectedCharsetCode]\t" + name.charAt(i) + "\t" + uncodeBlock.toString());
			}
		}
		if (allChinese) {
			return 0;
		} else if (allEnglish) {
			return 1;
		} else {
			return 2;
		}
	}

	private static String[] splitNameChinese(String name) {
		if (name.length() <= 1) {
			return new String[] { name };
		}
		if (name.length() > 2) {
			String possibleLastName = name.substring(0, 2);
			if (complexLastNames.contains(possibleLastName)) {
				return new String[] { possibleLastName, name.substring(2, name.length()) };
			}
		}
		return new String[] { name.substring(0, 1), name.substring(1, name.length()) };
	}

	private static String[] splitNameEnglish(String name) {
		/**
		 * JohnSmith John E. Smith John E. S. John Eddie Smith JOHN SMITH
		 */
		List<Integer> upperCaseLocation = new ArrayList<Integer>();
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i)) == true) {
				upperCaseLocation.add(i);
			}
		}
		String[] nameSpaceSplit = name.split("\\s+");
		if (nameSpaceSplit.length == 1) {
			if (upperCaseLocation.size() <= 1 || upperCaseLocation.size() == name.length()) {
				return new String[] { name };
			} else if (upperCaseLocation.size() == 2) {
				int firstNameLocation = upperCaseLocation.get(1);
				return new String[] { name.substring(0, firstNameLocation).trim(), name.substring(firstNameLocation, name.length()).trim() };
			} else {
				int firstNameLocation = upperCaseLocation.get(1);
				int lastNameLocation = upperCaseLocation.get(upperCaseLocation.size() - 1);
				return new String[] { name.substring(0, firstNameLocation).trim(), name.substring(firstNameLocation, lastNameLocation).trim(), name.substring(lastNameLocation, name.length()).trim() };
			}
		} else if (nameSpaceSplit.length == 2 || nameSpaceSplit.length == 3) {
			return nameSpaceSplit;
		} else {
			StringBuilder nameBuilder = new StringBuilder();
			for (int i = 1; i < nameSpaceSplit.length - 1; i++)
				nameBuilder.append(nameSpaceSplit[i]).append(" ");
			return new String[] { nameSpaceSplit[0], nameBuilder.toString().trim(), nameSpaceSplit[nameSpaceSplit.length - 1] };
		}
	}

	private static List<String> transferToPinyinFromChineseString(String chineseNameString) {
		char[] charArray = chineseNameString.toCharArray();
		List<Set<String>> charEnglishList = new ArrayList<Set<String>>();
		for (char c : charArray) {
			Set<String> pinyinSet = new HashSet<String>();
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
			if (null == pinyinArray || pinyinArray.length == 0) {
				System.err.println("[pinyin not exist]" + c);
				return new ArrayList<String>();
			}

			for (String pinyin : pinyinArray) {
				if (null == pinyin) {
					System.err.println("[pinyin not exist]" + c);
				}

				if (pinyin.matches("[a-z:]+\\d")) {
					pinyin = pinyin.substring(0, pinyin.length() - 1);
				}
				char fl = pinyin.charAt(0);
				char upperCase = Character.toUpperCase(fl);
				pinyin = upperCase + pinyin.substring(1);

				if (pinyin.indexOf("u:") != -1) {
					pinyinSet.add(pinyin.toLowerCase().replaceAll("u:", "v"));
					pinyinSet.add(pinyin.toLowerCase().replaceAll("u:", "ü"));
				} else {
					pinyinSet.add(pinyin);
				}
			}
			charEnglishList.add(pinyinSet);
		}
		return generateNamesWithListSet(charEnglishList, 0, null);
	}

	private static List<String> generateNamesWithListSet(List<Set<String>> dataSource, int index, List<String> halfResult) {
		if (dataSource.size() == 0) {
			return new ArrayList<String>();
		}
		Set<String> currentSet = dataSource.get(index);
		List<String> result = new ArrayList<String>();
		if (index == 0) {
			result.addAll(currentSet);
		} else {
			for (String half : halfResult) {
				for (String current : currentSet) {
					result.add(half + current);
					// result.add(half + "-" + current); // Mark
				}
			}
		}
		if (null != halfResult)
			halfResult.clear();
		if (index == dataSource.size() - 1) {
			return result;
		} else {
			return generateNamesWithListSet(dataSource, index + 1, result);
		}
	}

	// private static List<String> generateShortNameList(List<String> NameList, Boolean open) {
	// if (open == true) {
	// List<String> ShortNameList = new ArrayList<String>();
	//
	// for (String name : NameList) {
	// List<Integer> upperCaseLocation = new ArrayList<Integer>();
	// for (int i = 0; i < name.length(); i++) {
	// if (Character.isUpperCase(name.charAt(i)) == true) {
	// upperCaseLocation.add(i);
	// }
	// }
	// if (upperCaseLocation.size() <= 1) {
	// ShortNameList.add(name.substring(0, 1).toUpperCase());
	// ShortNameList.add(name.substring(0, 1).toUpperCase() + ".");
	// } else {
	// int Location0 = upperCaseLocation.get(0);
	// char upperCase0 = name.charAt(Location0);
	// int Location = upperCaseLocation.get(1);
	// char upperCase = name.charAt(Location);
	// char lowerCase = Character.toLowerCase(upperCase);
	// ShortNameList.add(name.substring(0, Location) + "-" + name.substring(Location, name.length()));
	// ShortNameList.add(name.substring(0, Location) + lowerCase + name.substring(Location + 1, name.length()));
	// ShortNameList.add(name.substring(0, Location) + "-" + lowerCase + name.substring(Location + 1, name.length()));
	// ShortNameList.add(upperCase0 + "" + upperCase);
	// ShortNameList.add(upperCase0 + ". " + upperCase + ".");
	// ShortNameList.add(upperCase0 + "." + upperCase + ".");
	// ShortNameList.add(upperCase0 + ".-" + upperCase + ".");
	// ShortNameList.add(name.substring(0, Location) + " " + upperCase + ".");
	// }
	// }
	//
	// return ShortNameList;
	// } else
	// return new ArrayList<String>();
	//
	// }

	protected static List<String> formShortNameForm(String nameEnString) {
		List<String> result = new ArrayList<String>();
		if (nameEnString.toUpperCase().equals(nameEnString)) {
			result.add(new StringBuilder().append(nameEnString.charAt(0)).append('.').toString());
			return result;
		}

		Set<String> resultCombineSet = new HashSet<String>();
		char[] charArray = nameEnString.toCharArray();
		// AxxxBxxx -> A.
		resultCombineSet.add(new StringBuilder().append(charArray[0]).append('.').toString());
		// AxxxBxxx -> A.B.|A. B.
		{
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			for (int i = 0; i < charArray.length; i++) {
				if (Character.isUpperCase(charArray[i])) {
					if (i != 0)
						sb2.append(" ");
					sb1.append(charArray[i]).append('.');
					sb2.append(charArray[i]).append('.');
				}
			}
		}
		result.addAll(resultCombineSet);
		return result;
	}

	protected static List<String> formAdditionalNameForm(String nameEnString) {
		List<String> result = new ArrayList<String>();
		char[] charArray = nameEnString.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < charArray.length; i++) {
			if (i != 0 && charArray[i - 1] != ' ' && charArray[i - 1] != '-' && Character.isUpperCase(charArray[i])) {
				sb.append("-");
			}
			sb.append(charArray[i]);
		}
		return result;
	}

	private static List<String> formPattern(List<String> firstNameList, List<String> middleNameList, List<String> lastNameList, boolean simplify, boolean expand) {
		List<String> result = new ArrayList<String>();
		Set<String> formWithPattern_English = new HashSet<String>();

		int firstSize = firstNameList.size();
		int middleSize = middleNameList.size();
		int lastSize = lastNameList.size();

		Set<String> firstNameDetailSet = new HashSet<String>();
		Set<String> middleNameDetailSet = new HashSet<String>();
		Set<String> lastNameDetailSet = new HashSet<String>();

		if (firstSize > 0) {
			firstNameDetailSet.addAll(firstNameList);
			if (simplify)
				for (String name : firstNameList) {
					if (name.indexOf('.') == -1)
						firstNameDetailSet.addAll(formShortNameForm(name));
				}
			if (expand)
				for (String name : firstNameList) {
					firstNameDetailSet.addAll(formAdditionalNameForm(name));
				}
		}

		if (middleSize > 0) {
			middleNameDetailSet.addAll(middleNameList);
			if (simplify)
				for (String name : middleNameList) {
					if (name.indexOf('.') == -1)
						middleNameDetailSet.addAll(formShortNameForm(name));
				}
			if (expand)
				for (String name : middleNameList) {
					middleNameDetailSet.addAll(formAdditionalNameForm(name));
				}
		}

		if (lastSize > 0) {
			lastNameDetailSet.addAll(lastNameList);
			if (simplify)
				for (String name : lastNameList) {
					if (name.indexOf('.') == -1)
						lastNameDetailSet.addAll(formShortNameForm(name));
				}
			if (expand)
				for (String name : lastNameList) {
					lastNameDetailSet.addAll(formAdditionalNameForm(name));
				}
		}

		if (firstSize > 0) { // F M L | F L
			if (middleSize > 0) {
				int degree = 2;
				for (String middle : middleNameDetailSet) {
					for (String first : firstNameDetailSet) {
						for (String last : lastNameDetailSet) {
							for (int i = 0; i < degree; i++) {
								List<String> patternList = namePattern.get(i);
								for (String pattern : patternList) {
									formWithPattern_English.add(pattern.replaceFirst("==firstName==", first).replaceFirst("==middleName==", middle).replaceFirst("==lastName==", last));
								}
							}
						}
					}
				}
			} else {
				int degree = 1;
				for (String first : firstNameDetailSet) {
					for (String last : lastNameDetailSet) {
						for (int i = 0; i < degree; i++) {
							List<String> patternList = namePattern.get(i);
							for (String pattern : patternList) {
								formWithPattern_English.add(pattern.replaceFirst("==firstName==", first).replaceFirst("==lastName==", last));
							}
						}
					}
				}
			}
		} else {
			if (lastSize > 0) {
				formWithPattern_English.addAll(lastNameDetailSet);
			}
		}

		// for (String p : pattern) {
		// for (String ln : lastNameList) {
		// String s = p.replaceAll("==lastName==", ln);
		// formWithPattern_English.add(s);
		// }
		// }

		result.addAll(formWithPattern_English);
		return result;
	}

	public static List<String> generateEnglishNames(String name, boolean simplify, ExpandOption expand) {

		boolean doExpand = false;
		if (expand.equals(ExpandOption.Expand))
			doExpand = true;
		else if (expand.equals(ExpandOption.EliminateExpand))
			name = name.replaceAll("\\-", "");

		List<String> result = new ArrayList<String>();

		try {
			int nameType = getNameType(name);
			List<String> firstNameList = new ArrayList<String>();
			List<String> middleNameList = new ArrayList<String>();
			List<String> lastNameList = new ArrayList<String>();

			if (nameType == 0) {
				if (name.length() >= 6) {
					throw new Exception(String.format("Chinese name too long:\t%s", name));
				}
				String[] splitNames = splitNameChinese(name);
				if (splitNames.length == 1) {
					String singleName = splitNames[0];
					List<String> tempPinYinList = transferToPinyinFromChineseString(singleName);
					// lastNameList.addAll();
				} else {
					String lastName = splitNames[0];
					String firstName = splitNames[1];
					firstNameList.addAll(transferToPinyinFromChineseString(firstName));
					if (fixComplexNamepinyin.containsKey(lastName))
						lastNameList.add(fixComplexNamepinyin.get(lastName));
					else
						lastNameList.addAll(transferToPinyinFromChineseString(lastName));

					// result = formWithPattern_Chinese(firstNameList, lastNameList, patterns1, simplify);
				}
			} else if (nameType == 1) {
				String[] splitNames = splitNameEnglish(name);
				if (splitNames.length == 1) {
					String lastName = splitNames[0];
					lastNameList.add(lastName);
					// result = formWithPattern4_English(lastNameList, patterns4);
				}
				if (splitNames.length == 2) {
					String lastName = splitNames[1];
					String firstName = splitNames[0];
					firstNameList.add(firstName);
					lastNameList.add(lastName);
					// result = formWithPattern3_English(firstNameList, lastNameList, patterns3);
				} else if (splitNames.length == 3) {
					String lastName = splitNames[2];
					String middleName = splitNames[1];
					String firstName = splitNames[0];
					firstNameList.add(firstName);
					middleNameList.add(middleName);
					lastNameList.add(lastName);
					// result = formWithPattern2_English(firstNameList, middleNameList, lastNameList, patterns2);
				}
			} else if (nameType == 2) {
				throw new Exception("unpredictable type for name:\t" + name);
			}
			result = formPattern(firstNameList, middleNameList, lastNameList, simplify, doExpand);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result.size() == 0) {
				result.add(name);
			}
		}

		return result;
	}

	public static void main(String[] args) {
		List<String> testNames = new ArrayList<String>();
		// for(String cn : complexLastNames) {
		// testNames.add(cn);
		// }
		testNames.add("司马光");
		testNames.add("欧阳峰");
		testNames.add("管琤然");
		testNames.add("尉迟敬得");
		testNames.add("Felix Guan");
		testNames.add("Alex N. Y. Tasu");
		testNames.add("JohnE.Y.Smith");
		testNames.add("Ken J. Brown");
		testNames.add("Ken John Brown");
		testNames.add("Yu-Chi Jing-De");
		/*
		 * for (String name : testNames) { List<String> formEnglishString = formEnglishString(name); if (formEnglishString.size() == 1) continue; String[] splitNameEnglish = splitNameEnglish(name); System.out.println(name); for (String n : splitNameEnglish) { System.out.print(n + "|"); } System.out.println(); } //
		 */

		//
		// List<String> names = generateEnglishNames("吕国志", false);
		for (String name : testNames) {
			List<String> names = generateEnglishNames(name, false, NameGenerationv2.ExpandOption.EliminateExpand);
			System.out.println(name);
			for (String s : names) {
				System.out.print(s + "\t||\t");
			}
			System.out.println();
		}
		// generateShortNameList( testNames, false);

		// System.out.println(names.size());
		// for (String name : names) {
		// System.out.print(name + "|");
		// }
		//

		// List<String> nameList = new ArrayList<String>();
		// nameList.add("ChengRan");
		// nameList.add("Chao");
		// List<String> shortNameList = generateShortNameList(nameList);
		// for(String name : shortNameList) {
		// System.out.print(name + "|");
		// }

		// getNameType("James Harden");
	}
}
