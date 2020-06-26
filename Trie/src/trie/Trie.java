package trie;

import java.util.ArrayList;

import javax.swing.tree.TreeNode;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	private static int samePrefix(String word1, String word2) {
		int index = -1;
		int minLength = Math.min(word1.length(), word2.length());
		for(int i = 0; i< minLength;i++) {
			if(word1.charAt(i) != word2.charAt(i))
				return index;
			index = i;
		}
		return index;
	}
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null,null,null);
		if(allWords.length == 0) {
			return root;
		}
		if(root.firstChild == null) {
			short num = 0;
			short endIndex =(short) (allWords[0].length()-1);
			Indexes currWrd = new Indexes(0,num, endIndex);
			root.firstChild = new TrieNode(currWrd,null,null);
		}		
		for(int i = 1; i < allWords.length; i++) {
			TrieNode ptr = root.firstChild, prev = ptr;
			int wordIndex = -1, startIndex = -1, endIndex = -1, sameIndex = 0;
			while(ptr != null) {
				wordIndex = ptr.substr.wordIndex;
				startIndex = ptr.substr.startIndex;
				endIndex = ptr.substr.endIndex;
				if(startIndex > allWords[i].length()) {
					prev = ptr;
					ptr = ptr.sibling;
					continue;
				}
				sameIndex = samePrefix(allWords[wordIndex].substring(startIndex, endIndex+1),allWords[i].substring(startIndex));
				
				if(sameIndex == -1) {
					prev = ptr;
					ptr = ptr.sibling;
				}
				else {
					sameIndex += startIndex;
					if(sameIndex == endIndex) {
						prev = ptr;
						ptr = ptr.firstChild;
					}
					else{
						prev = ptr;
						break;
					}
				}	
			}
			if(ptr == null) {
				Indexes temp = new Indexes(i,(short)startIndex, (short)(allWords[i].length()-1));
				prev.sibling = new TrieNode(temp,null,null);
			}
			else {
				TrieNode nodeTemp = prev.firstChild;
				Indexes indexTemp = new Indexes(prev.substr.wordIndex,(short)(sameIndex+1), prev.substr.endIndex);
				prev.substr.endIndex = (short)sameIndex;
				TrieNode nodeTemp1 = new TrieNode(new Indexes(i,(short)(sameIndex+1), (short)(allWords[i].length()-1)),null,null);
				prev.firstChild = new TrieNode(indexTemp, nodeTemp, nodeTemp1);
			}	
		}
		return root;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		TrieNode ptr = root.firstChild;
		ArrayList<TrieNode> words = new ArrayList<TrieNode>();
		while(ptr != null) {
			String fullWord = allWords[ptr.substr.wordIndex];
			String word = allWords[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1);
			if(checkPrefix(prefix,word) || checkPrefix(fullWord,prefix)) {
				if(ptr.firstChild != null) {
					words.addAll(completionList(ptr,allWords, prefix));
					//System.out.println(ptr.toString());
					ptr = ptr.sibling;
				}
				else {
					words.add(ptr);
					//System.out.println(ptr.toString());
					ptr = ptr.sibling;
				}
			}
			else {
				//System.out.println(ptr.toString());
				ptr = ptr.sibling;
			}
		}
		if(words.isEmpty())
			return null;
		else
			return words;
	}
	private static boolean checkPrefix(String wrd, String wrd1) {
		int minLength = Math.min(wrd.length(), wrd1.length());
		for(int i = 0; i< minLength;i++) {
			if(wrd.charAt(i) != wrd1.charAt(i))
				return false;
		}
		return true;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
