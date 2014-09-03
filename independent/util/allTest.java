package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import SchoolSearch.services.utils.dataUpdateTools.utils.NLPUtils;

public class allTest {
	public static void writeGrapubNoMap(){
		String fileNameOut = "D:\\Users\\jingyuanliu\\data\\unfenciedDoc";
		try{
			File f = new File(fileNameOut,"grapubNoMap.txt");
			PrintWriter out = new PrintWriter(f);
			int count =0;
			while(count < 4878 ){
				out.println(count+"\t" +(count+1));
				count++;
			}
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void buildStopWordGrapubDic(){
		String fileNameIn = "D:\\Users\\jingyuanliu\\dic\\prepare\\grapubdic1.txt";
		String fileNameOut = "D:\\Users\\jingyuanliu\\dic\\prepare";
		try{
			BufferedReader in = new BufferedReader(new  FileReader(fileNameIn));
			String readline;
			String writeline;
			File f = new File(fileNameOut,"grapubdicFinal.txt");
			PrintWriter out = new PrintWriter(f);
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split(" ");
				writeline = wordList[0];
				out.println(writeline);
			}
			out.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static List<List<Float>> loadBigData (String fileNameIn){
		List<List<Float>> result = new ArrayList<List<Float>>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameIn));
			String readline ;
			while((readline = in.readLine())!=null){
				List<Float> temp = new ArrayList<Float>();
				String [] wordList = readline.split(" ");
				for(String everyword: wordList){
					temp.add(Float.parseFloat(everyword));
				}
				result.add(temp);
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void buildTitleAbstractDoc(){
		String fileIn = "D:\\Users\\jingyuanliu\\data\\unfenciedDoc\\grapub.txt";
		String fileOut= "D:\\Users\\jingyuanliu\\data\\unfenciedDoc";
		String fileOut1= "D:\\Users\\jingyuanliu\\data\\unfenciedDoc";
		try{
			BufferedReader in= new BufferedReader(new FileReader(fileIn));
			String readline;
			File f = new File(fileOut,"grapubAbTitle3.txt");
			PrintWriter out =new PrintWriter(f);
			File f1 = new File(fileOut1,"deletedGrapub.txt");
			PrintWriter out2 = new PrintWriter(f1);
			int count = 0;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split("\t");
				if(wordList.length==2){
					out.println(wordList[0]+" "+ wordList[1]);
				}else if(wordList.length == 3){
					out.println(wordList[0]+" "+wordList[1]+wordList[2]);
				}else {
					out2.println((Integer.parseInt(wordList[0])-1));
				}
				count++;
			}
			in.close();
			out.close();
			out2.close();
			System.out.println(count);
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	public static void testSetList(){
		String testInput = "D:\\Users\\jingyuanliu\\data\\fenciedUnfilteredDoc\\result1025\\GrapubFenciResult10251.txt";
		List<String> testList = new ArrayList<String>();
		Set<String> testSet = new HashSet<String>();
		try{
			BufferedReader in1  = new BufferedReader(new FileReader(testInput));
			BufferedReader in2  = new BufferedReader(new FileReader(testInput));
			String readline1 ;
			long listTimeStart = System.currentTimeMillis();
			while((readline1 = in1.readLine())!=null){
				String [] wordList = readline1.split(" ");
				for(String everyWord : wordList){
					if((!testList.contains(everyWord))&&NLPUtils.isChinese(everyWord)){
						testList.add(everyWord);
					}
				}
			}
			long listTimeEnd = System.currentTimeMillis();
			in1.close();
			
			String readline2;
			long SetTimeStart = System.currentTimeMillis();
			while((readline2=in2.readLine())!=null){
				String [] wordSet = readline2.split(" ");
				for (String setWord : wordSet){
					if((!testSet.contains(setWord))&&NLPUtils.isChinese(setWord)){
						testSet.add(setWord);
					}
				} 
			}
			long SetTimeEnd = System.currentTimeMillis();
			in2.close();
			long ListTime = listTimeEnd - listTimeStart; 
			System.out.println("the list cost time is "+ListTime);
			long SetTime = SetTimeEnd - SetTimeStart; 
			System.out.println("the set cost time is " +SetTime);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public static void main(String [] args){
//		String fileNameIn = "D:\\Users\\jingyuanliu\\workspaceformatlab\\lm\\finalresult1.txt"; 
//		List<List<Float>> test = allTest.loadBigData(fileNameIn);
//		System.out.println(" matrix line number is " + test.size());
//		System.out.println(" matrix colum number is" + test.get(19100).size());
//		System.out.println(" matrix answer check 1 is " + test.get(0).get(0));
//		System.out.println(" matrix answer chech 2 is " + test.get(15000).get(4300));
//		allTest.buildStopWordGrapubDic();
//		allTest.writeGrapubNoMap();
		//		String fileName = "D:\\Users\\jingyuanliu\\data\\test\\resultmatirx1.txt";
//		List<List<Double>> result = new ArrayList<List<Double>>();
//		try{
//			BufferedReader in = new BufferedReader (new FileReader( fileName));
//			String readline ;
//			String writeline;
//			while((readline = in.readLine())!=null){
//				List<Double> temp = new ArrayList<Double> ();
//				String [] wordlist = readline.split(" ");
//				for (String tempWord : wordlist){
//					temp.add(Double.parseDouble(tempWord));
////					need to do something to change the int to double
//				}
//				result.add(temp);
//			}
//			in.close();
//		}catch(IOException e ){
//			e.printStackTrace();
//		}
//		List<Double> tempList = new ArrayList<Double>(result.get(1));
//		Map<Double,Integer> tempMap = new HashMap<Double, Integer>();
//		int count = 0 ;
//		for (Double tempCount : tempList){
//			tempMap.put(tempCount, count);
//			count++;
//		}
//		Collections.sort(tempList);
//		System.out.println(tempList.get(tempList.size()-1));
//		System.out.println(tempMap.get(tempList.get(tempList.size()-1))+1);
	
		allTest.buildTitleAbstractDoc();
	
//		allTest.testSetList();
	
	}
}







