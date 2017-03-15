import java.awt.Label;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.rowset.FilteredRowSet;

/**
 * Extract selected VM information from file "part-00001-of-00500.csv". Then
 * show the extracted information in file "./forPaper_VMDataOutput.csv" and
 * "./forPapaer_CPUUsageOutput.csv".
 * 
 * The output style would be similar as "csvIO.java".
 */
public class csvExtract_oneMeasurementPeriod {
	// Parameters underneath are for counting total CPU Usage in each Time Slot
	private static float CPUUsage[];
	private static long CPUUsageTimeSlot[];
	private static long previousStartTime = 0;
	private static int thisTimeSlot = 0;
	private static float minCPUUsage = 99999;
	private static float maxCPUUsage = 0;
	private static long minTimeSlot = 0;
	private static long maxTimeSlot = 0;
	private static int CPUUsage1 = 0;
	private static int CPUUsage2 = 0;
	private static int CPUUsage3 = 0;

	private static float CPURequest = 0;
	private static int recentFile = 0;
		
	/*private static List<Long> itemJobIDEvents = new ArrayList<Long>();
	private static List<Long> itemTimeEvents = new ArrayList<Long>();
	private static List<Integer> itemTaskEvents = new ArrayList<Integer>();
	private static List<String[]> itemForRequest = new ArrayList<String[]>();*/

	private final static int CSV_COUNT = 10;

