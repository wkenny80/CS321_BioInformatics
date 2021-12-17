import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * BTree class provides constructors and necessary methods for creating and
 * managing a BTree. The BTree uses the class BTreeNode and can be initialized 
 * with a cache.
 * @author Will Kenny
 * Other group members include Justin Halbert and KC Kircher
 */

public class BTree {
	private BTreeNode nodeMEM;
	private RandomAccessFile raFile;
	private BTreeNode root;
	private int t;	
	private int maxL;
	int cur;
	Cache cache;
	Boolean bool = false;
	Boolean found = false;
	Boolean useCache = false;

	/**
	 * Constructor
	 * @param t
	 * @param raFile
	 * @throws Exception
	 */
	public BTree(int t, RandomAccessFile raFile) throws Exception {
		cur = 0;
		maxL = (2*t)-1;
		this.t = t;
		this.raFile = raFile;
		initialize();
		root = new BTreeNode(allocateNode(), maxL, raFile);
		root.isLeaf = true;
		BTreeNode x = root;
		nodeMEM = x;
		x.setLeaf(1);
		x.setObjectCount(0);
		x.diskWrite();
	}

	/**
	 * Constructor
	 * @param raFile
	 * @param check
	 * @throws IOException
	 */
	public BTree(RandomAccessFile raFile, boolean check) throws IOException {

		nodeMEM = root;
		raFile.seek(0);
		this.t= raFile.readInt();
		maxL = (2*t)-1;
		root = new BTreeNode(4, maxL, raFile);
		root = root.diskReadR();
	}
	
	/**
	 * setUseCache
	 * @param inCache
	 */
	public void setUseCache(Cache inCache)	{
		useCache = true;
		cache = inCache;
	}
	
	/**
	 * allocateNode
	 * @return
	 * @throws Exception
	 */
	private int allocateNode() throws Exception	{

		int a = cur;
		raFile.seek(cur);
		raFile.writeInt(cur);
		raFile.writeInt(0);
		raFile.writeInt(0);
		cur += 3*4;

		// Init ptrs to -1
		for (int i = 1; i <= (2*t + 1); i++) {
			raFile.writeInt(-1);
			cur+=4;
		}

		// Init all Object values
		for (int i = 1; i <= (2*t - 1); i++) {
			raFile.writeLong(-1);
			raFile.writeInt(0);
			cur += 12;
		}
		return a;
	}

	/**
	 * initialize method
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		raFile.seek(0);
		raFile.writeInt(t);
		cur = 4;
		allocateNode();
	}

	/**
	 * splitTree method
	 * @param node
	 * @param index
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private boolean splitTree(BTreeNode node, int index, TreeObject in) throws Exception {

		BTreeNode tempN = node.diskRead(index);
		int object = tempN.getObjectCount();

		for (int i = 1; i <= object; i ++) {

			if (in.getValue() == tempN.getObject(i).getValue()) {

				tempN.getObject(i).incrFreq();
				tempN.diskWrite();

				return false;
			}
		}
		return true;
	}

	/**
	 * splitChild method
	 * @param parentNode
	 * @param index
	 * @throws Exception
	 */
	private void splitChild(BTreeNode parentNode, int index) throws Exception {

		// Creates a new node for after split
		BTreeNode rightNode = new BTreeNode(allocateNode(), maxL, raFile);		

		// Pull child
		BTreeNode leftNode = parentNode.diskRead(index);
		rightNode.isLeaf = parentNode.getLeaf();

		// Puts into right node 	
		for (int j = 1; j < t; j++) {
			rightNode.setObject(j, leftNode.getObject(j + t));
		}

		if (!leftNode.getLeaf()) {
			// arrange pointers
			for (int j = 1; j <= t; j++) {
				rightNode.setChild(j, leftNode.getChild(j + t));
			}
		}

		// add to right node child pointers
		for (int j = parentNode.getObjectCount()+1; j > index; j--) {
			parentNode.setChild(j+1, parentNode.getChild(j));
		}

		// Add
		parentNode.setChild(index + 1, rightNode.offset);

		// Create space 
		for (int j = parentNode.getObjectCount(); j >= index; j--) {
			parentNode.setObject(j+1, parentNode.getObject(j));
		}

		// Moving object from the left node to parent
		parentNode.setObject(index, leftNode.getObject(t));
		parentNode.setObjectCount(parentNode.getObjectCount()+1);

		for (int j = leftNode.getObjectCount(); j > t; j--) {
			leftNode.removeObject(j);
		}

		if (!leftNode.getLeaf()) {
			for (int j = leftNode.getChildPCount(); j > t; j--) {
				leftNode.removeChild(j);
			}
		}	
		leftNode.removeObject(t);

		// Update
		parentNode.setLeaf(0);
		rightNode.setObjectCount(t-1);
		leftNode.setObjectCount(t-1);
		
		// write
		leftNode.diskWrite();
		rightNode.diskWrite();		
		parentNode.diskWrite();		
	}
	
