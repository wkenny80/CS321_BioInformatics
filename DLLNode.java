public class DLLNode<T> {
	private DLLNode<T> next; // ref of next node
	private DLLNode<T> prev; // ref of prev node
	private T obj;	// ref to object 

	/**
	 * DLLNode class provides a constructor and the necessary methods to create a 
	 * node appropriate for use within a double linked list.
	 * @author Will Kenny
	 * Other group members include Justin Halbert and KC Kircher
	 */
	
	/*
	 * Constructor
	 */
	public DLLNode(T obj) {
		setElement(obj);
		setNext(null);
	}
	
	/* 
	 * get element
	 */
	public T getElement() {
		return obj;
	}

	/*
	 * set element
	 */
	public void setElement(T element) {
		this.obj = element;
	}

	/*
	 * get next node
	 */
	public DLLNode<T> getNext() {
		return next;
	}

	/*
	 * set next
	 */
	public void setNext(DLLNode<T> next) {
		this.next = next;
	}

	
	/*
	 * get ref of previous node
	 */
	public DLLNode<T> getPrev() {
		return prev;
		
	}
	/*
	 * set previous
	 */
	public void setPrev(DLLNode<T> prev) {
		this.prev = prev;
	}
	
}