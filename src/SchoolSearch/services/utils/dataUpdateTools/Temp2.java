package SchoolSearch.services.utils.dataUpdateTools;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import SchoolSearch.services.utils.dataUpdateTools.utils.IOUtils;
import SchoolSearch.services.utils.dataUpdateTools.utils.ProcessBar;

public class Temp2 {
	public static void main(String[] args) {
		int max=112123331;
		ProcessBar pc = new ProcessBar(max,true);
		for(int i=0;i<max;i++){
			pc.tictoc();
		}
		pc.stop();
	}
}
