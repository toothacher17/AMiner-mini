package util;

import java.io.*;
import java.util.*;

import SchoolSearch.services.utils.dataUpdateTools.utils.NLPUtils;

public class getPreDocForFenci {
	
	public static void main(String [] args){
		String fileNameInFenci = "D:\\Users\\jingyuanliu\\data\\unfenciedDoc\\grapub.txt";
		String fileNameOut1 = "D:\\Users\\jingyuanliu\\data\\unfenciedDoc";
		String fileNameOut2 = "D:\\Users\\jingyuanliu\\data\\unfenciedDOc";
		String fileNameOut3 = "D:\\Users\\jingyuanliu\\data\\unfenciedDOc";
		try{
			int count = 0;
			File f1 = new File(fileNameOut1,"grapubNo.txt");
			File f2 = new File(fileNameOut2,"grapubTitle.txt");
			File f3 = new File(fileNameOut3,"grapubAbstract.txt");
			PrintWriter out1 = new PrintWriter(f1);
			PrintWriter out2 = new PrintWriter(f2);
			PrintWriter out3 = new PrintWriter(f3);		
			BufferedReader in = new BufferedReader (new FileReader(fileNameInFenci));
			String line ; 
			while((line = in.readLine())!=null){
				
				String [] StringList = line.split("\t");
				System.out.println("the length  "+StringList.length );
				if (StringList.length>2){
					out1.println(StringList[0]);
					out2.println(StringList[1]);
					System.out.println(">"+StringList[1]);
					out3.println(StringList[2]);
				}else{
					System.out.println(count);
				}
				count ++;	
			}
		}catch(IOException e ){
			e.printStackTrace();
		}
	}
}







