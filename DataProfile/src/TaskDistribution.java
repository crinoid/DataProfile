import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskDistribution {
	public static void main(String[] args) throws IOException {

	}
	
	public void taskDistribution(String[] fileName) throws IOException{
		BufferedWriter bw_permanent = new BufferedWriter(new FileWriter(new File(fileName[0])));
		BufferedWriter bw_normal = new BufferedWriter(new FileWriter(new File(fileName[1])));
		BufferedWriter bw_tiny = new BufferedWriter(new FileWriter(new File(fileName[2])));
		
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName[3])));
		List<String> list = new ArrayList<String>(); //permanentid
		String line="";
		
		BufferedReader bf = new BufferedReader(new FileReader(new File("output.csv")));
		String[] row = new String[5];
		while (((line = bf.readLine()) != null)) {
			row = line.split(",");
			if (Long.parseLong(row[3]) <= 51000000000L && Long.parseLong(row[4]) >= 141000000000L) {
				list.add(row[0]);
			}

		}
		
		while((line=br.readLine())!=null) {
			if (list.contains(line.split(",")[2])) {
				bw_permanent.write(line+"\r\n");
				bw_permanent.flush();
			} else {
				if (Double.valueOf(line.split(",")[6])<0.015) {
					bw_tiny.write(line+"\r\n");
					bw_tiny.flush();
				} else {
					bw_normal.write(line+"\r\n");
					bw_normal.flush();
				}
			}
		}
	}
}
