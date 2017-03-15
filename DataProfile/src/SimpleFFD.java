import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleFFD {
	final static double LARGE_STORAGE = 1.5;
	final static int LARGE_COUNT = 4;
	final static double MEDIUM_STORAGE = 1.0;
	final static int MEDIUM_COUNT = 6;
	final static double TINY_STORAGE = 0.5;

	static List<String[]> jobList = new ArrayList<String[]>();
	static List<List<String[]>> PMList = new ArrayList<List<String[]>>();
	static List<String> permanentID = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		
		readPermanentJobs("output.csv");
		
		BufferedReader bf = new BufferedReader(new FileReader(new File("output200.csv")));
		String line;
		String[] row = new String[3];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			jobList.add(row);
		}
		tasksAllocation(jobList);
		
//		BufferedWriter pmlist = new BufferedWriter(new FileWriter(new File("permanents1000_1.csv")));
//		for (int i = 0; i < PMList.size(); i++) {
//			pmlist.write(String.valueOf(50700 + 300 * i) + ",");
//			for (int k = 0; k < permanentID.size(); k++) {
//				pmlist.write(permanentID.get(k) + "(");
//				for (int j = 0; j < PMList.get(i).size(); j++) {
//					if (PMList.get(i).get(j)[1].contains(permanentID.get(k))) {
//						int count = getAppearTimes(PMList.get(i).get(j)[1], permanentID.get(k));
//						pmlist.write(PMList.get(i).get(j)[0] + ":" + String.valueOf(count) + ";");
//					}
//				}
//				pmlist.write("),");
//			}
//			pmlist.write("\r\n");
//			pmlist.flush();
//		}

//		BufferedWriter PMListCountWriter = new BufferedWriter(new FileWriter(new File("PMNumber400_1.csv")));
		BufferedWriter PMUtilizationWriter = new BufferedWriter(new FileWriter(new File("PMUtilization200_1.csv")));
//		BufferedWriter PMListUsageWriter = new BufferedWriter(new FileWriter(new File("PMUsage400_1.csv")));
		double totalUsage = 0.0f;
		for (int i = 0; i < PMList.size(); i++) {
			totalUsage = 0.0;
//			PMListCountWriter.write(String.valueOf(PMList.get(i).size()));
			for (int j = 0; j < PMList.get(i).size(); j++) {
				double maxStorage = getPMStorage(Integer.parseInt(PMList.get(i).get(j)[0]));
				double usage = Double.parseDouble(PMList.get(i).get(j)[2]);
				double rate = usage / maxStorage;
				PMUtilizationWriter.write(PMList.get(i).get(j)[0]+":"+String.valueOf(rate) + ",");
//				for (int k = 0; k < PMList.get(i).get(j).length; k++) {
//					totalUsage += Double.parseDouble(PMList.get(i).get(j)[2]);
//				}
			}
//			PMListUsageWriter.write(String.valueOf(totalUsage));
//			PMListUsageWriter.write("\r\n");
//			PMListUsageWriter.flush();
			PMUtilizationWriter.write("\r\n");
			PMUtilizationWriter.flush();
//			PMListCountWriter.write("\r\n");
//			PMListCountWriter.flush();

		}

		System.out.println("Done.");
		long endTime = System.currentTimeMillis();
		System.out.println("Time cost:" + (endTime - startTime) / 1000f);
	}

	public static void tasksAllocation(List<String[]> jobList) {
		List<String[]> currentJobs = new ArrayList<String[]>();

		// CPUStorage [PMid, job(task count), total usage]
		List<String[]> CPUStorage = new ArrayList<String[]>();

		for (int t = 0; t < 301; t++) {

			System.out.println("Processing time slot" + t + "...");

			currentJobs = new ArrayList<String[]>();
			String jobID = "";
			String[] tasks;
			String taskID = "";
			String taskRequest = "";

			for (int i = 0; i < jobList.size(); i++) {
				jobID = jobList.get(i)[0];
				// get task id of the time slot
				tasks = regularExpTasks(jobList.get(i)[1], t).split(";");
				// if (task.length > 1) {
				// taskID = task[0];
				// taskRequest = task[1];
				// String[] tasks = taskID.split(";");
				for (int j = 0; j < tasks.length; j++) {
					taskID = tasks[j].split("\\*")[0];
					taskRequest = tasks[j].split("\\*")[1];
					String[] s = new String[] { jobID, taskID, taskRequest };
					if (!isContains(currentJobs, s)) {
						currentJobs.add(s);

					}
				}
			}

			CPUStorage = getFFD(t, currentJobs, CPUStorage);

			PMList.add(CPUStorage);
			CPUStorage = new ArrayList<String[]>();

		}
	}

	public static boolean isContains(List<String[]> list, String[] s2) {
		boolean flag = false;
		for (String[] s1 : list) {
			if (Arrays.equals(s1, s2)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static List<String[]> getFFD(Integer timeSlot, List<String[]> item, List<String[]> CPUStorage) {
		double storage = 0.0;
		boolean isFind;

		for (int i = 0; i < item.size(); i++) {
			isFind = false;

			double CPU_Total = 0;
			String jobID = item.get(i)[0];
			String taskID = item.get(i)[1];
			double CPURequest = Double.parseDouble(item.get(i)[2]);

			for (int j = 0; j < CPUStorage.size(); j++) {
				storage = getPMStorage(j);

				CPU_Total = Double.parseDouble(CPUStorage.get(j)[2]) + CPURequest;
				if (CPU_Total <= storage) {
					CPUStorage.set(j,
							new String[] { CPUStorage.get(j)[0],
									CPUStorage.get(j)[1] + "." + jobID + "(" + taskID + ")",
									String.valueOf(CPU_Total) });
					isFind = true;
					break;
				}
			}

			if (!isFind) {
				String PIndex = "";
				int PMIndex = CPUStorage.size();
				PIndex = String.valueOf(PMIndex);
				CPUStorage.add(new String[] { PIndex, jobID + "(" + taskID + ")", String.valueOf(CPURequest) });

			}

		}
		return CPUStorage;
	}

	public static double getPMStorage(int index) {
		if (index < LARGE_COUNT) {
			return LARGE_STORAGE * 0.85;
		} else if (index < LARGE_COUNT + MEDIUM_COUNT) {
			return MEDIUM_STORAGE * 0.85;
		} else {
			return TINY_STORAGE * 0.85;
		}
	}

	public static String regularExpTasks(String s, int index) {
		int taskIndex = 0;
		String returnValue = "";
		Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			if (taskIndex == index) {
				returnValue = matcher.group(0);
				break;
			}
			taskIndex++;
		}
		return returnValue;
	}
	
	public static void readPermanentJobs(String path) throws IOException {
		System.out.println("Read " + path + "...");
		BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
		String line;
		String[] row = new String[5];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			if (Long.parseLong(row[3]) <= 51000000000L && Long.parseLong(row[4]) >= 141000000000L) {
				permanentID.add(row[0]);
			}

		}
		System.out.println("Finished reading permanent jobs.");
	}
	
	public static int getAppearTimes(String str, String ch) {
		int index = 0;
		int count = 0;
		while ((index = str.indexOf(ch)) != -1) {
			str = str.substring(index + ch.length(), str.length());
			count++;
		}
		return count;
	}
}
