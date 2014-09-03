package courseExtraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CourseListInfoExtraction implements Runnable {

	Integer begin = null;
	Integer end = null;
	
	public CourseListInfoExtraction(Integer begin, Integer end) {
		this.begin = begin;
		this.end = end;
	}
	
	public static List<String> getHtmlContent(URL url, String encode) {  
    	List<String> htmlContent = new ArrayList<String>();
        int responseCode = -1;  
        HttpURLConnection con = null;  
        try {  
            con = (HttpURLConnection) url.openConnection();  
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 "); 
            con.setConnectTimeout(60000);  
            con.setReadTimeout(60000);  
            // 获得网页返回信息码  
            responseCode = con.getResponseCode();  
            if (responseCode == -1) {  
                System.out.println(url.toString() + " : connection is failure...");  
                con.disconnect();  
                return null;  
            }  
            if (responseCode >= 400) // 请求失败  
            {  
                System.out.println("请求失败:get response code: " + responseCode);  
                con.disconnect();  
                return null;  
            }  
  
            InputStream inStr = con.getInputStream();  
            InputStreamReader istreamReader = new InputStreamReader(inStr, encode);  
            BufferedReader buffStr = new BufferedReader(istreamReader);  
  
            String str = null;  
            while ((str = buffStr.readLine())!= null) {
            	if(str.trim().equals(""))
            		continue;
            	htmlContent.add(str);
            }
            inStr.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
            System.out.println("error: " + url.toString());  
        } finally {  
            con.disconnect();  
        }  
        return htmlContent;  
    }  
	
	public static List<String> getHtmlContent(String url, String encode) {  
        if (!url.toLowerCase().startsWith("http://")) {  
            url = "http://" + url;  
        }  
        try {  
            URL rUrl = new URL(url);  
            return getHtmlContent(rUrl, encode);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
	
	public static CourseInfo[] getInfoListByPage(List<String> htmlContent) {
		
		CourseInfo[] courseInfo = new CourseInfo[50];
		for(int i=0; i<50; i++) {
			courseInfo[i] = new CourseInfo();
		}
		
		String[] field = { "courseid[", "coursename[", "courseno[",
				"teacherid[", "teachername[", "majorid[", "majorname[",
				"browsetimes[", "semesterid[", "semestername[", "opentype[",
				"studentnum[" };
		int fieldIndex = 0;
		int courseIndex = 0;
		
		for(String content : htmlContent) {
			if(content.trim().toLowerCase().startsWith(field[fieldIndex])) {
				saveInfo(fieldIndex, courseIndex, content, courseInfo);
				fieldIndex ++;
			}
			if(fieldIndex == 12) {
				fieldIndex = 0;
				courseIndex ++;
			}
		}
		return courseInfo;
	}
	
	public static void saveInfo(int fieldIndex, int courseIndex, String content, CourseInfo[] courseInfo) {
		switch(fieldIndex) {
		case 0: {
			Long courseId = Long.parseLong(content.substring(content.indexOf("'")+1, content.lastIndexOf("'")));
			courseInfo[courseIndex].setCourseId(courseId);
			courseInfo[courseIndex] = CourseInfoExtraction.getCourseInfo(courseId, courseInfo[courseIndex]);
		} break;
		
		case 1: {
			String courseName = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setCourseName(courseName);
		} break;
		
		case 2: {
			String courseNo = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setCourseNo(courseNo);
		} break;
		
		case 3: {
			String teacherId = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setTeacherId(teacherId);
		} break;
		
		case 4: {
			String teacherName = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setTeacherName(teacherName);
		} break;
		
		case 5: {
			String majorId = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setMajorId(majorId);
		} break;
		
		case 6: {
			String majorName = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setMajorName(majorName);
		} break;
		
		case 7: {
			Integer browseTimes = Integer.parseInt(content.substring(content.indexOf("=")+2, content.lastIndexOf(";")));
			courseInfo[courseIndex].setBrowseTimes(browseTimes);
		} break;
		
		case 8: {
			Integer semesterId = Integer.parseInt(content.substring(content.indexOf("'")+1, content.lastIndexOf("'")));
			courseInfo[courseIndex].setSemesterId(semesterId);
		} break;
		
		case 9: {
			String semesterName = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setSemesterName(semesterName);
		} break;
		
		case 10: {
			String openType = content.substring(content.indexOf("'")+1, content.lastIndexOf("'"));
			courseInfo[courseIndex].setOpenType(openType);
		} break;
		
		case 11: {
			Integer studentNum = Integer.parseInt(content.substring(content.indexOf("'")+1, content.lastIndexOf("'")));
			courseInfo[courseIndex].setStudentNum(studentNum);
		} break;
		
		}
	}

	public List<String> getPage() {

		List<String> urlList = new ArrayList<String>();
		int[] semesterid = { 100020, 100080, 100120, 100140, 100160, 100180,
				100200, 100220, 100240, 100260, 100280, 100300, 100320, 100341,
				100342, 100380, 100400, 100420, 100440, 100460, 100480, 100500,
				100520, 100521, 100540, 100560, 100561, 100580, 100600, 100601,
				100620, 100640, 100641, 100660, 100680, 100681 };
		int[] pageNum = { 0, 3, 8, 12, 15, 21, 22, 1, 30, 28, 3, 34, 31, 2, 38,
				33, 2, 41, 34, 2, 41, 35, 3, 45, 39, 3, 46, 40, 3, 49, 42, 3,
				52, 45, 2, 0 };
		for (int i = 0; i < semesterid.length; i++) {
			for (int j = 1; j <= pageNum[i]; j++) {
				String url = "http://learn.tsinghua.edu.cn/learn/search_course.jsp?go_page="
						+ j
						+ "&student=2&semesterid="
						+ semesterid[i]
						+ "&pre_page=1&key_coursename=&key_teachername=&depart_id=999&typepage=3";
				urlList.add(url);
			}
		}

		List<String> partUrlList = new ArrayList<String>();
		for (int i = begin; i < end; i++) {
			partUrlList.add(urlList.get(i));
		}
		return partUrlList;
	}
	
	public void saveAllInfoList() {
		
		DataTransfer dataTransfer = DataTransfer.getInstance();
		
		List<String> urlList = getPage();
		
		int urlNum = 1;
		
		for (String url : urlList) {
			long t1 = System.currentTimeMillis();
			List<String> htmlContent = getHtmlContent(url, "gbk");
			CourseInfo[] list = getInfoListByPage(htmlContent);

			for (int i = 0; i < list.length; i++) {
				dataTransfer.dataWriter(list[i].getCourseId() + "||"
						+ list[i].getCourseName() + "||"
						+ list[i].getCourseNo() + "||" + list[i].getTeacherId()
						+ "||" + list[i].getTeacherName() + "||"
						+ list[i].getMajorId() + "||" + list[i].getMajorName()
						+ "||" + list[i].getBrowseTimes() + "||"
						+ list[i].getSemesterId() + "||"
						+ list[i].getSemesterName() + "||"
						+ list[i].getOpenType() + "||"
						+ list[i].getStudentNum() + "||" + list[i].getSex()
						+ "||" + list[i].getPosition() + "||"
						+ list[i].getDuty() + "||" + list[i].getTelephone()
						+ "||" + list[i].getEmail() + "||"
						+ list[i].getAddress() + "||" + list[i].getPostcode()
						+ "||" + list[i].getPersonProfile() + "||"
						+ list[i].getCourseDescription() + "||"
						+ list[i].getTeachingMaterial() + "||"
						+ list[i].getCredit() + "||" + list[i].getClass_hour()
						+ "||" + list[i].getChecking() + "||"
						+ list[i].getTeachingMaterialNum() + "||"
						+ list[i].getPaperNum() + "||" + list[i].getHomework()
						+ "||" + list[i].getUrl() + "\n");
			}
			
			System.out.print("用时: "
					+ (System.currentTimeMillis() - t1) + "||");
			
			System.out.println("进度：" + (double)urlNum/urlList.size());
			
			urlNum ++;

//			for (int i = 0; i < list.length; i++) {
//				System.out.println(list[i].getCourseId() + "|"
//						+ list[i].getCourseName() + "|" + list[i].getCourseNo()
//						+ "|" + list[i].getTeacherId() + "|"
//						+ list[i].getTeacherName() + "|" + list[i].getMajorId()
//						+ "|" + list[i].getMajorName() + "|"
//						+ list[i].getBrowseTimes() + "|"
//						+ list[i].getSemesterId() + "|"
//						+ list[i].getSemesterName() + "|"
//						+ list[i].getOpenType() + "|" + list[i].getStudentNum()
//						+ "<<>><<>><<>>" + list[i].getSex() + "|"
//						+ list[i].getPosition() + "|" + list[i].getDuty() + "|"
//						+ list[i].getTelephone() + "|" + list[i].getEmail()
//						+ "|" + list[i].getAddress() + "|"
//						+ list[i].getPostcode() + "|"
//						+ list[i].getPersonProfile() + "|"
//						+ list[i].getCourseDescription() + "|"
//						+ list[i].getTeachingMaterial() + "|"
//						+ list[i].getCredit() + "|" + list[i].getClass_hour()
//						+ "|" + list[i].getChecking() + "|"
//						+ list[i].getTeachingMaterialNum() + "|"
//						+ list[i].getPaperNum() + "|" + list[i].getHomework()
//						+ "|" + list[i].getUrl());
//				System.out.println();
//			}
			
		}
	}

	@Override
	public void run() {
    	saveAllInfoList();
	} 
	
	public static void main(String args[]){  
    	CourseListInfoExtraction test1 = new CourseListInfoExtraction(0,200);
    	CourseListInfoExtraction test2 = new CourseListInfoExtraction(20,400);
    	CourseListInfoExtraction test3 = new CourseListInfoExtraction(400,600);
    	CourseListInfoExtraction test4 = new CourseListInfoExtraction(600,807);
    	Thread t1 = new Thread(test1);
    	Thread t2 = new Thread(test2);
    	Thread t3 = new Thread(test3);
    	Thread t4 = new Thread(test4);
    	t1.start();
    	t2.start();
    	t3.start();
    	t4.start();
    }
}
