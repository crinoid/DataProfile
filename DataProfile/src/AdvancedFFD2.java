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

public class AdvancedFFD2 {
	final static double LARGE_STORAGE = 1.5;
	final static int LARGE_COUNT = 0;
	final static double MEDIUM_STORAGE = 1.0;
	final static int MEDIUM_COUNT = 2;
	final static double TINY_STORAGE = 0.5;

	static List<String> permanentID = new ArrayList<String>();
	static List<String[]> jobList = new ArrayList<String[]>();
	static List<String[]> permanentJobs = new ArrayList<String[]>();
	static List<String[]> normalJobs = new ArrayList<String[]>();

	List<List<String[]>> PMList = new ArrayList<List<String[]>>();

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		AdvancedFFD2 f = new AdvancedFFD2();
		readPermanentJobs("output.csv");
		readTasks("output1000.csv");

		tasksAllocation(f, permanentJobs, "permanent");
		
//		BufferedWriter pmlist = new BufferedWriter(new FileWriter(new File("permanents1000.csv")));
//		for (int i = 0; i < f.PMList.size(); i++) {
//			pmlist.write(String.valueOf(50700 + 300 * i) + ",");
//			for (int k = 0; k < permanentID.size(); k++) {
//				pmlist.write(permanentID.get(k) + "(");
//				for (int j = 0; j < f.PMList.get(i).size(); j++) {
//					if (f.PMList.get(i).get(j)[1].contains(permanentID.get(k))) {
//						int count = getAppearTimes(f.PMList.get(i).get(j)[1], permanentID.get(k));
//						pmlist.write(f.PMList.get(i).get(j)[0] + ":" + String.valueOf(count) + ";");
//					}
//				}
//				pmlist.write("),");
//			}
//			pmlist.write("\r\n");
//			pmlist.flush();
//		}
	
	
		tasksAllocation(f, normalJobs, "normal");

		BufferedWriter PMListCountWriter = new BufferedWriter(new FileWriter(new File("PMNumber200.csv")));
		BufferedWriter PMUtilizationWriter = new BufferedWriter(new FileWriter(new File("PMUtilization200.csv")));
		BufferedWriter PMListUsageWriter = new BufferedWriter(new FileWriter(new File("PMUsage200.csv")));
//		BufferedWriter PermanentJobsDistribuition = new BufferedWriter(new FileWriter(new File("permanents.csv")));
		double totalUsage = 0.0;
//		int count=0;
		for (int i = 0; i < f.PMList.size(); i++) {
			for (int j = 0; j < f.PMList.get(i).size(); j++) {
				for(int k=0;k<permanentID.size();k++) {
					if (f.PMList.get(i).get(j)[1].contains(permanentID.get(k))) {
//						PermanentJobsDistribuition.write(f.PMList.get(i).get(j)[0]);
//					    int index=0; 
//					    String key=permanentID.get(i);
//					    String str=f.PMList.get(i).get(j)[1];
//						while((index=str.indexOf(key))!=-1)  
//					        {  
//					            str = str.substring(index+key.length());  
//					            count++;      
//					        }  
					}
				}
			}
			PMListCountWriter.write(String.valueOf(f.PMList.get(i).size()));
			totalUsage = 0.0;
			for (int j = 0; j < f.PMList.get(i).size(); j++) {
				double maxStorage = getPMStorage(Integer.parseInt(f.PMList.get(i).get(j)[0]));
				double usage = Double.parseDouble(f.PMList.get(i).get(j)[2]);
				double rate = usage / maxStorage;
				if (rate > 1) {
					System.out.println(String.valueOf(i)+","+String.valueOf(j)+"error");
				}
				PMUtilizationWriter.write(String.valueOf(rate) + ",");
				for (int k = 0; k < f.PMList.get(i).get(j).length; k++) {
					totalUsage += Double.parseDouble(f.PMList.get(i).get(j)[2]);
				}
			}
			PMListUsageWriter.write(String.valueOf(totalUsage));
			PMListUsageWriter.write("\r\n");
			PMListUsageWriter.flush();
			PMUtilizationWriter.write("\r\n");
			PMUtilizationWriter.flush();
			PMListCountWriter.write("\r\n");
			PMListCountWriter.flush();
	}