	/**
	 * incrementCache method: increments on node when object is found
	 * @param nodeMatch
	 * @param in
	 * @throws Exception
	 */
	private void incrementCache(BTreeNode nodeMatch, TreeObject in) throws Exception 
		{

			// no search if hit found
			if (cache.cacheCheck(in) == true)
			{
				nodeMatch = cache.returnHead();
				
				int i = nodeMatch.getObjectCount();
				for (int j = 1; j <= i; i++) {
					if (in.getValue()==nodeMatch.getObject(j).getValue()) {
						nodeMatch.getObject(j).incrFreq();
						nodeMatch.diskWrite();
						found = true;
						return;
					}
				}
			}
	}

	/**
	 * insertNode method: adds a node
	 * @param in
	 * @throws Exception
	 */
	public void insertNode(TreeObject in) throws Exception {

		BTreeNode temp = root;

		if (root.getObjectCount() == maxL) { // node is full

			BTreeNode nextRoot = new BTreeNode(allocateNode(), maxL, raFile);

			nextRoot.isLeaf = false;
			nextRoot.setObjectCount(0);
			nextRoot.setChild(1, temp.offset);

			if (!splitTree(nextRoot, 1, in) ) {
				return;
			}
			splitChild(nextRoot, 1);
			insertNonfull(nextRoot, in);

			root = nextRoot;
		} else {
			// check cache init first
			found = false;
			if (useCache) 
			{
				incrementCache(root, in);
			}
			// if not found
			if (found == false) {
			insertNonfull (root, in);
			}
		}
	}

	/**
	 * insertNonfull: finds a place to insert a node
	 * @param anc
	 * @param in
	 * @throws Exception
	 */
	private void insertNonfull(BTreeNode anc, TreeObject in) throws Exception 
	{
		int i = anc.getObjectCount();
		for (int j = 1; j <= i; j++) {

			if (in.getValue() == anc.getObject(j).getValue()) {
				anc.getObject(j).incrFreq();
				anc.diskWrite();
				
				if (useCache == true) {
				cache.addCache(anc);
				}

				return;
			}
		}
		if (anc.getLeaf()) {
			// Iterate until insert is located
			while (i >= 1 && in.getValue() < anc.getObject(i).getValue() ) {
				anc.setObject(i+1, anc.getObject(i));
				i--;		
			}

			anc.setObject(i+1, in);
			anc.setObjectCount(anc.getObjectCount()+1);
			anc.diskWrite();
			if (useCache == true) {
			cache.addCache(anc);
			}
		} else { 

			// Iterate until child node is located, store
			while (i >= 1 && in.getValue() < anc.getObject(i).getValue()) 
			{

				i--;
			}
			i++;		

			nodeMEM = anc.diskRead(i);

			if (nodeMEM.getObjectCount() == maxL) {
				if (!splitTree(anc, i, in)) {
					return;
				}
				splitChild(anc, i);
				nodeMEM = anc.diskRead(i);
				{
					i++;
					nodeMEM = anc.diskRead(i);
				}
			} 
			insertNonfull(nodeMEM, in);
		}
	}
 
