import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class fileOutput {
public static void main(String[] args) throws InterruptedException {
//	try {
//		File outputCSV = new File("1.csv");
//		outputCSV.createNewFile();
//		BufferedWriter CSVOut = new BufferedWriter(new FileWriter(outputCSV));
//		for (int i=0;i<5;i++) {
//			CSVOut.write(String.valueOf(i)+"\n");
//			CSVOut.flush();
//			System.out.println(String.valueOf(i));
//			Thread.sleep(1000);
//		}
//
//		CSVOut.flush();
//		CSVOut.close();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	int len;
	String back;
	for (int i=0;i<200;i++) {
		System.out.print(i);
		Thread.sleep(10);
		len=String.valueOf(i).length();
		back="";
		for (int j=1;j<=len;j++) {
			back+="\b";
		}
		System.out.print(back);
	}
}
}
