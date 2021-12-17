import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * The GeneBankSearch class is the driver for the functionality of reading in a GeneBank
 * file and searching the file. It takes arguments from the user and prints out the results.
 * @author Will Kenny
 * Other group members include Justin Halbert and KC Kircher
 */

public class GeneBankSearch {
	
	private static int useCache;
	private static int cacheSize;
	private static int sSize;
	private static int debugLevel = 1;
	private static String BTFile;
	private static String queryF;
	private static String fileTitle;
	private static File file;
	
	public static void main(String args[]) throws Exception {
		
		try {
			BTFile = args[1];
			queryF = args[2];
			useCache = Integer.parseInt(args[0]);
			
			File fileA = new File(BTFile);
			File fileB = new File(queryF);
			
			if (!fileA.exists() || !fileB.exists()) {
				
				throw new FileNotFoundException();
			}
			
			if (useCache == 1) {
				cacheSize = Integer.parseInt(args[3]); 
				
				if (args.length == 5) {
					
					debugLevel = Integer.parseInt(args[4]);
				}
			} else if (args.length == 5) {
				
				debugLevel = Integer.parseInt(args[4]);
			} else if (args.length == 4) {
				
				debugLevel = Integer.parseInt(args[3]);
			}
			
			if (debugLevel > 1 || debugLevel < 0) {
				
				throw new Exception();
			}
		} catch (FileNotFoundException e) {
			
			System.err.println("Error: files were not recognized.");
			System.exit(1);
		} catch (Exception e) {
			
			printUsageError();
			System.exit(1);
		}
		
		int i;
		
		for (i = 0; i < BTFile.length(); i++) {
			
			if (args[1].charAt(i) == '.') {
				
				break;
			}
			if (i == args[1].length() - 1) {
				
				throw new Exception();
			}
		}
		
		fileTitle = BTFile.substring(0, i);
		 
		int first = 0;
		int sec = 0;
		
		for (int j = BTFile.length()-1; j >= 0; j--) {
			
			if (BTFile.charAt(j) == '.') {
				
				if (first != 0 && sec == 0) {
					
					sec = j;
					break;
				}
				
				if (first == 0) {
					
					first = j;
				}
			}
		}
		
		sSize = Integer.parseInt(BTFile.substring(sec+1, first));
		
		String newFileName = fileTitle + "_query" + sSize + "_result";
		
		file = new File(newFileName); // take in file
		
		startSearch(sSize);
		System.out.println("\n completed search. results: " + newFileName);
	}
	
	private static void startSearch(int k) throws Exception {
		
		RandomAccessFile raF = new RandomAccessFile(new File(BTFile), "rw");
		BTree tree = new BTree(raF, true);
		FileWriter fileW = new FileWriter(file);
		File file = new File(queryF); 
		Scanner scan = new Scanner(file);
		String token;
		int x = 0;
		
		if (useCache==1) 
		{
			Cache inCache = new Cache(cacheSize); 
			tree.setUseCache(inCache);
		}
		
		while (scan.hasNextLine()) {
			// declare
			token = scan.nextLine().toLowerCase();
			int freq = tree.beginSearch(toLong(token));
			x++;
			if (x % 100==0) 
			{
				System.out.print(".");
			}
			if (x % 2000==0)
			{
				System.out.print("\n");
			}
			
			
			if (freq != 0) {
				
				String str = backToString(toLong(token), freq, sSize);
				
				fileW.write(str + "\n");
				
				if (debugLevel == 0) {
					
					System.out.println(str);
				}
			}
		}
		
		scan.close();
		fileW.close();
		
	}
	
	private static String backToString(long stream, int freq, int strLength) {
		
		String str = Long.toBinaryString(stream);
		StringBuilder strB = new StringBuilder();
		
		int x = Math.abs(str.length()-(2*strLength));
		
		for (int i = 0; i < x; i++) {
			
			strB.append('0');
		}
		
		strB.append(str);
		
		String val = strB.toString();
		
		strB = new StringBuilder();
		
		for (int i = 0; i < val.length(); i+=2) {
			
			String substr = val.substring(i, i+2);
			
			if (substr.equals("00")) {
				
				strB.append('a');
			}
			
			else if(substr.equals("01")) {
				
				strB.append('c');
			}
			
			else if(substr.equals("10")) {
				
				strB.append('g');
			}
			
			else if(substr.equals("11")) {
				
				strB.append('t');
			}
		}
		
		strB.append(": " + freq);
		return strB.toString();
	}
	
	private static long toLong(String subString) {	
		
		String biString = "";
		long stream = 0;
		int fac = 1;
		
		for (int i = 0 ; i < sSize ; i++) {
			
			if(subString.charAt(i) == 'a'||subString.charAt(i) == 'A') {
				
				biString += "00";
				continue;
			} 
			
			else if(subString.charAt(i) == 't'|| subString.charAt(i) == 'T') {
				
				biString += "11";
				continue;
			} 
			
			else if(subString.charAt(i) == 'c'|| subString.charAt(i) == 'C') {
				
				biString += "01";
				continue;
			} 
			
			else if(subString.charAt(i) == 'g'|| subString.charAt(i) == 'G') {
				
				biString += "10";
				continue;
			}				
		}
		
		for (int i = biString.length()-1; i >= 0; i--) {
			
			stream += ((int) biString.charAt(i) - 48) * fac;
			fac = fac*2;
		}
		
		return stream;
	}
	
	
	private static void printUsageError() {
		
		System.out.println("<0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
	}

}