package util;

import java.io.*;
import java.util.*;

public class BuildFilteredGrapubDocMap {
	
	public static void main(String [] args){
		String fileNameIn1 = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\grapub\\prepare\\grapubNoMap.txt";
		String fileNameIn2 = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\grapub\\prepare\\GrapubDeletNo10261a.txt";	
		String fileNameOut = "D:\\Users\\jingyuanliu\\data\\finalProbaResult\\result1026\\grapub\\prepare";
		try{
			BufferedReader in1 = new BufferedReader(new FileReader(fileNameIn1));
			BufferedReader in2 = new BufferedReader(new FileReader(fileNameIn2));
			Map<String, String> compareMap = new HashMap<String, String>();
			List<String> deletedList = new ArrayList<String>();
			File f = new File (fileNameOut, "remainedGrapubDocNoTest.txt" );
			PrintWriter out = new PrintWriter(f);
			String readline1;
			String readline2; 
			String writeline;
			while((readline1 = in1.readLine())!=null){
				String [] wordList = readline1.split("\t");
				compareMap.put(wordList[0],wordList[1]);
			}
			in1.close();
			
			List<String> allDocNo = new ArrayList<String>(compareMap.keySet());
			List<Integer> allDocNoOrdered = new ArrayList<Integer>();
			for (String everyString : allDocNo){
				Integer temp = Integer.parseInt(everyString);
				allDocNoOrdered.add(temp);
			}
			Collections.sort(allDocNoOrdered);
			
			while ((readline2 = in2.readLine())!=null){
				deletedList.add(readline2);
			}
			in2.close();
			
			int countNum = 0;
			for(Integer everyWord : allDocNoOrdered){
				if(deletedList.contains(everyWord.toString())){
					System.out.println("the deleted docNo " + everyWord);
					countNum ++;
				}else{
					out.println(compareMap.get(everyWord.toString()));
				}
			}
			System.out.println("all the deleted docs number" + countNum);
			out.close();
		}catch(IOException e ){
			e.printStackTrace();
		}
	}
}







