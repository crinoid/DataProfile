import java.awt.Label;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extract selected VM information from file "part-00010-of-00500.csv" to file "part-00027-of-00500.csv".
 * Then show the extracted information in file ".\\forPaper_VMDataOutput.csv".
 * 
 * The output sytle would be similar as "csvIO.java".
 */
public class csvExtract_oneMeasurementPeriod_v3{
	
	private static float CPURequest = 0;
	
	private static int fileCounter = 0;	
//	private static String[] eventsFileName = {"task_events/part-00010-of-00500.csv","task_events/part-00011-of-00500.csv","task_events/part-00012-of-00500.csv","task_events/part-00013-of-00500.csv","task_events/part-00014-of-00500.csv",
//			"task_events/part-00015-of-00500.csv","task_events/part-00016-of-00500.csv","task_events/part-00017-of-00500.csv","task_events/part-00018-of-00500.csv","task_events/part-00019-of-00500.csv",
//			"task_events/part-00020-of-00500.csv","task_events/part-00021-of-00500.csv","task_events/part-00022-of-00500.csv","task_events/part-00023-of-00500.csv","task_events/part-00024-of-00500.csv",
//			"task_events/part-00025-of-00500.csv","task_events/part-00026-of-00500.csv","task_events/part-00027-of-00500.csv"};
	private static String[] eventsFileName ={"forPaper_VMDataOutput_every200version_part-00010-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00011-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00012-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00013-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00014-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00015-of-00500.csv_v2.csv",			
			"forPaper_VMDataOutput_every200version_part-00016-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00017-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00018-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00019-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00020-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00021-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00022-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00023-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00024-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00025-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00026-of-00500.csv_v2.csv",
			"forPaper_VMDataOutput_every200version_part-00027-of-00500.csv_v2.csv"};
	
	private static List<String> requestRAM = new ArrayList<String>();
	
	/**
	 * --------MAIN--------
	 */
	public static void main(String[] args){
		//24 hour version
//		String[] usageFileName = {"part-00010-of-00500.csv","part-00011-of-00500.csv","part-00012-of-00500.csv","part-00013-of-00500.csv",
//				"part-00014-of-00500.csv","part-00015-of-00500.csv","part-00016-of-00500.csv","part-00017-of-00500.csv","part-00018-of-00500.csv",
//				"part-00019-of-00500.csv","part-00020-of-00500.csv","part-00021-of-00500.csv","part-00022-of-00500.csv","part-00023-of-00500.csv",
//				"part-00024-of-00500.csv","part-00025-of-00500.csv","part-00026-of-00500.csv","part-00027-of-00500.csv"};
		String[] usageFileName ={"forPaper_VMDataOutput_every200version_part-00010-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00011-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00012-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00013-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00014-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00015-of-00500.csv_v2.csv",			
				"forPaper_VMDataOutput_every200version_part-00016-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00017-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00018-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00019-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00020-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00021-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00022-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00023-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00024-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00025-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00026-of-00500.csv_v2.csv",
				"forPaper_VMDataOutput_every200version_part-00027-of-00500.csv_v2.csv"};
		for (int fileCounter = 0; fileCounter < 18; fileCounter++){			
			initialization(eventsFileName[fileCounter]);
			System.out.println("Extracting from: " + usageFileName[fileCounter] + "...");
			System.out.println("Extracted :\t");
			printEvery200VMData(usageFileName[fileCounter]);
			System.out.println(" Done.");
		}
		
	}
	
	//////////PRIVATE STATIC METHODS//////////

