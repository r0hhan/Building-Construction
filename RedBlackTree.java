import java.io.FileWriter;
import java.util.ArrayList;

/* Referenced
 * insert
 * and
 * delete
 * from
 * https://algorithmtutor.com/Data-Structures/Tree/Red-Black-Trees/
*/

class Node {
	int bnum; // holds the building number
	int etime; // holds the executed time
	int ttime; // holds the total time required
	Node parent; // pointer to the parent
	Node left; // pointer to left child
	Node right; // pointer to right child
	int colour; // 0. Black, 1. Red
}

public class RedBlackTree {
	private Node root;
	private Node PNULL;										// pseudo parent of the r-b tree

	private Node searchTreeHelper(Node node, int key) {		// searches for the node in the tree
		if(node == PNULL)
			return null;
		
		while(node!=PNULL) {
			if(key>node.bnum)
				node=node.right;
			else if(key<node.bnum)
				node=node.left;
			else {
				//System.out.println("FINDNODE VALUES "+node.bnum+" "+node.etime+" "+node.ttime);
				return node;
			}
		}
		return null;
	}

	private void swapNodes(Node u, Node v) {		// this function is called when a node with 1 or 2 children is deleted
		if(u.parent==null)							// it just swaps the positions of the two nodes
			root = v;
		else if(u==u.parent.left)
			u.parent.left = v;
		else
			u.parent.right = v;
		
		v.parent = u.parent;
	}
	
	private void deleteNodeHelper(Node node, int key) {			// deletes node from the tree
		Node z = searchTreeHelper(this.root, key);				// find the node containing key in the tree
		Node x, y;

		if(z==PNULL) {											// this will not happen in our program
			System.out.println("Couldn't find key in the tree");// just for debugging purposes
			return;
		}
		y = z;
		int yOriginalcolour = y.colour;
		if(z.left==PNULL) {										// node with only right child is deleted
			x = z.right;										// we swap those two nodes and delete the child (after swapNodes)
			swapNodes(z, z.right);
		}
		else if(z.right==PNULL) {								// mirror of the above case
			x = z.left;
			swapNodes(z, z.left);
		}
		else {													// if node has 2 children, choose minimum of the right subtree
			y = minimum(z.right);								// and swap nodes
			yOriginalcolour = y.colour;
			x = y.right;
			if(y.parent==z)
				x.parent = y;
			else {
				swapNodes(y, y.right);
				y.right = z.right;
				y.right.parent = y;
			}
			swapNodes(z, y);
			y.left = z.left;
			y.left.parent = y;
			y.colour = z.colour;
		}
		
		if(yOriginalcolour==0)
			fixDelete(x);										// calls fixDelete only if the node to be deleted at the start was black
	}
	
	private void fixDelete(Node x) {						// fix the red-black modified by delete operation						
		Node s;
		while(x!=root && x.colour==0) {
			if(x==x.parent.left) {
				s = x.parent.right;							// S is sibling of X
				if(s.colour==1) {							// case 3.1
					s.colour = 0;							// S is red
					x.parent.colour = 1;					// we switch the colors of S and X.parent
					leftRotate(x.parent);					// and then perform the left rotation on x.parent
					s = x.parent.right;						// this reduces it to either of case 3.2, 3.3, 3.4
				}

				if(s.left.colour==0 && s.right.colour==0) {	// case 3.2
					s.colour = 1;							// S is black and both of S's children are black and
					x = x.parent;							// color of X.parent can be red or black
				}											// switch the color of S to red, if the color of X.parent is red
															// we change its color to black and this transforms into case 3.4
															// otherwise we make X.parent the new X and repeat the process from case 3.1
				
				else {
					if(s.right.colour==0) {					// case 3.3
						s.left.colour = 0;					// S is black, S.left is red, S.right is black
						s.colour = 1;						// switch the colors of S and S.left and then perform a right rotation on S
						rightRotate(s);						// without violating any of the red-black properties
						s = x.parent.right;					// now the tree is transformed into into case 3.4
					}
					s.colour = x.parent.colour;				// case 3.4
					x.parent.colour = 0;					// S is black, S.right is red
					s.right.colour = 0;						// we change the color of S.right to black, X.parent to black
					leftRotate(x.parent);					// and perform the left rotation on X.parent
					x = root;								// in this way we remove the extra black node on X
				}
			}
			else {
				s = x.parent.left;
				if(s.colour==1) {							// case 3.1
					s.colour = 0;
					x.parent.colour = 1;
					rightRotate(x.parent);
					s = x.parent.left;
				}

				if (s.left.colour==0 && s.right.colour==0) {// case 3.2
					s.colour = 1;
					x = x.parent;
				}
				else {
					if (s.left.colour==0) {					// case 3.3
						s.right.colour = 0;
						s.colour = 1;
						leftRotate(s);
						s = x.parent.left;
					}
					s.colour = x.parent.colour;				// case 3.4
					x.parent.colour = 0;
					s.left.colour = 0;
					rightRotate(x.parent);
					x = root;
				}
			}
		}
		x.colour = 0;
	}
	
