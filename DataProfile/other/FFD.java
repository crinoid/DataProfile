import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

public class FFD {
	final static float MAX_STORAGE = (float) 0.5;

	List<String> permanentID = new ArrayList<String>(); // permanent job
														// id
	List<String[]> jobList = new ArrayList<String[]>();
	List<String[]> permanentJobs = new ArrayList<String[]>();
	List<String[]> normalJobs = new ArrayList<String[]>();

	List<String[]> PMList = new ArrayList<String[]>();
	List<Integer> PMCountList = new ArrayList<Integer>();
	
	static int PMCount;

	public static void main(String[] args) throws IOException {
		PMCount=0;
		List<Integer> maxCPUUsage = new ArrayList<Integer>();
		maxCPUUsage=readJobDistribution("job_distribution.csv");
		
		FFD f = new FFD();
		readPermanentJobs(f, "output.csv");
		readTasks(f, "output7.csv");
		
		List<String[]> currentPermanentJobs = new ArrayList<String[]>();
		List<String[]> currentNormalJobs = new ArrayList<String[]>();
		
		List<String[]> oldJobList = new ArrayList<String[]>();
		List<String[]> newJobList = new ArrayList<String[]>();
		
		// CPUStorage [PMid, job(task count), total usage]
		List<String[]> CPUStorage = new ArrayList<String[]>();
		List<String[]> CPUStorage_Init = new ArrayList<String[]>();
		
		String[] each_PM = new String[f.jobList.size()]; // store PMs for
		// each timeslot
		
		for (int t = 0; t < 1; t++) {
			System.out.println("Processing time slot"+t);
			int tasks = 0;
			currentPermanentJobs= new ArrayList<String[]>();
			for (int i = 0; i < f.permanentJobs.size(); i++) {
				// has tasks in the time slot
				tasks = Integer.parseInt(f.permanentJobs.get(i)[1].split("\\.")[t]);
				if (tasks != 0) {
					currentPermanentJobs.add(new String[] { f.permanentJobs.get(i)[0], String.valueOf(tasks),
							f.permanentJobs.get(i)[2],f.permanentJobs.get(i)[3] });
				}
			}
			oldJobList = currentPermanentJobs;
			currentPermanentJobs = BubbleSort(currentPermanentJobs, 2);

			CPUStorage = getFFD(currentPermanentJobs, CPUStorage);
			CPUStorage_Init=CPUStorage;
			List<String[]> eachAllocation = CPUStorage;

			for (int i = 0; i < eachAllocation.size(); i++) {
				// get jobs
				for (String s : eachAllocation.get(i)[1].split(",")) {
					if (!s.equals("")) {
						int index = 0; // job index in jobList

						for (String[] job : f.jobList) {
							if (s.contains(job[0])) {
								break;
							}
							index++;
						}

						// set PMID
						String[] t1 = s.split("\\(");
						if (index < each_PM.length) {
							String cur_PM = String.valueOf(eachAllocation.get(i)[0]) + "(" + t1[1];
							String exist_PM = each_PM[index] == null ? "" : each_PM[index] + ",";
							each_PM[index] = exist_PM + cur_PM;
						}
					}
				}
			}
			f.PMList.add(each_PM);
			f.PMCountList.add(getPMCount(CPUStorage));
		}
		newJobList=currentPermanentJobs;
		for (int t = 1; t < 10; t++) {
			System.out.println("Processing permanent task time slot"+t);
			int tasks = 0;
			currentPermanentJobs = new ArrayList<String[]>();
			for (int i = 0; i < f.permanentJobs.size(); i++) {
				tasks = Integer.parseInt(f.permanentJobs.get(i)[1].split("\\.")[t]);
				// has tasks in the time slot
				if (tasks != 0) {
					currentPermanentJobs.add(new String[] { f.permanentJobs.get(i)[0], String.valueOf(tasks),
							f.permanentJobs.get(i)[2],f.permanentJobs.get(i)[3] });
					for (int j = 0; j < oldJobList.size(); j++) {
						if (oldJobList.get(j)[0].equals(currentPermanentJobs.get(currentPermanentJobs.size()-1)[0])) {
							// repectiveJobs.add(oldJobList.get(j));
							String[] s = oldJobList.get(j);

							int index = Integer.parseInt(s[3]); //job index for PMList
							List<Integer> taskCountList = regularExpTasks(each_PM[index]);
							List<Integer> PMIndexList = regularExpPMs(each_PM[index]);

							for (int m = 0; m < taskCountList.size(); m++) {
								tasks = tasks - taskCountList.get(m);
								if (tasks <= 0) {
									break;
								} else {
									CPUStorage.set(PMIndexList.get(m), new String[] {
											String.valueOf(PMIndexList.get(m)),
											oldJobList.get(j)[0] + "(" + taskCountList.get(m) + "),", String.valueOf(
													taskCountList.get(m) * Float.valueOf(oldJobList.get(j)[2])) });
								}
							}
							if (tasks > 0) {
								currentPermanentJobs.set(currentPermanentJobs.size()-1,new String[]{ f.permanentJobs.get(i)[0], String.valueOf(tasks),
										f.permanentJobs.get(i)[2]});
							}
							else {
								currentPermanentJobs.remove(currentPermanentJobs.size()-1);
							}						
							oldJobList.remove(j);

							break;
						}
					}
				}
			}
			
			oldJobList=newJobList;	
			currentPermanentJobs = BubbleSort(currentPermanentJobs, 2);
			
			CPUStorage = getFFD(currentPermanentJobs, CPUStorage);
			List<String[]> eachAllocation = CPUStorage;
			each_PM = new String[f.jobList.size()]; // store PMs for
																// each timeslot
			for (int i = 0; i < eachAllocation.size(); i++) {
				// get jobs
				for (String s : eachAllocation.get(i)[1].split(",")) {
					if (!s.equals("")) {
						int index = 0; // job index in jobList

						for (String[] job : f.jobList) {
							if (s.contains(job[0])) {
								break;
							}
							index++;
						}

						// set PMID
						String[] t1 = s.split("\\(");
						if (index < each_PM.length) {
							String cur_PM = String.valueOf(eachAllocation.get(i)[0]) + "("+ t1[1];
							String exist_PM = each_PM[index] == null ? "" : each_PM[index] + ",";
							each_PM[index] = exist_PM + cur_PM;
						}
					}
				}
			}
			f.PMList.add(each_PM);
			f.PMCountList.add(getPMCount(CPUStorage));
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("PMList1.csv")));
		for (int i=0;i<f.PMList.size();i++) {
			for (int j = 0; j < f.PMList.get(i).length; j++) {
				bw.write(f.PMList.get(i)[j]+",");
			}
			bw.write("\r\n");
			bw.flush();
		}
		bw = new BufferedWriter(new FileWriter(new File("PMListCount.csv")));
		for (int i = 0; i < f.PMCountList.size(); i++) {
			bw.write(f.PMCountList.get(i));

			bw.write("\r\n");
			bw.flush();
		}
		
