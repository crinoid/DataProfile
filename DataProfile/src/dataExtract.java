import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class dataExtract {	
	private final static int CSV_COUNT = 10;
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		try {
			readSourceFile();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		double t = (System.currentTimeMillis() - time) / 1000.00;
		System.out.println("\ntotal time:" + t + "sec");
	}
	
	private static String setSize(String num) {
		return num.length() < 2 ? "0" + num : num;
	}

	private static void readSourceFile() throws NumberFormatException, IOException {
		List<Long> itemJobIDEvents = new ArrayList<Long>();
		List<Integer> itemTimeEvents = new ArrayList<Integer>();
		List<Integer> itemTaskEvents = new ArrayList<Integer>();
		List<String[]> itemForRequest = new ArrayList<String[]>();
		String[] eventsFileName = new String[CSV_COUNT];
		for (int i = 1; i <= CSV_COUNT; i++) {
			eventsFileName[i-1] = "task_events/part-000" + setSize(String.valueOf(i)) + "-of-00500.csv";
		}
		BufferedReader reader;
		String[] item = null;
		String line = null;
		for (int i = 0; i < eventsFileName.length; i++) {
			reader = new BufferedReader(new FileReader(eventsFileName[i]));
			while ((line = reader.readLine()) != null) {
				item = line.split(",");
				itemTimeEvents.add(Integer.parseInt(item[0].substring(0, 5)));
				itemJobIDEvents.add(Long.parseLong(item[2]));
				itemTaskEvents.add(Integer.parseInt(item[3]));
				itemForRequest.add(item);	
			}
		}
	}
}
