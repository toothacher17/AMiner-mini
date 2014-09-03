import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;

public class RemoveDup {
	public static void main(String[] args) {
		ReadFromDataBase rd = ReadFromDataBase.getInstance("schoolsearch2",
				"course");
		rd.getAllData();
		AntiReduplicationCheck.prepareDB("schoolsearch2", "course_copy");
		AntiReduplicationCheck antiAC = AntiReduplicationCheck
				.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(),
						rd.getDataCache());
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("couresId", true);
		antiAC.checkBatch(true);
		AntiReduplicationCheck.insertAndUpdateWithConstruct(false, "courseId");

	}
}
