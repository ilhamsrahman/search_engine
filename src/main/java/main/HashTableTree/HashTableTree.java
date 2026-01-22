package main.HashTableTree;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class HashTableTree
{
    private RecordManager recman;
    private HTree hashtable;

    public HashTableTree(String recordmanager, String objectname) throws IOException
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
    public void finalize() throws IOException
    {
        recman.commit();
        recman.close();
    }

    public void addEntry(String word, String url, int freq) throws IOException {
        if (hashtable.get(word) != null) {
            String tmp = hashtable.get(word) + url + ":::" + freq + "\n";

            hashtable.put(word, tmp);
        } else {

            String tmp = url + ":::" + freq + "\n";
            hashtable.put(word, tmp);
        }


    }
    public void delEntry(String word) throws IOException
    {
        if(hashtable.get(word)!=null){
            hashtable.remove(word);
        }

    }
    public String get(String word) throws IOException
    {
        return (String) hashtable.get(word);

    }

    public void totxt(String filename) throws IOException {
        FastIterator iter = hashtable.keys();
        String key;
        FileWriter file = new FileWriter(filename);
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        while ((key = (String) iter.next()) != null) {
            writer.println(key+": \n"+hashtable.get(key));
        }
        writer.close();
        file.close();
    }
}

