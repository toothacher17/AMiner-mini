package util;

import java.io.*;
import java.util.*;

public class searchResult {
	
	public static Integer findWordNoViaWordMap(String wordString){
		Integer result = null ;
		String fileNameInWordmap = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord\\wordmap.txt";
		Map<String,Integer> wordMap = new HashMap<String, Integer>();
		try{
			BufferedReader in = new BufferedReader (new FileReader(fileNameInWordmap));
			String readline ;
			int count = 1 ;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split(" ");
//				Integer temp = wordList.length;
//				if (temp == 2){
////					System.out.println(count + "   " + temp);
//				}else{
//					System.out.println(count + ">> " + temp);
//				}
				
				Integer temp = Integer.parseInt(wordList[1]);
				wordMap.put(wordList[0], temp);
				count++;
			}
			in.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		result = wordMap.get(wordString);
		return result;
	}
	
	public static List<Integer> findTheBiggestCountInMatirx(Integer lineNum){
		String fileName = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord\\resultmatrix.txt";
//		juzhen  xuyao  zhuanzhiguo!!
		List<List<Double>> result = new ArrayList<List<Double>>();
		try{
			BufferedReader in = new BufferedReader (new FileReader( fileName));
			String readline ;
			while((readline = in.readLine())!=null){
				List<Double> temp = new ArrayList<Double> ();
				String [] wordlist = readline.split(" ");
				for (String tempWord : wordlist){
					temp.add(Double.parseDouble(tempWord));
				}
				result.add(temp);
			}
			in.close();
		}catch(IOException e ){
			e.printStackTrace();
		}
		List<Double> tempList = new ArrayList<Double>(result.get(lineNum));
		Map<Double,Integer> tempMap = new HashMap<Double, Integer>();
		int count = 0 ;
		for (Double tempCount : tempList){
			tempMap.put(tempCount, count);
			count++;
		}
		Collections.sort(tempList);
		System.out.println("the biggest value " + tempList.get(tempList.size()-1));
		System.out.println("the most related doc" + tempMap.get(tempList.get(tempList.size()-1))+1);
		List<Integer> returnResult = new ArrayList<Integer>();
		returnResult.add(tempMap.get(tempList.get(tempList.size()-1))+1);
		returnResult.add(tempMap.get(tempList.get(tempList.size()-2))+1);
		returnResult.add(tempMap.get(tempList.get(tempList.size()-3))+1);
		returnResult.add(tempMap.get(tempList.get(tempList.size()-4))+1);
		returnResult.add(tempMap.get(tempList.get(tempList.size()-5))+1);
		return returnResult;
	}

	public static Integer findCourseDocViaDocList(Integer docNumber){
		Integer result = null;
		String fileNameInRemainedDocNo = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord\\remainedGrapubDocNo.txt";
		List <Integer> docNum = new ArrayList<Integer>();
		try{
			BufferedReader in = new BufferedReader (new FileReader(fileNameInRemainedDocNo));
			String readline;
			while((readline = in.readLine())!=null){
				docNum.add(Integer.parseInt(readline));
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		result = docNum.get(docNumber-1);
		return result;
	}
	
	public static String printCorseInformation(Integer docNum){
		String result = null;
		String fileNameInPrimary = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord\\grapub.txt";
		Map<Integer,String> resultMap = new HashMap<Integer, String>(); 
		try{
			BufferedReader in = new BufferedReader (new FileReader(fileNameInPrimary));
			String readline;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split("\t");
				StringBuilder sb = new StringBuilder();
				for (String tempWord : wordList){
					sb.append(tempWord + " ");
				}
				resultMap.put(Integer.parseInt(wordList[0]), sb.toString());
				
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		result = resultMap.get(docNum);
		return result;
	}
	
	public static List<String> getQueryFromWordMap(Integer queryNum){
		List<String> result = new ArrayList<String>();
		Map<Integer,String> referredMap = new HashMap<Integer, String>();
		String filenameIn = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord\\wordmap.txt";
		try{
			BufferedReader in = new BufferedReader(new FileReader(filenameIn));
			String readline;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split(" ");
				referredMap.put(Integer.parseInt(wordList[1]), wordList[0]);
			}
		}catch( IOException e){
			e.printStackTrace();
		}
		List<Integer> randomNum = new ArrayList<Integer>();
		for(int i = 0 ; i < queryNum ; i ++){
			randomNum.add((int)(Math.random() * 19000));
		}
		for (Integer tempNum : randomNum){
			result.add(referredMap.get(tempNum));
		}
		return result;
	}
	
	
	public static void main(String [] args){
		
//		List<String> queryList = searchResult.getQueryFromWordMap(10);
//		String fileNameOut = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord";
//		File f = new File(fileNameOut,"grapubresult.txt");
//		try{
//			PrintWriter out = new PrintWriter(f);
//			for (String query : queryList){
//				System.out.println(query);
//				Integer queryLineNo = searchResult.findWordNoViaWordMap(query);
//				System.out.println("the query colum number " + queryLineNo);
//				List<Integer> docNum =  searchResult.findTheBiggestCountInMatirx(queryLineNo);
//	//		zhuyi zaizheli xuyao dui queryLineNo jinxing  + 1 dedao1zhengchangdezhi
//				out.println(query);
//				for(Integer tempDocNum : docNum){
//					System.out.println("the the biggest relevance doc number " + tempDocNum);
//					out.println(tempDocNum);
//					Integer docRealNum = searchResult.findCourseDocViaDocList(tempDocNum);
//					System.out.println(docRealNum);
//					out.println(docRealNum);
//					String result = searchResult.printCorseInformation(docRealNum);
//					System.out.println(result);
//					out.println(result);
//					System.out.println("---------------------------------------------------");
//					out.println("--------------------------------------------------------");
//				}
//			}
//			out.close();
//		}catch (IOException e ){
//			e.printStackTrace();
//		}
		
		
		
		
		
		String query ="重金属";
		System.out.println(query);
		Integer queryLineNo = searchResult.findWordNoViaWordMap(query);
		System.out.println("the query colum number " + queryLineNo);
		List<Integer> docNum =  searchResult.findTheBiggestCountInMatirx(queryLineNo);
//	zhuyi zaizheli xuyao dui queryLineNo jinxing  + 1 dedao1zhengchangdezhi
		String fileNameOut = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1021\\test1\\docWord";
		File f = new File(fileNameOut,"result.txt");
		try{
			PrintWriter out = new PrintWriter(f);
			for(Integer tempDocNum : docNum){
				System.out.println("the the biggest relevance doc number " + tempDocNum);
				out.println(tempDocNum);
				Integer docRealNum = searchResult.findCourseDocViaDocList(tempDocNum);
				System.out.println(docRealNum);
				out.println(docRealNum);
				String result = searchResult.printCorseInformation(docRealNum);
				System.out.println(result);
				out.println(result);
				System.out.println("---------------------------------------------------");
				out.println("--------------------------------------------------------");
			}
		}catch (IOException e ){
			e.printStackTrace();
		}
	}
}







