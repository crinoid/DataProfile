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

public class AdvancedFFD {
	final static double LARGE_STORAGE = 1.5;
	final static int LARGE_COUNT = 10;
	final static double MEDIUM_STORAGE = 1.0;
	final static int MEDIUM_COUNT = 15;
	final static double TINY_STORAGE = 0.5;

	static List<String> permanentID = new ArrayList<String>();
//	static List<String[]> jobList = new ArrayList<String[]>();
//	static List<String[]> permanentJobs = new ArrayList<String[]>();
//	static List<String[]> normalJobs = new ArrayList<String[]>();

	static List<List<Double[]>> PMList = new ArrayList<List<Double[]>>();

	public static void main(String[] args) throws IOException {
		AdvancedFFD f = new AdvancedFFD();
//		readPermanentJobs("output.csv");
		
//		f.readTasks("task_simple_400.csv");
//		f.tasksAllocation(f.readTasks("task_simple_400.csv"), "permanent");

	}
	
	public void outputFiles(String[] fileName) throws IOException {
		BufferedWriter PMListCountWriter = new BufferedWriter(new FileWriter(new File(fileName[0])));
		BufferedWriter PMUtilizationWriter = new BufferedWriter(new FileWriter(new File(fileName[1])));
		BufferedWriter PMListUsageWriter = new BufferedWriter(new FileWriter(new File(fileName[2])));

		double totalUsage = 0.0;
		double totalRate=0.0;
		double rateAvg = 0.0;
		for (int i = 0; i < PMList.size(); i++) {
			rateAvg = 0.0;	
			totalRate=0.0;
			totalUsage = 0.0;
			for (int j = 0; j < PMList.get(i).size(); j++) {
				double maxStorage = getPMStorage(PMList.get(i).get(j)[0]);
				double usage = PMList.get(i).get(j)[1];
				double rate = usage / maxStorage;
				if (rate > 1) {
					System.out.println(String.valueOf(i) + "," + String.valueOf(j) + "error");
				}
				totalRate+=rate;
				PMUtilizationWriter.write(PMList.get(i).get(j)[0] + ":" + String.valueOf(rate) + ",");
				for (int k = 0; k < PMList.get(i).get(j).length; k++) {
					totalUsage += PMList.get(i).get(j)[1];
				}
			}
			rateAvg=totalRate/PMList.get(i).size();
			PMListCountWriter.write(String.valueOf(PMList.get(i).size()) + "," + String.valueOf(rateAvg));
			PMListCountWriter.write("\r\n");
			PMListCountWriter.flush();
			PMListUsageWriter.write(String.valueOf(totalUsage));
			PMListUsageWriter.write("\r\n");
			PMListUsageWriter.flush();
			PMUtilizationWriter.write("\r\n");
			PMUtilizationWriter.flush();


		}
	}
	
	public void tasksAllocation(List<String[]> jobList, String jobType) {
		List<String[]> currentTasks = new ArrayList<String[]>();
		List<String[]> lastJobList = new ArrayList<String[]>();

		// CPUStorage [PMid, total usage]
		List<Double[]> CPUStorage = new ArrayList<Double[]>();
		List<String[]> CPUStorageCurrent = new ArrayList<String[]>();
		

		for (int t = 0; t < 301; t++) {
			if (jobType.equals("normal")||jobType.equals("tiny")) {
				CPUStorage = PMList.get(t);
			}

			System.out.println("Processing " + jobType + " time slot" + t + "...");

			for (int i=0;i<jobList.get(t).length;i++) {
				String[] s=jobList.get(t)[i].split("\\*");
				if (!s[1].equals("0"))
					currentTasks.add(s);
			}

			if (CPUStorage.size() > 0) {
				CPUStorage = BubbleSort(CPUStorage, 1);
			}

			CPUStorage = getFFD(currentTasks,CPUStorage);

			if (jobType.equals("permanent")) {
				PMList.add(CPUStorage);
				CPUStorage = new ArrayList<Double[]>();
			}

			currentTasks.clear();
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

	public static List<Double[]> getFFD(List<String[]> item, List<Double[]> CPUStorage) {
		
		Double total = 0.0;
		Double vm = 0.0;
		int count = 0;
		for (int i = 0; i < item.size(); i++) {
			vm = Double.valueOf(item.get(i)[0]);
			count = Integer.valueOf(item.get(i)[1]);
			for (int j = 0; j < CPUStorage.size(); j++) {
				total = getPMStorage(CPUStorage.get(j)[0]) - CPUStorage.get(j)[1];
				int number = (int) (total / vm);
				count -= number;
				total = CPUStorage.get(j)[1] + number * vm;
				CPUStorage.set(j, new Double[] { CPUStorage.get(j)[0], total });
				if (count <= 0) {
					break;
				}
			}
			while (count > 0) {
				total = getPMStorage((double) CPUStorage.size());
				int number = (int) (total / vm);
				count -= number;
				if (count < 0)
					number = number + count;
				total = number * vm;
				Double[] d = { Double.valueOf(CPUStorage.size()), total };
				CPUStorage.add(d);
			}
		}

		return CPUStorage;
	}

	public static double getPMStorage(Double index) {
		if (index < LARGE_COUNT) {
			return LARGE_STORAGE;
		} else if (index < LARGE_COUNT + MEDIUM_COUNT) {
			return MEDIUM_STORAGE;
		} else {
			return TINY_STORAGE;
		}
	}

	public List<String[]> readTasks(String path) throws IOException {
		System.out.println("Read " + path + "...");
		List<String[]> jobList = new ArrayList<String[]>();
		BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
		String line;
		String[] row = new String[3];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			jobList.add(row);
		}
		System.out.println("Finished reading tasks.");
		return jobList;
	}

	public static List<Double[]> BubbleSort(List<Double[]> a, int index) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < a.size() - i - 1; j++) {
				if (a.get(j)[index] < a.get(j + 1)[index]) {
					Double[] temp = a.get(j);
					a.set(j, a.get(j + 1));
					a.set(j + 1, temp);
				}
			}
		}
		return a;
	}

	public static List<String[]> BubbleSortAsc(List<String[]> a, int index) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < a.size() - i - 1; j++) {
				if (Double.parseDouble(a.get(j)[index]) > Double.parseDouble(a.get(j + 1)[index])) {
					String[] temp = a.get(j);
					a.set(j, a.get(j + 1));
					a.set(j + 1, temp);
				}
			}
		}
		return a;
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
	
	public static List<String> regularExpTasks1(String s) {
//		int taskIndex = 0;
		List<String> returnValue = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			returnValue.add(matcher.group(0));
		}
		return returnValue;
	}
	
	public static List<Integer> regularExpPMs(String s) {
		List<Integer> str = new ArrayList<Integer>();
		Pattern pattern = Pattern.compile("([0-9]*)\\(");
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			str.add(Integer.parseInt(matcher.group(1)));
		}
		return str;
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
