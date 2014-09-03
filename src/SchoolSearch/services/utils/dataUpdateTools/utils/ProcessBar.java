package SchoolSearch.services.utils.dataUpdateTools.utils;

public class ProcessBar {
	private long max;
	private long counter1;
	private long counter2;
	private long pre;
	private long after;
	private long percentage;
	private long interval;
	private boolean hasPercentage;
	public ProcessBar(int max,boolean hasPercentage){
		this.max = max;
		this.hasPercentage = hasPercentage;
		if(max<100000){
			interval=5;
		}else{
			interval=1;
		}
		pre = System.currentTimeMillis();
		System.out.print("程序正在进行中");
		if(hasPercentage){
			System.out.println("...");
		}
	}
	public void tictoc(){
		counter1++;
		counter2++;
		if(hasPercentage){
			if(counter1*100/max-percentage>=interval){
				percentage = counter1*100/max;
				System.out.println("process percentage:"+percentage+"%");
			}
		}else{
			if(counter2>=max){
				counter2=0;
				System.out.print(".");
			}
		}
	}
	public void stop(){
		after = System.currentTimeMillis();
		long cost = after-pre;
		if(cost>=1000){
			cost = cost/1000;
			System.out.println("计时结束,共耗时:"+cost+"秒");
		}else{
			System.out.println("计时结束,共耗时:"+cost+"毫秒");
		}
	}
}
