import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FFD_step1 {
	final static float MAX_STORAGE = (float) 0.5;

	public static void main(String[] args) {
		FFD("VMDataEvery200/forPaper_VMDataOutput_every200version_part-00010-of-00500.csv_v2.csv");
	}

	public static void FFD(String path) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(path));
			String line = "";
			String[] row = new String[7];
			List<String[]> items = new ArrayList<String[]>();
			while ((line = bf.readLine()) != null) {
				row = line.split(",");
				items.add(row);
			}

			items = BubbleSort(items);

			float CPU_Total;
			boolean isFind;
			List<String[]> CPUStorage = new ArrayList<String[]>(); // [cpurequest,total]
			for (int i = 0; i < items.size(); i++) {
				isFind = false;
				for (int j = 0; j < CPUStorage.size(); j++) {
					CPU_Total = Float.parseFloat(CPUStorage.get(j)[1]) + Float.parseFloat(items.get(i)[6]);
					if (CPU_Total <= MAX_STORAGE) {
						String[] s = { CPUStorage.get(j)[0] + "," + items.get(i)[6], String.valueOf(CPU_Total) };
						CPUStorage.set(j, s);
						isFind = true;
						break;
					}
				}
				if (!isFind) {
					// add the new value to CPUStorage
					CPUStorage.add(new String[] { items.get(i)[6], items.get(i)[6] });
				}
			}
			System.out.println("Required PM:" + CPUStorage.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sort by CPU request desc
	 * 
	 * @param a
	 * @return
	 */
	public static List<String[]> BubbleSort(List<String[]> a) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < a.size() - i - 1; j++) {
				if (Float.parseFloat(a.get(j)[6]) < Float.parseFloat(a.get(j + 1)[6])) {
					String[] temp = a.get(j);
					a.set(j, a.get(j + 1));
					a.set(j + 1, temp);
				}
			}
		}
		return a;
	}
}
