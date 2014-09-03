package util;

import java.io.*;
import java.nio.Buffer;
import java.util.*;


public class SearchResultCourse{
	
	public static Map<String, Integer> loadWordMap(String fileNameIn){
		Map<String,Integer> result = new HashMap<String, Integer>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameIn));
			String readline ; 
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split(" ");
				result.put(wordList[0],Integer.parseInt(wordList[1]));
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> getRandomQueryList(List<String> wordMap, Integer queryListNum){
		List<String> result = new ArrayList<String>();
		List<Integer> randomNum = new ArrayList<Integer>();
		for(int i = 0 ; i < queryListNum ; i ++){
			randomNum.add((int)(Math.random()*19000));
		}
		for(Integer tempNum : randomNum){
			result.add(wordMap.get(tempNum));
		}
		return result;
	} 
	
	public static List<List<Double>> loadResultMatrix (String fileNameIn){
		List<List<Double>> result = new ArrayList<List<Double>>();
		try{
			BufferedReader in = new BufferedReader (new FileReader(fileNameIn));
			String readline ; 
			while((readline = in.readLine())!=null){
				List<Double> temp = new ArrayList<Double>();
				String [] wordList = readline.split(" ");
				for(String tempWord : wordList){
					temp.add(Double.parseDouble(tempWord));
				}
				result.add(temp);
			}
			in.close();
			
		}catch( IOException e ){
			e.printStackTrace();
		}
		System.out.println("just for test to output " + result.get(0).get(0));
		return result;
	}
	
	public static List<Integer> findTheBiggestColum(List<Double> tempArray){
		Map<Double,Integer> tempMap = new HashMap<Double, Integer>();
		List<Double> tempList = new ArrayList<Double>(tempArray);
		List<Integer> result = new ArrayList<Integer>();
		int count = 0;
		for(Double tempNum : tempArray){
			tempMap.put(tempNum, count);
			count ++;
		}
		Collections.sort(tempList);
		result.add(tempMap.get(tempList.get(tempList.size()-1)));
		result.add(tempMap.get(tempList.get(tempList.size()-2)));
		result.add(tempMap.get(tempList.get(tempList.size()-3)));
		result.add(tempMap.get(tempList.get(tempList.size()-4)));
		result.add(tempMap.get(tempList.get(tempList.size()-5)));
		return result;
	}
	
	public static Map<Integer,Integer> getRemainedDoc (String fileNameIn){
		Map<Integer,Integer> result = new HashMap<Integer, Integer>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameIn));
			String readline ;
			int count = 0;
			while((readline = in.readLine())!=null){
				result.put(count,Integer.parseInt(readline));
				count++;
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<Integer, String> getRealDoc(String fileNameIn){
		Map<Integer,String> result = new HashMap<Integer, String>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameIn));
			String readline ;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split("\t");
				StringBuilder sb = new StringBuilder();
				for (String tempWord : wordList){
					sb.append(tempWord + " ");
				}
				result.put(Integer.parseInt(wordList[0]), sb.toString());
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> manualMakeQueryList(){
		List<String> result = new ArrayList<String>();
		result.add("数据挖掘");
		result.add("信息检索");
		result.add("能源");
		result.add("通信");
		result.add("文学");
		result.add("经济");
		result.add("英语");
		result.add("天文");
		return result;
	}
	
	public static void main(String [] args){
		String fileInWordmap = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\ldalmsearch\\wordmap.txt";
		String fileInRemainedDoc = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\ldalmsearch\\remainedCourseDocNo.txt";
		String fileInMatrix = "D:\\Users\\jingyuanliu\\workspaceformatlab\\course1026\\courseldalmresult.txt";
		String fileInDoc = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\ldalmsearch\\course.txt";
		String fileOut = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\ldalmsearch";
		Map<String,Integer> wordMap = SearchResultCourse.loadWordMap(fileInWordmap);
//		List<String> wordMapList = new ArrayList<String>(wordMap.keySet());

//		List<String> randomQueryList = SearchResultCourse.getRandomQueryList(wordMapList, 10);
		
		List<String> manualQueryList = SearchResultCourse.manualMakeQueryList();
		
		List<List<Double>> matrix = loadResultMatrix(fileInMatrix); 
		
		Map<Integer,Integer> remainedDoc = SearchResultCourse.getRemainedDoc(fileInRemainedDoc) ;
		
		Map<Integer, String> outputDoc = SearchResultCourse.getRealDoc(fileInDoc);
		
		File f = new File(fileOut,"course1manual.txt");
		try{
			PrintWriter out = new PrintWriter(f);
			for(String tempQuery : manualQueryList){
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println(tempQuery);
				out.println(tempQuery);
				Integer wordNumber = wordMap.get(tempQuery);
				System.out.println("the wordNumber is " + wordNumber);
				out.println("the wordNumber is " + wordNumber);
				List<Double> getWordLineInMatrix  = matrix.get(wordNumber);
				List<Integer> getTheTop5RelatedDoc = SearchResultCourse.findTheBiggestColum(getWordLineInMatrix);
				for(Integer tempInteger : getTheTop5RelatedDoc){
					System.out.println("<<<<<<<" + tempInteger);
					Integer realDocNum = remainedDoc.get(tempInteger);
					System.out.println("the realDocNum is " + realDocNum);
					String realDocName = outputDoc.get(realDocNum);
					System.out.println("the realdoc:  " + realDocName);
					out.println("the realdoc:  " + realDocName);
					System.out.println("------------------------------------ ");
					out.println("------------------------------------ ");
				}
			}
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
} 






