import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JobDistribution {
	/**
	 * Calculate how many jobs running in each time slot
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("output5-400.csv"));
			String line = "";
			String[] arr_line = null;
			Long[][] arr_job = new Long[302][2]; //302 time slots
			double[] arr_request=new double[302];
			double[] arr_usage=new double[302];
			long startTime = 50700000000L; //start time
			// assign time slot value and job value(begin with 0) to arr_job array
			for (int i = 0; i < arr_job.length; i++) {
				arr_job[i][0] = startTime;
				arr_job[i][1] = 0L;
				startTime += 300000000L;
				arr_request[i]=0.0;
				arr_usage[i]=0.0;
			}
			int index = 0;
			int sIndex, eIndex;
			while ((line = br.readLine()) != null) {
				arr_line = line.split(",");
				//find the initial time interval of the job
				sIndex = (int) ((Long.parseLong(arr_line[3]) - 50700000000L) / 300000000L);
				//find the final time interval of the job
				eIndex = (int) ((Long.parseLong(arr_line[4]) - 50700000000L) / 300000000L);
				for (index = sIndex; index <= eIndex; index++) {
					arr_job[index][1]++; //each job value increases 1 from sIndex to eIndex
					arr_request[index]+=Double.valueOf(arr_line[1]);
					arr_usage[index]+=Double.valueOf(arr_line[2]);
					
				}
			}

			File f = new File("job_distribution400.csv");
			f.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < arr_job.length; i++) {
				System.out.println(i);
				bf.write(String.valueOf(arr_job[i][0]) + "," + arr_job[i][1] + ","+String.valueOf(arr_request[i])+","+String.valueOf(arr_usage[i])+"\r\n");
				bf.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
