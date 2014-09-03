package SchoolSearch.services.utils.dataUpdateTools.utils;
/**代理服务，维护代理池
 * @author CXLYC
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyService {
	public static class Proxy{
		String ip;
		Integer port;
		//1表示可用但是暂时被封，0表示完全可用
		Integer status;
		
		public static Proxy construct(String ip, Integer port){
			Proxy tmp = new Proxy();
			tmp.ip = ip;
			tmp.port = port;
			tmp.status = -1;
			return tmp;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ip == null)?0:ip.hashCode());
			result = prime * result + port;
			return result;
		}
		
		@Override
		public boolean equals(Object obj)  
	    {  
	        if (this == obj) return true;  
	        if (obj == null) return false;  
	        if (getClass() != obj.getClass()) return false;  
	        final Proxy other = (Proxy) obj;  
	        if (ip == null && other.ip != null)  
	        {  
	        	return false;  
	        }  
	        else if (!ip.equalsIgnoreCase(other.ip)) {
	        	return false;  
	        }
	        
	        if(port != other.port){
	        	return false;
	        }
	        return true;  
	    }  
		
	}
	
	public enum Operation{
		ADD,DELETE,ACTIVE,RESTRAIN,GET
	}
	private long startTime;
	private int roundCounts;
	private int refreshCounts;
	public Integer pointer;
	public List<Proxy> proxyList;
	public File filePath;
	public static ProxyService instance;
	public static ProxyService getInstance(String filePathStr){
		if(null == instance){
			instance = new ProxyService(filePathStr);
		}
		return instance;
	}
	
	public ProxyService(String dirPathStr){
		//构造proxy
		initLogFile(dirPathStr);
		proxyList = new ArrayList<ProxyService.Proxy>();
		pointer = 0;
		roundCounts = 0;
		refreshCounts = 0;
		refreshProxy();
		checkProxies();
		startTime = System.currentTimeMillis();
	}
	
	private void refreshProxy(){
		System.out.println("[RefreshProxyInfo]REFRESHING......");
		proxyList.clear();
		pointer = 0;
		roundCounts = 1;
		refreshCounts ++;
		try {
			fetchMoreProxy1();
		} catch (Exception e) {
			proxyList.clear();
		}
		fetchMoreProxy2();
	}
	
	//http://www.veryhuo.com/res/ip/网站，有简单的字母映射加密
	private void fetchMoreProxy1() throws Exception{
		for(int i=1;i<=10;i++){
			String content = Crawler.getURLContentWithoutProxy("http://www.veryhuo.com/res/ip/page_" + i + ".php");
			Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+[\\s\\S]+?</SCRIPT>");
			Matcher m = p.matcher(content);
			while(m.find()){
				String sub1 = null;
				String sub2 = null;
				String key = m.group(0);
				Pattern p1 = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
				Pattern p2 = Pattern.compile("\":\"[\\s\\S]+?\\)");
				Matcher m1 = p1.matcher(key);
				Matcher m2 = p2.matcher(key);
				if(m1.find() && m2.find()){
					sub1 = m1.group(0);
					sub2 = m2.group(0).substring(3).replace("+", "").replace(")", "").replace("v", "3").replace("m", "4").
							replace("a", "2").replace("l", "9").replace("q", "0").replace("b", "5").replace("i", "7").replace("w", "6").replace("r", "8").replace("c", "1");
					proxyList.add(Proxy.construct(sub1.trim(), Integer.parseInt(sub2.trim())));
				}
			}
		}
	}
	
	
	private void fetchMoreProxy2(){
		//生成所有可用的url，自动更新判断
		List<String> urlList = new ArrayList<String>();
		urlList.addAll(fetchMoreProxy2Util("http://www.youdaili.cn/Daili/guonei"));
		urlList.addAll(fetchMoreProxy2Util("http://www.youdaili.cn/Daili/guowai"));
		urlList.addAll(fetchMoreProxy2Util("http://www.youdaili.cn/Daili/http"));
		for(String url:urlList){
			fetchMoreProxyNormal(url);
		}
	}
	
	private List<String> fetchMoreProxy2Util(String urll){
		List<String> urlList = new ArrayList<String>();
		String content = Crawler.getURLContentWithoutProxy(urll);
		Pattern p1 = Pattern.compile("<ul\\sclass=\"newslist_line\">[\\s\\S]+?</ul>");
		Matcher m1 = p1.matcher(content);
		if(m1.find()){
			String sub = m1.group(0);
			p1 = Pattern.compile("http://www.youdaili.cn/Daili/[\\s\\S]+?html");
			Matcher m2 = p1.matcher(sub);
			//寻找最新的两个
			if(m2.find()){
				urlList.add(m2.group(0));
				String contentSub = Crawler.getURLContentWithoutProxy(m2.group(0));
				int size;
				p1 = Pattern.compile("共\\d*页");
				Matcher m3 = p1.matcher(contentSub);
				if(m3.find()){
					String sizeStr = m3.group(0).substring(m3.group(0).length()-2, m3.group(0).length()-1);
					size = Integer.parseInt(sizeStr);
					if(size >= 2){
						for(int i=2;i<=size;i++){
							String url = m2.group(0).replaceAll("\\.html", "_" + i +".html");
							urlList.add(url);
						}
					}
				}
			}
			if(m2.find()){
				urlList.add(m2.group(0));
				String contentSub = Crawler.getURLContentWithoutProxy(m2.group(0));
				int size;
				p1 = Pattern.compile("共\\d*页");
				Matcher m3 = p1.matcher(contentSub);
				if(m3.find()){
					String sizeStr = m3.group(0).substring(m3.group(0).length()-2, m3.group(0).length()-1);
					size = Integer.parseInt(sizeStr);
					if(size >= 2){
						for(int i=2;i<=size;i++){
							String url = m2.group(0).replaceAll("\\.html", "_" + i +".html");
							urlList.add(url);
						}
					}
				}
			}
		}
		return urlList;
	}
	//默认正则，匹配XX.XX.XX.XX:XX
	private void fetchMoreProxyNormal(String url){
		String regexp = "\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+";
		fetchMoreProxyNormal(url, regexp); 
	}
	
	private void fetchMoreProxyNormal(String url, String regexp){
		String content = Crawler.getURLContentWithoutProxy(url);
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(content);
		while(m.find()){
			String []proxy  = m.group(0).split(":");
			if(2 == proxy.length){
				proxyList.add(Proxy.construct(proxy[0].trim(), Integer.parseInt(proxy[1].trim())));
			}
		}
	}
	
	private void checkProxies(){
		int preCount = proxyList.size();
		List<String> proxyListt = new ArrayList<String>();
		for(Proxy proxy:proxyList){
			String proxyStr = proxy.ip + ":" + proxy.port.toString().trim();
			if(!proxyListt.contains(proxyStr)){
				proxyListt.add(proxyStr);
			}
		}
		
		proxyList.clear();
		for(String proxy:proxyListt){
			String []proxyStr = proxy.split(":");
			if(2 == proxyStr.length){
				proxyList.add(Proxy.construct(proxyStr[0], Integer.parseInt(proxyStr[1])));
			}
		}
		int afterCount = proxyList.size();
		System.out.println("[CheckProxyInfo]TOTALLY REMOVE:" + (preCount-afterCount));
	}
	
	private void initLogFile(String dirPathStr){
		Date d = new Date();
		String date = d.getMonth()+1 + "." + d.getDate();
		
		File dirPath = new File(dirPathStr + File.separatorChar + date);
		if(!dirPath.exists()){
			dirPath.mkdirs();
		}
		
		File []fileList = dirPath.listFiles();
		if(0 == fileList.length){
			filePath = new File(dirPath.toString() + File.separatorChar + "0.txt");
		}else{
			List<Integer> idList = new ArrayList<Integer>();
			for(File f:fileList){
				String idStr = f.toString().replaceAll("[\\s\\S]+\\\\", "").replaceAll("\\.txt", "");
//				System.out.println(idStr);
				int id = Integer.parseInt(idStr);
				idList.add(id);
			}
			Collections.sort(idList);
			filePath = new File(dirPath.toString() + File.separatorChar + String.valueOf(idList.get(idList.size()-1)+1) + ".txt");
		}
		try {
			filePath.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeLog(int totalProxy, int activeProxy, int restrainProxy, int unknownProxy){
		float  costTime = (float)(System.currentTimeMillis()-startTime)/1000;
		String content = String.valueOf(costTime) + " " + totalProxy + " " + activeProxy + " " + restrainProxy + " " + unknownProxy; 
		IOUtils.appendToFile(filePath, content);
	}
	
	public synchronized Object operation(Operation o, String ip, Integer port){
		if(o.equals(Operation.ADD)){
			addProxy(ip, port);
			return null;
		}else if(o.equals(Operation.DELETE)){
			deleteProxy(ip, port);
			return null;
		}else if(o.equals(Operation.ACTIVE)){
			activeProxy(ip, port);
			return null;
		}else if(o.equals(Operation.RESTRAIN)){
			restrainProxy(ip, port);
			return null;
		}else if(o.equals(Operation.GET)){
			return getProxy();
		}else{
			System.out.println("[ErrorInfo]UNKNOWN OPERATION!PLAEA CHECK!");
			return null;
		}
	}
	
	private void deleteProxy(String ip, Integer port){
		Map<String, Integer> proxy2Index = new HashMap<String, Integer>();
		int count = 0;
		for(Proxy proxy:proxyList){
			String key = proxy.ip.trim() + ":"+ proxy.port.toString().trim();
			proxy2Index.put(key, count++);
		}
		String newKey = ip.trim() + ":"+ port.toString().trim();
		if(proxy2Index.containsKey(newKey)){
			int removeIndex= proxy2Index.get(newKey).intValue();
			if(pointer > removeIndex || (removeIndex == pointer && removeIndex == proxyList.size()-1)){
				pointer --;
			}
			proxyList.remove(removeIndex);
			System.out.println("[DeleteProxyInfo]Delete " + ip + ":" + port + " SUCCESSFULLY!");
		}else{
			System.out.println("[DeleteProxyError]" + ip + ":" + port + " DO NOT EXIST!PLEASE CHECK!");
		}
	}
	
	private void addProxy(String ip, Integer port){
		Map<String, Integer> proxy2Index = new HashMap<String, Integer>();
		int count = 0;
		for(Proxy proxy:proxyList){
			String key = proxy.ip.trim() + ":"+ proxy.port.toString().trim();
			proxy2Index.put(key, count++);
		}
		String newKey = ip.trim() + ":"+ port.toString().trim();
		if(!proxy2Index.containsKey(newKey)){
			proxyList.add(Proxy.construct(ip, port));
			System.out.println("[AddProxyInfo]Add " + ip + ":" + port + " SUCCESSFULLY!");
		}else{
			System.out.println("[AddProxyInfo]" + ip + ":" + port + " ALREADY EXISTS!SKIP IT!");
		}
	}
	
	private void activeProxy(String ip, Integer port){
		Map<String, Integer> proxy2Index = new HashMap<String, Integer>();
		int count = 0;
		for(Proxy proxy:proxyList){
			String key = proxy.ip.trim() + ":"+ proxy.port.toString().trim();
			proxy2Index.put(key, count++);
		}
		String newKey = ip.trim() + ":"+ port.toString().trim();
		if(proxy2Index.containsKey(newKey)){
			proxyList.get((proxy2Index.get(newKey)).intValue()).status = 0;
			System.out.println("[ActiveProxyInfo]ACTIVE " + ip + ":" + port + " SUCCESSFULLY!");
		}else{
			System.out.println("[ActiveProxyError]" + ip + ":" + port + " DO NOT EXIST!PLEASE CHECK!");
		}
	}
	
	private void restrainProxy(String ip, Integer port){
		Map<String, Integer> proxy2Index = new HashMap<String, Integer>();
		int count = 0;
		for(Proxy proxy:proxyList){
			String key = proxy.ip.trim() + ":"+ proxy.port.toString().trim();
			proxy2Index.put(key, count++);
		}
		String newKey = ip.trim() + ":"+ port.toString().trim();
		if(proxy2Index.containsKey(newKey)){
			proxyList.get((proxy2Index.get(newKey)).intValue()).status = 1;
			System.out.println("[RestrainProxyInfo]RESTRAIN " + ip + ":" + port + " SUCCESSFULLY!");
		}else{
			System.out.println("[RestrainProxyError]" + ip + ":" + port + " DO NOT EXIST!PLEASE CHECK!");
		}
	}
	
	private Proxy getProxy(){
		//打印proxy信息
		int totalProxy = proxyList.size();
		int activeProxy = 0;
		int restrainProxy = 0;
		int unknownProxy = 0;
		for(Proxy proxy:proxyList){
			if(0 == proxy.status){
				activeProxy ++;
			}else if(1 == proxy.status){
				restrainProxy ++;
			}else if(-1 == proxy.status){
				unknownProxy ++;
			}
		}
		System.out.println("[GetProxyInfo]PROXY TOTAL:" + totalProxy + "||PROXY ACTIVE:" + activeProxy + "||PROXY RESTRAIN:" + restrainProxy + "||PROXY UNKNOWN:" + unknownProxy +"||POINTER:" + pointer + "(" + refreshCounts + "." + roundCounts + ")");
		writeLog(totalProxy, activeProxy, restrainProxy, unknownProxy);
		//寻找可用节点，循环链表结构
		int anchor = pointer;
		//为1表示为restrain的代理
		while(1 == proxyList.get(pointer).status){
			if(pointer < proxyList.size()-1){
				pointer ++;
			}else{
				roundCounts ++;
				pointer = 0;
			}
			//转了一圈回到自己
			if(anchor == pointer){
				System.out.println("EMPTY!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				//开始refresh
				refreshProxy();
				checkProxies();
				continue;
			}
		}
		//用完当前pointer后下移一位
		anchor = pointer;
		if(pointer < proxyList.size()-1){
			pointer ++;
		}else{
			//每循环一次写回一次数据
			roundCounts ++;
			pointer = 0;
		}
		return proxyList.get(anchor);
	}
	
	
	public void printProxies(){
		for(Proxy proxy:proxyList){
			System.out.println(proxy.ip + ":" + proxy.port);
		}
	}
	public static void main(String[] args) {
		ProxyService ps = ProxyService.getInstance("c:/data/proxy");
	}
}