	/**
	 * beginSearch method: starts search recursively at root of elements frequency
	 * @param k
	 * @return
	 */
	public int beginSearch(long k) 
	{
		if (useCache) { // if cache is being used, check for element
			TreeObject toCheck = new TreeObject(k);
			
			if (cache.cacheCheck(toCheck) == true) { // if hit
				return search(cache.returnHead(), k);
			}
		}
		// search
		return search(root, k);
	}

	/**
	 * search method: Recursive search of BTree
	 * @param node
	 * @param k
	 * @return
	 */
	private int search(BTreeNode node, long k) {
		try {
			int i = 1;

			while (i <= node.getObjectCount() && k > node.getObject(i).getValue()) {

				i++;
			}
			if (i <= node.getObjectCount() && k == node.getObject(i).getValue()) {
				if(useCache == true) {
					cache.addCache(node);
				}
				return node.getObject(i).getFrequency();
				
			} else if (node.getLeaf()) {

				return 0;
			} else {
				// new node
				BTreeNode newNode = node.diskRead(i);
				return search(newNode, k);
			}
		}

		catch(Exception e) {

			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * traverse method
	 * @param traverse
	 * @param fileW
	 * @param length
	 * @throws Exception
	 */
	private void traverse(BTreeNode traverse, FileWriter fileW, int length) throws Exception {
		int obj = traverse.getObjectCount();
		int child = traverse.getChildPCount();
		int val = length;

		for (int i = 1; i <= child; i++) {

			traverse(traverse.diskRead(i), fileW, val);

			if (obj >= i) {

				String string = convertBackToString(traverse.getObject(i).getValue(), traverse.getObject(i).getFrequency(), val);
				fileW.write(string + "\n");
			}	
		}

		if (traverse.getLeaf()) {

			for (int j = 1; j <= obj; j++) {

				String string = convertBackToString(traverse.getObject(j).getValue(), traverse.getObject(j).getFrequency(), val);
				fileW.write(string + "\n");
			}
		}	
	}

	/**
	 * traverseTree
	 * @param file
	 * @param longV
	 * @param length
	 * @throws Exception
	 */
	public void traverseTree(String file, int longV, int length) throws Exception {
		int s = GeneBankCreateBTree.sSize;
		
		FileWriter fileW = new FileWriter(new File(file + ".btree.dump." + length));
		traverse(root, fileW, length);
		fileW.close(); // close

		// print
		System.out.println("Result: " + file + ".btree.dump." + s);

	}

	/**
	 * convertBackToString
	 * @param Bstr
	 * @param freq
	 * @param lengthS
	 * @return
	 */
	private String convertBackToString(long Bstr, int freq, int lengthS) {

		String str = Long.toBinaryString(Bstr);
		StringBuilder strB = new StringBuilder();
		String val = strB.toString();
		int x = Math.abs(str.length()-(2*lengthS));

		for (int i = 0; i < x; i++) {

			strB.append('0');
		}

		strB.append(str);
		strB = new StringBuilder();

		for (int i = 0; i < val.length(); i+=2) {

			String subString = val.substring(i, i+2);

			if (subString.equals("00")) {
				strB.append('a');
				
			} else if(subString.equals("01")) {
				strB.append('c');
				
			} else if(subString.equals("10")) {
				strB.append('g');
				
			} else if(subString.equals("11")) {
				strB.append('t');
				
			}
		}

		strB.append(": " + freq);

		return strB.toString();
	}
	
	/**
	 * finish
	 * @throws Exception
	 */
	public void finish() throws Exception {	
		raFile.seek(4);
		root.diskWriteAsRoot();
	}
}
