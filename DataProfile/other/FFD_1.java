import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FFD_1 {
	final static float MAX_STORAGE = (float) 0.5;

	public static List<String[]> allJobs = new ArrayList<String[]>();
	List<String[]> jobList = new ArrayList<String[]>();
	List<String[]> permanentJobList = new ArrayList<String[]>();
	List<String> permanentJob = new ArrayList<String>(); //store all permanent job id
	List<String[]> PMList = new ArrayList<String[]>(); // time,jobs
	List<String[]> permanentPMs = new ArrayList<String[]>(); // PMs store permanent tasks
//	static List<Integer> time = new ArrayList<Integer>(); // 5 mins in 24 hour

	public static void main(String[] args) {
		FFD_1 ffd = new FFD_1();
		
		List<String[]> p1 = new ArrayList<String[]>();
		getItems(ffd, "output5.csv");
//		readFile("output.csv");
		List<Integer> request = readCPURequest("job_distribution.csv");
		int PMRequest;
		// timeAllocation();
		// items = BubbleSort(items,3);

		List<String[]> oldJobList = new ArrayList<String[]>();
		List<String[]> newJobList = new ArrayList<String[]>();
		List<String[]> repetitiveJobList = new ArrayList<String[]>();
		
		ffd.permanentPMs=PermanentJobsAllocation(ffd);
		
		int n = 0;
		PMRequest = request.get(n); // PM request of the time slot(n)
		oldJobList = getSpecificiedTime(ffd,(50700000000L + n * 300000000L), 50700000000L + (n + 1) * 300000000L);

		List<String[]> CPUStorage = ffd.permanentPMs;
//		CPUStorage = BubbleSort(CPUStorage, 2);
		List<String[]> oneTimeAllocation = getFFD(oldJobList, CPUStorage);
		String[] each_PM = new String[ffd.allJobs.size()]; // store PMs for each
														// timeslot
		for (int i = 0; i < oneTimeAllocation.size(); i++) {
			// get jobs
			for (String s : oneTimeAllocation.get(i)[1].split(",")) {
				if (!s.equals("")) {
					int index = 0; // job index in jobList

					for (String[] job : ffd.allJobs) {
						if (s.contains(job[0])) {
							break;
						}
						index++;
					}

					// set PMID
					String[] t = s.split("\\(");
					if (index<each_PM.length){
						String cur_PM = String.valueOf(oneTimeAllocation.get(i)[0])+"("+t[1];
						String exist_PM=each_PM[index]==null?"":each_PM[index]+"+";
						each_PM[index] = exist_PM+cur_PM;
					}
				}
			}
		}
		ffd.PMList.add(each_PM);

		for (n = 1; n < 3; n++) {
			PMRequest = request.get(n); // PM request of the current time slot
			newJobList = getSpecificiedTime(ffd,(50700000000L + n * 300000000L), 50700000000L + (n + 1) * 300000000L);
			for (String[] j1 : newJobList) {
				for (String[] j2 : oldJobList) {
					// get repetitive jobs
					if (j1[0].equals(j2[0]) && j1[0] != null) {
						repetitiveJobList.add(j1);
					}
				}
			}

			oldJobList = newJobList;

			CPUStorage = ffd.permanentPMs;
			
			 // repetitive jobs can be kept
			if (PMRequest >= repetitiveJobList.size()) {
//				for (int i = 0; i < request.get(n); i++) {
//					CPUStorage.add(new String[] { String.valueOf(CPUStorage.size()), "", "0" });
//				}

				for (String[] s : repetitiveJobList) {
//					int jobIndex = 0;
					int PMIndex = -1;
//					for (String[] job : ffd.jobList) {
//						if (s[0].equals(job[2])) {
//							break;
//						}
//						jobIndex++; // get job index
//					}
					// get each job's PM index
					int jobIndex=Integer.valueOf(s[3]);
					PMIndex = Integer.parseInt(String.valueOf(ffd.PMList.get(n - 1)[jobIndex]));

					// set value to CPUStorage
					float CPU_Total = Float.parseFloat(CPUStorage.get(PMIndex)[2])
							+ Float.parseFloat(repetitiveJobList.get(repetitiveJobList.indexOf(s))[1]);
					//job id, cpu total
					String[] CPU = new String[] { CPUStorage.get(PMIndex)[0],CPUStorage.get(PMIndex)[1] + "," + ffd.jobList.get(jobIndex)[2],
							String.valueOf(CPU_Total) };
					CPUStorage.set(PMIndex, CPU);
				}
				// generate a new job list does not contain repetitive jobs
				List<String[]> currentJobList = new ArrayList<String[]>();
				for (String[] s : oldJobList) {
					if (repetitiveJobList.indexOf(s) == -1) { // s not in the
																// list
						currentJobList.add(s);
					}
				}
				
				CPUStorage = BubbleSort(CPUStorage, 2); //sort by index 2, total
				oneTimeAllocation = getFFD(currentJobList, CPUStorage);
				each_PM = new String[ffd.jobList.size()];
				for (int i = 0; i < oneTimeAllocation.size(); i++) {
					for (String s : oneTimeAllocation.get(i)[1].split(",")) {
						if (!s.equals("")) {
							int index = 0;
							for (String[] job : ffd.jobList) {
								if (s.equals(job[2])) {
									break;
								}
								index++;
							}
							if (index < each_PM.length) {
								each_PM[index] = String.valueOf(oneTimeAllocation.get(i)[0]);
							}
						}
					}
				}
				ffd.PMList.add(each_PM);
			}
		}
		/*
		 * else { int maxSize=0; //size of CPUStorage for (int
		 * k=0;k<PMRequest;k++) { //keep first PMRequest jobs int jobIndex=0;
		 * int PMIndex=-1; for (String job : jobList) { if
		 * (newJobList.get(k)[0].equals(job)) { break; } jobIndex++; } } } }
		 */
//		outputToFile(ffd,"PM1.csv");
	}

	public static void getItems(FFD_1 ffd, String path) {
		try {
			@SuppressWarnings("resource")
			BufferedReader bf = new BufferedReader(new FileReader(path));
			String line;
			String[] row = new String[6];
			String curJob = "";
			while ((line = bf.readLine()) != null) {
				row = line.split(",");
				// add unique job
				if (!row[0].equals(curJob)) {
					ffd.jobList.add(row);
					curJob = row[0];
				}
			}
			ffd.allJobs=ffd.jobList;
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// get job id, tasks count, cpu request, job index in the time slot
	public static List<String[]> getSpecificiedTime(FFD_1 ffd,Long t1, Long t2) {
		List<String[]> ids = new ArrayList<String[]>();
		int index=0;
		for (String[] s : ffd.jobList) {
			if ((t1 <= Long.parseLong(s[3]) && Long.parseLong(s[3]) < t2)
					|| (t1 < Long.parseLong(s[4]) && Long.parseLong(s[4]) <= t2)) {
				ids.add(new String[] { s[0], s[1], s[2], String.valueOf(index)});
			}
			index++;
		}
		return ids;
	}

	public static List<Integer> readCPURequest(String path) {
		List<Integer> request = new ArrayList<Integer>();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(path));
			String line;
			String[] row = new String[2];

			while ((line = bf.readLine()) != null) {
				row = line.split(",");
				request.add(Integer.parseInt(row[1]));
			}

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request;
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

	public static List<String[]> getFFD(List<String[]> item_inTime, List<String[]> CPUStorage) {

		boolean isFind;

		for (int i = 0; i < item_inTime.size(); i++) {
			isFind = false;

			float CPU_Total = 0f;
			int task_count = 0;
			int task_total = 0;
			float each_request=0f;
			int j = 0;
			float CPU_Used=0;
			// how many tasks in the job
			task_total = Integer.parseInt(item_inTime.get(i)[1]);
			// cpu request for each task
			each_request = Float.parseFloat(item_inTime.get(i)[2]);
			do {
				// for (int j = 0; j < CPUStorage.size(); j++) {
				// how much CPU left
				CPU_Total = MAX_STORAGE - Float.parseFloat(CPUStorage.get(j)[2]);
				// how many tasks can be placed in the current PM
				task_count = (int) (CPU_Total / each_request);
				if (task_count>task_total) {
					task_count=task_total;
				}
				CPU_Used=Float.parseFloat(CPUStorage.get(j)[2])+task_count*each_request;
				// how many tasks left
				task_total = task_total - task_count;
				if (task_count != 0) {
					String[] s = { CPUStorage.get(j)[0],
							CPUStorage.get(j)[1] + item_inTime.get(i)[0] + "(" + task_count + "),",
							String.valueOf(CPU_Used) };
					CPUStorage.set(j, s);
				}
				j++;
			} while (!(task_total <= 0 || j == CPUStorage.size()));
			if (task_total == 0)
				isFind = true;

			if (!isFind) {
				// add the new value to CPUStorage
				do {
					j = CPUStorage.size();
					CPUStorage.add(new String[] { String.valueOf(j), "", "0" });
					task_count = (int) (MAX_STORAGE / each_request);
					if (task_count > task_total) {
						task_count = task_total;
					}
					CPU_Used=task_count*each_request;
					task_total = task_total - task_count;
					if (task_count != 0) {
						String[] s = { CPUStorage.get(j)[0],
								CPUStorage.get(j)[1] + item_inTime.get(i)[0] + "(" + task_count + "),",
								String.valueOf(CPU_Used) };
						CPUStorage.set(j, s);
					}
				} while (task_total > 0);
			}
		}
		return CPUStorage;
	}
	
	public static void outputToFile(FFD_1 ffd, String path) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
			for (String[] line : ffd.PMList) {
				if (line.length==0)
					break;
				else {
				for (String l : line) {
//					if (!(l.equals("") || l.equals(null))) {
						bw.write(l+",");
//					}
				}
				}
				bw.write("\r\n");
				bw.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// allocate permanent jobs to PMs
	public static List<String[]> PermanentJobsAllocation(FFD_1 ffd) {
		for (String[] s : ffd.jobList) {
			if (Long.parseLong(s[3]) <= 51000000000L && Long.parseLong(s[4]) >= 141000000000L) {
				ffd.permanentJobList.add(s);
//				ffd.jobList.remove(ffd.jobList.indexOf(s));
			}
		}
		for (String[] s : ffd.permanentJobList) {
			int index=ffd.jobList.indexOf(s);
			if (index>=0)
				ffd.jobList.remove(index);
		}
		List<String[]> CPUStorage = new ArrayList<String[]>();
		CPUStorage.add(new String[]{"0","","0"});
		getFFD(ffd.permanentJobList, CPUStorage);
		return CPUStorage;
	}
}