	/**
	 * --------MAIN--------
	 */
	public static void main(String[] args) {

		// 24 hour version
		/*
		 * String[] usageFileName =
		 * {"part-00001-of-00500.csv","part-00002-of-00500.csv",
		 * "part-00003-of-00500.csv","part-00004-of-00500.csv",
		 * "part-00005-of-00500.csv","part-00006-of-00500.csv",
		 * "part-00007-of-00500.csv","part-00008-of-00500.csv",
		 * "part-00009-of-00500.csv",
		 * "part-00010-of-00500.csv","part-00011-of-00500.csv",
		 * "part-00012-of-00500.csv","part-00013-of-00500.csv",
		 * "part-00014-of-00500.csv",
		 * "part-00015-of-00500.csv","part-00016-of-00500.csv",
		 * "part-00017-of-00500.csv","part-00018-of-00500.csv"};
		 */
		long time=System.currentTimeMillis();
		try {
			readSourceFile();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double t = (System.currentTimeMillis() - time) / 1000.00;
		System.out.println("\ntotal time:"+t+"sec");
		
		 String[] usageFileName = { "11.csv" };
		 for (int i = 0; i <usageFileName.length; i++) 
		 { printEvery200VMData(usageFileName[i]); }
		  
		 //advancedOutput();
		 

	}

	private static String setSize(String num) {
		return num.length() < 2 ? "0" + num : num;
	}

	private static void readSourceFile() throws NumberFormatException, IOException {
		// All event files
		List<Long> itemJobIDEvents = new ArrayList<Long>();
		List<Integer> itemTimeEvents = new ArrayList<Integer>();
		List<Integer> itemTaskEvents = new ArrayList<Integer>();
		List<String[]> itemForRequest = new ArrayList<String[]>();
		String[] eventsFileName = new String[CSV_COUNT];
		for (int i = 1; i <= CSV_COUNT; i++) {
			eventsFileName[i-1] = "task_events/part-000" + setSize(String.valueOf(i)) + "-of-00500.csv";
			//System.out.println(eventsFileName[i-1]);
		}
		// String[] eventsFileName = {"task_events/part-00000-of-00500.csv"};
		boolean foundCPURequest = false;
		
		BufferedReader reader;
		String[] item = null;
		String line = null;
		int len;
		long fileLine=0;
		String back;
		System.out.print("current line:0");
		for (int i = 0; i < eventsFileName.length; i++) {
			reader = new BufferedReader(new FileReader(eventsFileName[i]));
			while ((line = reader.readLine()) != null) {

				item = line.split(",");
				itemTimeEvents.add(Integer.parseInt(item[0].substring(0, 5)));
				itemJobIDEvents.add(Long.parseLong(item[2]));
				itemTaskEvents.add(Integer.parseInt(item[3]));
				itemForRequest.add(item);	
							

//				len=String.valueOf(fileLine).length();
//				back="";
//				for (int j=0;j<len;j++) {
//					back+="\b";
//				}
//				System.out.print(back);
//				fileLine++;
//				System.out.print(fileLine);
//				try {
//					Thread.sleep(60);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}


		}
	}

	private static void printEvery200VMData(String fileName) {
		try {
			BufferedReader CSVreader = new BufferedReader(new FileReader(fileName));
			// BufferedReader CSVreader = new BufferedReader(new
			// FileReader("Book1.csv"));
			String line = null;
			CPUUsage = new float[50000];
			CPUUsageTimeSlot = new long[50000];

			File outputCSV = new File("forPaper_VMDataOutput_every400version_" + fileName + ".csv");
			outputCSV.createNewFile();
			BufferedWriter CSVOut = new BufferedWriter(new FileWriter(outputCSV));

			File outputCPUCSV = new File("forPapaer_CPUUsageOutput_every400version_" + fileName + ".csv");
			outputCPUCSV.createNewFile();
			BufferedWriter CPUCSVOut = new BufferedWriter(new FileWriter(outputCPUCSV));

			int i = 1;
			long currentStartTime = 0;

			// Read line by line
			String item[];
			CSVreader.readLine(); //start from second data
			while ((line = CSVreader.readLine()) != null) {

				// Save line splitted by "," as item[]
				item = line.split(",");

				long itemStartTime = Long.parseLong(item[0]);
				long itemEndTime = Long.parseLong(item[1]);
				long itemJobID = Long.parseLong(item[2]);
				int itemTaskCategory = Integer.parseInt(item[3]);
				float itemCPUUsage = Float.parseFloat(item[13]);
				// String fileIndex=fileName.substring(8,10);

				i++;

				// Extract every first 20 VM information of each Time Slot
				if (i == 400) {
					i = 1;

					// countCPUUsage(itemStartTime, itemCPUUsage);

					// Temporary output
					System.out.print(item[0] + ",\t" + item[1] + ",\t" + item[2] + "-" + item[3] + ",\t" + item[4]
							+ ",\t" + item[13] + ",\t");
					CSVOut.write(item[0] + "," + item[1] + "," + item[2] + "," + item[3] + "," + item[4] + ","
							+ item[13] + ",");
					// CSVOut.write(item[0] + "," + item[1] + "," + item[2] +
					// "," + item[3] + "," + item[4] + "," + item[13] + "\r\n");
					CSVOut.flush();
					Runnable r1 = new Runner(itemJobID, itemTaskCategory, itemStartTime);
					Runnable r2 = new Runner(itemJobID, itemTaskCategory, itemStartTime);
					// Runnable r3 = new Runner(itemJobID, itemTaskCategory);
					new Thread(r1).start();
					new Thread(r2).start();
					// new Thread(r3).start();

					// searchForCPURequest_24h(itemJobID,itemTaskCategory);
					System.out.println(CPURequest);
					CSVOut.write(CPURequest + "\r\n");
				}
			}

			statCPUUsage();

			// Print Total CPU Usage
			for (int n = 0; n < CPUUsage.length; n++) {

				if (CPUUsage[n] != 0) {
					System.out.println(CPUUsage[n] + ",\t" + CPUUsageTimeSlot[n - 1]);
					CPUCSVOut.write(CPUUsage[n] + "," + CPUUsageTimeSlot[n - 1] + "\r\n");
				}
			}

			// Temporary output stated CPU usage
			System.out.println(minCPUUsage + ",\t" + minTimeSlot);
			CPUCSVOut.write("MIN," + minCPUUsage + "," + minTimeSlot + "\r\n");
			System.out.println(maxCPUUsage + ",\t" + maxTimeSlot);
			CPUCSVOut.write("MAX," + maxCPUUsage + "," + maxTimeSlot + "\r\n");
			System.out.println(CPUUsage1 + ",\t" + CPUUsage2 + ",\t" + CPUUsage3);
			CPUCSVOut.write(CPUUsage1 + "," + CPUUsage2 + "," + CPUUsage3 + "\r\n");

			// Flush cache and close files
			CSVOut.flush();
			CPUCSVOut.flush();
			CSVOut.close();
			CPUCSVOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Search for "CPU request" of recent VM according to itemJobID and
	 * itemTaskCategory in "Task_events".
	 * 
	 * @param itemJobID
	 * @param itemTaskCategory
	 */

	/**
	 * Search for "CPU request" of recent VM according to itemJobID and
	 * itemTaskCategory in all related files in "Task_events".
	 * 
	 * @param itemJobID
	 * @param itemTaskCategory
	 */
	private static void searchForCPURequest_24h(long itemJobID, int itemTaskCategory) {
		try {
			// All event files
			String[] eventsFileName = { "task_events/part-00000-of-00500.csv", "task_events/part-00001-of-00500.csv",
					"task_events/part-00002-of-00500.csv", "task_events/part-00003-of-00500.csv",
					"task_events/part-00004-of-00500.csv", "task_events/part-00005-of-00500.csv",
					"task_events/part-00006-of-00500.csv", "task_events/part-00007-of-00500.csv",
					"task_events/part-00008-of-00500.csv", "task_events/part-00009-of-00500.csv",
					"task_events/part-00010-of-00500.csv", "task_events/part-00011-of-00500.csv",
					"task_events/part-00012-of-00500.csv", "task_events/part-00013-of-00500.csv",
					"task_events/part-00014-of-00500.csv", "task_events/part-00015-of-00500.csv",
					"task_events/part-00016-of-00500.csv", "task_events/part-00017-of-00500.csv",
					"task_events/part-00018-of-00500.csv" };
			boolean foundCPURequest = false;

			// Search in all event files
			for (int i = 9; i >= 0; i--) {
				BufferedReader reader = new BufferedReader(new FileReader(eventsFileName[i]));
				String line = null;

				// Read line by line
				while ((line = reader.readLine()) != null) {

					// Save lineForRequest splitted by "," as itemForRequest[]
					String itemForRequest[] = line.split(",");

					long itemJobIDEvents = Long.parseLong(itemForRequest[2]);
					int itemTaskEvents = Integer.parseInt(itemForRequest[3]);

					// When Jod ID and Task Category match in two files, save
					// its CPU Request
					if ((itemJobIDEvents == itemJobID) && (itemTaskEvents == itemTaskCategory)) {

						// Found
						foundCPURequest = true;

						// Save this CPU request
						// If CPU request does not exist in this record, save it
						// as 0
						if (itemForRequest.length >= 10) {
							CPURequest = Float.parseFloat(itemForRequest[9]);
						} else {
							CPURequest = 0;
						}
						break;
					}
				}

				// Break whenever a record matches
				if (foundCPURequest == true) {
					break;
				}

			}

			// Return 0 if no record matches
			if (foundCPURequest == false) {
				CPURequest = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Runner implements Runnable {
		private long itemJobID, itemStartTime;
		private int itemTaskCategory;
		private String fileIndex;

		public Runner(long itemJobID, int itemTaskCatagory, long itemStartTime) {
			this.itemJobID = itemJobID;
			this.itemTaskCategory = itemTaskCatagory;
			this.itemStartTime = itemStartTime;
			// this.fileIndex=fileIndex;

		}

		public void run() {
			/*try {
				// All event files
				String[] eventsFileName = { "task_events/part-00000-of-00500.csv",
						"task_events/part-00001-of-00500.csv", "task_events/part-00002-of-00500.csv",
						"task_events/part-00003-of-00500.csv", "task_events/part-00004-of-00500.csv",
						"task_events/part-00005-of-00500.csv", "task_events/part-00006-of-00500.csv",
						"task_events/part-00007-of-00500.csv", "task_events/part-00008-of-00500.csv",
						"task_events/part-00009-of-00500.csv", "task_events/part-00010-of-00500.csv",
						"task_events/part-00011-of-00500.csv", "task_events/part-00012-of-00500.csv",
						"task_events/part-00013-of-00500.csv", "task_events/part-00014-of-00500.csv",
						"task_events/part-00015-of-00500.csv", "task_events/part-00016-of-00500.csv",
						"task_events/part-00017-of-00500.csv", "task_events/part-00018-of-00500.csv" };
				boolean foundCPURequest = false;

				for (int i = itemJobIDEvents.size() - 1; i >= 0; i--) {
					if ((itemJobIDEvents.get(i) == itemJobID) && (itemTaskEvents.get(i) == itemTaskCategory)
							&& (itemStartTime >= itemTimeEvents.get(i))) {
						// Found
						foundCPURequest = true;

						// Save this CPU request
						// If CPU request does not exist in this record, save it
						// as 0
						if (itemForRequest.get(i).length >= 10) {
							CPURequest = Float.valueOf(itemForRequest.get(i)[9]);
						} else {
							CPURequest = 0;
						}
						break;
					}
					if (foundCPURequest == true) {

						break;
					}
				}

				// Return 0 if no record matches
				if (foundCPURequest == false) {
					CPURequest = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}

	private static void advancedOutput() {
		List<Long> listJob = new ArrayList<Long>();
		List<Float> listTask = new ArrayList<Float>(); // maximum
		List<String[]> listLifeTime=new ArrayList<String[]>();
		List<Float> listUsage = new ArrayList<Float>(); // maximum
		List<String[]> listitem = new ArrayList<String[]>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(
					new FileReader("forPaper_VMDataOutput_every400version_part-00010-of-00500.csv.csv"));
			String[] item = null;
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					item = line.split(",");
					listJob.add(Long.parseLong(item[2]));
					listitem.add(item);
				}
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// find unique jobs
		List<Long[]> uniqueJob = new ArrayList<Long[]>(); //uniqueJob[jobID, repeatTimes]

		int j = 0;
		for (int i = 0; i < listJob.size(); i++) {
			for (j = 0; j < uniqueJob.size(); j++) {
				if (uniqueJob.get(j)[0].equals(listJob.get(i))) {
					uniqueJob.get(j)[1]++; //same one
					break;
				}
			}
			if (j == uniqueJob.size()) { // new job
				Long[] l = {listJob.get(i),(long) 1};
				uniqueJob.add(l);
			}
		}

		File f = new File("new.csv");
		try {
			f.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			listTask = getMaxData(uniqueJob, listitem, 3);
			listUsage = getMaxData(uniqueJob, listitem, 6);
			for (int i = 0; i < uniqueJob.size(); i++) {
				listLifeTime.add(getTimeSlot(uniqueJob.get(i)[0], uniqueJob.get(i)[1], listitem));
				for (j=0;j<listLifeTime.get(i).length;j++) {
					bf.write(uniqueJob.get(i)[0] + "," + listTask.get(i).intValue() + "," + listUsage.get(i)+","+listLifeTime.get(i)[j] + "\r\n");
					bf.flush();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<Float> getMaxData(List<Long[]> job, List<String[]> item, int index) {
		List<Float> maxData = new ArrayList<Float>();
		float max = 0;
		for (int i = 0; i < job.size(); i++) {
			for (int j = 0; j < item.size(); j++) {
				if (Float.valueOf(item.get(j)[index]) > max && Long.valueOf(item.get(j)[2]).equals(job.get(i)[0])) {
					max = Float.valueOf(item.get(j)[index]);
				}
			}
			maxData.add(max);
			max = 0;
		}
		return maxData;
	}

	private static String[] getTimeSlot(long job, long count, List<String[]> listitem) {
		List<String> timeSlot = new ArrayList<String>();
		long sTime = -1, eTime = -1;
		int jobCount = 0;
		int i = 0;
		//find the index(i) of the first job
		while (i < listitem.size() && jobCount < count) {
			if (Long.valueOf(listitem.get(i)[2]).equals(job) && Long.valueOf(listitem.get(i)[0]) >= eTime) {
				jobCount++;
				break;
			}
			i++;
		}
		if (i < listitem.size()) {
			sTime = Long.valueOf(listitem.get(i)[0]);  //start time
			eTime = Long.valueOf(listitem.get(i)[1]);  //end time
			while (i < listitem.size()) {
				i++;
				if (i == listitem.size()) {
					timeSlot.add(String.valueOf(sTime) + "," + String.valueOf(eTime));
					break;
				}

				if (Long.valueOf(listitem.get(i)[2]).equals(job)) {
					//continuous job (with 300s deviation)
					if (Long.valueOf(listitem.get(i)[0]) <= eTime + 300000000) {
						jobCount++;
						sTime = Long.valueOf(listitem.get(i)[0]) < sTime ? Long.valueOf(listitem.get(i)[0]) : sTime;
						eTime = Long.valueOf(listitem.get(i)[1]) > eTime ? Long.valueOf(listitem.get(i)[1]) : eTime;
					}
					// discontinuous job
					else {
						timeSlot.add(String.valueOf(sTime) + "," + String.valueOf(eTime));
						sTime = Long.valueOf(listitem.get(i)[0]);
						eTime = Long.valueOf(listitem.get(i)[1]);
					}
				} else {
					// if test to the last job, add the result to timeSlot array
					if (jobCount == count) {
						timeSlot.add(String.valueOf(sTime) + "," + String.valueOf(eTime));
						break;
					}
				}
			}
		}

		String[] time = new String[timeSlot.size()];
		for (int k = 0; k < time.length; k++) {
			time[k] = timeSlot.get(k);
		}

		return time;
	}

	/**
	 * Count "CPU Usage" for each "Time Slot" (about 300 seconds).
	 * 
	 * @param itemStartTime
	 * @param itemCPUUsage
	 */
	private static void countCPUUsage(long itemStartTime, float itemCPUUsage) {
		// Add CPU Usage of one VM, in one Time Slot, into total Usage
		if (itemStartTime != previousStartTime) {
			CPUUsageTimeSlot[thisTimeSlot++] = itemStartTime;
			CPUUsage[thisTimeSlot] += itemCPUUsage;
		} else {
			CPUUsage[thisTimeSlot] += itemCPUUsage;
		}

		previousStartTime = itemStartTime;
	}

	/**
	 * Get static "CPU Usage" information. Includes "'Time Slots" which matches:
	 * Maximum Total CPU Usage, Minimum Total CPU Usage; Total CPU Usage less
	 * than 1, Total CPU Usage greater than 1 but less than 10, Total CPU Usage
	 * greater than 10.
	 */
	private static void statCPUUsage() {
		for (int i = 0; i < CPUUsage.length; i++) {

			// Save minimun Total CPU Usage
			if ((CPUUsage[i] < minCPUUsage) && (CPUUsage[i] != 0)) {
				minCPUUsage = CPUUsage[i];
				minTimeSlot = CPUUsageTimeSlot[i - 1];
			}

			// Save maximum Total CPU Usage
			if (CPUUsage[i] > maxCPUUsage) {
				maxCPUUsage = CPUUsage[i];
				maxTimeSlot = CPUUsageTimeSlot[i - 1];
			}

			// Count Number of different CPU Usage Time Slots
			if ((CPUUsage[i] < 1) && (CPUUsage[i] != 0)) {
				CPUUsage1 += 1;
			} else if (CPUUsage[i] >= 10) {
				CPUUsage3 += 1;
			} else if (CPUUsage[i] != 0) {
				CPUUsage2 += 1;
			}
		}
	}
}