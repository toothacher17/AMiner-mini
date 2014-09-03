package courseExtraction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataTransfer {
	private static DataTransfer instance = null;
	private DataTransfer(){}
	public static synchronized DataTransfer getInstance(){
		if(instance == null) instance = new DataTransfer();
		return instance;
	}
	
	public synchronized void dataWriter(String content) {
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(
					"/Users/guanchengran/work/myEclipse/SchoolSearch2/courseinfodata.txt", true);
			writer = new BufferedWriter(fw);
			writer.append(content);
			
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				fw.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
