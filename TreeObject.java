public class TreeObject {

	/**
	 * The TreeObject class
	 * @author Will Kenny
	 * Other group members include Justin Halbert and KC Kircher
	 */
	
	public int freq;
	private long Value;
	
	
	public TreeObject(long stream) {
		this.freq = 1;
		this.Value = stream;
	}

	public TreeObject(long stream, int frequency) {
		
		this.freq = frequency;
		this.Value = stream;
	}
	
	public void incrFreq() {
		
		this.freq++;
	}
	
	public long getValue() {
		
		long value = Value;
		return value;
	}
	
	public int getFrequency() {
		
		return freq;
	}

}
