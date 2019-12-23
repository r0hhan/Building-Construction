import java.io.FileWriter;

public class MinHeap {
	public Building[] heap; // initialized heap of user defined datatype building
	public int size;		// stores current size of heap
	private int maxsize;	// stores maxsize of heap (does not exceed 2000)
	
	risingCity rc = new risingCity();
	RedBlackTree rbt = new RedBlackTree();
	
	private static final int FRONT=1;	// points to the top of the heap

	public MinHeap(int maxsize) {
		this.maxsize = maxsize;
	    this.size = 0;
	    heap = new Building[this.maxsize + 1];
	    heap[0] = new Building(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);	// 0th element is not counted in heap
	}
	
	private int parent(int pos) {			// returns parent of the key
		return pos/2;
	}

	private int leftChild(int pos) {		// returns left child of the key
		return 2*pos;
	}

	private int rightChild(int pos) {		// returns right child of the key
		return 2*pos+1;
	}

	private void swap(int pos1, int pos2) {	// function to swap buildings in heap
		Building tmp;
	    tmp = heap[pos1];
	    heap[pos1] = heap[pos2];
	    heap[pos2] = tmp;
	}

	private void minHeapify(int pos) {		// heapify reconstructs the minheap after every increment cycle
		int l = leftChild(pos), r = rightChild(pos), smallest;
    	if(l<=size && (heap[l].executed_time<heap[pos].executed_time || 
    		(heap[l].executed_time==heap[pos].executed_time && 
    		heap[l].building_num<heap[pos].building_num)))
    		smallest=l;
    	else
    		smallest=pos;
    	if(r<=size && (heap[r].executed_time<heap[smallest].executed_time || 
    			(heap[r].executed_time==heap[smallest].executed_time && 
    			heap[r].building_num<heap[smallest].building_num)))
    		smallest=r;
    	if(smallest!=pos) {
    		swap(pos, smallest);
    		minHeapify(smallest);
    	}
	}
	   
	public void insert(int buildingnum, int endtime) {			// insert new element into the heap
		rbt.insert(buildingnum, endtime);					// insert into red-black also when inserting in the heap
		heap[++size] = new Building(buildingnum, 0, endtime);	// actual insertion in heap
	    /*int current = size;
	    while (heap[current].executed_time < heap[parent(current)].executed_time ||
	    		(heap[current].executed_time == heap[parent(current)].executed_time &&
	    		heap[current].building_num < heap[parent(current)].building_num)) {
	    	swap(current,parent(current));
	    	current = parent(current);
	    }*/
	}
	
	public void minHeap() {					// calls heapify to construct the tree into a minheap
		for(int pos=size/2; pos>=1; pos--)
			minHeapify(pos);
	}
	   
	public Building remove() {				// removes the top of the heap
		Building popped = heap[FRONT];		// calls when the building is finished completion
	    heap[FRONT] = heap[size--];
	    minHeapify(FRONT);
	    return popped;
	}
	
	// this method increases counters just by 1 so we do not miss any instruction statements
	public int increaseKeyBeforeInputComplete(String[] checkPrint) {
		if(heap[FRONT].total_time-heap[FRONT].executed_time>1) {	// building completion taking more than 1day
			heap[FRONT].executed_time++;							// make appropriate incremental updates and return
			rbt.update(heap[FRONT].building_num, 1);
			return 1;
		}
		else {								// if executed time is just 1 short of total time
			boolean didOperate = false;		// checks if we executed a print statement on the day of completion of the building
			heap[FRONT].executed_time++;	// increment time and update heap
			rbt.update(heap[FRONT].building_num, 1);
			// if there exists a print statement on the day of completion, execute print statement first and then remove building
			if(rc.globalcounter == Integer.parseInt(checkPrint[0]) && checkPrint[1].equalsIgnoreCase("printbuilding")) {
				if(checkPrint[3].equals("0"))									// printbuilding(x) case
					rbtWriteToFile(Integer.parseInt(checkPrint[2]));
				else															// printbuilding(x,y) case
					rbtFindNodesInRange(Integer.parseInt(checkPrint[2]), Integer.parseInt(checkPrint[3]));
				didOperate=true;			// operated on the special case so we can return -1 and let the main function know
			}
			rbt.deleteNode(heap[FRONT].building_num);	// now delete and print in file
			Building building=remove();			
			writeToFile(building, rc.globalcounter);
			if(didOperate)
				return -1;
			
			return 0;									// if did no operate, just delete the building and return 0
		}
	}
	
	public int increaseKey(int increment) {					// this method is called when the file has no more inputs
		int required = heap[FRONT].total_time-heap[FRONT].executed_time;
		if(required > increment) {							// to the heap will be made we increment the counter by 5
			heap[FRONT].executed_time+=increment;			//  or difference between executedtime & totaltime whichever is less and then do
			rbt.update(heap[FRONT].building_num, increment);// the appropriate functions we have two different functions for increment
			minHeapify(FRONT);								// is since after the file is read completely, we can now keep incrementing
			return increment;								// the counter by 5 (or until the building finishes completion)
		}													// and hence can complete the tasks faster
		else {
			heap[FRONT].executed_time+=required;			// same as the case in increaseKeyBeforeInputComplete, we just dont check for
			rbt.deleteNode(heap[FRONT].building_num);		// print command since isnt going to be any
			Building building=remove();
			writeToFile(building, rc.globalcounter+required);
			return required;
		}
	}

	public void print() {									// prints the heap, for debugging purposes only
		if(size==1)
			System.out.print("PARENT:("+heap[FRONT].building_num+","+heap[FRONT].executed_time+","+heap[FRONT].total_time+")");
		for(int i=1; i<=size/2; i++){
			System.out.print("PARENT:("+heap[i].building_num+","+heap[i].executed_time+","+heap[i].total_time+")"+
					" LEFT CHILD:("+heap[2*i].building_num+","+heap[2*i].executed_time+","+heap[2*i].total_time+")");
			if(2*i+1<=size)
				System.out.print(" RIGHT CHILD:("+heap[2*i+1].building_num+","+heap[2*i+1].executed_time+","+heap[2*i+1].total_time+")");
			System.out.println();
		}
		System.out.println();
		rbt.print();									// calls to print the red-black as well, for debugging purposes only
	}

	   
	private void writeToFile(Building building, int time) {				// writes to file the building number and the globalcounter
		try {															// when the building finishes completion
			FileWriter fw = new FileWriter("output_file.txt", true);
			fw.write("("+building.building_num+","+time+")\r\n");
			fw.close();
		}
		catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void rbtWriteToFile(int key) {								// calls write to file in red-black 
		rbt.writeToFile(key);
	}
	
	public void rbtFindNodesInRange(int k1, int k2) {					// calls find nodes in range in red-black
		rbt.findNodesInRange(k1, k2);
	}
}