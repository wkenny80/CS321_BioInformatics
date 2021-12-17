import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 * GeneBankCreateBTree class is the driver for the functionality of creating
 * a BTree from a gene file and storing it. This class reads in the options given it
 * by the user, creates and stores the corresponding GeneBank file.
 * @author Will Kenny
 * Other group members include Justin Halbert and KC Kircher
 */

public class GeneBankCreateBTree {
	static BTree bTree;
	static Cache cache;
	static int bSize = 4096;
	static int sSize = 0; 
	static int t = 0;
	static boolean debugCheck = false;
	
	static File file;
	static File fileA;
	static File dumpF;

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String args[]) {

		try {
			if (args.length < 4 || args.length > 6) {
				System.err.println("Error: wrong number of arguments");
				printUsage();
			}
			if (Integer.parseInt(args[0]) == 0 || Integer.parseInt(args[0]) == 1) {
				// no cache requested, set cachesize == zero
				if (Integer.parseInt(args[0]) == 0) {
					cache = new Cache(0);
				} else { // cache is specified wait for size
					
				}
				
			} else {
				System.err.println("Error: Cache argument = 0 or 1");
				printUsage();
			}

			if (args.length > 4) {
				if (args.length == 5) {
					if (cache == null) { // check if cache arg is 0
						cache = new Cache(Integer.parseInt(args[4]));
					}

					// If cache init to 0, then no argument
					if (cache.capacity() == 0 && (Integer.parseInt(args[4]) == 1 || Integer.parseInt(args[4]) == 0)) {
						if (Integer.parseInt(args[4]) == 1) {
							debugCheck = true;
						}
					} else if (cache.capacity() == 0 && (Integer.parseInt(args[4]) != 1 || Integer.parseInt(args[4]) != 0)) { // error handling
						System.err.println("Error: Debug argument = 0 or 1");
						printUsage();
					}
				}

				if (args.length == 6) {
					cache = new Cache(Integer.parseInt(args[4]));

					if (Integer.parseInt(args[5]) == 1){
						debugCheck = true;
					}
				}
			}

			// Set degree size
			if (Integer.parseInt(args[1]) == 0) {
				t = (bSize - 4) / 32;
			} else {      // If degree is not zero, then set degree to that argument.
				t = Integer.parseInt(args[1]); // take in degree
			}

			try {
				fileA = new File(args[2]);
			} catch (Exception e) {
				System.err.println("Error: File not found");
				printUsage();
			}

			// Take in sequence length.
			if (Integer.parseInt(args[3]) >= 1 || Integer.parseInt(args[3]) <= 31) {
				sSize = Integer.parseInt(args[3]); // Substring length
			} else {
				System.err.println("Error: sequence length must be between 1 and 31");
				printUsage();
			}
			// declare
			file = new File(args[2]+".btree.data." + sSize + "." + t); 
			RandomAccessFile randomAccess = new RandomAccessFile(file, "rw");
			bTree = new BTree(t, randomAccess);
			boolean foundS = false;
			String sStr = "";
			TreeObject obj;
			System.out.print("Generating BTree");
			StringBuilder strB = new StringBuilder();
			BufferedReader in = new BufferedReader(new FileReader(fileA)); 
			String token;
			int x = 0;
			
			
			if (cache.capacity()>=1) {
					bTree.setUseCache(cache);
			}
			

			while ((token = in.readLine()) != null)  {

				// scan
				Scanner scan = new Scanner(token);
				String str1 = token.replaceAll("\\s", "");
				String str = str1.replaceAll("\\d", "");

				if (str.equals("ORIGIN")) {

					foundS = true;
				} 

				else if (token.equals("//")) {

					foundS = false;
					strB = new StringBuilder();
				}

				else if (foundS == true) {

					for (int i =0; i < str.length(); i++) {

						char tokenF = str.charAt(i);	

						if (tokenF == 'n' || tokenF == 'N') {

							strB = new StringBuilder();
						} else if (tokenF == 'a' || tokenF == 't' || tokenF == 'c' || tokenF == 'g' || tokenF == 'A' || tokenF == 'T' || tokenF == 'C' || tokenF == 'G') {	

							strB.append(Character.toLowerCase(tokenF));
						}	

						if (strB.length() > sSize) {

							String st = strB.toString();

							strB = new StringBuilder();
							strB.append(st.substring(1, sSize +1));
						}

						if (sSize == strB.length()) {

							long stream = toLong(strB.toString());						
							obj = new TreeObject(stream);

							x++;
							if (x % 100==0)
							{
								System.out.print(".");
							}
							if (x % 2000==0)
							{
								System.out.print("\n");
							}
							
							bTree.insertNode(obj);
						}												
					}					
				}

				scan.close();
			}	

			bTree.finish();
			System.out.println("\n *BTree Generated*");
			
			if (debugCheck) {

				bTree.traverseTree(args[2], t, sSize);
			}

		} catch (FileNotFoundException f) {

			System.err.println("\nError: File not Found");
			printUsage();

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println(
					"\njava GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		} 
	}

	private static long toLong(String sString) {

		String biString = "";

		for (int i = 0 ; i < sSize ; i++) {

			if(sString.charAt(i) == 'a'|| sString.charAt(i) == 'A') {

				biString += "00";
				continue;
			} else if (sString.charAt(i) == 't'|| sString.charAt(i) == 'T') {

				biString += "11";
				continue;	
			} 

			else if(sString.charAt(i) == 'c'|| sString.charAt(i) == 'C') {

				biString += "01";
				continue;
			} 

			else if(sString.charAt(i) == 'g'|| sString.charAt(i) == 'G') {
				
				biString += "10";
				continue;
			}				
		}

		long stream = 0;
		int f = 1;

		for (int i = biString.length()-1; i >= 0; i--) {

			stream += ((int) biString.charAt(i) - 48) * f;
			f = f*2;
		}

		return stream;
	}

	private static void printUsage() {

		System.out.println("java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		System.exit(1);
	}
}