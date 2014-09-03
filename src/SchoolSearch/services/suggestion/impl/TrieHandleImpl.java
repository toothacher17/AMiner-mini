package SchoolSearch.services.suggestion.impl;

import instante.TriePersonScore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import util.NameGeneration;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.suggestion.TrieHandle;
import SchoolSearch.services.suggestion.model.TrieNode;
import SchoolSearch.services.suggestion.model.TrieNodeContent;
import SchoolSearch.services.suggestion.model.TrieNodeContent.TrieInfoType;

public class TrieHandleImpl implements TrieHandle {
	final int contentSize = 10;
	final double END_SCORE_ADJUST = 100.0;
	
	final String targetFilePath = ConsistanceService.get("suggest.defaultFilePath");
	
	public TrieNode root;
	public List<TrieNodeContent> contentList;
	public Map<String, Integer> fulltextMap;

	public TrieHandleImpl() {
		if (!read()) {
			root = newNode();
			contentList = new ArrayList<TrieNodeContent>();
			contentList.add(null);
			fulltextMap = new HashMap<String, Integer>();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean read() {
		System.out.println("[loading suggestion]");
		long t0 = System.currentTimeMillis();
		
		File file = new File(targetFilePath);
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			root = (TrieNode) objectInputStream.readObject();
			contentList = (List<TrieNodeContent>) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != objectInputStream) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("[suggestion loaded] in " + (System.currentTimeMillis() - t0) + " ms" );
		return true;
	}

	private void write() {
		File file = new File(targetFilePath);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (null != parentFile)
				parentFile.mkdirs();
		}
		ObjectOutputStream objectOutStream = null;
		try {
			objectOutStream = new ObjectOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			objectOutStream.writeObject(root);
			objectOutStream.writeObject(contentList);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != objectOutStream) {
				try {
					objectOutStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setup(String key, String fullText, TrieNodeContent.TrieInfoType type, Integer id, double score) {
		Integer contentIndex = fulltextMap.get(fullText);
		if (null == contentIndex) {
			TrieNodeContent content = new TrieNodeContent(fullText, type, id, score);
			contentIndex = contentList.size();
			contentList.add(content);
			fulltextMap.put(fullText, contentIndex);
		}
		// System.out.println(">>" + key + "\t" + fullText + "\t" + contentIndex
		// + "\t" + score);
		char[] charArray = key.toCharArray();
		TrieNode currentNode = root;
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			int compareStart = 0;
			TrieNode trieNode = currentNode.getSubNodes().get(c);
			if (null == trieNode) {
				trieNode = newNode();
				currentNode.getSubNodes().put(c, trieNode);
			}
			if (i == charArray.length - 1) {
				score += END_SCORE_ADJUST;
			} else {
				compareStart = trieNode.endContent;
			}
			int[] nodeContentIndexArray = trieNode.getContentIndex();

			for (int j = compareStart; j < contentSize; j++) {
				int nodeContentIndex = nodeContentIndexArray[j];
				if (nodeContentIndex == 0) {
					nodeContentIndexArray[j] = contentIndex;
					break;
				} else if (nodeContentIndex == contentIndex) {
					break;
				} else {
					double nodeContentScore = contentList.get(nodeContentIndex).getScore();
					if (j < trieNode.endContent) {
						nodeContentScore += END_SCORE_ADJUST;
					}
					if (score > nodeContentScore) {
						for (int k = contentSize - 1; k > j; k--)
							nodeContentIndexArray[k] = nodeContentIndexArray[k - 1];
						nodeContentIndexArray[j] = contentIndex;
						break;
					}
				}
			}

			if (i == charArray.length - 1) {
				trieNode.endContent++;
			}
			currentNode = trieNode;
		}
	}

	private TrieNode newNode() {
		return new TrieNode(contentSize);
	}

	@Override
	public List<TrieNodeContent> findNodeContent(String key) {
		char[] charArray = key.toCharArray();
		TrieNode currentNode = root;
		for (int i = 0; i < charArray.length; i++) {
			Character c = charArray[i];
			currentNode = currentNode.getSubNodes().get(c);
			if (currentNode == null) {
				return new ArrayList<TrieNodeContent>();
			}
		}
		int[] contentIndex = currentNode.getContentIndex();
		// System.out.println(contentIndex.length);
		List<TrieNodeContent> result = new ArrayList<TrieNodeContent>();
		for (int index : contentIndex) {
			if (index == 0)
				break;
			result.add(contentList.get(index));
		}
		return result;
	}

	@Override
	public void rebuild() {
		if (null != root)
			root.clear();
		if (null != contentList)
			contentList.clear();
		if (null != fulltextMap)
			fulltextMap.clear();
		root = newNode();
		contentList = new ArrayList<TrieNodeContent>();
		contentList.add(null);
		fulltextMap = new HashMap<String, Integer>();
		// setup finished.

		TriePersonScore personScorer = new TriePersonScore();
		List<Person> allPerson = persondao.walkAll();
		for (Person p : allPerson) {
			List<String> generateEnglishNames = NameGeneration.generateEnglishNames(p.name, false);
			double score = personScorer.scoreRankByPerson(p.id);
			for (String key : generateEnglishNames) {
				key = key.trim().toLowerCase();
				setup(key, p.name, TrieInfoType.PERSON, p.id, score);
				key = key.replaceAll("[\\ \\.\\-\\_]", "");
				setup(key, p.name, TrieInfoType.PERSON, p.id, score);
			}
			setup(p.name, p.name, TrieInfoType.PERSON, p.id, score);
		}

		// build trie finished
		write();
	}

	@Inject
	PersonDao persondao;
}
