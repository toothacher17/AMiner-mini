package util;

import java.io.*;
import java.util.*;

import SchoolSearch.services.utils.dataUpdateTools.utils.NLPUtils;

public class FilterFenciResult {
	
	public static void useSet(){
		String fileNameInDic = "D:\\Users\\jingyuanliu\\newdata\\fenci\\unfiltered\\lastStopWords.txt";
		String fileNameInFenci = "D:\\Users\\jingyuanliu\\newdata\\fenci\\unfiltered\\CourseFenciResult10291.txt";
		String fileNameOut1 = "D:\\Users\\jingyuanliu\\newdata\\fenci\\unfiltered";
		String fileNameOut2 = "D:\\Users\\jingyuanliu\\newdata\\fenci\\unfiltered";
		Set<String> stopword = new HashSet<String>();
		Map<String,Integer> wordshowtimes = new HashMap<String, Integer>();
		Set<String> result = new HashSet<String>();
		try{
			int countNum  = 0;
			File f1= new File(fileNameOut1,"CourseFilterResult10291.txt");
			File f2= new File(fileNameOut2,"CourseDeletNo10291.txt");
			PrintWriter out1 = new PrintWriter(f1);
			PrintWriter out2 = new PrintWriter(f2);
			
			BufferedReader in1  = new BufferedReader(new FileReader(fileNameInDic));
			String readstopword ;
			int stopcount = 0;
			while((readstopword=in1.readLine())!=null){
				String [] stopWordList = readstopword.split(" ");
				for(String everystopword : stopWordList ){
					if(!stopword.contains(everystopword)){
						stopword.add(everystopword);
					}
				}
				stopcount ++;
			}
			System.out.println(" the stopword countNUme is  " + stopcount);
			in1.close();
			
			BufferedReader in2 = new BufferedReader( new FileReader(fileNameInFenci));
			String readfenci;
			while((readfenci = in2.readLine())!=null){
				String [] fenciwordlist = readfenci.split(" ");
				for(String everyfenciword : fenciwordlist){
					if((!stopword.contains(everyfenciword))&&NLPUtils.isChinese(everyfenciword)){
						if(result.contains(everyfenciword)){
							int counttemp = wordshowtimes.get(everyfenciword);
							counttemp++;
							wordshowtimes.put(everyfenciword, counttemp);
						}else{
							result.add(everyfenciword);
							System.out.println(everyfenciword);
							wordshowtimes.put(everyfenciword, 0);
						}
					}
				}
			}
			in2.close();
			System.out.println("the resultset size is "+result.size());
			
			BufferedReader in3 = new BufferedReader(new FileReader(fileNameInFenci));
			String readline3;	
			while((readline3 = in3.readLine())!=null){
				String [] tempWordList = readline3.split(" ");
				List<String> filterResult = new ArrayList<String>();
				for (String everyWord : tempWordList){
					if((result.contains(everyWord))&&(wordshowtimes.get(everyWord)>1)){
//						直接滤去了英文，因为现阶段英文未做“分词”，所以未做翻译，所以会影响分词和过滤的结果
						filterResult.add(everyWord);
					}
				}
				System.out.println("the current num is " + countNum);
				if(filterResult.size()>5){
					StringBuilder sb = new StringBuilder();
					for(String finalWord : filterResult){
						sb.append(finalWord + " ");
					}
					out1.println(sb.toString().trim());
				}else{
//					System.out.println("deletNum2 is " + countNum);
					out2.println(countNum);
				}
				countNum ++ ;
			}	
			System.out.println("the finalNum is " + countNum);
			in3.close();
			out1.close();
			out2.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public static void useList(){
		String fileNameInDic = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\lastStopWords.txt";
		String fileNameInFenci = "D:\\Users\\jingyuanliu\\data\\fenciedUnfilteredDoc\\result1025\\GrapubFenciResult10251.txt";
		String fileNameOut1 = "D:\\Users\\jingyuanliu\\data\\fenciedFilteredDoc\\result1026";
		String fileNameOut2 = "D:\\Users\\jingyuanliu\\data\\fenciedFilteredDOc\\result1026";
 		List<String> stopWord = new ArrayList<String>();
 		Map<String,Integer> wordShowTimes = new HashMap<String, Integer>();
		List<String> result = new ArrayList<String>();
 		try{
			int countNum = 0;
			File f1 = new File(fileNameOut1,"GrapubFiterResult10261.txt");
			File f2 = new File(fileNameOut2,"GrapubDeletNo10261.txt");
			PrintWriter out1 = new PrintWriter(f1);
			PrintWriter out2 = new PrintWriter(f2);
					
			BufferedReader in1 = new BufferedReader (new FileReader(fileNameInDic));
			String lineStopWords ;
			int stopCount = 0;
			while((lineStopWords = in1.readLine())!=null){
				System.out.println(">> stopCount is " + stopCount);
				stopWord.add(lineStopWords);
				stopCount ++ ;
			}
			in1.close();
			
			BufferedReader in2 = new BufferedReader(new FileReader(fileNameInFenci));
			String lineFenciWords ;
			while ((lineFenciWords = in2.readLine())!=null){
				String [] wordList = lineFenciWords.split(" ");
				for(String tempWord : wordList){
					if((!stopWord.contains(tempWord))&&NLPUtils.isChinese(tempWord)){
//						直接滤去了英文，因为现阶段英文未做“分词”，所以未做翻译，所以会影响分词和过滤的结果
						if(result.contains(tempWord)){
							int countTemp = wordShowTimes.get(tempWord);
							countTemp ++;
							wordShowTimes.put(tempWord,countTemp);
						}else{
							result.add(tempWord);
							System.out.println(tempWord);
							wordShowTimes.put(tempWord, 0);
						}
					}
				}
			}	
			in2.close();	
			System.out.println(result.size());
			
			BufferedReader in3 = new BufferedReader(new FileReader(fileNameInFenci));
			String readline;	
			while((readline = in3.readLine())!=null){
				String [] tempWordList = readline.split(" ");
				List<String> filterResult = new ArrayList<String>();
				for (String everyWord : tempWordList){
					if((result.contains(everyWord))&&(wordShowTimes.get(everyWord)>1)){
//						直接滤去了英文，因为现阶段英文未做“分词”，所以未做翻译，所以会影响分词和过滤的结果
						filterResult.add(everyWord);
					}
				}
				System.out.println("the current num is " + countNum);
				if(filterResult.size()>5){
					StringBuilder sb = new StringBuilder();
					for(String finalWord : filterResult){
						sb.append(finalWord + " ");
					}
					out1.println(sb.toString().trim());
				}else{
//					System.out.println("deletNum2 is " + countNum);
					out2.println(countNum);
				}
				countNum ++ ;
			}	
			System.out.println("the finalNum is " + countNum);
			in3.close();
			out1.close();
			out2.close();
		}catch(IOException e ){
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	public static void main(String [] args){
		useSet();
	}
}







