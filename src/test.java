import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class test {
	public static void main(String[] args) {
		InputStream is = test.class.getClassLoader().getResourceAsStream("IKAnalyzer/ext.dic");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while((line = reader.readLine())!= null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
