import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		String n = "400";
		
//		//separate jobs into different categories
//		TaskDistribution tk = new TaskDistribution();
//		tk.taskDistribution(new String[] { "permanent" + n + ".csv", "normal" + n + ".csv", "tiny" + n + ".csv",
//				"forPaper_VMDataOutput_every" + n + "version.csv" });
//		
//		//convert task usage to fixed VM
		TaskOutput1 output = new TaskOutput1();
//		output.readFiles(new String[] { "permanent" + n + ".csv", "task_permanent_" + n + ".csv", "" });
		output.readFiles(new String[] { "normal" + n + ".csv", "task_normal_" + n + ".csv", "" });
//		output.readTinyFiles(
//				new String[] { "tiny" + n + ".csv", "task_tiny_" + n + ".csv", "tiny_count_" + n + ".csv" });
//		
//		// ffd
//		AdvancedFFD ffd = new AdvancedFFD();
//		ffd.tasksAllocation(ffd.readTasks("task_permanent_" + n + ".csv"), "permanent");
//		ffd.tasksAllocation(ffd.readTasks("task_normal_" + n + ".csv"), "normal");
//		ffd.tasksAllocation(ffd.readTasks("task_tiny_" + n + ".csv"), "tiny");
//		ffd.outputFiles(new String[] { "VMPM" + n + ".csv", "PMUtilization" + n + ".csv", "PMUsage" + n + ".csv" });
//
//		// calculate work
//		Power p = new Power();
//		p.getWork("PMUtilization" + n + ".csv");


//		TaskOutput1 output = new TaskOutput1();
//		output.readFiles(new String[] { "forPaper_VMDataOutput_every" + n + "version.csv", "task_simple_" + n + ".csv",
//				"tiny_count_simple_" + n + ".csv" });
//				
//		AdvancedFFD ffd = new AdvancedFFD();
//		ffd.tasksAllocation(ffd.readTasks("task_simple_" + n + ".csv"), "permanent");
//		ffd.outputFiles(new String[] { "VMPM" + n + "_simple.csv", "PMUtilization" + n + "_simple.csv",
//				"PMUsage" + n + "_simple.csv" });
//		
//		Power p = new Power();
//		p.getWork("PMUtilization" + n + "_simple.csv");
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time cost:" + (endTime - startTime) / 1000f);
		
	}
}
