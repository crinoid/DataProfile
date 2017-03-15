import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Power {
	public static void main(String[] args) throws IOException {
		Power p = new Power();
		p.getWork("PMUtilization400_simple.csv");

	}
	
	public void getWork(String filename) throws IOException{
		double n1 = getE(6.0, 0.6);
		double n2 = getE(4.5, 0.6);
		double n3 = getE(4.0, 0.6);
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		String line;
		double w = 0.0;
		int b=0;

		while (((line = reader.readLine()) != null)) {
			String[] usage = line.split(",");
			if (++b==301)
				break;
			for (String u1 : usage) {
				String[] PMIndex = u1.split("\\:");
				if (Double.valueOf(PMIndex[0]) < 10)
					w += (220 - 60 / (Math.pow(Math.E, n1 * Double.valueOf(PMIndex[1])))) * 300;
				else if (Double.valueOf(PMIndex[0]) < 25)
					w += (250 - 90 / (Math.pow(Math.E, n2 * Double.valueOf(PMIndex[1])))) * 300;
				else
					w += (280 - 120 / (Math.pow(Math.E, n3 * Double.valueOf(PMIndex[1])))) * 300;
			}
		}
		System.out.println(w);
	}

	public static double getE(Double p, double u) {
		double d = Math.log(p) / Math.log(Math.E);
		return (d / u);
	}
}
