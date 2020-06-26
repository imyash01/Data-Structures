package lse;

import java.io.*;
import java.util.*;

public class test {
	
	LittleSearchEngine lse;
	
	public test() {
		lse = new LittleSearchEngine();
	}
	
	public void loadNoise() throws FileNotFoundException {
		Scanner sc = new Scanner(new File("noisewords.txt"));
		while (sc.hasNext()) {
			String word = sc.next();
			this.lse.noiseWords.add(word);
		}
		sc.close();
	}
	
	public void getWordTester() throws FileNotFoundException {
		this.loadNoise();
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter input: ");
		String st = lse.getKeyword(sc.next());
		System.out.println();
		System.out.println(st);
		sc.close();
	}
	
	public void loadKeyWordsTester() throws FileNotFoundException {
		this.loadNoise();
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter input file: ");
		HashMap<String,Occurrence> keyHash = lse.loadKeywordsFromDocument(sc.next());
		sc.close();
		Set<String> keySet = keyHash.keySet();
		Iterator<String> keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			String st = keyIt.next();
			System.out.print(st + " " + keyHash.get(st).frequency + "\n");
		}
	}
	
	public void makeIndexTester() throws FileNotFoundException {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter the docFile: ");
		String docFile = sc.next();
		System.out.println();
		System.out.print("Enter the noiseFile: ");
		String noiseWordsFile = sc.next();
		System.out.println();
		
		this.lse.makeIndex(docFile, noiseWordsFile);
		
		HashMap<String, ArrayList<Occurrence>> keyHash = this.lse.keywordsIndex;
		
		Set<String> allKeys = keyHash.keySet();
		
		Iterator<String> keyIterator = allKeys.iterator();
		
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			ArrayList<Occurrence> occList = keyHash.get(key);
			System.out.print(key + "\t: ");
			for (int i = 0; i < occList.size(); i++) {
				Occurrence occObj = occList.get(i);
				System.out.print("(" + occObj.document + ", " + occObj.frequency + ") --> ");
			}
			System.out.println();
		}
		
		sc.close();
	}
	
	public void top5Tester() throws FileNotFoundException {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter the docFile: ");
		String docFile = sc.next();
		System.out.println();
		System.out.print("Enter the noiseFile: ");
		String noiseWordsFile = sc.next();
		System.out.println();
		
		this.lse.makeIndex(docFile, noiseWordsFile);
		
		String quit = "no";
		
		while (!"quit".equals(quit)) {
			System.out.print("Enter kw1: ");
			String kw1 = sc.next();
			System.out.println();
			System.out.print("Enter kw2: ");
			String kw2 = sc.next();
			System.out.println();
			
			ArrayList<String> result = this.lse.top5search(kw1, kw2);
			
			for (int i = 0; i < result.size(); i++) {
				System.out.print(result.get(i) + "\t");
			}
			System.out.println();
			System.out.print("Enter 'quit' to quit, anything else to continue: ");
			quit = sc.next();
			System.out.println();
		}
		sc.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		test driver = new test();
		
		Scanner sc = new Scanner(System.in);
		
		System.out.print("(a)test getWord\t(b)test loadKeyWords\t(c)test makeIndex\t(d)test top5search: ");
		String option = sc.next();
		System.out.println();
		
		switch (option) {
		
			case "a" :
				driver.getWordTester();
				break;
			case "b" :
				driver.loadKeyWordsTester();
				break;
			case "c" :
				driver.makeIndexTester();
				break;
			case "d" :
				driver.top5Tester();
				break;
			default :
				sc.close();
		}
	}

}
