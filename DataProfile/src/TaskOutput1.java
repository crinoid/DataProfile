import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskOutput1 {

	public static void main(String[] args) throws IOException {
	

		
	}

	/**
	 * Output duration, maximum tasks and maximum usage of each job
	 * 
	 * @param fileInput
	 * @param fileOutput
	 */
	public void output(String[] fileInput, String fileOutput) {
		List<Long> listJob = new ArrayList<Long>();
//		List<Float> listTask = new ArrayList<Float>(); // maximum
//		List<String[]> listLifeTime = new ArrayList<String[]>();
		List<Float> listUsage = new ArrayList<Float>(); // maximum
		List<String[]> listitem = new ArrayList<String[]>();
//		List<Integer> taskCount = new ArrayList<Integer>();

		BufferedReader reader;
		String file;
		List<Long[]> uniqueJob = null;
//		System.out.println(fileInput.length);
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
		}

		File f = new File(fileOutput);
		try {
			f.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
//			listUsage = getMaxData(uniqueJob, listitem, 6);

			System.out.println(uniqueJob.size());
			for (int i = 0; i < uniqueJob.size(); i++) {
				System.out.println(i);
				String t;
//				t=getSpecificiedTime(listitem,String.valueOf(uniqueJob.get(i)[0]));
//				for (int j = 0; j < taskCount.size(); j++) {
//					long t1, t2;
//					t1 = 50700000000L + j * 300000000L;
//					t2 = 50700000000L + (j + 1) * 300000000L;
//					bf.write(uniqueJob.get(i)[0] + "," + String.valueOf(taskCount.get(j)) + "," + listUsage.get(i) + ","
//							+ String.valueOf(t1) + "," + String.valueOf(t2));
//					bf.write("\r\n");
//					bf.flush();
//				}
//				bf.write(uniqueJob.get(i)[0] + "," + t+ ",");
				bf.write("\r\n");
				bf.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("finish");
	}
	
	public void readFiles(String[] fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName[0])));
		String line="";
		List<String[]> s = new ArrayList<String[]>();
		while((line = br.readLine()) != null) {
			s.add(line.split(","));
		}
		packageTasks(s,fileName[1],fileName[2]);
	}
	
	public void readTinyFiles(String[] fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName[0])));
		String line="";
		List<String[]> s = new ArrayList<String[]>();
		while((line = br.readLine()) != null) {
			s.add(line.split(","));
		}
		packageTinyTasks(s,fileName[1],fileName[2]);
	}
	
	// for tiny jobs
	public void packageTinyTasks(List<String[]> joblist, String filename, String filename2) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
		BufferedWriter tinyNumber = new BufferedWriter(new FileWriter(new File(filename2)));
		int n = 0;
		long t1,t2;

		for (; n < 301; n++) {
			t1 = 51000000000L + n * 300000000L;
			t2 = 51000000000L + (n + 1) * 300000000L;
			int count = 1;
			double total=0.0;
			for (String[] s : joblist) {
				if ((t1 <= Long.parseLong(s[0]) && Long.parseLong(s[0]) < t2)
						|| (t1 < Long.parseLong(s[1]) && Long.parseLong(s[1]) <= t2)) {
					if(total<=0.1){
						total+=Double.valueOf(s[6]);
					} else {
						total=Double.valueOf(s[6]);
						count++;
					}
				}
			}
			bw.write("0.015*"+String.valueOf(count)+"\r\n");
			bw.flush();
			tinyNumber.write(String.valueOf(count)+"\r\n");
			tinyNumber.flush();
		}
	}
	
	public void packageTasks(List<String[]> joblist, String filename,String filename2) throws IOException {
		BufferedWriter tinyNumber = null;
		if (filename2!="") {
			tinyNumber = new BufferedWriter(new FileWriter(new File(filename2)));
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
		int n = 0;
		long t1,t2;

		for (; n < 301; n++) {
//			t1 = 51000000000L + n * 300000000L;
//			t2 = 51000000000L + (n + 1) * 300000000L;
			int[] count = { 0, 0, 0, 0, 0, 0 };
			int[] repetitionTime = { 0, 0, 0, 0, 0, 0 };
			String usage="";
			int i,j;
			System.out.println("time"+String.valueOf(n));
			for (i = 0; i < joblist.size(); i++) {
				t1 = 51000000000L + n * 300000000L;
				t2 = 51000000000L + (n + 1) * 300000000L;
				if(Long.parseLong(joblist.get(i)[0])>=t2)
					break;
//				System.out.println(i);
				if ((t1 <= Long.parseLong(joblist.get(i)[0]) && Long.parseLong(joblist.get(i)[0]) < t2)
						|| (t1 < Long.parseLong(joblist.get(i)[1]) && Long.parseLong(joblist.get(i)[1]) <= t2)) {
					usage=convertTaskUsage(joblist.get(i)[6]);
					switch (usage) {
					case "0.45":
						count[0]++;
						break;
					case "0.3":
						count[1]++;
						break;
					case "0.15":
						count[2]++;
						break;
					case "0.1":
						count[3]++;
						break;
					case "0.045":
						count[4]++;
						break;
					case "0.015":
						count[5]++;
						break;
					default:
						break;
					}
				}

				for (j=i+1;j<joblist.size();j++) {
					t2 = 51000000000L + (n + 2) * 300000000L;
					if(Long.parseLong(joblist.get(j)[0])>=t2)
						break;
					if (joblist.get(j)[2].equals(joblist.get(i)[2]) && joblist.get(j)[3].equals(joblist.get(i)[3])) {
//						System.out.println(convertTaskUsage(joblist.get(j)[6]));
//						System.out.println(usage);
						if (convertTaskUsage(joblist.get(j)[6]).equals(usage)) {

							t1 = 51000000000L + (n + 1) * 300000000L;
							if ((t1 <= Long.parseLong(joblist.get(j)[0]) && Long.parseLong(joblist.get(j)[0]) < t2)
									|| (t1 < Long.parseLong(joblist.get(j)[1])
											&& Long.parseLong(joblist.get(j)[1]) <= t2)) {
								System.out.println(joblist.get(j)[2]);
								switch (usage) {
								case "0.45":
									repetitionTime[0]++;
									break;
								case "0.3":
									repetitionTime[1]++;
									break;
								case "0.15":
									repetitionTime[2]++;
									break;
								case "0.1":
									repetitionTime[3]++;
									break;
								case "0.045":
									repetitionTime[4]++;
									break;
								case "0.015":
									repetitionTime[5]++;
									break;
								default:
									break;
								}
								break;
							}
						}
					}
				}
			}

			bw.write("0.45*" + String.valueOf(count[0]) + "(" + String.valueOf(repetitionTime[0]) + "),0.3*"
					+ String.valueOf(count[1]) + "(" + String.valueOf(repetitionTime[1]) + "),0.15*"
					+ String.valueOf(count[2]) + "(" + String.valueOf(repetitionTime[2]) + "),0.1*"
					+ String.valueOf(count[3]) + "(" + String.valueOf(repetitionTime[3]) + "),0.045*"
					+ String.valueOf(count[4]) + "(" + String.valueOf(repetitionTime[4]) + "),0.015*"
					+ String.valueOf(count[5]) + "(" + String.valueOf(repetitionTime[5]) + ")\r\n");
//			bw.write("0.45*" + String.valueOf(count[0]) + ",0.3*"
//					+ String.valueOf(count[1]) + ",0.15*"
//					+ String.valueOf(count[2]) + ",0.1*"
//					+ String.valueOf(count[3]) + ",0.045*"
//					+ String.valueOf(count[4]) + ",0.015*"
//					+ String.valueOf(count[5])+"\r\n");
			bw.flush();
			if (filename2!="") {
				tinyNumber.write(String.valueOf(count[5])+"\r\n");
				tinyNumber.flush();
			}
		}
	}
	
	private static String convertTaskUsage(String usage) {
		double d = Double.valueOf(usage);
		if (d <= 0.015) {
			d = 0.015;
		} else if (d <= 0.045) {
			d = 0.045;
		} else if (d <= 0.1) {
			d = 0.1;
		} else if (d <= 0.15) {
			d = 0.15;
		} else if (d <= 0.3) {
			d = 0.3;
		} else {
			d = 0.45;
		}
		return String.valueOf(d);
	}
	
}
