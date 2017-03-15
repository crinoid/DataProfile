import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedOutput {
	final static int N=18;
	static List<Integer> cc = new ArrayList<Integer>();
	static List<Double> request = new ArrayList<Double>();
	static List<Double> usage = new ArrayList<Double>();
	
	public static void main(String[] args) {
		AdvancedOutput advancedOutput = new AdvancedOutput();
		String[] fileName = new String[18];
		for (int i = 10; i <= 9+N; i++) {
			fileName[i - 10] = "VMDataEvery200/forPaper_VMDataOutput_every200version_part-000" + String.valueOf(i)
					+ "-of-00500.csv_v2.csv";
		}
//		fileName[0]="forPaper_VMDataOutput_every400version.csv";
		advancedOutput.output(fileName, "output5-200.csv");
	}

	/**
	 * Output duration, maximum tasks and maximum usage of each job
	 * 
	 * @param fileInput
	 * @param fileOutput
	 */
	public void output(String[] fileInput, String fileOutput) {
		List<Long> listJob = new ArrayList<Long>();
		List<Float> listTask = new ArrayList<Float>(); // maximum
		List<String[]> listLifeTime = new ArrayList<String[]>();
		List<Float> listUsage = new ArrayList<Float>(); // maximum
		List<String[]> listitem = new ArrayList<String[]>();

		BufferedReader reader;
		String file;
		List<Long[]> uniqueJob = null;
		System.out.println(fileInput.length);
		for (int index = 0; index< fileInput.length; index++) {
			file = fileInput[index];
			try {
				// forPaper_VMDataOutput_every200version_part-00010-of-00500.csv.csv
				reader = new BufferedReader(new FileReader(file));
				String[] item = null;
				String line = null;

				while ((line = reader.readLine()) != null) {
					item = line.split(",");
					listJob.add(Long.parseLong(item[2]));
					listitem.add(item);
				}

			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// find unique jobs
			uniqueJob = new ArrayList<Long[]>(); // uniqueJob[jobID,
																// repeatTimes]

			int j = 0;
			for (int i = 0; i < listJob.size(); i++) {
				for (j = 0; j < uniqueJob.size(); j++) {
					if (uniqueJob.get(j)[0].equals(listJob.get(i))) {
						uniqueJob.get(j)[1]++; // same one
						break;
					}
				}
				if (j == uniqueJob.size()) { // find a new job
					Long[] l = { listJob.get(i), (long) 1 };
					uniqueJob.add(l);
				}
			}
			System.out.println("current:"+index);
		}

		File f = new File(fileOutput);
		try {
			f.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
//			listTask = getMaxData(uniqueJob, listitem, 1);
//			listUsage = getMaxData(uniqueJob, listitem, 6);
			for (int i = 0; i < uniqueJob.size(); i++) {
				System.out.println("unique"+String.valueOf(i));
//				cc = new ArrayList<Integer>();
				request=new ArrayList<Double>();
				usage=new ArrayList<Double>();
					listLifeTime.add(getTimeSlot(uniqueJob.get(i)[0], uniqueJob.get(i)[1], listitem));

					for (int j = 0; j < listLifeTime.get(i).length; j++) {
						bf.write(uniqueJob.get(i)[0] + "," + String.valueOf(request.get(j)) + "," + String.valueOf(usage.get(j)) + ","
								+ listLifeTime.get(i)[j] + "\r\n");
						bf.flush();
					}
//				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Find the maximum value for task and usage
	 * 
	 * @param job
	 * @param item
	 * @param index
	 * @return
	 */
	private List<Float> getMaxData(List<Long[]> job, List<String[]> item, int index) {
		List<Float> maxData = new ArrayList<Float>();
		float max = 0;
//		System.out.println("size:"+job.size());
		for (int i = 0; i < job.size(); i++) {
//			System.out.println("max"+i);
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

	/**
	 * Calculate the continuous duration of the job (in an array) Allow 300
	 * seconds deviation between end time to start time
	 * 
	 * @param job
	 * @param count
	 * @param listitem
	 * @return
	 */

	private String[] getTimeSlot(long job, long count, List<String[]> listitem) {
		List<String> timeSlot = new ArrayList<String>();
		long sTime = -1, eTime = -1;
		int jobCount = 0; // total rows of one job
		int i = 0;
		double requestTotal=0.0;
		double usageTotal=0.0;

		// find the index(i) of the first job
		while (i < listitem.size() && jobCount < count) {
			if (Long.valueOf(listitem.get(i)[2]).equals(job) && Long.valueOf(listitem.get(i)[0]) >= eTime) {
				jobCount++;
//				requestTotal+=Double.valueOf(listitem.get(i)[5]);
//				usageTotal+=Double.valueOf(listitem.get(i)[6]);
				break;
			}
			i++;
		}
		if (i < listitem.size()) {
			requestTotal=Double.valueOf(listitem.get(i)[5]);
			usageTotal=Double.valueOf(listitem.get(i)[6]);
			sTime = Long.valueOf(listitem.get(i)[0]); // start time
			eTime = Long.valueOf(listitem.get(i)[1]); // end time
			while (i < listitem.size()) {
				i++;
				if (i == listitem.size()) {
					request.add(requestTotal);
					usage.add(usageTotal);
					timeSlot.add(String.valueOf(sTime) + "," + String.valueOf(eTime));
					break;
				}

				if (Long.valueOf(listitem.get(i)[2]).equals(job)) {
					// continuous job (with 300s deviation)
					if (Long.valueOf(listitem.get(i)[0]) <= eTime + 300000000) {
						requestTotal+=Double.valueOf(listitem.get(i)[5]);
						usageTotal+=Double.valueOf(listitem.get(i)[6]);
//						int m=0;
//						for(;m<c_task.size();m++) {
//							if (c_task.get(m).equals(listitem.get(i)[3])) {
//								break;
//							}
//						}
//						if (m==c_task.size()) { //no existing task
//							c_value++;
//							cc.set(c_index,c_value);
//							c_task.add(listitem.get(i)[3]);
//						}


						jobCount++;
						sTime = Long.valueOf(listitem.get(i)[0]) < sTime ? Long.valueOf(listitem.get(i)[0]) : sTime;
						eTime = Long.valueOf(listitem.get(i)[1]) > eTime ? Long.valueOf(listitem.get(i)[1]) : eTime;
					}
					// discontinuous job
					else {
						request.add(requestTotal);
						usage.add(usageTotal);
						requestTotal=Double.valueOf(listitem.get(i)[5]);
						usageTotal=Double.valueOf(listitem.get(i)[6]);
//						c_value=1;
//						cc.add(c_value);
//						c_index++;
//						c_task=new ArrayList<String>();
//						c_task.add(listitem.get(i)[3]);
						
//						System.out.println(String.valueOf(sTime) + "," + String.valueOf(eTime));
						timeSlot.add(String.valueOf(sTime) + "," + String.valueOf(eTime));
						sTime = Long.valueOf(listitem.get(i)[0]);
						eTime = Long.valueOf(listitem.get(i)[1]);
					}
				} else {
					// if test to the last job, add the result to timeSlot array
					if (jobCount == count) {
						request.add(requestTotal);
						usage.add(usageTotal);
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
}
