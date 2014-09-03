package SchoolSearch.services.utils.dataUpdateTools.utils;
public class NLPUtils {
	public static boolean isSubSequence(String ref,String sub){
		boolean result = true;
		char[] refChar = ref.toCharArray();
		char[] subChar = sub.toCharArray();
		int refIndex = 0;
		int subIndex = 0;
		while(true){
			for(int j=refIndex;j<ref.length();j++){
				refIndex=j+1;
				if(refChar[j]==subChar[subIndex]){
					subIndex++;
					break;
				}
			}
			if(refIndex>=ref.length()&&subIndex<sub.length()){
				result = false;
				break;
			}else if(subIndex>=sub.length()){
				break;
			}
		}
		return result;
		
	}
	public static int editDistance(String target,String source){
		if(null!=target && null!=source){
			target=target.trim();
			source=source.trim();
			int length1	= target.length();
			int length2 = source.length();
			int deleteCost = 1;
			int insertCost = 5;
			int replaceCost = 5;
			int [][]distance=new int[length1+1][length2+1];
			//初始化
			for(int i=0;i<=length1;i++){
				distance[i][0]=i*insertCost;
			}
			for(int i=0;i<=length2;i++){
				distance[0][i]=i*deleteCost;
			}
			for(int i=1;i<=length1;i++){
				for(int j=1;j<=length2;j++){
					int tmp=distance[i-1][j-1];
					if(target.charAt(i-1)!=source.charAt(j-1)){
						tmp +=replaceCost;
					}
					int min=tmp<(distance[i-1][j]+insertCost)?tmp:(distance[i-1][j]+insertCost);
					distance[i][j] = min<(distance[i][j-1]+deleteCost)?min:(distance[i][j-1]+deleteCost);
				}
			}
//			for(int i=0;i<=length1;i++){
//				for(int j=0;j<=length2;j++){
//					System.out.print(distance[i][j]+" ");
//				}
//				System.out.println();
//			}
			return distance[length1][length2];
		}else{
			return -1;
		}
		
	}
	
	private static final boolean isChinese(char c) {  
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }  
	
	public static final boolean isChinese(String strName) {  
        char[] ch = strName.toCharArray();  
        for (int i = 0; i < ch.length; i++) {  
            char c = ch[i];  
            if (isChinese(c)) {  
                return true;  
            }  
        }  
        return false;  
    } 
	public static void main(String[] args) {
		System.out.println(isSubSequence("计算机科学与技术系", "计算机系"));
	}
}
