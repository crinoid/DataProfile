import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.*;

/**
 * Calculate total CPU request on all physical machines for each differed time slot.
 * 
 * The output file is "forPaper_CPUProfile.csv".
 * Including the start time for each time slot, and the relevant total CPU request.
 */
public class profiling{
	
	private static List timeSlot = new ArrayList();
	
	//Files which will be used in 24-hour-profiling
	private static String[] fileName_24h = {
			"forPaper_VMDataOutput_every200version_part-00001-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00002-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00003-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00004-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00005-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00006-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00007-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00008-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00009-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00010-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00011-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00012-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00013-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00014-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00015-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00016-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00017-of-00500.csv.csv",
			"forPaper_VMDataOutput_every200version_part-00018-of-00500.csv.csv"};
	
	/**
	 * --------MAIN--------
	 */
	public static void main(String[] args){
		//Choose one set of measures to run at one time
		//timeProfiling("forPaper_VMDataOutput_every200version.csv");
		//CPUProfiling("forPaper_VMDataOutput_every200version.csv");
		//CPUProfiling_EntireMeasurementPeriod("forPaper_VMDataOutput_every200version.csv");
		
		//timeProfiling("forPaper_VMDataOutput_130000version.csv");
		//CPUProfiling("forPaper_VMDataOutput_130000version.csv");
		//CPUProfiling_EntireMeasurementPeriod("forPaper_VMDataOutput_130000version.csv");
		
		//timeProfiling("forPaper_VMDataOutput_80000version.csv");
		//CPUProfiling("forPaper_VMDataOutput_80000version.csv");
		//CPUProfiling_EntireMeasurementPeriod("forPaper_VMDataOutput_80000version.csv");
		
		CPUProfiling_EntireMeasurementPeriod_24h();
		CPUProfiling_EntireMeasurementPeriod_24h_peak();
	}
	
	//////////PRIVATE STATIC METHODS//////////
	
