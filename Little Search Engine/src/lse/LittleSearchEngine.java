package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String, Occurrence> keywords = new HashMap<String, Occurrence>();
		
		Scanner sc = new Scanner(new File(docFile));
		
		while(sc.hasNext()) {
			String word = getKeyword(sc.next());
			
			if(word != null) {
				if(!keywords.containsKey(word)) {
					Occurrence first = new Occurrence(docFile, 1);
					keywords.put(word, first);
				}
				else
					keywords.get(word).frequency++;
			}
		}
		sc.close();
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for(String key : kws.keySet()) {
			Occurrence frequency = kws.get(key);
			ArrayList<Occurrence> toAdd = new ArrayList<Occurrence>();
			if(keywordsIndex.containsKey(key))
				toAdd = keywordsIndex.get(key);
			toAdd.add(frequency);
			insertLastOccurrence(toAdd);
			keywordsIndex.put(key,toAdd);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		if(word.equals("")||word == null)
			return null;
		word = word.toLowerCase();
		boolean isCorrect = false;
		boolean noNext = false;
		
		String keyword = "";
		
		for(int i = 0; i< word.length(); i++) {
			char curr = word.charAt(i);
			char next = 0;
			
			if(i == word.length() -1)
				noNext = true;
			else
				next = word.charAt(i+1);
			
			if(Character.isLetter(curr)) {
				keyword = keyword + curr;
				isCorrect = true;
			}
			else {
				if(!checkPunctuation(curr) && Character.isLetter(curr)) {
					isCorrect = false;
					break;
				}
				if(noNext || checkPunctuation(next))
					isCorrect = true;
				else {
					isCorrect = false;
					break;
				}
			}
		}
		
		if(keyword.equals(""))
			return null;
		if(isCorrect && !noiseWords.contains(keyword))
			return keyword;
		
		return null;
	}
	private static boolean checkPunctuation(char letter) {
		switch(letter) {
		case '.':
			return true;
		case ',':
			return true;
		case '?':
			return true;
		case ':':
			return true;
		case ';':
			return true;
		case '!':
			return true;
		}
		return false;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() == 1) {
			return null;
		}
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		Occurrence insert = occs.remove(occs.size()-1);
		int lo = 0;
		int hi = occs.size() - 1;
		int mid = (hi+lo)/2;
		
		if(occs.size() == 1) {
			midpoints.add(0);
			if(insert.frequency > occs.get(0).frequency)
				occs.add(0, insert);
			else
				occs.add(insert);
			return midpoints;
		}
		
		while(lo <= hi) {
			mid = (hi+lo)/2;
			midpoints.add(mid);
			if(occs.get(mid).frequency == insert.frequency)
				break;
			else if(occs.get(mid).frequency < insert.frequency)
				hi = mid - 1;
			else
				lo = mid + 1;
		}
		
		if(hi < lo)
			occs.add(lo, insert);
		else
			occs.add(mid,insert);
		
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> docs = new ArrayList<String>();
		
		ArrayList<Occurrence> occ = new ArrayList<Occurrence>();
		
		if(keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)) {
			ArrayList<Occurrence> list1 = keywordsIndex.get(kw1);
			ArrayList<Occurrence> list2 = keywordsIndex.get(kw2);
			
			int list1Index = 0;
			int list2Index = 0;
			
			while(list1Index < list1.size() && list2Index < list2.size()) {
				if(list1.get(list1Index).document.equals(list2.get(list2Index).document)) {
					if(list1.get(list1Index).frequency >= list2.get(list2Index).frequency)
						occ.add(list1.get(list1Index));
					else
						occ.add(list2.get(list2Index));
					
					insertLastOccurrence(occ);
					
					 list1Index++;
					 list2Index++;
				}
				else {
					if(list1.get(list1Index).frequency >= list2.get(list2Index).frequency) {
						occ.add(list1.get(list1Index));
						list1Index ++;
					}
					else {
						occ.add(list2.get(list2Index));
						list2Index ++;
					}
				}
			}
			if(list1Index == list1.size() && list2Index < list2.size()) {
				while(list2Index < list2.size()) {
					occ.add(list2.get(list2Index));
					list2Index++;
				}
			}
			else if(list2Index == list2.size() && list1Index < list1.size()) {
				while(list1Index < list1.size()) {
					occ.add(list1.get(list1Index));
					list1Index++;
				}
			}
			
			int index = 0;
			while(index < 5 && index < occ.size()) {
				if(!docs.contains(occ.get(index).document)) {
					docs.add(occ.get(index).document);
					index++;
				}
				else
					occ.remove(index);
			}
		}
		else if(keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)) {
			occ = keywordsIndex.get(kw1);
			int index = 0;
			while(index < 5 && index < occ.size()) {
				if(!docs.contains(occ.get(index).document)) {
					docs.add(occ.get(index).document);
					index++;
				}
				else
					occ.remove(index);
			}
		}
		else if(!keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)) {
			occ = keywordsIndex.get(kw2);
			int index = 0;
			while(index < 5 && index < occ.size()) {
				if(!docs.contains(occ.get(index).document)) {
					docs.add(occ.get(index).document);
					index++;
				}
				else
					occ.remove(index);
			}
		}
		return docs;
	
	}
}
