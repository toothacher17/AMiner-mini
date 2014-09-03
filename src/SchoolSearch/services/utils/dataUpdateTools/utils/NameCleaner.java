package SchoolSearch.services.utils.dataUpdateTools.utils;
/**将输入字符串中(ming,xin)形式的姓名转化为(xin ming)的形式
 * @author CX
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameCleaner {
	public static List<String> convert(List<String> dirtyNameList){
		List<String> cleanNameList = new ArrayList<String>();
		int length =dirtyNameList.size();
		for(int i=0; i<length; i++){
			String name = dirtyNameList.get(i);
			if(null != name){
				StringBuilder sb = new StringBuilder();
				String[] names = name.trim().split("\\|\\|");
				for(int j=0; j<names.length; j++){
					if(names[j].contains(",")){
						String[] tmp = names[j].trim().split(",");
						sb.append(tmp[1]);
						sb.append(" ");
						sb.append(tmp[0]);
					}else{
						sb.append(names[j]);
					}
					sb.append("||");
				}
				sb.delete(sb.length()-2, sb.length());
				System.out.println(name + "----->" + sb.toString());
				cleanNameList.add(sb.toString());
			}
		}
		return cleanNameList;
	}
	public static void main(String[] args) {
		List<String> cn = NameCleaner.convert(Arrays.asList("john.smith"));
		System.out.println(cn);
	}
}