		System.out.println("Done.");

		long endTime = System.currentTimeMillis();
		System.out.println("Time cost:" + (endTime - startTime) / 1000f);
	}

	public static void tasksAllocation(AdvancedFFD2 f, List<String[]> jobList, String jobType) {
		List<String[]> currentJobs = new ArrayList<String[]>();
		List<String[]> lastJobList = new ArrayList<String[]>();

		// CPUStorage [PMid, job(task), total usage]
		List<String[]> CPUStorage = new ArrayList<String[]>();
		List<String[]> CPUStorageCurrent = new ArrayList<String[]>();

		for (int t = 0; t < 301; t++) {
			if (jobType.equals("normal")) {
				CPUStorage = f.PMList.get(t);
				if (t==237) {
					System.out.println("PPP");
				}
			}

			System.out.println("Processing " + jobType + " time slot" + t + "...");

			currentJobs = new ArrayList<String[]>();
			String jobID = "";
			String[] tasks;
			String taskID = "";
			String taskRequest = "";
//			List<List<String>> repeatNum = new ArrayList<List<String>>();
			for (int i = 0; i < jobList.size(); i++) {
				jobID = jobList.get(i)[0];

				// get task id of the time slot
				tasks = regularExpTasks(jobList.get(i)[1], t).split(";");
				// taskID = regularExpTasks(jobList.get(i)[1], t);
				// taskRequest = jobList.get(i)[2];
				// String[] tasks = taskID.split(";");
				List<Integer> removeIndex = new ArrayList<Integer>();
				for (int j = 0; j < tasks.length; j++) {
					

					
					taskID = tasks[j].split("\\*")[0];
					taskRequest = tasks[j].split("\\*")[1];
					String[] s = new String[] { jobID, taskID, taskRequest };

					if (!isContains(currentJobs, s)) {
//						currentJobs.add(s);
						
						if (jobType.equals("normal")) {
							if (!repeatTimes(jobList.get(i), tasks[j], t).split(",")[0].equals("0")) {
								if (jobList.get(i).equals("6221861800")) {
									System.out.println("6221861800");
								}
								System.out.println(jobList.get(i)+","+tasks[j]+","+String.valueOf(t));
							}
							String []repeatTime=repeatTimes(jobList.get(i), tasks[j], t).split(",");
							s = new String[3+repeatTime.length];
							s[0]=jobID;s[1]=taskID;s[2]=taskRequest;
							for (int n=3;n<s.length;n++) {
								s[n]=repeatTime[n-3];
							}
							currentJobs.add(s);
//							repeatNum.add(repeatTimes(jobList.get(i), tasks[j], t));
						} else {
							s = new String[4];
							s[0]=jobID;s[1]=taskID;s[2]=taskRequest;s[3]="0";
							currentJobs.add(s);
						}

						for (int k = 0; k < lastJobList.size(); k++) {
							if (lastJobList.get(k)[0].equals(jobID)) {
								if (lastJobList.get(k)[1].equals(tasks[j].split("\\*")[0])) {
									// find repetitive task
									CPUStorageCurrent = f.PMList.get(t - 1);
									for (int m = 0; m < CPUStorageCurrent.size(); m++) {
										if (CPUStorageCurrent.get(m)[1].contains(jobID + "(" + tasks[j].split("\\*")[0] + ")")) {
											String jobTask = "";
											if (CPUStorage.size() > m) {
												jobTask = CPUStorage.get(m)[1] + ".";
											}
											System.out.println(jobID + "(" + tasks[j] + ")");
											if (jobType.equals("normal")) {
												int n = 0;
												for (; n < CPUStorage.size(); n++) {
													if (CPUStorage.get(n)[0].equals(CPUStorageCurrent.get(m)[0])) {
														break;
													}
												}
												if (n < CPUStorage.size()) {
													double usage = Double.parseDouble(taskRequest)
															+ Double.parseDouble(CPUStorage.get(n)[2]);
						
													CPUStorage.set(n,
															new String[] {
																	CPUStorageCurrent.get(m)[0], CPUStorage.get(n)[1]
																			+ "." + jobID + "(" + tasks[j] + ")",
																	String.valueOf(usage) });
												}
											} else {
												CPUStorage.add(new String[] { CPUStorageCurrent.get(m)[0],
														jobTask + jobID + "(" + tasks[j] + ")",
														String.valueOf(taskRequest) });
											}
											removeIndex.add(currentJobs.size() - 1);
											break;
										}
									}
								}
							}
						}
					}
					List<String[]> cc=new ArrayList<String[]>();
					for (int k = 0; k < currentJobs.size(); k++) {
					 if(removeIndex.indexOf(k)==-1) {
						 cc.add(currentJobs.get(k));
					 }
					}
					currentJobs=cc;
//					for (int k = 0; k < removeIndex.size(); k++) {
//						currentJobs.remove(removeIndex.get(k));
//					}
				}
			}

			lastJobList = currentJobs;
			if (CPUStorage.size() > 0) {
				CPUStorage = BubbleSort(CPUStorage, 2);
			}
			currentJobs = BubbleSort(currentJobs, 2);
			CPUStorage = getFFD(f, t, currentJobs, CPUStorage, jobType);

			if (jobType.equals("permanent")) {
				f.PMList.add(CPUStorage);
			}
			CPUStorage = new ArrayList<String[]>();

		}
	}

	public static boolean isContains(List<String[]> list, String[] s2) {
		boolean flag = false;
		for (String[] s1 : list) {
			if(s1[0].equals(s2[0]) &&s1[1].equals(s2[1])&&s1[2].equals(s2[2]) ){
//			if (Arrays.equals(s1, s2)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static String repeatTimes(String[] items, String task, int index) {
//		List<String> d = new ArrayList<String>(); //count, each cpu request
		String d="";
		int count = 0;
//		d.add("0");
		boolean isFind = false;
		String taskID=task.split("\\*")[0]; //taskID
		List<String> tasks=regularExpTasks1(items[1]);
		for (int i=index + 1;i<tasks.size();i++)
		{
//		String[] tasks = items[1].split("\\."); // get taskcount(task.n)
//		for (int i = index + 1; i < tasks.length; i++) {
			isFind = false;
			String[] t = tasks.get(i).split(";"); // get each task
			int j=0;
			for (; j < t.length; j++) {
				if (t[j].split("\\*")[0].equals(taskID)) {
					isFind = true;
					break;
				}
			}
			if (isFind) {
//				d.add(t[j].split("\\*")[1]);
				d+=","+(t[j].split("\\*")[1]);
				count++;
			} else {
				break;
			}
		}
//		d.set(0, String.valueOf(count));
		d=String.valueOf(count)+d;
		return d;

	}

	public static List<String[]> getFFD(AdvancedFFD2 f, Integer timeSlot, List<String[]> item, List<String[]> CPUStorage,
			String type) {
		double storage = 0.0;
		double CPUTotal;
		boolean isFind;

		for (int i = 0; i < item.size(); i++) {
			isFind = false;

			CPUTotal = 0;

			String jobID = item.get(i)[0];
			String taskID = item.get(i)[1];
			double CPURequest = Double.parseDouble(item.get(i)[2]);

			List<String> PMid = new ArrayList<String>();

			for (int j = 0; j < CPUStorage.size(); j++) {
				storage = getPMStorage(Integer.valueOf(CPUStorage.get(j)[0]));

				CPUTotal = Double.parseDouble(CPUStorage.get(j)[2]) + CPURequest;
				if (CPUTotal <= storage) {

					boolean isValid = true;
					if (type.equals("normal")) {
						// for repetitive tasks, check the following PM storage
						for (int t = 0; t < Integer.valueOf(item.get(i)[3]); t++) {
							for (int k = 0; k < f.PMList.get(timeSlot + 1 + t).size(); k++) {
								if (f.PMList.get(timeSlot + 1 + t).get(k)[0].equals(CPUStorage.get(j)[0])) {
									if (Double.parseDouble(f.PMList.get(timeSlot + 1 + t).get(k)[2])
											+ Double.valueOf(item.get(i)[4+t]) > getPMStorage(
													Integer.parseInt(f.PMList.get(timeSlot + 1 + t).get(k)[0]))) {
										isValid = false;
										break;
									}
								}
							}
						}
					}
					if (isValid) {
						CPUStorage.set(j,
								new String[] { CPUStorage.get(j)[0],
										CPUStorage.get(j)[1] + "." + jobID + "(" + taskID + ")",
										String.valueOf(CPUTotal) });
						isFind = true;
						break;
					}
				}
				if (!PMid.contains(CPUStorage.get(j)[0]))
					PMid.add(CPUStorage.get(j)[0]);
			}

			if (!isFind) {
				String PIndex = "";
//				int PMIndex = type.equals("normal") ? LARGE_COUNT + MEDIUM_COUNT : 0;
				int PMIndex=0;
				while (PMid.contains(String.valueOf(PMIndex)) || PMid.contains("N" + String.valueOf(PMIndex))) {
					PMIndex++;
				}
				if (type.equals("normal") && Integer.valueOf(item.get(i)[3])> 0) {
					boolean isValid = false;
					for (; !isValid; PMIndex++) {
						boolean isFound=true;
						for (int t = 0; t < Integer.valueOf(item.get(i)[3]); t++) {
							for (int k = 0; k < f.PMList.get(timeSlot + 1 + t).size(); k++) {
//								if (timeSlot + 1 + t<f.PMList.size()){
								if (f.PMList.get(timeSlot + 1 + t).get(k)[0].equals(String.valueOf(PMIndex))) {
									if (Double.parseDouble(f.PMList.get(timeSlot + 1 + t).get(k)[2])
											+ Double.valueOf(item.get(i)[4+t]) > getPMStorage(
													Integer.parseInt(f.PMList.get(timeSlot + 1 + t).get(k)[0]))) {
										isFound=false;
										break;
									}
								}
							}
						}
						if (isFound) {
							isValid=true;
						}
					}
					
//					PIndex = String.valueOf(PMIndex + 1000);
				} else {
					
				}
				PIndex = String.valueOf(PMIndex);
				CPUStorage.add(new String[] { PIndex, jobID + "(" + taskID + ")", String.valueOf(CPURequest) });
				PMid.add(PIndex);
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

	public static void readTasks(String path) throws IOException {
		System.out.println("Read " + path + "...");
		BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
		String line;
		int jobIndex = 0;
		String[] row = new String[3];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			jobList.add(row);
			int i = 0;
			for (; i < permanentID.size(); i++) {
				if (line.split(",")[0].equals(permanentID.get(i))) {
					permanentJobs.add(new String[] { row[0], row[1] });
					break;
				}
			}
			if (i == permanentID.size()) {
				normalJobs.add(new String[] { row[0], row[1] });
			}
			jobIndex++;
		}
		System.out.println("Finished reading tasks.");
	}

	public static List<String[]> BubbleSort(List<String[]> a, int index) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < a.size() - i - 1; j++) {
				if (Double.parseDouble(a.get(j)[index]) < Double.parseDouble(a.get(j + 1)[index])) {
					String[] temp = a.get(j);
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
