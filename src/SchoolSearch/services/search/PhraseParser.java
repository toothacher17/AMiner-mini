package SchoolSearch.services.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import SchoolSearch.services.translation.TranslationService;

public class PhraseParser {
	
	private Set<String> userDict = null;
	private int maxLength;
	private boolean isMaxLengthMatchingOnly;
	private boolean needPhaseOnlySingleWord;

	public PhraseParser(int maxLength) {
		this.userDict = new HashSet<String>();
		this.maxLength = maxLength;
		this.isMaxLengthMatchingOnly = true;
		this.needPhaseOnlySingleWord = true;
	}

	public PhraseParser(Set<String> userDict, int maxLength) {
		this.userDict = userDict;
		this.maxLength = maxLength;
		this.isMaxLengthMatchingOnly = true;
		this.needPhaseOnlySingleWord = true;
	}

	public PhraseParser(Set<String> userDict, int maxLength, boolean isMaxLengthMatchingOnly) {
		this.userDict = userDict;
		this.maxLength = maxLength;
		this.isMaxLengthMatchingOnly = isMaxLengthMatchingOnly;
		this.needPhaseOnlySingleWord = true;
	}

	private List<String> arrStr(String str) {
		char[] phasebl = str.toLowerCase().toCharArray();
		boolean afterSpace = true;
		StringBuffer sb = new StringBuffer();
		List<String> wordList = new ArrayList<String>();
		for (char ch : phasebl) {
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
				afterSpace = false;
				sb.append(ch);
			} else if (!afterSpace) {
				wordList.add(sb.toString());
				sb = new StringBuffer();
				afterSpace = true;
			}
		}
		wordList.add(sb.toString());
		return wordList;
	}

	public void addPhase(String phase) {
		List<String> wordList = arrStr(phase);
		StringBuffer sb = new StringBuffer();
		for (String word : wordList) {
			sb.append(word).append(" ");
		}
		// System.out.println("add word : " + sb.toString());
		userDict.add(sb.toString().trim());
	}

	/**
	 * accept english word only
	 * 
	 * @param word
	 * @return
	 */
	public List<String> parse(String paragraph) {
		List<String> phraseList = new ArrayList<String>();
		String[] sentenceList = paragraph.split("[,.?\\\"\\\']");
		for (String sentence : sentenceList) {
			List<String> wordList = this.arrStr(sentence);
			int wordListLength = wordList.size();
			for (int i = 0; i < wordListLength; i++) {
				StringBuffer sb = new StringBuffer();
				int jMax = (i + maxLength >= wordListLength) ? (wordListLength - i) : maxLength;
				// System.out.println("len :" + jMax);
				Stack<String> wordsStack = new Stack<String>();
				for (int j = 0; j < jMax; j++) {
					sb.append(wordList.get(i + j)).append(" ");
					wordsStack.push(sb.toString());
				}
				boolean found = false;
				for (int j = jMax - 1; j >= 0; j--) {
					String testPhase = wordsStack.pop();
					// System.out.println("check : " + testPhase);
					if (userDict.contains(testPhase.trim())) {
						phraseList.add(testPhase.toString().trim());
						found = true;
						if (isMaxLengthMatchingOnly) {
							i += j;
							break;
						}
					}
				}
				if (!found && needPhaseOnlySingleWord) {
					phraseList.add(wordList.get(i).trim());
				}
			}
		}
		return phraseList;
	}

	public Set<String> getUserDict() {
		return userDict;
	}

	public void setUserDict(Set<String> userDict) {
		this.userDict = userDict;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isMaxLengthMatchingOnly() {
		return isMaxLengthMatchingOnly;
	}

	public void setMaxLengthMatchingOnly(boolean isMaxLengthMatchingOnly) {
		this.isMaxLengthMatchingOnly = isMaxLengthMatchingOnly;
	}

	public boolean isNeedPhaseOnlySingleWord() {
		return needPhaseOnlySingleWord;
	}

	public void setNeedPhaseOnlySingleWord(boolean needPhaseOnlySingleWord) {
		this.needPhaseOnlySingleWord = needPhaseOnlySingleWord;
	}

	public static void main(String[] args) {
		PhraseParser parser = new PhraseParser(TranslationService.returnSet(), 4);
		parser.setNeedPhaseOnlySingleWord(false);
		parser.setMaxLengthMatchingOnly(true);

		String testString = "data mining algorithm";
		List<String> phaseList = parser.parse(testString);
		System.out.println("Phase may as follow : ");
		for (String phase : phaseList) {
			System.out.println(":" + phase);
		}
	}

}
