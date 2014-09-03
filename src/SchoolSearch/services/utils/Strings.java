package SchoolSearch.services.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

/**
 * @desc - Strings - tools for String.
 * 
 * @author gb<elivoa@gmail.com> @date Mar 13, 2009 @version 0.1.0.0
 * 
 * @ModifiedDate Mar 13, 2009
 * @ModifiedDate Mar 19, 2009 - Add parseToInt();
 * @ModifiedDate Mar 26, 2009 - Add
 *               removeUnvalidCharacters(),combineBlankCharacters();
 * 
 */
public class Strings {

	public static String EMPTY_STRING = "";
	public static String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * An unmodifiable set containing some common English words that are not
	 * usually useful for searching.
	 */

	public static String makePreviewString(String oriString, int amount, String suffix) {
		if (null == oriString || "".equals(oriString.trim())) {
			return "";
		}
		String result = oriString.substring(0, Math.min(amount, oriString.length()));
		if (amount < oriString.length()) {
			result += " ...";
		}
		return result;
	}

	/**
	 * return empty string if input is null.
	 */
	public static String killNull(String string) {
		return null == string ? "" : string;
	}

	/**
	 * Return trimed string of input. Return empty string if input is null.
	 */
	public static String safeTrim(String string) {
		return null == string ? "" : string.trim();
	}

	public static String deepTrim(String query) {
		return (null == query) ? EMPTY_STRING : query.replaceAll("\\s+", " ").trim();
	}

	/**
	 * Return true if input string is null or is an empty string.
	 */
	public static boolean isEmpty(String string) {
		return null == string || "".equals(string.trim());
	}

	public static boolean isNotEmpty(String string) {
		return null != string && !"".equals(string.trim());
	}

