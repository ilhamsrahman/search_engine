package main.TestProgram;
import java.io.File;
import java.io.IOException;

import main.HashTableTree.HashTableTree;
import main.InvertedIndex.InvertedIndex;

public class TestProgram {
    /**
     * Usage: translate the jdbm to txt
	 * It generate two txt: spider results for Phase 1, forwardIndex for debugging
     * @param args
     */
    public static void main(String[] args) {
		File delfile = new File("spider_results.txt");
		delfile.delete();
		delfile = new File("forwardIndex.txt");
		delfile.delete();
        try{
			InvertedIndex index = new InvertedIndex("printContent","ht1");
			index.totxt("spider_results.txt");
		}
		catch(IOException ex){
			System.err.println(ex.toString());
		}
		try{
			HashTableTree index = new HashTableTree("ForwardIndexTree","ht1");
			index.totxt("forwardIndex.txt");
		}
		catch(IOException ex){
			System.err.println(ex.toString());
		}
    }
}
