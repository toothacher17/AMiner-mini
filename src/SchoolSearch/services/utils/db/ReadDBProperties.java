package SchoolSearch.services.utils.db;

import java.io.InputStream;
import java.util.Properties;

public class ReadDBProperties {
	private Properties properties;
	private String webUrl = "";

	public ReadDBProperties() {
		properties = new Properties();
		InputStream in = null;
		try {
			Package pack = ReadDBProperties.class.getPackage(); // �õ���İ����
			ClassLoader loader = ReadDBProperties.class.getClassLoader(); // �õ���ļ���·������
			in = loader.getResourceAsStream("resource.properties"); // �õ���ݿ������ļ���
			properties.load(in); // ������ݿ������ļ�
			in.close(); // �ر��ļ���
			in = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getWebUrl() {
		return webUrl;
	}
}