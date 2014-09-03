package SchoolSearch.services.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class LogWriter {
	static PrintWriter writer = null;
	static {
		File file = new File("D:\\share\\yxy\\data\\schoolDemo\\log.txt");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void println(String message) {
		writer.println(message);
	}
	
	public static void close() {
		writer.close();
	}
}
