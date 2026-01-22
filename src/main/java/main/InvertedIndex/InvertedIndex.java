package main.InvertedIndex;
/* --
COMP336 Lab1 Exercise
Student Name:
Student ID:
Section:
Email:
*/

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class Posting implements Serializable
{
	public String doc;
	public int freq;
	Posting(String doc, int freq)
	{
		this.doc = doc;
		this.freq = freq;
	}
}

public class InvertedIndex
{
	private RecordManager recman;
	private HTree hashtable;

	public InvertedIndex(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		long recid = recman.getNamedObject(objectname);
			
		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( "ht1", hashtable.getRecid() );
		}
	}

/**
 * This func is very important to add change to db file
 */
	public void finalize() throws IOException
	{
		recman.commit();
		recman.close();				
	} 
/**
 * add Entry to jbdm
 * @param word the key
 * @param content the content
 * @throws IOException
 */
	public void addEntry(String word, String content) throws IOException
	{
		// Add a "docX Y" entry for the key "word" into hashtable
		hashtable.put(word,content);

	}


	/**
	 * delete Entry in jbdm
	 * @param word the key
	 * @throws IOException
	 */
	public void delEntry(String word) throws IOException
	{
		// Delete the word and its list from the hashtable
		hashtable.remove(word);

	}

	/**
	 * Turn the jbdm content to txt
	 * @throws IOException
	 */
	public void totxt(String a) throws IOException {
		FastIterator iter = hashtable.keys();
		String key;
		FileWriter file = new FileWriter(a);
		PrintWriter writer = new PrintWriter(a, "UTF-8");
		while ((key = (String) iter.next()) != null) {
			writer.println(hashtable.get(key));
		}
		writer.close();
		file.close();
	}
	
}
