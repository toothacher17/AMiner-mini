package util;


import java.util.*;
import java.io.*;

public class lm {
	
	public static Map<String,Integer> loadWordMap (String fileNameIn){
		Map<String,Integer> result = new HashMap<String,Integer>();
		try{
			BufferedReader in = new BufferedReader(new FileReader (fileNameIn));
			String readline ;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split(" ");
				result.put(wordList[0], Integer.parseInt(wordList[1]));
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<List<String>> fenciFilteredDoc (String fileNameIn){
		List<List<String>> result = new ArrayList<List<String>>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameIn));
			String readline;
			while((readline = in.readLine())!=null){
				List<String> temp = new ArrayList<String>();
				String [] wordList = readline.split(" ");
				for(String everyword : wordList){
					temp.add(everyword);
				}
				result.add(temp);
			}
			in.close();
		}catch(IOException e){
			 e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String []args){
		String fileInWordMap = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\lm\\wordmap.txt";
		String fileInCourseDoc = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\lm\\CourseFilteredResult10261.txt";
		String fileOut = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\course\\lm";
		int inputDataWordNum = 10256;
		int inputDataDocNum = 5451;
		
		
		Map<String,Integer> wordMap =lm.loadWordMap(fileInWordMap);
		List<List<String>> fenciFilteredDoc =lm.fenciFilteredDoc(fileInCourseDoc); 
		
		int [] pWordColl = new int [inputDataWordNum];
		int [][] pWordDoc = new int [inputDataDocNum][inputDataWordNum];
		
		int [] everydocWordFrenquency = new int [inputDataDocNum];
		int allWordFrenquency = 0;
		int DocNum = 0 ;
		
		for(List<String> tempList : fenciFilteredDoc){
			for(String tempWord : tempList){
				Integer wordNum = wordMap.get(tempWord);
				pWordColl[wordNum] ++;
				pWordDoc [DocNum][wordNum] ++;
				allWordFrenquency ++;
				everydocWordFrenquency[DocNum] ++;
			}
			DocNum ++;
		}
		
		Float [][] lmResult= new Float[inputDataDocNum][inputDataWordNum];
		for(int i = 0 ; i < inputDataDocNum ; i ++){
			for(int j = 0 ; j < inputDataWordNum ; j ++){
				Float part = ((float)everydocWordFrenquency[i])/((float)(everydocWordFrenquency[i]+1000));
				lmResult[i][j] =  (((float) (part*pWordDoc[i][j])/(float)(everydocWordFrenquency[i]))+( (1-part)*((float)pWordColl[j])/((float)allWordFrenquency)))*100000;
			}
		}
		
		System.out.println("the all word number is " + allWordFrenquency);
		System.out.println("the all doc number is" + DocNum);
		System.out.println("the doc number in doc is" + everydocWordFrenquency[0]);
		System.out.println("the number of 会计  in doc 1 " + pWordDoc[0][0]);
		System.out.println("the number of 会计  in collection" + pWordColl[0]);
		System.out.println("the result is " + lmResult[0][0]);
	
		System.out.println("---------------------------------------");
		
		System.out.println("the doc number in doc is" + everydocWordFrenquency[0]);
		System.out.println("the number of 会计  in doc 1 " + pWordDoc[0][1]);
		System.out.println("the number of 会计  in collection" + pWordColl[1]);
		System.out.println("the result is " + lmResult[0][1]);
		
		System.out.println("---------------------------------------");
		
		System.out.println("the doc number in doc is" + everydocWordFrenquency[1]);
		System.out.println("the number of 会计  in doc 1 " + pWordDoc[1][0]);
		System.out.println("the number of 会计  in collection" + pWordColl[0]);
		System.out.println("the result is " + lmResult[1][0]);
		
		
		
		
		File f = new File(fileOut,"lmCourseResultTest10261.txt");
		try{
			PrintWriter out = new PrintWriter(f);
			for(int lineNumber = 0 ; lineNumber < inputDataDocNum ; lineNumber ++){
				StringBuilder sb = new StringBuilder();
				for (int wordNum = 0 ; wordNum < inputDataWordNum ; wordNum ++ ){
					sb.append(lmResult[lineNumber][wordNum]+" ");
				}
				out.println(sb.toString());
			}
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	} 
}
