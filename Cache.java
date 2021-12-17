import java.util.NoSuchElementException;
import java.util.LinkedList;

/**
 * Cache class provides constructors and necessary methods for creating and
 * managing a cache. This cache can be utilized by the user to improve performance
 * of the GeneBank.
 * @author Will Kenny
 * Other group members include Justin Halbert and KC Kircher
 */

public class Cache {
	private int size = 0;
	private int maxCapacity;
	private double hitCount = 0;
	private double totalA = 0;
	private DLLNode<BTreeNode> head, tail;
	private LinkedList<BTreeNode> cache;
	
	/* constructor */
	public Cache() {
		maxCapacity = 100;
		head = tail = null;
		cache = new LinkedList<BTreeNode>();

	}

	/* Constructor for capacity */
	public Cache(int cap) 
	{
		head = tail = null;
		maxCapacity = cap;
		cache = new LinkedList<BTreeNode>();
	}
	
	/*
	 * goes through Nodes stored in cache, return true once a node matches objects value
	 */
	public Boolean cacheCheck(TreeObject in) {

		int i = size; // check # of nodes

		// Loop until all checked
		for (int j = 1; j <= i; j++) 
		{
			BTreeNode check = cache.get(j);
			int l = check.getObjectCount();

			// compare
			for (int k = 1; k <= l; k++) 
			{
				if (in.getValue() == check.getObject(k).getValue()) 
				{
					addCache(check);
					return true;
				}
			}
		}
		return false;
	}
	
	public BTreeNode returnHead() {
		return cache.getFirst();
	}
	
	public int capacity() {
		return maxCapacity;
	}
	
	public int size() { 
		return size;
	}
	
	public void addCache(BTreeNode addObject) {
		if (cache.size() < maxCapacity) {  // check
			if (cache.contains(addObject)) {
				cache.remove(addObject);
			}
			cache.addFirst(addObject);
		} else {
			if (cache.contains(addObject)) {
				cache.remove(addObject);
				cache.addFirst(addObject);
			} else { // make room
				cache.removeLast();
				cache.addFirst(addObject);
			}
		}
	}
	public BTreeNode get(BTreeNode T) {
		DLLNode<BTreeNode> curr = head;
		boolean f = false;
		while (curr != null && !f) {
			if (T.equals(curr.getElement())) { 
				f = true;
				hitCount++;

			} else {
				curr = curr.getNext();
			}
		}
		totalA++;
		if (!f) {
			add(T);  
			return null;
		}
		if (size() == 1) { 

			head = tail = curr;
		} else if (curr == tail) { 

			tail = curr.getPrev();
			curr.setPrev(null);
			tail.setNext(null);
			curr.setNext(head);
			head.setPrev(curr);
			head = curr;
		} else if (curr == head) {

			curr = head;
		} else { 
			curr.getPrev().setNext(curr.getNext());
			curr.getNext().setPrev(curr.getPrev());
			curr.setNext(head);
			head.setPrev(curr);
			head = curr;
		}

		return curr.getElement();
	}

	public void add(BTreeNode dataN) {

		DLLNode<BTreeNode> toA = new DLLNode<BTreeNode>(dataN);

		if (isEmpty()) { 

			head = tail = toA;
		} else if (size >= maxCapacity) { 

			removeLast(); 
			DLLNode<BTreeNode> current = head;
			head = toA;
			toA.setNext(current);
			current.setPrev(toA);

		} else {

			DLLNode<BTreeNode> current = head;  
			head = toA;
			toA.setNext(current);
			current.setPrev(toA);

		}
		size++;
	}

	public void remove(BTreeNode T) {

		if (isEmpty()) {

			throw new NoSuchElementException();
		}
		boolean f = false;
		DLLNode<BTreeNode> curr = head;

		while (curr != null && !f) {

			if (T.equals(curr.getElement())) { 

				f = true;
				hitCount++;
			} else {

				curr = curr.getNext(); 
			}
		}

		if (!f) {
			throw new NoSuchElementException();
		}

		if (size() == 1) { 

			head = tail = null;
		} else if (curr == head) { 

			head = curr.getNext();
		} else if (curr == tail) { 

			tail = curr.getPrev();
			curr.setPrev(null);
			tail.setNext(null);
		} else {    
			curr.getPrev().setNext(curr.getNext());
			curr.getNext().setPrev(curr.getPrev());
		}
		size--;
	}
	
	public void removeLast() {

		if (isEmpty()) { 

			throw new IllegalStateException();
		}
		if (size > 1) 
		{ 
			tail = tail.getPrev();
			tail.setNext(null);
		} 
		else {

			head = tail = null;
		}
		size--;
	}

	public void write(BTreeNode dataN) {
		
		boolean f = false;

		if (isEmpty()) { 
			throw new NoSuchElementException();
		}

		DLLNode<BTreeNode> curr = head;

		while (curr != null && !f) {

			if (dataN.equals(curr.getElement())) { 

				f = true;
			} else {
				curr = curr.getNext();   
			}
		}

		if (!f) {

			add(dataN);   
			throw new NoSuchElementException();
		}

		if (size() == 1) { 

			head = tail = curr;
		} else if (curr == head) {  

			curr = head;
		} else if (curr == tail) { 
			tail = curr.getPrev();
			tail.setNext(null);
			curr.setPrev(null);
			curr.setNext(head);
			head.setPrev(curr);
			head = curr;
		} else {   
			curr.getPrev().setNext(curr.getNext());
			curr.getNext().setPrev(curr.getPrev());
			curr.setNext(head);
			head.setPrev(curr);
			head = curr;
		}

	}
	
	public void clear() {

		head = null;  
		tail = null;
		size = 0;
	}
	
	public double getMissRate() {

		double missR = 0;
		missR = 1 - getHitRate(); 

		return missR;
	}

	public double getHitRate() {

		double hitR = 0;

		if (totalA != 0) {  
			hitR = hitCount / totalA;
		} else {  
			return 0.00;  
		}
		return hitR;
	}

	public boolean isEmpty() {

		if (size == 0) { 
			return true;
		} else {
			return false;
		}
	}
}