		currentPermanentJobs=null;
		
		for (int t = 0; t < 1; t++) {
			System.out.println("Processing normal tasks time slot" + t);
			// normal tasks
			int tasks = 0;
			for (int i = 0; i < f.normalJobs.size(); i++) {
				// has tasks in the time slot
				tasks = Integer.parseInt(f.normalJobs.get(i)[1].split("\\.")[t]);
				if (tasks != 0) {
					currentNormalJobs.add(new String[] { f.normalJobs.get(i)[0], String.valueOf(tasks),
							f.normalJobs.get(i)[2], f.normalJobs.get(i)[3] });
				}
			}

			oldJobList = currentNormalJobs;
			currentNormalJobs = BubbleSort(currentNormalJobs, 2);

			CPUStorage = CPUStorage_Init;

			CPUStorage = getFFD(currentNormalJobs, CPUStorage);
			List<String[]> eachAllocation = CPUStorage;

			for (int i = 0; i < eachAllocation.size(); i++) {
				// get jobs
				for (String s : eachAllocation.get(i)[1].split(",")) {
					if (!s.equals("")) {
						int index = 0; // job index in jobList

						for (String[] job : f.jobList) {
							if (s.contains(job[0])) {
								break;
							}
							index++;
						}

						// set PMID
						String[] t1 = s.split("\\(");
						if (index < each_PM.length) {
							String cur_PM = String.valueOf(eachAllocation.get(i)[0]) + "(" + t1[1];
							String exist_PM = each_PM[index] == null ? "" : each_PM[index] + ",";
							each_PM[index] = exist_PM + cur_PM;
						}
					}
				}
			}
			f.PMList.set(t, each_PM);
			f.PMCountList.add(getPMCount(CPUStorage));
		}
		newJobList=currentNormalJobs;
		for (int t = 1; t < 10; t++) {
			System.out.println("Processing normal tasks time slot" + t);
			int tasks = 0;
			currentNormalJobs = new ArrayList<String[]>();
			for (int i = 0; i < f.normalJobs.size(); i++) {
				tasks = Integer.parseInt(f.normalJobs.get(i)[1].split("\\.")[t]);
				// has tasks in the time slot
				if (tasks != 0) {
					currentNormalJobs.add(new String[] { f.normalJobs.get(i)[0], String.valueOf(tasks),
							f.normalJobs.get(i)[2],f.normalJobs.get(i)[3] });
					for (int j = 0; j < oldJobList.size(); j++) {
						if (oldJobList.get(j)[0].equals(currentNormalJobs.get(currentNormalJobs.size()-1)[0])) {
							String[] s = oldJobList.get(j);

							int index = Integer.parseInt(s[3]); //job index for PMList
							List<Integer> taskCountList = regularExpTasks(each_PM[index]);
							List<Integer> PMIndexList = regularExpPMs(each_PM[index]);

							for (int m = 0; m < taskCountList.size(); m++) {
								tasks = tasks - taskCountList.get(m);
								if (tasks <= 0) {
									break;
								} else {
									CPUStorage.set(PMIndexList.get(m), new String[] {
											String.valueOf(PMIndexList.get(m)),
											oldJobList.get(j)[0] + "(" + taskCountList.get(m) + "),", String.valueOf(
													taskCountList.get(m) * Float.valueOf(oldJobList.get(j)[2])) });
								}
							}
							if (tasks > 0) {
								currentNormalJobs.set(currentNormalJobs.size()-1,new String[]{ f.normalJobs.get(i)[0], String.valueOf(tasks),
										f.normalJobs.get(i)[2]});
							}
							else {
								currentNormalJobs.remove(currentNormalJobs.size()-1);
							}
							
							oldJobList.remove(j);

							break;
						}
					}
				}
			}
			
			oldJobList=newJobList;	
			currentNormalJobs = BubbleSort(currentNormalJobs, 2);
			
			CPUStorage = getFFD(currentNormalJobs, CPUStorage);
			List<String[]> eachAllocation = CPUStorage;
			each_PM = new String[f.jobList.size()]; // store PMs for
																// each timeslot
			for (int i = 0; i < eachAllocation.size(); i++) {
				// get jobs
				for (String s : eachAllocation.get(i)[1].split(",")) {
					if (!s.equals("")) {
						int index = 0; // job index in jobList

						for (String[] job : f.jobList) {
							if (s.contains(job[0])) {
								break;
							}
							index++;
						}

						// set PMID
						String[] t1 = s.split("\\(");
						if (index < each_PM.length) {
							String cur_PM = String.valueOf(eachAllocation.get(i)[0]) + "("+ t1[1];
							String exist_PM = each_PM[index] == null ? "" : each_PM[index] + ",";
							each_PM[index] = exist_PM + cur_PM;
						}
					}
				}
			}
			f.PMList.set(t, each_PM);
			f.PMCountList.add(getPMCount(CPUStorage));
		}
		