	/**
	 * Combine the input string into one string splited by the given splitSign.
	 */
	public static String concat(Collection<?> list, String splitSign) {
		assert !isEmpty(splitSign);
		if (null == list) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Object string : list) {
			if (count++ > 0) {
				sb.append(splitSign);
			}
			sb.append(string);
		}
		return sb.toString();
	}

	/**
	 * Combine the input string into one string splited by the given splitSign.
	 */
	public static String joinInt(Collection<Integer> list, String splitSign) {
		assert !isEmpty(splitSign);
		if (null == list) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Integer intvalue : list) {
			if (count++ > 0) {
				sb.append(splitSign);
			}
			sb.append(intvalue);
		}
		return sb.toString();
	}

	/**
	 * Combine the input string into one string splited by the given splitSign.
	 */
	public static String joinInt(int[] list, String splitSign) {
		assert !isEmpty(splitSign);
		if (null == list) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Integer intvalue : list) {
			if (count++ > 0) {
				sb.append(splitSign);
			}
			sb.append(intvalue);
		}
		return sb.toString();
	}

	/**
	 * Combine Strings, use splitSign to split.
	 */
	public static String concat(String splitSign, String... strings) {
		return concat(strings, splitSign);
	}

	/**
	 * Combine String array to one String, use splitSign to split.
	 */
	public static String concat(String[] stringArray, String splitSign) {
		assert !isEmpty(splitSign);
		if (null == stringArray) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String string : stringArray) {
			if (count++ > 0) {
				sb.append(splitSign);
			}
			sb.append(Strings.safeTrim(string));
		}
		return sb.toString();
	}

	/**
	 * Combine String array to one String, use splitSign to split.
	 */
	public static String concat(int[] intArray, String splitSign) {
		assert !isEmpty(splitSign);
		if (null == intArray) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (int intValue : intArray) {
			if (count++ > 0) {
				sb.append(splitSign);
			}
			sb.append(intValue);
		}
		return sb.toString();
	}

	public static String safeConcat(Collection<String> list, String splitSign) {
		String result = concat(list, splitSign);
		return null == result ? EMPTY_STRING : result;
	}

	public static String saveConcat(String[] stringArray, String splitSign) {
		String result = concat(stringArray, splitSign);
		return null == result ? EMPTY_STRING : result;
	}

	public static List<String> splitToList(String str, String splitSign) {
		return splitToList(str, splitSign, false);
	}

	public static List<String> splitToList(String str, String splitSign, boolean killEmpty) {
		assert !isEmpty(splitSign);

		if (null == str) {
			return null;
		}
		if ("".equals(str.trim())) {
			return Collections.emptyList();
		}

		List<String> resultList = new ArrayList<String>();
		String[] splits = str.split(splitSign);
		if (killEmpty) {
			for (String term : splits) {
				term = term.trim();
				if (!"".equals(term)) {
					resultList.add(term.trim());
				}
			}
		} else {
			for (String term : splits) {
				resultList.add(term.trim());
			}
		}
		return resultList;
	}

	public static String getShortPersonName(String name) {
		if (Strings.isEmpty(name)) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		String[] splits = name.split(" ");
		for (int i = 0; i < splits.length - 1; i++) {
			if (splits[i].length() > 0) {
				sb.append(splits[i].charAt(0)).append("");
			}
		}
		sb.append(" ");
		sb.append(splits[splits.length - 1]);
		return sb.toString();
	}

	public static List<Integer> splitAndParseToIntegerList(String str, String splitSign, boolean killEmpty) {
		assert !isEmpty(splitSign);

		if (null == str) {
			return null;
		}
		if ("".equals(str.trim())) {
			return Collections.emptyList();
		}

		List<Integer> resultList = new ArrayList<Integer>();
		String[] splits = str.split(splitSign);
		if (killEmpty) {
			for (String term : splits) {
				if (!term.trim().equals("undefined")) {
					Integer id = null;
					try {
						id = Integer.parseInt(term.trim());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (null != id) {
						resultList.add(id);
					}
				}
			}
		} else {
			for (String term : splits) {
				Integer id = null;
				try {
					id = Integer.parseInt(term.trim());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				resultList.add(id);
			}
		}
		return resultList;
	}

	public static List<Integer> safeSplitAndParseToInt(String str, String splitSign) {
		assert !isEmpty(splitSign);

		if (null == str) {
			return null;
		}
		if ("".equals(str.trim())) {
			return Collections.emptyList();
		}

		List<Integer> resultList = new ArrayList<Integer>();
		String[] splits = str.split(splitSign);
		for (String term : splits) {
			try {
				resultList.add(Integer.parseInt(term.trim()));
			} catch (NumberFormatException e) {
				System.err.println("Error(Strings::splitAndParseToIntSafe): Parse Int error. " + term.trim());
			}
		}
		return resultList;
	}

	public static List<String> safeSplitToList(String str, String splitSign) {
		List<String> resultList = splitToList(str, splitSign);
		if (null == resultList) {
			return Collections.emptyList();
		}
		return resultList;
	}

	public static String[] splitToArray(String str, String splitSign) {
		List<String> resultList = splitToList(str, splitSign);
		if (null == resultList) {
			return null;
		}

		return resultList.toArray(new String[resultList.size()]);
	}

	public static String[] safeSplitToArray(String str, String splitSign) {
		List<String> resultList = splitToList(str, splitSign);
		if (null == resultList) {
			return EMPTY_STRING_ARRAY;
		}
		return resultList.toArray(new String[resultList.size()]);
	}

	/**
	 * Parse String to int, return defaultValue if failed.
	 * 
	 * @param intString
	 * @param defaultValue
	 * @return
	 */
	public static int parseToInt(String intString, int defaultValue) {
		if (isEmpty(intString)) {
			return defaultValue;
		}
		int result = defaultValue;
		try {
			result = Integer.parseInt(intString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	// ///
	/**
	 * 解析Request传过来的URL，带参数的�? TODO regular expression.
	 * 
	 * @param src
	 * @param length
	 * @return
	 */
	public static Map<String, String> parseParameter(String url) {
		// TODO verify format.
		if (isEmpty(url)) {
			return Collections.emptyMap();
		}
		// __prefix = http://www.powazi.com/sight?
		// __domain = www.powazi.com
		// __port =
		// __path = sight
		// guid = ...
		// ...
		Map<String, String> map = new HashMap<String, String>();
		int i1 = url.indexOf("?");
		if (i1 >= 0) {
			map.put("__prefix", url.substring(0, i1));
		}
		i1 = Math.max(i1, 0);
		String rest = url.substring(i1 + 1);
		String[] paramPair = rest.split("&");
		for (String pair : paramPair) {
			int index = pair.indexOf("=");
			if (index > 0) {
				map.put(pair.substring(0, index), pair.substring(index + 1));
			} else {
				map.put(pair, "");
			}
		}
		return map;
	}

	/*****************************
	 * Remove invalid characters
	 *****************************/
	public static String combineBlankCharacters(String str) {
		return str.replaceAll("\\s+", " ");
	}

	/**
	 * This method ensures that the output String has only valid XML unicode
	 * characters as specified by the XML 1.0 standard. For reference, please
	 * see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty String if the input is
	 * null or empty.
	 * 
	 * @param in
	 *            The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String removeUnvalidCharacters(String in) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF))) {
				out.append(current);
			} else {
				System.err.print("[" + current + "|" + (int) current + "]");
			}

		}
		return out.toString();
	}

	public static String toFileName(String name) {
		if (isEmpty(name)) {
			return "fn_" + UUID.randomUUID();
		}
		String result = name.replaceAll("[^a-zA-Z0-9_]", "_").trim();
		return result;
	}

	/**
	 * @deprecated
	 */
	public static String filterString(String s1) {
		String s = s1.replaceAll("[^\\w]", " ");
		s = s.replaceAll("[^\\w]", " ");
		s = s.replaceAll("\\s+", " ");
		s = s.trim().toLowerCase();
		return s;
	}

	public static String templateFile(String pageClassName) {
		return templateFile(pageClassName, false);
	}

	public static String templateFile(String pageClassName, boolean lowerCased) {
		assert null != pageClassName : "template class name can't be null";
		if (pageClassName.length() <= 5) {
			return pageClassName;
		}
		StringBuilder sb = new StringBuilder();
		boolean leftUpper = false;
		for (int i = 0; i < pageClassName.length(); i++) {
			char c = pageClassName.charAt(i);

			if (Character.isUpperCase(c) && !leftUpper && i != 0) {
				sb.append("_");
			}

			if (Character.isUpperCase(c)) {
				leftUpper = true;
			} else {
				leftUpper = false;
			}

			sb.append(lowerCased ? Character.toLowerCase(c) : c);
		}
		return sb.toString();
	}

	public static boolean isInteger(String str) {
		for (int i = 0; i < str.length(); i++) {
			int ascii = (int) str.charAt(i);
			if (48 > ascii || ascii > 57) {
				return false;
			}
		}
		return true;
	}

	public static int count(StringBuilder authorSB, char c) {
		if (null == authorSB) {
			return -1;
		}
		int count = 0;
		for (int i = 0; i < authorSB.length(); i++) {
			if (c == authorSB.charAt(i)) {
				count++;
			}
		}
		return count;
		// System.out.println("");
	}

	public static String escapeQuery(String str) {
		return killNull(str).replaceAll("[%<>/\']+", "");
	}

	public static String escapeInJS(String str) {
		return safeTrim(str).replaceAll("'", "\\'");
	}

	public static String translateSEOLink(String str) {
		return translateSEOLink(str, "-");
	}

	public static String translateSEOLink(String str, String splitChar) {
		// TODO find best performace way, string token?
		String[] splits = safeTrim(str).split("[^a-zA-Z]+");
		int maxlength = 145;
		StringBuilder sb = new StringBuilder();
		for (String term : splits) {
			if (!Strings.isEmpty(term)) {
				maxlength -= term.length();
				if (maxlength < 0) {
					break;
				}
				sb.append(term).append(splitChar);
			}
		}
		if (sb.length() >= 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String replace_back(String value) {
		if (null == value) {
			return null;
		}
		return value.replace("_", " ");
	}

	public static String translateSEOLinkBack(String str) {
		return safeTrim(str).replaceAll("[_-]", " ").trim();
	}

	/**
	 * 比较两个字符串是否相等�?可兼容字符串为null的情况�? null和空字符串会被认为是相等�?\r\n和\n会被认为是相�?前后的空格会被忽�?	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		boolean isEquals = false;
		if (str1 == null) {
			str1 = "";
		}
		if (str2 == null) {
			str2 = "";
		}
		str1 = Strings.safeTrim(str1).replace("\r", "").trim();
		str2 = Strings.safeTrim(str2).replace("\r", "").trim();
		isEquals = str1.equals(str2);
		return isEquals;
	}
	
	public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
	
	public static void main(String[] args) {
		System.err .println(
				Strings.translateSEOLink("25 years isca retrospectives and reprints25 years isca retrospectives and reprints25 years isca retrospectives and reprints25 years isca retrospectives "));
		System.err.println(Strings.translateSEOLinkBack("25-years-isca-retrospectives-and-reprints-2453.html"));
		System.out
				.println(Strings
						.translateSEOLink("Ability, Breadth, and Parsimony in Computational Models of Higher-Order Cognition."));
		// System.err.println(Strings.getShortPersonName("Jie Tang"));
		// System.err.println(Strings.getShortPersonName("James A. Hendler"));
		// System.out
		// .println(Strings
		// .escapeQuery("keyword=haofen%2520wang%3Cscript%3Ealert%28document.cookie%29%3C/script%3Ehaofen%20wang&id=490609#haofen%2520wang%3Cscript%3Ealert%28document.cookie%29%3C/script%3E"));
	}

}
