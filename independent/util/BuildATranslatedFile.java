package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildATranslatedFile {
	
	public static Map<String, String> readInFile (String fileName,String problem){
		Map<String, String> result = new HashMap<String, String>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line ;
			while((line = in.readLine())!= null){
				String [] temp = line.split("\t\t");
				result.put(temp[0], temp[1]);
			}
			in.close();
		}catch (IOException e) {
			System.out.println(problem);
		}
		return result;
		
	} 
	
	public static void main(String [] args){
		String fileNameIn1 = "D:\\Users\\jingyuanliu\\translationMap\\bigMapFinal1.txt";
		String problem1 = "there is a problem while reading the bigmapFinal1.txt";
		String fileNameIn2 = "D:\\Users\\jingyuanliu\\translationMap\\grapubTranslateMap1.txt";
		String problem2 = "there is a problem while reading the grapubtranslateMap1.txt";
		Map <String, String> translationMapSmall = readInFile(fileNameIn2, problem2);
		Map <String, String> translationMapBig = readInFile(fileNameIn1, problem1);
		
		String fileNameOfDoc = "D:\\Users\\jingyuanliu\\data\\courseInput.txt";// the file should be after the fenci procedure!!
		String fileNameOut = "D:\\Users\\jingyuanliu";
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileNameOfDoc));
			String readline ;
			String wirteline;
			StringBuilder sb = new StringBuilder();
			File f = new File(fileNameOut,"bigMapFinal1.txt");
			PrintWriter out = new PrintWriter(f);
			List<String> englishInput1 = new ArrayList<String>(translationMapSmall.keySet());
			List<String> englishInput2 = new ArrayList<String>(translationMapBig.keySet());
			while ((readline=in.readLine())!=null){
				String [] wordList = readline.split(" ");
				boolean flag = false;
				for(String wordResult : wordList){
					if (wordResult == "yingwen"){// need to judge if the word is an english word rather than a chinese word
						for (String wordInput1 : englishInput1){
							if (wordResult.equalsIgnoreCase(wordInput1)){
								wordResult = translationMapSmall.get(wordInput1);
								flag = true;
							}
						}
						if(flag == false){
							for (String wordInput2 : englishInput2){
								if (wordResult.equalsIgnoreCase(wordInput2)){
									wordResult = translationMapBig.get(wordInput2);
								}
							}
						}
					}	
					sb.append(wordResult+" ");
				}
				wirteline = sb.toString(); 
				out.println(wirteline);
			}
			in.close();
			out.close();
		}catch(IOException e){
			System.out.println("there is an io exception while writing the file");
		}
	}

}