		bw = new BufferedWriter(new FileWriter(new File("PMList.csv")));
		for (int i=0;i<f.PMList.size();i++) {
			for (int j = 0; j < f.PMList.get(i).length; j++) {
				bw.write(f.PMList.get(i)[j]+",");
			}
			bw.write("\r\n");
			bw.flush();
		}



//		tasksAllocation(f, f.permanentJobs,"permanent tasks");
		
//		currentPermanentJobs=null;
		
//		tasksAllocation(f, f.normalJobs,"normal tasks");
		

		System.out.println("Done.");
		
	}
	
	public static void tasksAllocation(FFD f, List<String[]> jobs,String taskType) {

		List<String[]> currentJobs = new ArrayList<String[]>();
		
		List<String[]> oldJobList = new ArrayList<String[]>();
		List<String[]> newJobList = new ArrayList<String[]>();
		
		// CPUStorage [PMid, job(task count), total usage]
		List<String[]> CPUStorage = new ArrayList<String[]>();
		List<String[]> CPUStorage_Init = new ArrayList<String[]>();
		
		String[] each_PM = new String[f.jobList.size()]; // store PMs for
		// each timeslot

		for (int t = 0; t < 10; t++) {
			System.out.println("Processing "+taskType+" time slot"+t);
			int tasks = 0;
			currentJobs = new ArrayList<String[]>();
			for (int i = 0; i < jobs.size(); i++) {
				tasks = Integer.parseInt(jobs.get(i)[1].split("\\.")[t]);
				// has tasks in the time slot
				if (tasks != 0) {
					currentJobs.add(
							new String[] { jobs.get(i)[0], String.valueOf(tasks), jobs.get(i)[2], jobs.get(i)[3] });
					if (t > 0) {
						for (int j = 0; j < oldJobList.size(); j++) {
							if (oldJobList.get(j)[0].equals(currentJobs.get(currentJobs.size() - 1)[0])) {
								String[] s = oldJobList.get(j);

								int index = Integer.parseInt(s[3]); // job index
																	// for
																	// PMList
								if (t==4 && index==129) {
									 System.out.println(index);
								}
								List<Integer> taskCountList = regularExpTasks(each_PM[index]);
								List<Integer> PMIndexList = regularExpPMs(each_PM[index]);

								for (int m = 0; m < taskCountList.size(); m++) {
									tasks = tasks - taskCountList.get(m);
									if (tasks <= 0) {
										break;
									} else {
										CPUStorage.set(PMIndexList.get(m), new String[] {
												String.valueOf(PMIndexList.get(m)),
												oldJobList.get(j)[0] + "(" + taskCountList.get(m) + "),",
												String.valueOf(
														taskCountList.get(m) * Float.valueOf(oldJobList.get(j)[2])) });
									}
								}
								if (tasks > 0) {
									currentJobs.set(currentJobs.size() - 1,
											new String[] { jobs.get(i)[0], String.valueOf(tasks), jobs.get(i)[2] });
								} else {
									currentJobs.remove(currentJobs.size() - 1);
								}
								oldJobList.remove(j);

								break;
							}
						}
					}
				}
			}
			
			oldJobList = newJobList;
			currentJobs = BubbleSort(currentJobs, 2);
			
			if (t==0 && taskType.equals("normal tasks")) {
				CPUStorage = CPUStorage_Init;
			}
			
			CPUStorage = getFFD(currentJobs, CPUStorage);
			if (t==0 && taskType.equals("permanent tasks")){
				CPUStorage_Init=CPUStorage;
			}
			
			List<String[]> eachAllocation = CPUStorage;
			each_PM = new String[f.jobList.size()]; // store PMs for
																// each timeslot
			for (int i = 0; i < eachAllocation.size(); i++) {
				// get jobs
				for (String s : eachAllocation.get(i)[1].split(",")) {
					if (!s.equals("")) {
						int index = 0; // job index in jobList

						for (String[] job : f.jobList) {
							if (s.contains(job[0])) {
								break;
							}
							index++;
						}

						// set PMID
						String[] t1 = s.split("\\(");
						if (index < each_PM.length) {
							String cur_PM = String.valueOf(eachAllocation.get(i)[0]) + "("+ t1[1];
							String exist_PM = each_PM[index] == null ? "" : each_PM[index] + ",";
							each_PM[index] = exist_PM + cur_PM;
						}
					}
				}
			}
			f.PMList.add(each_PM);
			newJobList=currentJobs;
		}
	}
	
	public static List<Integer> readJobDistribution(String path) throws IOException {
		List<Integer> cpu = new ArrayList<Integer>();
		System.out.println("Read " + path + "...");
		BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
		String line;
		while (((line = bf.readLine()) != null)) {
			cpu.add(Integer.parseInt(line.split(",")[1]));

		}
		System.out.println("Finished reading job distribution.");
		return cpu;
	}

	public static void readPermanentJobs(FFD f, String path) throws IOException {
		System.out.println("Read " + path + "...");
		BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
		String line;
		String[] row = new String[5];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			if (Long.parseLong(row[3]) <= 51000000000L && Long.parseLong(row[4]) >= 141000000000L) {
				f.permanentID.add(row[0]);
			}

		}
		System.out.println("Finished reading permanent jobs.");
	}

	public static void readTasks(FFD f, String path) throws IOException {
		System.out.println("Read " + path + "...");
		BufferedReader bf = new BufferedReader(new FileReader(new File(path)));
		String line;
		int jobIndex=0;
		String[] row = new String[3];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			f.jobList.add(row);
			int i = 0;
			for (; i < f.permanentID.size(); i++) {
				if (line.split(",")[0].equals(f.permanentID.get(i))) {
					f.permanentJobs.add(new String[]{row[0],row[1],row[2],String.valueOf(jobIndex)});
					break;
				}
			}
			if (i == f.permanentID.size()) {
				f.normalJobs.add(new String[]{row[0],row[1],row[2],String.valueOf(jobIndex)});
			}
			jobIndex++;
		}
		System.out.println("Finished reading tasks.");
	}

	public static List<String[]> getFFD(List<String[]> item_inTime, List<String[]> CPUStorage) {

		boolean isFind;

		for (int i = 0; i < item_inTime.size(); i++) {
			
			isFind = false;

			float CPU_Total = 0f;
			int task_count = 0;
			int task_total=Integer.parseInt(item_inTime.get(i)[1]);
			float each_request = 0f;
			int j = 0;
			float CPU_Used = 0;

			// cpu request for each task
			each_request = Float.parseFloat(item_inTime.get(i)[2]);
			
//			long jobid=Integer.parseInt(item_inTime.get(i)[0]);
			
			List<Integer> CPUIndexList = new ArrayList<Integer>();
			for (int k=0;k<CPUStorage.size();k++) {
				CPUIndexList.add(Integer.parseInt(CPUStorage.get(k)[0]));
			}
			
			while (!(task_total == 0 || j == CPUStorage.size())){
				// how much CPU left
				CPU_Total = MAX_STORAGE - Float.parseFloat(CPUStorage.get(j)[2]);
				// how many tasks can be placed in the current PM
				task_count = (int) (CPU_Total / each_request);
				if (task_count > task_total) {
					task_count = task_total;
				}
				CPU_Used = Float.parseFloat(CPUStorage.get(j)[2]) + task_count * each_request;
				// how many tasks left
				task_total = task_total - task_count;
				if (task_count != 0) {
					String[] s = { CPUStorage.get(j)[0],
							CPUStorage.get(j)[1] + item_inTime.get(i)[0] + "(" + task_count + "),",
							String.valueOf(CPU_Used) };
					CPUStorage.set(j, s);
				}
				j++;
			}
			if (task_total == 0 && j>0)
				isFind = true;

			if (!isFind) {
				// add the new value to CPUStorage
				int newIndex=0;
				do {
					while(CPUIndexList.contains(newIndex)) {
						newIndex++;
					}
					CPUIndexList.add(newIndex);
					
//					j = newIndex;
					CPUStorage.add(new String[] { String.valueOf(newIndex), "", "0" });
//					PMCount++;
					task_count = (int) (MAX_STORAGE / each_request);
					if (task_count > task_total) {
						task_count = task_total;
					}
					CPU_Used = task_count * each_request;
					task_total = task_total - task_count;
					if (task_count != 0) {
						String[] s = { CPUStorage.get(j)[0],
								CPUStorage.get(newIndex)[1] + item_inTime.get(i)[0] + "(" + task_count + "),",
								String.valueOf(CPU_Used) };
						CPUStorage.set(newIndex, s);
					}
				} while (task_total > 0);
			}

		}
		return CPUStorage;
	}
	
	public static int getPMCount(List<String[]> CPUStorage) {
		int count=0;
		for (int i=0;i<CPUStorage.size();i++) {
			if (CPUStorage.get(i)!=null) {
				count++;
			}
		}
		return count;
	}

	public static List<String[]> BubbleSort(List<String[]> a, int index) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < a.size() - i - 1; j++) {
				if (Float.parseFloat(a.get(j)[index]) < Float.parseFloat(a.get(j + 1)[index])) {
					String[] temp = a.get(j);
					a.set(j, a.get(j + 1));
					a.set(j + 1, temp);
				}
			}
		}
		return a;
	}
	
	// get task count in each PM(each bracket) of one job
	public static List<Integer> regularExpTasks(String s) {
		List<Integer> str = new ArrayList<Integer>();
		Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			str.add(Integer.parseInt(matcher.group(0)));
		}
		return str;
	}
	
	public static List<Integer> regularExpPMs(String s) {
		List<Integer> str = new ArrayList<Integer>();
		Pattern pattern = Pattern.compile("([0-9]*)\\(");
		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			str.add(Integer.parseInt(matcher.group(1));
		}
		return str;
	}

}
