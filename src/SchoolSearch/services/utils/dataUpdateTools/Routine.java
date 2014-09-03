package SchoolSearch.services.utils.dataUpdateTools;

import SchoolSearch.services.utils.dataUpdateTools.basic.write.RemoveFromDataBase;
import transfer.PersonRelationTransfer;

public class Routine {
public static void main(String[] args) {
//	DepartmentImportTool dtt = new DepartmentImportTool();
//	dtt.process("import", "departments_tbl");
//	try {
//		Thread.sleep(3000);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
	DBPersonImportTool dit = new DBPersonImportTool();
	//System.out.println("PROCESS faculty_tbloriginal");
	dit.process("import", "faculty_tblhykxyjsxy");
	//dit.process("import", "faculty_tblxxjsyjy");
//	try {
//		Thread.sleep(3000);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//	System.out.println("PROCESS faculty_tblxxkxyjsxy");
//	dit.process("import", "faculty_tblxxkxyjsxy");
//	try {
//		Thread.sleep(3000);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//	System.out.println("PROCESS faculty_tbllxymsxy");
//	dit.process("import", "faculty_tbllxymsxy");
//	try {
//		Thread.sleep(3000);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//	System.out.println("PROCESS faculty_tblmany");
//	dit.process("import", "faculty_tblmany");
	
//	DBPersonImportTool dit = new DBPersonImportTool();
//	dit.process("import2", "faculty_tbl");
//	try {
//		Thread.sleep(3000);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
	
	//更新person2relation
	RemoveFromDataBase rm = RemoveFromDataBase.getIndependentInstance("cache", "personrelation");
	rm.truncate();
	PersonRelationTransfer pr = new PersonRelationTransfer();
	pr.person2RelationTransfer();
}
}