	private void fixInsert(Node y) {						// fix the red-black tree
		Node u;
		while(y.parent.colour==1) {							// fixInsert is called always, but worked on only when parent is red
			if(y.parent==y.parent.parent.right) {
				u = y.parent.parent.left; 					// uncle
				if (u.colour==1) {
					u.colour = 0;							// case 3.1
					y.parent.colour = 0;					// P(parent) is red and U(uncle) is also red
					y.parent.parent.colour = 1;				// we flip the color of nodes P, U and G (Grandparent)
					y = y.parent.parent;					// now P is black, U is black and G is red
				}
				else {										// case 3.2
					if(y==y.parent.left) {					// P is red and U is black or null
						y = y.parent;						// case 3.2.2
						rightRotate(y);						// P is right child of G and Y (node) is left child of P
					}										// do a right rotation at P, this reduces it to the case 3.2.1 
					y.parent.colour = 0;					// case 3.2.1
					y.parent.parent.colour = 1;				// P is right child of G and Y (node) is right child of P
					leftRotate(y.parent.parent);			// first perform the left rotation at G that makes G the new sibling S of Y
				}											// then we change the color of S to red and P to black
			}
			else {
				u = y.parent.parent.right; 					// uncle

				if(u.colour==1) {
					u.colour = 0;							// mirror case 3.1
					y.parent.colour = 0;
					y.parent.parent.colour = 1;
					y = y.parent.parent;	
				}
				else {
					if (y==y.parent.right) {
						y = y.parent;						// mirror case 3.2.2
						leftRotate(y);
					}
					y.parent.colour = 0;					// mirror case 3.2.1
					y.parent.parent.colour = 1;
					rightRotate(y.parent.parent);
				}
			}		
			if (y==root)
				break;
		}
		root.colour = 0;									// root always has black colour
	}

	private void printHelper(Node root, String indent, boolean last) {	// print the tree structure on the screen
	   	if (root!=PNULL) {												// for debugging purposes only
		   System.out.print(indent);
		   if(last) {
		      System.out.print("R----");
		      indent += "     ";
		   }
		   else {
		      System.out.print("L----");
		      indent += "|    ";
		   }
            String scolour;
           if(root.colour == 1)
        	   scolour = "RED";
           else
        	   scolour = "BLACK";
           
		   System.out.println("("+root.bnum+","+root.etime+","+root.ttime+")" + "(" + scolour + ")");
		   printHelper(root.left, indent, false);
		   printHelper(root.right, indent, true);
		}
	}
	
	private void updateHelper(Node node, int key, int increment) {		// function to update the executed time of red-black node
		while(node!=PNULL) {
			if(key>node.bnum)											// since I have written 2 functions for increaseKey
				node=node.right;										// increment increments the value accordingly
			else if(key<node.bnum)										
				node=node.left;
			else {
				node.etime+=increment;
				return;
			}
		}
	}
	
	private void writeToFileHelper(ArrayList<Node> list) {				// function for both print statements
		try {
    		FileWriter fw = new FileWriter("output_file.txt", true);	// prints out all the nodes from the list
    		if(list.size()!=0) {										// if its not the last element in the list, we add comma(,)
    			for(int i=0; i<list.size(); i++) {
    				if(i+1!=list.size())
    					fw.write("("+list.get(i).bnum+","+list.get(i).etime+","+list.get(i).ttime+"),");
    				else
    					fw.write("("+list.get(i).bnum+","+list.get(i).etime+","+list.get(i).ttime+")\r\n");
    			}
    		}
    		else
    			fw.write("(0,0,0)\r\n");								// writes (0,0,0) if no building exists in the list
    		fw.close();
    	}
    	catch(Exception e) {
    		System.err.println(e);
    		e.printStackTrace();
    	}
	}
	
