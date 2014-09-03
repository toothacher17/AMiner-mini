package courseExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataModifier {
	
	public static void dataModifier(String filepath) {
		
		DataTransfer dataTransfer = DataTransfer.getInstance();
		
		File file = new File(filepath);
		BufferedReader reader = null;  
		try {
			reader = new BufferedReader(new FileReader(file));
			String content = null;
			
			while ((content = reader.readLine()) != null) {
				String content2 = content.replace("||", "|");
				dataTransfer.dataWriter(content2 + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		dataModifier("/Users/guanchengran/work/myEclipse/SchoolSearch2/coursedata.txt");
	}
}
