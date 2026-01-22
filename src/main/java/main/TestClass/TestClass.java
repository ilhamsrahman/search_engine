package main.TestClass;

import main.WebPage.WebPage;
import org.htmlparser.util.ParserException;

import main.Searching.Searching;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@SuppressWarnings("unchecked")

public class TestClass {

    public TestClass () {
        System.out.println("Constructor");
    }

    public Map<String, Double> se_search(String[] entry) throws IOException, ParserException {
        // This is for testing the search engine
        Map<String, Double> returned = new HashMap<>();
        try {
            FileInputStream fileIn = new FileInputStream("crawledPage.ser");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            HashMap<String, WebPage> myObject = (HashMap<String, WebPage>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            Vector<String> s = Searching.retainSearchURL(entry);
            returned = Searching.rank(s, entry, myObject);
            int i = 0;
            for (Map.Entry<String, Double> content : returned.entrySet()) {
                if (i >= 50)
                    break;
                i++;
                myObject.get(content.getKey()).displayInfo(content.getValue());
                System.out.println("-----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Called");
        return returned;
    }
}