	private void findNodesInRangeHelper(ArrayList<Node> list, Node node, int k1, int k2) {	// find nodes in range for printing
		if(node==PNULL)
			return;
		
	    if(node.bnum>=k1)												// while node value is greater than k1 keep going left
	        findNodesInRangeHelper(list, node.left, k1, k2);			// if you get null, the parent is added to list and
	    if(node.bnum>=k1 && node.bnum<=k2)								// now we keep going right till and do the same thing as we did
	    	list.add(node);												// for the left side
	    
	    if(node.bnum<k2)
	        findNodesInRangeHelper(list, node.right, k1, k2);
	    	
	    return;
	}

	public RedBlackTree() {									// initializing PNULL as NULL, which acts as a pseudo root
		PNULL = new Node();
		PNULL.colour = 0;
		PNULL.left = null;
		PNULL.right = null;
		root = PNULL;
	}

	public Node searchTree(int key) {						// search the tree for the key k and return the corresponding node
		return searchTreeHelper(this.root, key);
	}

	public Node minimum(Node node) {						// find the node with the minimum key
		while(node.left!=PNULL)
			node = node.left;
		
		return node;
	}

	public Node maximum(Node node) {						// find the node with the maximum key
		while(node.right!=PNULL)
			node = node.right;

		return node;
	}

	public void leftRotate(Node x) {						// rotate left at node x
		Node y = x.right;
		x.right = y.left;
		if(y.left!=PNULL)									// right child becomes the parent and the parent becomes its left child
			y.left.parent = x;

		y.parent = x.parent;
		if(x.parent==null)
			this.root = y; 
		else if(x==x.parent.left)
			x.parent.left = y;
		else
			x.parent.right = y;

		y.left = x;
		x.parent = y;
	}

	public void rightRotate(Node x) {						// rotate right at node x
		Node y = x.left;
		x.left = y.right;									// left child becomes the parent and the parent becomes its right child
		if(y.right!=PNULL)
			y.right.parent = x;

		y.parent = x.parent;
		if(x.parent==null)
			this.root = y;
		else if(x==x.parent.right)
			x.parent.right = y;
		else
			x.parent.left = y;

		y.right = x;
		x.parent = y;
	}

	public void insert(int bnum, int ttime) {	// insert the key to the tree in its appropriate position and fix the tree
		Node node = new Node();								// Ordinary Binary Search Insertion
		node.parent = null;
		node.bnum = bnum;
		node.etime = 0;
		node.ttime = ttime;
		node.left = PNULL;
		node.right = PNULL;
		node.colour = 1; 									// new node must be red

		Node y = null;
		Node x = this.root;

		while(x!=PNULL) {									// finding the position to insert the new node
			y = x;
			if(node.bnum<x.bnum)
				x = x.left;
			else
				x = x.right;
		}

		node.parent = y;									// y is parent of x
		if(y == null)										//  if tree is empty, make new node as root
			root = node;
		else if(node.bnum<y.bnum)
			y.left = node;
		else
			y.right = node;

		if(node.parent==null) {								// if new node is a root node, change colour to black and return
			node.colour = 0;
			return;
		}

		if(node.parent.parent==null)						// if the grandparent is null, simply return
			return;

		fixInsert(node);									// Fix the tree
	}

	public void deleteNode(int data) {						// delete the node from the tree
		deleteNodeHelper(this.root, data);
	}

	public void print() {								// print the tree structure on the screen
        printHelper(this.root, "", true);
	}
	
	public void update(int key, int increment) {			// updates the executed time of the given key in the tree
		updateHelper(this.root, key, increment);
	}
	
	public void writeToFile(int key) {						// puts the node to be printed in the list and calls the helper function
		Node node = searchTree(key);
		ArrayList<Node> list = new ArrayList<Node>();
		if(node!=null)
			list.add(node);
		writeToFileHelper(list);
	}
	
	public void findNodesInRange(int k1, int k2) {			// puts all the nodes in the range to be printed in the list and calls
		ArrayList<Node> list = new ArrayList<Node>();		// helper function
		findNodesInRangeHelper(list, this.root, k1, k2);
		writeToFileHelper(list);
	}
}