import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

/**
 * BTreeNode class provides a Node object useful in populating the BTree used by GeneBankCreateBTree.
 * Other methods, getters, and setters are also included. The class also has methods for reading and
 * writing to disk. 
 * @author Will Kenny
 * Other group members include Justin Halbert and KC Kircher
 */

public class BTreeNode {

	private List<BTreeNode> children;
	private List<TreeObject> objects;
	private List<Integer> childP;
	
	private BTreeNode parent;
	private RandomAccessFile raFile;
	
	private int parentP;
	private int objectCount;
	private int maxP;
	
	public int maxO;
	int offset; // points to 1st byte
	boolean isLeaf;
	
	/**
	 * BTreeNode Constructor
	 * @param offset
	 * @param maxO
	 * @param raFile
	 */
	public BTreeNode(int offset, int maxO, RandomAccessFile raFile) {
		
		this.offset = offset;
		this.raFile = raFile;
		this.maxO = maxO;
		this.maxP = maxO + 1;
		this.objectCount = 0;
		objects = new ArrayList<TreeObject>();
		objects.add(new TreeObject(-1, -1));
		parentP = 0;
		childP = new ArrayList<Integer>();
		childP.add(-1);
		children = new ArrayList<BTreeNode>();
		children.add(null);
		
	}
	
	/**
	 * objectCheck method
	 * @param in
	 * @return
	 */
	public boolean objectCheck(TreeObject in) {
		if (objects.contains(in)) 
		{
			return true;
		}
		return false;
	}
	
	/**
	 * setParent method
	 * @param n
	 */
	public void setParent(BTreeNode n) {
		
		this.parent = n;
	}
	
	/**
	 * setObjectCount method
	 * @param x
	 */
	public void setObjectCount(int x) {
		
		this.objectCount = x;
	}
	
	/**
	 * setParentP
	 * @param p
	 */
	public void setParentP(int p) {
		
		this.parentP = p;
	}
	
	/**
	 * setObjects
	 * @param list
	 */
	public void setObjects(List<TreeObject> list) {
		
		this.objects = list;
	}
	
	/**
	 * setObject
	 * @param index
	 * @param T
	 */
	public void setObject(int index, TreeObject T) {
		
		if (index < objects.size()) {
			
			objects.set(index, T);
		}
		
		else {
			
			objects.add(T);
		}
	}
	
	/**
	 * setLeaf method
	 * @param x
	 */
	public void setLeaf(int x) {
		
		if (x == 1) {
			
			isLeaf = true;
		}
		
		else isLeaf = false;
	}
	
	/**
	 * setChild method
	 * @param index
	 * @param n
	 */
	public void setChild(int index, int n) {
		
		if (index < childP.size()) {
			
			childP.set(index, n);
		}
		
		else if (index == childP.size()) {
			
			childP.add(n);
		}
	
	}

	/**
	 * setChildP method
	 * @param list
	 */
	public void setChildP(List<Integer> list) {
		
		this.childP = list;
	}
	
	/**
	 * getChildPCount method
	 * @return
	 */
	public int getChildPCount() {
		
		return childP.size() - 1;
	}
	
	/**
	 * getParentP method
	 * @return parentP
	 */
	public int getParentP() {
		
		return parentP;
	}
	
	/**
	 * getObjectCount method
	 * @return object count
	 */
	public int getObjectCount() {
		
		return this.objectCount;
	}
	
	/**
	 * getChild method
	 * @param index
	 * @return
	 */
	public int getChild(int index) {
		
		return this.childP.get(index);
	}
	
	/**
	 * getObject method
	 * @param index
	 * @return
	 */
	public TreeObject getObject(int index) {
		
		TreeObject object = objects.get(index);
		
		return object;
	}

	/**
	 * getLeaf
	 * @return
	 */
	public boolean getLeaf() {
		
		if (childP.size() == 1) {
			
			return true;
		}
		
		else return false;
	}
	
	/**
	 * removeChild
	 * @param index
	 */
	public void removeChild(int index) {
		
		childP.remove(index);
	}
	
	/**
	 * removeObject
	 * @param index
	 */
	public void removeObject(int index) {
		
		objects.remove(index);
	}
	
	/**
	 * addObject
	 * @param index
	 * @param object
	 */
	public void addObject(int index, TreeObject object) {
		
		objects.add(index, object);
	}
	
