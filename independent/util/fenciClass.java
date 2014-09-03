package util;

import java.io.*;
import java.nio.Buffer;
import java.util.*;

import SchoolSearch.services.utils.dataUpdateTools.utils.NLPUtils;


public class fenciClass {
	
//	to deal with the baidudic from website
	public static void resultOfBaiduPre (String fileNameIn, String fileNameOut ){
		List<String> giveupWords = new ArrayList<String>();
		List<String> usefulWords = new ArrayList<String>();
		try{
			BufferedReader in = new BufferedReader (new FileReader(fileNameIn));
			File f1 = new File(fileNameOut,"GiveupWords.txt");
			File f2 = new File(fileNameOut,"UsefulWords.txt");
			File f3 = new File(fileNameOut,"BaiduUsefulWords.txt");
			PrintWriter giveup = new PrintWriter(f1);
			PrintWriter useful = new PrintWriter(f2);
			PrintWriter baiduFinal = new PrintWriter(f3);
			String readline ;
			int count = 0;
			while((readline = in.readLine())!=null){
				String [] wordList = readline.split(" ");
				if(wordList.length>1){
					if(wordList[1].length()>1&&wordList[1].length()<7&&NLPUtils.isChinese(wordList[1])){
						useful.println(count+wordList[1]);
						baiduFinal.println(wordList[1]);
					}else{
						giveup.println(wordList[1]);
					} 
				}
			}
			in.close();
			giveup.close();
			useful.close();
			baiduFinal.close();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	public static void testHashMapList(){
		
		Map<String,Integer> testMap = new HashMap<String, Integer>(0);
		List<Integer> testList = new ArrayList<Integer>(0);
		System.out.println(">>>testMap>>>" + testMap.size());
		System.out.println(">>>testList>>>" + testList.size());
		
	}
	
	public static void betterFenciResult(String fileNameIn, String fileNameOut){
		List<String> allWord = new ArrayList<String>();
//		List<String> usedWord = new ArrayList<String>();
//		List<String> deletWord = new ArrayList<String>();
		Map<String,Integer> word2showTimes = new HashMap<String, Integer>(0);
		try{
			BufferedReader in = new BufferedReader (new FileReader(fileNameIn));
			File f1 = new File (fileNameOut,"remainedWords.txt");
			File f2 = new File (fileNameOut,"deletedWords.txt" );
			PrintWriter out1 = new PrintWriter(f1);
			PrintWriter out2 = new PrintWriter(f2);
			String readline;
			int count = 0 ;
			while((readline = in.readLine())!= null){
				String [] wordList = readline.split(" ");
				for(String everyword : wordList){
					if (allWord.contains(everyword)){
						Integer temp = word2showTimes.get(everyword);
						temp ++;
						word2showTimes.put(everyword, temp);
					}else{
						allWord.add(everyword);
						word2showTimes.put(everyword,0);
					}
				}
			}
			in.close();
			for(String everyallWord : allWord){
				int i = word2showTimes.get(everyallWord);
				if(i>2){
					out1.println(everyallWord);
				}else{
					out2.println(everyallWord);
				}
			}
			out1.close();
			out2.close();
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	public static List<String> buildMajorName (){
		List<String> result = new ArrayList<String>();
		String fileCourseMajor = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\courseRelated\\courseMajorName.txt";
		String fileDepartName = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\departmentRelated\\departmentName.txt";
		String fileGrapub = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\grapubRelated\\grapubSubjectName.txt";
		String institute = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\institute\\institueName.txt";
		String schoolName = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\school\\schoolName.txt";
		String fileout = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer";
		try{
			BufferedReader in1 = new BufferedReader ( new FileReader(fileCourseMajor));
			String readline1 ;
			while((readline1 = in1.readLine())!=null){
				if(!result.contains(readline1)){
					result.add(readline1);
				}
			}
			in1.close();
			
			BufferedReader in2 = new BufferedReader ( new FileReader(fileDepartName));
			String readline2 ;
			while((readline2 = in2.readLine())!=null){
				if(!result.contains(readline2)){
					result.add(readline2);
				}
			}
			in2.close();
			
			BufferedReader in3 = new BufferedReader ( new FileReader(fileGrapub));
			String readline3 ;
			while((readline3 = in3.readLine())!=null){
				if(!result.contains(readline3)){
					result.add(readline3);
				}
			}
			in3.close();
			
			BufferedReader in4 = new BufferedReader ( new FileReader(institute));
			String readline4 ;
			while((readline4 = in4.readLine())!=null){
				if(!result.contains(readline4)){
					result.add(readline4);
				}
			}
			in4.close();
			
			BufferedReader in5 = new BufferedReader ( new FileReader(schoolName));
			String readline5 ;
			while((readline5 = in5.readLine())!=null){
				if(!result.contains(readline5)){
					result.add(readline5);
				}
			}
			in5.close();

			File f = new File(fileout,"majorName.txt");
			PrintWriter out = new PrintWriter(f) ;
			for(String tempWord : result){
				out.println(tempWord);
			}
			out.close();
		}catch( IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void courseNameFilter(String filenNameIn ){
		String fileOut = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer";
		try{
			BufferedReader in = new BufferedReader(new FileReader(filenNameIn));
			String readline;
			File f =  new File(fileOut,"courseName.txt");
			PrintWriter out = new PrintWriter(f);
			while((readline = in.readLine())!=null){
				String replace = readline.replaceAll("\\w?[\\(（].*[\\)）]", "").replaceAll("\\w*$","");
				out.println(replace);
			}
			in.close();
			out.close();
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	public static void personDic(){
//		to rough for processing
		String fileIn1 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\person\\personFiled.txt";
		String fileIn2 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\person\\personSummary.txt";
		
		try{
			BufferedReader in1  = new BufferedReader(new FileReader(fileIn1));
			BufferedReader in2  = new BufferedReader(new FileReader(fileIn2));
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	public static void useProjectKeyword(){
		String fileNameIn = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\project\\projectKeyWordsPre.txt";
		String fileOut = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer";
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameIn));
			File f = new File(fileOut,"projectKeywords.txt");
			PrintWriter out = new PrintWriter(f);
			String readline ;
			int count = 0;
			while((readline=in.readLine())!=null){
				String [] wordList = readline.split("[;；，。：,.:、　\\s]+");
//				regex expression /t , ， ；; ()
				for( String tempWord : wordList){
					String test = tempWord.replaceAll("\\w?[\\(（].*[\\)）]", "");
					if(test.length()>1&&NLPUtils.isChinese(test)){
						System.out.println(test);
						out.println(test);
					}
				}
				count++;
			}
			in.close();
			out.close();
			System.out.println(">>final count is "+count);
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	public static void buildPublicationDic(){
		String fileIn = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\publication\\publicationKeywordsutf.txt";
		String fileOut = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer";
		List<String> result =  new ArrayList<String>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			File f = new File(fileOut,"publicationKeywords.txt");
			PrintWriter out = new PrintWriter(f);
			String readline ;
			int count = 0;
			while((readline=in.readLine())!=null){
				String [] wordList = readline.split("\\|\\|");
				for( String tempWord : wordList){
					if(!(result.contains(tempWord))&&tempWord.length()>1&&NLPUtils.isChinese(tempWord)){
						System.out.println(tempWord);
						result.add(tempWord);
					}else{
						System.out.println("*"+tempWord);
					}
				}
				count++;
			}
			in.close();
			for(String everyWord : result){
				out.println(everyWord);
			}
			out.close();
			System.out.println(">>final count is "+count);
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	public static void getallDic (){
		String fileIn1 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\BaiduUsefulWords.txt";
		String fileIn2 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\courseName.txt";
		String fileIn3 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\grapubKeywords.txt";
		String fileIn4 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\majorName.txt";
		String fileIn5 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\personName.txt";
		String fileIn6 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\personTitle.txt";
		String fileIn7 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\projectKeywords.txt";
		String fileIn8 = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\publicationKeywords.txt";
		
		List<String> result = new ArrayList<String>();
		try{
			BufferedReader in1 = new BufferedReader(new FileReader(fileIn1));
			String readline1 ;
			while((readline1 = in1.readLine())!=null){
				if(!result.contains(readline1)){
					result.add(readline1);
				}
			}
			in1.close();
			
			BufferedReader in2 = new BufferedReader(new FileReader(fileIn2));
			String readline2 ;
			while((readline2 = in2.readLine())!=null){
				if(!result.contains(readline2)){
					result.add(readline2);
				}
			}
			in2.close();
			
			BufferedReader in3 = new BufferedReader(new FileReader(fileIn3));
			String readline3 ;
			while((readline3 = in3.readLine())!=null){
				if(!result.contains(readline3)){
					result.add(readline3);
				}
			}
			in3.close();
			
			BufferedReader in4 = new BufferedReader(new FileReader(fileIn4));
			String readline4 ;
			while((readline4 = in4.readLine())!=null){
				if(!result.contains(readline4)){
					result.add(readline4);
				}
			}
			in4.close();
			
			BufferedReader in5 = new BufferedReader(new FileReader(fileIn5));
			String readline5 ;
			while((readline5 = in5.readLine())!=null){
				if(!result.contains(readline5)){
					result.add(readline5);
				}
			}
			in5.close();
			
			BufferedReader in6 = new BufferedReader(new FileReader(fileIn6));
			String readline6 ;
			while((readline6 = in6.readLine())!=null){
				if(!result.contains(readline6)){
					result.add(readline6);
				}
			}
			in6.close();
		
			BufferedReader in7 = new BufferedReader(new FileReader(fileIn7));
			String readline7 ;
			while((readline7 = in7.readLine())!=null){
				if(!result.contains(readline7)){
					result.add(readline7);
				}
			}
			in7.close();
		
			BufferedReader in8 = new BufferedReader(new FileReader(fileIn8));
			String readline8 ;
			while((readline8 = in8.readLine())!=null){
				if(!result.contains(readline8)){
					result.add(readline8);
				}
			}
			in8.close();
		
			String fileOut = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer";
			File f = new File(fileOut,"finalDic.txt");
			PrintWriter out = new PrintWriter(f);
			for(String everyWord : result){
				if(NLPUtils.isChinese(everyWord)){
					out.println(everyWord);
					System.out.println(everyWord);	
				}
			}
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String [] args){
//		String fileNameOut2Dic = "D:\\Users\\jingyuanliu\\dic\\new";
		
//		String fileNameBaiduIn = "D:\\Users\\jingyuanliu\\dic\\new\\baidupreutf8.txt";
//		fenciClass.resultOfBaiduPre(fileNameBaiduIn, fileNameOut2Dic);
		
//		fenciClass.testHashMapList();
		
//		String fileInFenciResult = "D:\\Users\\jingyuanliu\\data\\fenciedFilteredDoc\\result1021\\GrapubFiterResult10213.txt";
//		fenciClass.betterFenciResult(fileInFenciResult, fileNameOut2Dic);
		
//		List<String> majorName = fenciClass.buildMajorName();
//		System.out.println("the majorname size is " + majorName.size());
//		fenciClass
		
//		String test = "等鞭金藻(1)                      ,异养转化                      ,脂肪和多不饱和脂肪酸";
//		System.out.println(test.replaceAll("\\w?[\\(（].*[\\)）]", "").replaceAll("\\w*$",""));
//		
		
//		String [] wordList = test.split("[;；，。：,.:、　\\s]+");
//		for(String tempWord : wordList){
//			String test2 = tempWord.replaceAll("\\w?[\\(（].*[\\)）]", "");
//			System.out.println(test2);
//		}
		fenciClass.getallDic();
		
//		String fileNameCourse = "D:\\Users\\jingyuanliu\\dic\\fenciDicPrepaer\\courseRelated\\courseName.txt";
//		fenciClass.courseNameFilter(fileNameCourse);
		
	
	
	
	}
	
	
}
