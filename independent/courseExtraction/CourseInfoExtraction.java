package courseExtraction;

import org.htmlparser.beans.StringBean;

import SchoolSearch.services.utils.Strings;


public class CourseInfoExtraction {
	public static String[] getContentUsingStringBean(String url) {
		StringBean sb = new StringBean();
		sb.setLinks(false);
		sb.setCollapse(true); 
		sb.setReplaceNonBreakingSpaces(true);
		sb.setURL(url);
		String courseInfoContent = sb.getStrings();
		String[] courseInfo = courseInfoContent.split("\n");
		return courseInfo;
	}
	
	public static CourseInfo getCourseInfo(Long courseId, CourseInfo courseInfo) {
		
		String url = "http://learn.tsinghua.edu.cn/learn/courseinfo.jsp?course_id=" + courseId;
		courseInfo.setUrl(url);
		
		String[] courseInfoContent = getContentUsingStringBean(url);
		
		String[] partField = {"简介:", "课程描述", "教材名称", "课程学分", "课程学时", "考试方式", "课件及讲义数", "讨论文章数", "布置作业"};
		int fieldIndex = 0;
		
		for(int i=0; i< courseInfoContent.length; i++) {
			
			if(courseInfoContent[i].startsWith("姓名")&&courseInfoContent[i+1].startsWith("电话")) {
				
				String sex = courseInfoContent[i].substring(courseInfoContent[i].indexOf("(")+1, courseInfoContent[i].indexOf("(")+2);
				courseInfo.setSex(sex);
				
				if((courseInfoContent[i].indexOf("职称:")+3)==courseInfoContent[i].lastIndexOf(",")) {
					courseInfo.setPosition(null);
				}  else {
					String position = courseInfoContent[i].substring(courseInfoContent[i].indexOf("职称:")+3, courseInfoContent[i].lastIndexOf(","));
					courseInfo.setPosition(position);
				}
				
				if((courseInfoContent[i].indexOf("职务:")+3)==courseInfoContent[i].indexOf(")")) {
					courseInfo.setDuty(null);
				} else {
					String duty = courseInfoContent[i].substring(courseInfoContent[i].indexOf("职务:")+3, courseInfoContent[i].indexOf(")"));
					courseInfo.setDuty(duty);
				}
			}
			
			if(courseInfoContent[i].startsWith("电话")) {
				String telephone = courseInfoContent[i].substring(courseInfoContent[i].indexOf(":")+1).trim();
				courseInfo.setTelephone(telephone);
			}
			
			if(courseInfoContent[i].startsWith("电子邮件")) {
				String email = courseInfoContent[i].substring(courseInfoContent[i].indexOf(":")+1).trim();
				courseInfo.setEmail(email);
			}

			try {
				if(courseInfoContent[i].startsWith("地址")) {
					String address = courseInfoContent[i].substring(courseInfoContent[i].indexOf(":")+1, courseInfoContent[i].indexOf("(")-1).trim();
					courseInfo.setAddress(address);
					if (Strings.isEmpty(courseInfoContent[i].substring(
							courseInfoContent[i].indexOf("邮编:") + 3,
							courseInfoContent[i].lastIndexOf(")")))
							|| courseInfoContent[i].substring(
									courseInfoContent[i].indexOf("邮编:") + 3,
									courseInfoContent[i].lastIndexOf(")")).equals(
									"－")) {
						courseInfo.setPostcode(null);
					} else {
						Long postcode = Long.parseLong(courseInfoContent[i].substring(courseInfoContent[i].indexOf("邮编:")+3, courseInfoContent[i].lastIndexOf(")")));
						courseInfo.setPostcode(postcode);
					}
				}
			} catch(Exception e) {
				continue;
			}
			
			
			if(courseInfoContent[i].startsWith(partField[fieldIndex])) {
				if(courseInfoContent[i+1].trim().equals(partField[fieldIndex+1])) {
					Choice1(fieldIndex, courseInfo);
				} else {
					Choice2(fieldIndex, courseInfo, courseInfoContent[i+1]);
				}
				fieldIndex++;
				if(fieldIndex == 8) 
					fieldIndex = 0;
			}
			
			if(courseInfoContent[i].startsWith("/提交作业")) {
				if(courseInfoContent[i+1].trim().equals("课程开放范围")) {
					courseInfo.setHomework(null);
				} else {
					String homework = courseInfoContent[i+1];
					courseInfo.setHomework(homework);
				}
			}
		}
		return courseInfo;
	}
	
	public static void Choice1(int fieldIndex, CourseInfo courseInfo) {
		switch(fieldIndex) {
		case 0: courseInfo.setPersonProfile(null); break;
		case 1: courseInfo.setCourseDescription(null); break;
		case 2: courseInfo.setTeachingMaterial(null); break;
		case 3: courseInfo.setCredit(null); break;
		case 4: courseInfo.setClass_hour(null); break;
		case 5: courseInfo.setChecking(null); break;
		case 6: courseInfo.setTeachingMaterialNum(null); break;
		case 7: courseInfo.setPaperNum(null); break;
		}
	}
	
	public static void Choice2(int fieldIndex, CourseInfo courseInfo, String info) {
		switch(fieldIndex) {
		case 0: courseInfo.setPersonProfile(info); break;
		case 1: courseInfo.setCourseDescription(info); break;
		case 2: courseInfo.setTeachingMaterial(info); break;
		case 3: courseInfo.setCredit(Integer.parseInt(info)); break;
		case 4: courseInfo.setClass_hour(Integer.parseInt(info)); break;
		case 5: courseInfo.setChecking(info); break;
		case 6: courseInfo.setTeachingMaterialNum(Integer.parseInt(info)); break;
		case 7: courseInfo.setPaperNum(Integer.parseInt(info)); break;
		}
	}
	
	public static void main(String args[]) {
		CourseInfo courseInfo = new CourseInfo();
		CourseInfo ci = getCourseInfo(4496l, courseInfo);
		System.out.println(ci.getEmail());
	}
}
