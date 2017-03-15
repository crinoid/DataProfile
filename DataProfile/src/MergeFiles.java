import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MergeFiles {
	public static void main(String[] args) {
		String name = "";
//		for (int i = 10; i <= 27; i++) {
//			name += "VMDataEvery200/forPaper_VMDataOutput_every200version_part-000" + String.valueOf(i)
//					+ "-of-00500.csv_v2.csv" + ",";
//		}
		name="forPaper_VMDataOutput_every200version.csv,";
		String[] fileName = name.split(",");
		merge(fileName, "forPaper_VMDataOutput_every1000version.csv");
	}

	public static void merge(String[] reader, String writer) {
		try {
			BufferedReader bf;
			BufferedWriter bw = new BufferedWriter(new FileWriter(writer));
			int index = 1;
			for (int i = 0; i < reader.length; i++) {
				if (reader[i] == "")
					break;
				bf = new BufferedReader(new FileReader(reader[i]));

				String line;
				while ((line = bf.readLine()) != null) {
					if (index % 5 == 0) {
						bw.write(line);
						bw.write("\r\n");
						bw.flush();
					}
					index++;
				}
				bf.close();
			}
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