	/**
	 * Initialize requestRAM list, and read relevant request file into this list.
	 */
	private static void initialization(String fileName){
		
		System.out.println("Initializing...");
		
		try{
			System.out.print("Reading: " + fileName + "...");
			BufferedReader iniReader = new BufferedReader(new FileReader(fileName));
			String line = null;
			
			//Initialize list
			requestRAM = new ArrayList();
			
			//Read line by line
			while ((line = iniReader.readLine()) != null){
				requestRAM.add(line);
			}
			
			//Try to clean up any unnecessary RAM
			System.gc();
			
			System.out.println(" Done.");
		} catch (Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Initialization finished.");
	}
	
	/**
	 * Print necessary data from "Task_usage" once every 200 records.
	 * 
	 * List extracted data in "VMDataOutput.csv" by:
	 * "Start Time","End Time","Job ID","Task Category","Machine ID","Peak CPU Usage","CPU Request"
	 * 
	 * List extracted data in "CPUUsageOutput.csv" by:
	 * "Total CPU Usage in this Time Slot","This Time Slot"
	 * Then:
	 * "Minimum Total CPU Usage","This Time Slot"
	 * "Maximum Total CPU Usage","This Time Slot"
	 * 
	 * @param fileName
	 */
	private static void printEvery200VMData(String fileName){		
		try{
			BufferedReader CSVreader = new BufferedReader(new FileReader(fileName));
			String line = null;
			
			File outputCSV = new File("forPaper_VMDataOutput_every1000version_" + fileName + "_v2.csv");
			outputCSV.createNewFile();
			BufferedWriter CSVOut = new BufferedWriter(new FileWriter(outputCSV));
			
			int i = 1;
			int counter = 1;
			long currentStartTime = 0;
			
			//Read line by line
			while ((line=CSVreader.readLine()) != null){
				
				//Save line splitted by "," as item[]
				String item[] = line.split(",");
				
//				long itemStartTime = Long.parseLong(item[0]);
//				long itemEndTime = Long.parseLong(item[1]);
//				long itemJobID = Long.parseLong(item[2]);
//				int itemTaskCategory = Integer.parseInt(item[3]);
//				float itemCPUUsage = Float.parseFloat(item[13]);
				
				i++;
				
				//Extract every first 20 VM information of each Time Slot
				if (i == 6){
					i = 1;
					
					//Reverse the request when it seems to be half-way
					if (counter == 7000){
						Collections.reverse(requestRAM);
					}
					
					//countCPUUsage(itemStartTime, itemCPUUsage);
					//searchForCPURequest_24h(itemJobID,itemTaskCategory,itemStartTime);
					
					//if (CPURequest != 0){
						//Temporary output
						//System.out.print(item[0] + ",\t" + item[1] + ",\t" + item[2] + "-" + item[3] + ",\t" + item[4] + ",\t" + item[13] + ",\t");
						CSVOut.write(item[0] + "," + item[1] + "," + item[2] + "," + item[3] + "," + item[4] + "," + item[5] + item[6]);
						//CSVOut.write(item[0] + "," + item[1] + "," + item[2] + "," + item[3] + "," + item[4] + "," + item[13] + "\r\n");
											
						//System.out.println(CPURequest);
						CSVOut.write("\r\n");
						
						//Recent flush
						CSVOut.flush();
						
						//Output a counter
						if (counter != 1){
							for (int backSpace = 0; backSpace <=String.valueOf(counter).length(); backSpace++){
								System.out.print("\b");
							}
						}
						//System.out.print(counter);
						counter++;
					//}					
				}
			}
			
			//Flush cache and close files
			CSVOut.flush();
			CSVOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Search for "CPU request" of recent VM according to itemJobID and itemTaskCategory in all related files in "Task_events".
	 * 
	 * 	@param itemJobID
	 * 	@param itemTaskCategory
	 */
	private static void searchForCPURequest_24h(long itemJobID, int itemTaskCategory, long itemStartTime){
		try{
			boolean foundCPURequest = false;
			
			//Search in all event files
			String line = null;
			
			//Read line by line
			for (int i = 0; i < requestRAM.size(); i++){
				
				//Save lineForRequest splitted by "," as itemForRequest[]
				line = requestRAM.get(i);
				String itemForRequest[] = line.split(",");
				
				long itemStartTimeEvents = Long.parseLong(itemForRequest[0]);
				long itemJobIDEvents = Long.parseLong(itemForRequest[2]);
				int itemTaskEvents = Integer.parseInt(itemForRequest[3]);
				
				if (itemStartTime < itemStartTimeEvents){
					foundCPURequest = true;
					break;
				}
				
				//When Jod ID and Task Category match in two files, save its CPU Request
				if ((itemJobIDEvents == itemJobID) && (itemTaskEvents == itemTaskCategory)){
					
					//Found
					foundCPURequest = true;
					
					//Save this CPU request
					//If CPU request does not exist in this record, save it as 0
					if (itemForRequest.length >= 10){
						CPURequest = Float.parseFloat(itemForRequest[9]);
					} else{
						CPURequest = 0;
					}
					break;
				}
			}
			
			//Return 0 if no record matches
			if (foundCPURequest == false){
				CPURequest = 0;
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}