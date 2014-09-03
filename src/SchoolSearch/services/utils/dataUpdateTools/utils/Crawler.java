package SchoolSearch.services.utils.dataUpdateTools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;


import SchoolSearch.services.utils.dataUpdateTools.utils.ProxyService.Operation;



public class Crawler{
	
		private static ProxyService ps;
		private static String cookie;
		private static boolean isUseCookie = false;
		
		public static void clearCookie(){
			cookie = null;
			isUseCookie = false;
		}
		
		public static void initCookie(String surl,String field1,String username,String field2,String password){
			try {
				URL url = new URL(surl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				OutputStreamWriter out = new OutputStreamWriter(connection
						.getOutputStream(), "utf-8");
				out.write(field1+"="+username+"&"+field2+"="+password);
				out.flush();
				out.close();
				cookie = connection.getHeaderField("Set-Cookie");  
				if(null!=cookie && !cookie.isEmpty()){
					isUseCookie=true;
				}
				url = new URL(surl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private static synchronized void initProxyService(){
			if(null == ps){
				ps = ProxyService.getInstance("c:/data/proxy");
			}
		}
		
		public static void restrainProxy(String ip, Integer port){
			ps.operation(Operation.RESTRAIN, ip, port);
		}
		
		public static void activeProxy(String ip, Integer port){
			ps.operation(Operation.ACTIVE, ip, port);
		}
	    /** 
	     * 对url进行编码 
	     */  
	    public static String encodeURL(String url) {  
	        try {  
	            return URLEncoder.encode(url, "UTF-8");  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	            return null;  
	        }  
	    }  
	      
	    /** 
	     * 对url进行解码 
	     * @param url 
	     * @return 
	     */  
	    public static String decodeURL(String url){  
	        try {  
	            return URLDecoder.decode(url, "UTF-8");  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	            return null;  
	        }  
	    }  
	    
	    public static List<MatchResult> findAll(String pattern, CharSequence text){
	    	List<MatchResult> results = new ArrayList<MatchResult>();
	    	Pattern p = Pattern.compile(pattern);
	    	Matcher m = p.matcher(text);
	    	while(m.find()) results.add(m.toMatchResult());
	    	return results;
	    }
	    /** 
	     * 判断URL地址是否存在 
	     * @param url 
	     * @return 
	     */  
	    public static boolean isURLExist(String url) {  
	        try {  
	            URL u = new URL(url);  
	            HttpURLConnection urlconn = (HttpURLConnection) u.openConnection();  
	            int state = urlconn.getResponseCode();  
	            if (state == 200) {  
	                return true;  
	            } else {  
	                return false;  
	            }  
	        } catch (Exception e) {  
	            return false;  
	        }  
	    }  
	      
	    /** 
	     * 将请求参数还原为key=value的形式
	     * @param params 
	     * @return 
	     */  
	    public static String getParamString(Map<?, ?> params) {  
	        StringBuffer queryString = new StringBuffer(256);  
	        Iterator<?> it = params.keySet().iterator();  
	        int count = 0;  
	        while (it.hasNext()) {  
	            String key = (String) it.next();  
	            String[] param = (String[]) params.get(key);  
	            for (int i = 0; i < param.length; i++) {  
	                if (count == 0) {  
	                    count++;  
	                } else {  
	                    queryString.append("&");  
	                }  
	                queryString.append(key);  
	                queryString.append("=");  
	                try {  
	                    queryString.append(URLEncoder.encode((String) param[i], "UTF-8"));  
	                } catch (UnsupportedEncodingException e) {  
	                }  
	            }  
	        }  
	        return queryString.toString();  
	    }  
	  
	    /** 
	     * 获得请求的路径及参数 
	     * @param request 
	     * @return 
	     */  
	    public static String getRequestURL(HttpServletRequest request) {  
	        StringBuffer originalURL = new StringBuffer(request.getServletPath());  
	        Map<?,?> parameters = request.getParameterMap();  
	        if (parameters != null && parameters.size() > 0) {  
	            originalURL.append("?");  
	            originalURL.append(getParamString(parameters));  
	        }  
	        return originalURL.toString();  
	    }  
	  
	    /** 
	     * 抓取网页内容,自动识别编码 
	     * @param urlString 
	     * @return 
	     * @throws Exception 
	     */  
	    public static String getURLContentViaProxy(String urlString, String ip, Integer port) throws Exception {  
	            StringBuffer html = new StringBuffer();  
	            URL url = new URL(urlString);
	            SocketAddress addr = new InetSocketAddress(ip, port);
	            Proxy typeProxy = new Proxy(Proxy.Type.HTTP, addr);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection(typeProxy);  
	            conn.setConnectTimeout(10000);
	            conn.setReadTimeout(10000);
	            InputStreamReader isr = new InputStreamReader(conn.getInputStream());  
	            BufferedReader br = new BufferedReader(isr);  
	            String temp;  
	            while ((temp = br.readLine()) != null) {  
	                html.append(temp).append("\n");  
	            }  
	            br.close();  
	            isr.close();  
	            return html.toString();  
	    }  
	    
	    public static String getURLContentWithoutProxy(String urlString){ 
            StringBuffer html = null;  
            URL url;
			try {
//				urlString = URLEncoder.encode(urlString,"utf-8");
				url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
				conn.setConnectTimeout(10000);
	            conn.setReadTimeout(10000);
	            //如果使用cookie
	            if(isUseCookie && null!=cookie && !cookie.isEmpty()){
	            	conn.setRequestProperty("Cookie", cookie);
	            }
	            conn.connect();
	            InputStreamReader isr = new InputStreamReader(conn.getInputStream());  
	            BufferedReader br = new BufferedReader(isr);  
	            String temp;  
	            html = new StringBuffer();
	            while ((temp = br.readLine()) != null) {  
	                html.append(temp).append("\n");  
	            }  
	            br.close();  
	            isr.close();  
	            return html.toString(); 
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	    }
	    
	    //返回http返回内容，同时封装调用的ip和port
	    public static List<String> getURLContentViaProxy(String urlString){ 
	    	initProxyService();
	    	List<String> infoList = null;
	    	while(null == infoList){
	    		ProxyService.Proxy proxy = (ProxyService.Proxy)ps.operation(Operation.GET, null, null);
	    		if(null != proxy){
	    			try {
						String content = getURLContentViaProxy(urlString, proxy.ip, proxy.port);
						infoList = new ArrayList<String>();
						infoList.add(content);
						infoList.add(proxy.ip);
						infoList.add(proxy.port.toString().trim());
					} catch (Exception e) {
//						e.printStackTrace();
						ps.operation(Operation.DELETE, proxy.ip, proxy.port);
						continue;
					}
	    			//代理用尽，break
	    		}else{
	    			break;
	    		}
	    	}
	    	return infoList;
	    }
	    
	    public static String getNationality(String position){
	    	//对position进行字符串预处理
	    	String nationality = null;
	    	if(null != position){
	    		int len = position.length();
	    		if(len >= 80){
	    			position = position.substring(len-80,len);
	    		}
	    		position = position.replaceAll("\\s", "%20").replaceAll("\\#", "");
	    		StringBuilder sb = new StringBuilder("http://maps.googleapis.com/maps/api/geocode/json?address=");
	    		sb.append(position); 
	    		sb.append("&sensor=false&language=en_us");
	    		while(null == nationality){
	    			List<String> infoList = null;
	    			infoList = Crawler.getURLContentViaProxy(sb.toString());
	    			//content为null，说明代理池代理用尽，返回null
	    			if(null != infoList){
	    				String content = infoList.get(0);
	    				String ip = infoList.get(1);
	    				Integer port = Integer.parseInt(infoList.get(2).trim());
	    				try{
	    					JSONObject obj = new JSONObject(content);
	    					String status = obj.getString("status").trim();
	    					//成功接收，返回content
	    					if(status.equalsIgnoreCase("ok")){
	    						JSONObject obj1 = obj.getJSONArray("results").getJSONObject(0);
	    						JSONArray array1 = obj1.getJSONArray("address_components");
	    						for(int i=0; i<array1.length();i++){
	    							JSONObject objTmp = array1.getJSONObject(i);
	    							JSONArray arrayTmp = objTmp.getJSONArray("types");
	    							for(int j=0;j<arrayTmp.length();j++){
	    								if(arrayTmp.get(j).equals("country")){
	    									nationality = objTmp.getString("long_name");
	    									ps.operation(Operation.ACTIVE, ip, port);
	    									System.out.println("-------------->" + nationality + "<-----------------");
	    									return nationality;
	    								}
	    							}
	    						}
	    						return nationality;
	    					}else if(status.equalsIgnoreCase("over_query_limit")){
	    						System.out.println("[Info]QUERY_OVER_LIMIT!TRY ANOTHER PROXY!");
	    						//代理暂时不能使用，告知proxyService
	    						ps.operation(Operation.RESTRAIN, ip, port);
	    						continue;
	    					}else if(status.equalsIgnoreCase("zero_results") || status.equalsIgnoreCase("invalid_request")){
	    						ps.operation(Operation.ACTIVE, ip, port);
	    						return nationality;
	    					}else{
	    						System.out.println(status);
	    						System.out.println(sb.toString());
	    						ps.operation(Operation.DELETE, ip, port); 
	    						continue;
	    					}
	    				}catch(Exception e){
	    					ps.operation(Operation.RESTRAIN, ip, port);
	    					System.out.println(content);
	    				}
	    			}else{
	    				return nationality;
	    			}
	    		}
	    	}else{
	    		return nationality;
	    	}
			return nationality;
	    }
	     public static void main(String[] args) { 
	     }
}
