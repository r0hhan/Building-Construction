import java.io.File;
import java.util.Scanner;
import java.util.HashSet;

public class risingCity {
	public static int globalcounter=0, secondarycounter=0;
	// secondarycounter runs while there are more lines to read from inputfile and it resets to 0
	// everytime it reaches 5 or a building is finished constructing
	// we increment globalcounter & secondarycounter by 1 everytime so we can execute printbuilding at the exact instruction time
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		HashSet<String> hset = new HashSet<String>(); 		// check if duplicate building entry is made
		MinHeap mh = new MinHeap(2000); 					// minheap size does not exceed 2000
		String inputline; 									// contains lines of input file commands (1 at a time)
		String inputfilename=args[0]; 						// input file name
		String[] brokenInput = new String[4]; 				// contains brokendown parts of the input line
		try {
			Scanner sc = new Scanner(new File(inputfilename));
			while(sc.hasNextLine()) {
				inputline=sc.nextLine();
				brokenInput = breakDown(inputline);
				while(true) {
					if(Integer.parseInt(brokenInput[0]) == globalcounter) {
						// insert case
						if(brokenInput[1].equalsIgnoreCase("insert")) {
							if(!(hset.contains(brokenInput[2]))) {
								hset.add((brokenInput[2])); // add building to hashset
								mh.insert(Integer.parseInt(brokenInput[2]), Integer.parseInt(brokenInput[3]));
								if(secondarycounter==0) 	// secondarycounter reset means the construction of building is done or 
									mh.minHeap();			//the building is worked for 5 days, so we execute minHeap and heapify it
							}
							else { 							// if building is in hashset, print error and stop execution
								System.err.println("Building "+brokenInput[2]+" Already Exists in City.");
								mh.print();
								System.exit(1);
							}
								
						}
						else if(brokenInput[1].equalsIgnoreCase("printbuilding")) {			// printbuilding case
							if(brokenInput[3].equals("0"))									// printbuilding(x) case
								mh.rbtWriteToFile(Integer.parseInt(brokenInput[2]));
							else															// printbuilding(x,y) case
								mh.rbtFindNodesInRange(Integer.parseInt(brokenInput[2]), Integer.parseInt(brokenInput[3]));
						}
						break; // required since we can have multiple input lines at the same instruction time
					}
					// start working on buildings incrementing by 1 only if heap/tree contains buildings
					while(mh.size>0 && Integer.parseInt(brokenInput[0])!=globalcounter) {
						globalcounter++;
						int checker=mh.increaseKeyBeforeInputComplete(brokenInput);
						if(checker == 1)						// returns 1 if just the building execution time increases by 1
							secondarycounter++;					// just increment secondary counter
						else if(checker == 0)					// returns 0 if building completes or secondary counter reaches 5
							secondarycounter=0;					// so we reset secondarycounter and heapify
						else {									// returns -1 if there is print command on the day of completion of building
							if(sc.hasNext())					// we execute the print statement in increaseKeyBeforeInputComplete
								inputline=sc.nextLine();		// only if the above case arises, else we execute print command normally
								brokenInput = breakDown(inputline); // since we executed a command, we again fill our array with new command
							secondarycounter=0;					// our case also covered building completion so we reset secondarycounter
						}						

						if(secondarycounter==5) {				// if worked on building for 5 days reset counter and min heapify
							secondarycounter=0;
							mh.minHeap();
						}
						//System.out.println(globalcounter+" "+secondarycounter);
						//mh.print();
					}
					if(mh.size==0)			// if tree is empty and the next instruction time is greater than global counter,
						globalcounter=Integer.parseInt(brokenInput[0]); // we change the globalcounter to the next instruction time.
				}								// (THIS DOESNT EXECUTE IF HEAP/TREE CONTAINS DATA SINCE WE CANT SKIP DAYS AT THAT TIME)
			}
			sc.close();
        }
        catch(Exception e) {
            System.err.println("File not found!");
            e.printStackTrace();
            System.exit(1);
        }
		
		
		while(mh.size>0) {							// after all input lines are read, globalcounter is increased by whichever is smaller
			globalcounter+=mh.increaseKey(5-secondarycounter); 	// 1. executedtime-totaltime
			secondarycounter=0; 								// 2. 5 (five)
			//System.out.println(globalcounter);	// NOTE: for the first time when this loop executes, globalcounter is increased by 	
			//mh.print();						// 5-secondarycounter, this is since if secondarycounter!=0, means we have already taken a 	
		}							// building to work on, so we complete 5 days of that building and then run the loop by the above method
		
		System.out.println("Done!");
	}
	
	// this function breaks down input line into 4 components
	// 1. instruction time
	// 2. type of command (insert/printbuilding)
	// 3. first attribute
	// 4. second attribute (this is 0 if printbuilding(x) is broken down)
	public static String[] breakDown(String inputline) {
		String[] str = new String[4];
		StringBuilder s = new StringBuilder(inputline);
		int bracket=s.indexOf("(");
		int colon = s.indexOf(":");
		int comma=s.indexOf(",");
		str[0]=s.substring(0, colon).toString();
		str[1]=s.substring(colon+2, bracket).toString();
		if(comma!=-1) {
			str[2]=s.substring(bracket+1, comma).toString();
			str[3]=s.substring(comma+1, s.length()-1).toString();
		}
		else {
			str[2]=s.substring(bracket+1, s.length()-1).toString();
			str[3]="0";
		}
		return str;
	}
	
}