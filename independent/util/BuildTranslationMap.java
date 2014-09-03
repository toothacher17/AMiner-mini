package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class BuildTranslationMap {
	public static void main(String [] args){
		String fileNameIn1 = "D:\\Users\\jingyuanliu\\dic\\chineseWordsPrior.txt";
		String fileNameIn2 = "D:\\Users\\jingyuanliu\\dic\\chineseWordsAfter.txt";
		String fileNameOut = "D:\\Users\\jingyuanliu\\dic";
		List<String> temp = new ArrayList<String>();
		try {
			BufferedReader in1 = new BufferedReader(new FileReader(fileNameIn1));
			String line1;
			while((line1=in1.readLine())!= null ){
				temp.add(line1);
			}
			BufferedReader in2 = new BufferedReader(new FileReader(fileNameIn2));
			String line2;
			while((line2=in2.readLine())!= null ){
				temp.add(line2);
			}
			File f = new File (fileNameOut,"finalChineseDic.txt");
			PrintWriter out = new PrintWriter(f); 
			for(String allWord : temp){
				out.println(allWord);
			}
			in1.close();
			in2.close();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
//		String fileNameOut = "D:\\Users\\jingyuanliu";
//		try{
//			File f = new File(fileNameOut,"chineseWordsPrior1.txt");
//			PrintWriter out = new PrintWriter(f);
////			List<String> englishInput = new ArrayList<String>(resultMap.keySet());
//			for (String tempWord : temp){
//				out.println(tempWord);
//			}
//			out.close();
//		}catch(IOException e){
//			System.out.println("there is an io exception while write the file");
//		}
	}

}







