package SchoolSearch.services.utils.dataUpdateTools.utils;
/**
 *
 * @author CX
 */
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CSVUtil {
	
	private static CSVUtil instance;
	
	public static CSVUtil getInstance(){
		if(null == instance)
			instance = new CSVUtil();
		return instance;
	}
	
	public static CSVUtil getIndependentInstance(){
		return new CSVUtil();
	}
	
    //读取csv文件
    public List<String[]> readCsv(String filePath) throws Exception {
        List<String[]> csvList = new ArrayList<String[]>();
        if (isCsv(filePath)) {
            CsvReader reader = new CsvReader(filePath, ',', Charset.forName("GBK"));
//            reader.readHeaders(); // 跳过表头   如果需要表头的话，不要写这句。
            while (reader.readRecord()) { //逐行读入除表头的数据
                csvList.add(reader.getValues());
            }
            reader.close();
        } else {
            System.out.println(filePath + " is not CSV File!");
        }
        return csvList;
    }
   
    public static void WriteCsv(String csvFilePath,String[] contents){  
        try {  
                CsvWriter wr =new CsvWriter(csvFilePath,',',Charset.forName("GBK"));//日文编码
                wr.writeRecord(contents);  
                wr.close();  
         } catch (IOException e) {  
            e.printStackTrace();  
         }  
    }  
    
    //判断是否是csv文件
    private boolean isCsv(String fileName) {
        return fileName.matches("^.+\\.(?i)(csv)$");
    }
    
    public static void main(String[] args) throws Exception {
        String filepath = "C:/Data/csvdata/bspjournal.csv";
        CSVUtil su = new CSVUtil();
        List<String[]> list = su.readCsv(filepath);
        for (int r = 0; r < list.size(); r++) {
            for (int c = 0; c < list.get(r).length; c++) {
                String cell = list.get(r)[c];
                System.out.print(cell + "\t");
            }
            System.out.print("\n");
        }
    }
}