	/**
	 * Extract all differed time slots diveded by different start and end timestamps.
	 * 
	 * all time intervals will be stored in List timeslot.
	 * 
	 * @param String fileName
	 */
	private static void timeProfiling(String fileName){
		try{
			BufferedReader timeReader = new BufferedReader(new FileReader(fileName));
			
			//List timeSlot = new ArrayList();
			boolean existStartTime = false;
			boolean existEndTime = false;
			String line = null;
			int i = 0;
			
			//Read line by line in file "forPaper_VMDataOutput.csv"
			while ((line = timeReader.readLine()) != null){
				
				//All compenents will be stored in array itemTime[]
				String itemTime[] = line.split(",");
				
				long itemStartTime = Long.parseLong(itemTime[0]);
				long itemEndTime = Long.parseLong(itemTime[1]);
				
				//Check if this start or end time already exists in timeSlot
				existStartTime = timeSlot.contains(itemStartTime);
				existEndTime = timeSlot.contains(itemEndTime);
				
				//Add new start time into timeSlot
				if (existStartTime == false){
					timeSlot.add(itemStartTime);
				}
				
				//Add new end time into timeSlot
				if (existEndTime == false){
					timeSlot.add(itemEndTime);
				}
				
				existStartTime = false;
				existEndTime = false;
			}
			
			//Sort timeSlot from inferior to greater
			timeSlot.sort(null);
			
			//System.out.println(timeSlot);
			//System.out.println(timeSlot.size());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate total CPU usage for each differed time slot.
	 * Then output the result in file "forPaper_CPUProfile.csv".
	 * 
	 * @param String fileName
	 */
	private static void CPUProfiling(String fileName){
		try{
			//BufferedReader CPUReader = new BufferedReader(new FileReader("combined_forPaper_VMDataOutput.csv"));
			
			File CPU = new File("forPaper_CPUProfile.csv");
			CPU.createNewFile();
			BufferedWriter CPUWriter = new BufferedWriter(new FileWriter(CPU));
			
			List totalCPURequest = new ArrayList();
			String line = null;
			
			//Scan within all time slots
			for (int i = 0; i < timeSlot.size()-1; i++){
				
				//Set one time slots for scanning
				long startTime = (Long)timeSlot.get(i);
				long endTime = (Long)timeSlot.get(i + 1);
				float CPURequest = 0;
				//System.out.println(CPURequest);
				
				BufferedReader CPUReader = new BufferedReader(new FileReader(fileName));
				
				//Find all relevant records which overlap this time slot
				while ((line = CPUReader.readLine()) != null){
					
					//All compenents will be stored in array item
					String item[] = line.split(",");
					 
					long itemStartTime = Long.parseLong(item[0]);
					long itemEndTime = Long.parseLong(item[1]);
					float itemRequest = Float.parseFloat(item[6]);
					
					//Add relevant CPU request into this time slot
					if ((endTime >= itemStartTime) && (startTime <= itemEndTime)){
						CPURequest += itemRequest;
					}
				}
				
				//Add total CPU request into totalCPURequest for stat
				totalCPURequest.add(CPURequest);
				
				CPUWriter.write(startTime + "," + endTime + "," + CPURequest + "\r\n");
				System.out.println(startTime + ",\t" + endTime + ",\t" + CPURequest);
				
				//Flush the RAM
				CPUWriter.flush();
			}
			
			//Output the max and min CPU requests in all CPU requests
			System.out.println(Collections.max(totalCPURequest));
			System.out.println(Collections.min(totalCPURequest));
			
			//Close the file
			CPUWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate total CPU usage for each 5-minute time slot.
	 * Then output the result in file "forPaper_CPUProfile_EntireMeasurementPeriod.csv".
	 * 
	 * @param String fileName
	 */
	private static void CPUProfiling_EntireMeasurementPeriod(String fileName){
		try{
			//BufferedReader CPUReader = new BufferedReader(new FileReader("combined_forPaper_VMDataOutput.csv"));
			
			File CPU = new File("forPaper_CPUProfile_EntireMeasurementPeriod.csv");
			CPU.createNewFile();
			BufferedWriter CPUWriter = new BufferedWriter(new FileWriter(CPU));
			
			List totalCPURequest = new ArrayList();
			String line = null;
			//int startTime = 5400;
			
			//Scan within all time slots
			for (int startTime = 5400; startTime <= 10500; startTime += 300){
				
				//Set one time slots for scanning
				int endTime = startTime + 300;
				float CPURequest = 0;
				//System.out.println(CPURequest);
				
				BufferedReader CPUReader = new BufferedReader(new FileReader(fileName));
				
				//Find all relevant records which overlap this time slot
				while ((line = CPUReader.readLine()) != null){
					
					//All compenents will be stored in array item
					String item[] = line.split(",");
					 
					long itemStartTime = Long.parseLong(item[0]);
					long itemEndTime = Long.parseLong(item[1]);
					float itemRequest = Float.parseFloat(item[6]);
					
					//Add relevant CPU request into this time slot
					if ((startTime <= itemStartTime/1000000) && (endTime >= itemEndTime/1000000)){
						CPURequest += itemRequest;
					}
				}
				
				//Add total CPU request into totalCPURequest for stat
				totalCPURequest.add(CPURequest);
				
				CPUWriter.write(startTime + "," + endTime + "," + CPURequest + "\r\n");
				System.out.println(startTime + ",\t" + endTime + ",\t" + CPURequest);
				
				//Flush the RAM
				CPUWriter.flush();
			}
			
			//Output the max and min CPU requests in all CPU requests
			System.out.println(Collections.max(totalCPURequest));
			System.out.println(Collections.min(totalCPURequest));
			
			//Close the file
			CPUWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate total CPU usage for each 5-minute time slot in 24 hours time.
	 * Then output the result in file "forPaper_CPUProfile_EntireMeasurementPeriod.csv".
	 */
	private static void CPUProfiling_EntireMeasurementPeriod_24h(){
		try{
			//BufferedReader CPUReader = new BufferedReader(new FileReader("combined_forPaper_VMDataOutput.csv"));
			
			File CPU = new File("forPaper_CPUProfile_EntireMeasurementPeriod_24h.csv");
			CPU.createNewFile();
			BufferedWriter CPUWriter = new BufferedWriter(new FileWriter(CPU));
			
			List totalCPURequest = new ArrayList();
			String line = null;
			int m = 0;
			//boolean finishThisPeriod = false;
			//int startTime = 5400;
			
			//Scan within all time slots
			for (int startTime = 5400; startTime <= 85800; startTime += 300){
				
				boolean finishedThisPeriod = false;
				
				//Set one time slots for scanning
				int endTime = startTime + 300;
				float CPURequest = 0;
				//System.out.println(CPURequest);
				
				//Start scanning from previous VM record file
				for (int i = m; i < 18 ; i ++){
					
					BufferedReader CPUReader = new BufferedReader(new FileReader(fileName_24h[i]));
					
					//Find all relevant records which overlap this time slot
					while ((line = CPUReader.readLine()) != null){
						
						//All compenents will be stored in array item
						String item[] = line.split(",");
						 
						long itemStartTime = Long.parseLong(item[0]);
						long itemEndTime = Long.parseLong(item[1]);
						float itemRequest = Float.parseFloat(item[6]);
						
						//Add relevant CPU request into this time slot
						if ((startTime <= itemStartTime/1000000) && (endTime >= itemEndTime/1000000)){
							CPURequest += itemRequest;
						}
						
						//Break when this measurement period is finished
						if (itemStartTime/1000000 >= endTime){
							finishedThisPeriod = true;
							m = i;
							break;
						}
					}
					
					//Break when this measurement period is finished
					if (finishedThisPeriod == true){
						break;
					}					
				}
				//Add total CPU request into totalCPURequest for stat
				totalCPURequest.add(CPURequest);
				
				CPUWriter.write(startTime + "," + endTime + "," + CPURequest + "\r\n");
				System.out.println(startTime + ",\t" + endTime + ",\t" + CPURequest);
				
				//Flush the RAM
				CPUWriter.flush();
			}
			
			//Output the max and min CPU requests in all CPU requests
			System.out.println(Collections.max(totalCPURequest));
			System.out.println(Collections.min(totalCPURequest));
			
			//Close the file
			CPUWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate total peak CPU usage for each 5-minute time slot in 24 hours time.
	 * Then output the result in file "forPaper_CPUProfile_EntireMeasurementPeriod.csv".
	 */
	private static void CPUProfiling_EntireMeasurementPeriod_24h_peak(){
		try{
			//BufferedReader CPUReader = new BufferedReader(new FileReader("combined_forPaper_VMDataOutput.csv"));
			
			File CPU = new File("forPaper_CPUProfile_EntireMeasurementPeriod_24h_peak.csv");
			CPU.createNewFile();
			BufferedWriter CPUWriter = new BufferedWriter(new FileWriter(CPU));
			
			List totalPeakCPUUsage = new ArrayList();
			String line = null;
			int m = 0;
			
			//Scan within all time slots
			for (int startTime = 5400; startTime <= 85800; startTime += 300){
				
				boolean finishedThisPeriod = false;
				
				//Set one time slots for scanning
				int endTime = startTime + 300;
				float peakCPUUsage = 0;
				
				//Start scanning from previous VM record file
				for (int i = m; i < 18 ; i ++){
					
					BufferedReader CPUReader = new BufferedReader(new FileReader(fileName_24h[i]));
					
					//Find all relevant records which overlap this time slot
					while ((line = CPUReader.readLine()) != null){
						
						//All compenents will be stored in array item
						String item[] = line.split(",");
						 
						long itemStartTime = Long.parseLong(item[0]);
						long itemEndTime = Long.parseLong(item[1]);
						float itemPeakUsage = Float.parseFloat(item[5]);
						
						//Add relevant CPU request into this time slot
						if ((startTime <= itemStartTime/1000000) && (endTime >= itemEndTime/1000000)){
							peakCPUUsage += itemPeakUsage;
						}
						
						//Break when this measurement period is finished
						if (itemStartTime/1000000 >= endTime){
							finishedThisPeriod = true;
							m = i;
							break;
						}
					}
					
					//Break when this measurement period is finished
					if (finishedThisPeriod == true){
						break;
					}					
				}
				//Add total CPU request into totalCPURequest for stat
				totalPeakCPUUsage.add(peakCPUUsage);
				
				CPUWriter.write(startTime + "," + endTime + "," + peakCPUUsage + "\r\n");
				System.out.println(startTime + ",\t" + endTime + ",\t" + peakCPUUsage);
				
				//Flush the RAM
				CPUWriter.flush();
			}
			
			//Output the max and min CPU requests in all CPU requests
			System.out.println(Collections.max(totalPeakCPUUsage));
			System.out.println(Collections.min(totalPeakCPUUsage));
			
			//Close the file
			CPUWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}