	/**
	 * diskWrite method
	 * @throws Exception
	 */
	public void diskWrite() throws Exception {
		
		raFile.seek(offset); 
		raFile.writeInt(offset);
		raFile.writeInt(getObjectCount());
	    
	    if (isLeaf) {
	    	
	    	raFile.writeInt(1);
	    }
	    
	    else { 
	    	raFile.writeInt(0);
	    }
	    
	    raFile.writeInt(parentP);
	    
	    int i, j;
	    
	    for (i = 1; i < childP.size(); i++) {
	    	
	    	if (i <= maxO+1) {
	    		
	    		raFile.writeInt(childP.get(i));
	    	}
	    }
	    for (j = i; j <= maxP; j++) {
	    	
	    	raFile.writeInt(-1);
	    }
	    
	    for (i = 1; i <= objectCount; i++) {
	    	
	    	if (i <= maxO) {
	    		
	    		raFile.writeLong(objects.get(i).getValue());
	    		raFile.writeInt(objects.get(i).getFrequency());
	    	}
	    }
	    
	    for (j = i; j <= maxO; j++) {
	    	
	    	raFile.writeLong(-1);
	    	raFile.writeInt(0);
	    }	    
	}
	
	/**
	 * diskWriteAsRoot method
	 * @throws Exception
	 */
	public void diskWriteAsRoot() throws Exception {
		
		raFile.seek(4); 
		raFile.writeInt(offset);
		raFile.writeInt(getObjectCount());
	    
	    if (isLeaf) {
	    	
	    	raFile.writeInt(1);
	    } else {
	    	raFile.writeInt(0);
	    }
	    
	    raFile.writeInt(parentP);
	    
	    int i, j;
	    
	    for (i = 1; i < childP.size(); i++) {
	    	
	    	if (i <= maxO+1) {
	    		
	    		raFile.writeInt(childP.get(i));
	    	}
	    }
	    
	    for (j = i; j <= maxP; j++) {
	    	
	    	raFile.writeInt(-1);
	    }
	    
	    for (i = 1; i <= objectCount; i++) {
	    	
	    	if (i <= maxO) {
	    		
	    		raFile.writeLong(objects.get(i).getValue());
	    		raFile.writeInt(objects.get(i).getFrequency());
	    	}
	    }
	    
	    for (j = i; j <= maxO; j++) {
	    	
	    	raFile.writeLong(-1);
	    	raFile.writeInt(0);
	    }	    
	}
	
	/**
	 * diskRead method
	 * @param childI
	 * @return
	 * @throws Exception
	 */
	public BTreeNode diskRead(int childI) throws Exception {
		
		BTreeNode nextNode = new BTreeNode(childP.get(childI), maxO, raFile);
		
		raFile.seek(childP.get(childI));
	    raFile.readInt();
	    
	    nextNode.setObjectCount(raFile.readInt());
	    nextNode.setLeaf(raFile.readInt());
	    
	    raFile.readInt();
	    
	    nextNode.setParentP(offset);
	    
	    List<Integer> pointer = new ArrayList<Integer>();
	    pointer.add(-1);
	    
	    for (int i = 0; i < maxP; i++) {
	    	
	    	int x = raFile.readInt();
	    	if (x != -1) {
	    		
	    		pointer.add(x);
	    	}
	    }
	    
	    nextNode.setChildP(pointer);
	    
	    List<TreeObject> Objects = new ArrayList<TreeObject>();
	    Objects.add(null);
	    
	    for (int i = 0; i < maxO; i++) {
	    	
	    	long x = raFile.readLong();
	    	int y = raFile.readInt();
	    	
	    	if (x != -1) {
	    		
	    		Objects.add(new TreeObject(x, y));
	    	}
	    }
	    
	    nextNode.setObjects(Objects);
	    return nextNode;
	}
	
	/**
	 * diskReadR
	 * @return
	 * @throws IOException
	 */
	public BTreeNode diskReadR() throws IOException {
		
		BTreeNode newNode = new BTreeNode(4, maxO, raFile);
		
		raFile.seek(4);
		raFile.readInt();
	    
	    newNode.setObjectCount(raFile.readInt());
	    newNode.setLeaf(raFile.readInt());
	    
	    raFile.readInt();
	    
	    newNode.setParentP(offset);
	    
	    List<Integer> cPoint = new ArrayList<Integer>(); // set child pointers
	    cPoint.add(-1);
	    
	    for (int i = 0; i < maxP; i++) {
	    	
	    	int r = raFile.readInt();
	    	
	    	if (r != -1) {
	    		
	    		cPoint.add(r);
	    	}
	    }
	    
	    newNode.setChildP(cPoint);
	    
	    List<TreeObject> treeO = new ArrayList<TreeObject>(); // set objects
	    
	    treeO.add(null);
	    
	    for (int i = 0; i < maxO; i++) {
	    	
	    	long l = raFile.readLong();
	    	int r = raFile.readInt();
	    	
	    	if (l != -1) {
	    		
	    		treeO.add(new TreeObject(l, r));
	    	}
	    }
	    
	    newNode.setObjects(treeO);
	    
	    return newNode;
	} 
}