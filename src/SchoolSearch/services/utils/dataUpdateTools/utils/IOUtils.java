package SchoolSearch.services.utils.dataUpdateTools.utils;
/**常规的IO操作
 * @author CXLYC
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IOUtils {
	
	public static void split(String sourceFile,String targetFile,int columnNum,String reg){
		File sourceF = new File(sourceFile);
		File targetF = createFile(targetFile);
		if(sourceF.exists()){
			BufferedReader reader = null;
			BufferedWriter writer = null;
			try {
				String temp = null;
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceF)));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetF,true)));
				ProcessBar pc = new ProcessBar(100000,false);
				int count=0;
				System.out.println("正在抽取文件"+sourceFile+"的前"+columnNum+"列");
				while((temp = reader.readLine()) != null){
					count++;
					pc.tictoc();
					if(null==reg){
						reg = "\\s+";
					}
					String []tokens = temp.split(reg);
					StringBuilder sb = new StringBuilder();
					if(tokens.length>=columnNum){
						for(int i=0;i<columnNum;i++){
							sb.append(tokens[i]);
							sb.append(" ");
						}
						writer.append(sb.toString().trim());
						writer.newLine();
					}else{
						System.out.println("column size is outofbound:index "+(columnNum-1)+"||size:"+tokens.length);
					}
				}
				pc.stop();
				System.out.println("共写入记录:"+count);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(null != reader){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(null != writer){
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			System.out.println("source file "+sourceFile+" is not found!");
		}
	}
	public static void saveData(Set<String> data,String filePath,Integer lineNum){
		File f = createFile(filePath);
		BufferedWriter writer = null;
		try {
			int count=0;
			System.out.println("正在写入文件"+filePath.toString());
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,true)));
			for(String d:data){
				writer.append(d);
				writer.newLine();
				count++;
				if(null!=lineNum){
					if(count>=lineNum){
						break;
					}
				}
			}
			System.out.println("写入完成，共写入"+count+"条记录至"+filePath.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != writer){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static Set<String> loadDataSet(String filePath){
		Set<String> data = new HashSet<String>();
		File f = new File(filePath);
		if(f.exists()){
			BufferedReader reader = null;
			try {
				String temp = null;
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				System.out.println("正在读取文件"+f.toString());
				ProcessBar pc = new ProcessBar(5000000,false);
				while((temp = reader.readLine()) != null){
					pc.tictoc();
					String[] tokens = temp.split("\\s+");
					for(String id:tokens){
						if(!data.contains(id.trim())){
							data.add(id.trim());
						}
					}
				}
				pc.stop();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(null != reader){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			System.out.println("file "+f.toString() +" is not exist!skip it!");
		}
		return data;
	}
	
	public static File createFile(String filename){
		File f = new File(filename);
		if(!f.exists()){
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			try {
				f.createNewFile();
				System.out.println("create new file:"+f.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println(f.toString()+" has exist!");
		}
		return f;
	}
	
	public static Integer getLinesNum(String filePathStr){
		File filePath = new File(filePathStr);
		return getLinesNum(filePath);
	}
	
	public static Integer getLinesNum(File filePath){
		Integer linesNum = 0;
		if(!filePath.exists()){
			try {
				filePath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			while( reader.readLine() != null){
				linesNum ++;
			}
			return linesNum;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != reader){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public static List<String> readString(String filePathStr){
		File filePath = new File(filePathStr);
		return readString(filePath);
	}
	
	public static List<String> readString(File filePath){
		List<String> content = new ArrayList<String>();
		if(!filePath.exists()){
			try {
				filePath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader reader = null;
		try {
			String temp = null;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			while((temp = reader.readLine()) != null){
				content.add(temp);
			}
			return content;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != reader){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static void appendToFile(String filePathStr, Object data){
		File filePath = new File(filePathStr);
		String str = String.valueOf(data);
		appendToFile(filePath, str);
	}
	
	public static void appendToFileWithoutLine(String filePathStr, String data){
		File filePath = new File(filePathStr);
		appendToFileWithoutLine(filePath, data);
	}
	
	public static synchronized void appendToFile(File filePath, String data){
		if(!filePath.exists()){
			try {
				filePath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,true)));
			writer.append(data);
			writer.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != writer){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static synchronized void appendToFileWithoutLine(File filePath, String data){
		if(!filePath.exists()){
			try {
				filePath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,true)));
			writer.append(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != writer){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writeToFile(String filePathStr, String data){
		File filePath = new File(filePathStr);
		writeToFile(filePath, data);
	}
	
	public static void writeToFile(File filePath, String data){
		if(!filePath.exists()){
			try {
				filePath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
			writer.write(data);
			writer.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != writer){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void saveObject(File filePath, Object obj){
		if(filePath.isDirectory()){
			System.out.println("[ERROR]" + filePath + "IS A DIR!PLEASE CHECK AGAIN!");
			return;
		}else{
			if(!filePath.exists()){
				if(filePath.getParentFile().exists() || filePath.getParentFile().mkdirs()){
					try {
						filePath.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					System.out.println("[ERROR]MAKE PARENTFILE " + filePath.getParent() + " FAIL!");
				}
			}
			ObjectOutputStream write = null;
			try {
				write = new ObjectOutputStream(new FileOutputStream(filePath));
				write.writeObject(obj);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(null != write){
					try {
						write.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}
	
	public static <T> T loadObject(File filePath, Class<T> type){
		if(filePath.isDirectory()){
			System.out.println("[ERROR]" + filePath + "IS A DIR!PLEASE CHECK AGAIN!");
			return null;
		}else{
			if(!filePath.exists()){
				return null;
			}else{
				ObjectInputStream read = null;
				try {
					read = new ObjectInputStream(new FileInputStream(filePath));
					T object = (T)read.readObject();
					return object;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}finally{
					if(null != read){
						try {
							read.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}
	public static void main(String[] args) {
	}
}
