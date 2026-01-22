package main.WebPage;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;


public class WebPage implements Serializable {
    private String _url;
    private String _title;
    private Set<String> _parentUrl;
    private Vector<String> _childUrl;
    private Map<String,Integer> _wordFreq;
    private long _date;
    private long _totalNumWord;
    private int _size;
    public WebPage(String url, String title, Vector<String> childUrl, Map<String,Integer> wordFreq, long date,long totalNumWord,int size){
        _date=date;
        _title=title;
        _wordFreq=wordFreq;
        _childUrl=childUrl;
        _url=url;
        _parentUrl=new HashSet<>();
        _totalNumWord=totalNumWord;
        _size=size;
    }
    public void addParent(String url){
        _parentUrl.add(url);
    }
    public long getTotalNumWord(){
        return _totalNumWord;
    }
    public String getTitle(){return _title;}
    public long getDate() {return _date;}
    public String getUrl(){return _url;}
    public int getSize(){return _size;}
    public Map<String,Integer> getWordFreq(){return _wordFreq;}
    public Set<String> getParent(){
        return _parentUrl;
    }
    public Vector<String> getChild(){
        return _childUrl;
    }

    /**
     * This function is used to display the result at the output page
     * @param score the calculated score of the page
     */
    public void displayInfo(double score){
        System.out.println("Score: "+score+"\nTitle: "+_title);
        System.out.println("url: "+_url);
        if(_date>=0)
            System.out.println("Last Update Date: "+new Date(_date)+" ,"+_size+" byte");
        else
            System.out.println("No last updated date"+" ,"+_size+" byte");
        int numDisplay= 0;
        System.out.println("Most Frequent Word: ");
        for(Map.Entry<String,Integer> p:_wordFreq.entrySet()){
            if(numDisplay>=5)
                    break;
            numDisplay++;
            System.out.print(p.getKey()+" : "+p.getValue()+" ; ");
        }
        System.out.println();
        System.out.println("Parent Link: ");
        numDisplay= Math.min(_parentUrl.size(), 5);
        for(int i=0;i<numDisplay;i++){
                System.out.println(_parentUrl.toArray()[i]);
        }
        numDisplay= Math.min(_childUrl.size(), 5);
        System.out.println("Child Link: ");
        for(int i=0;i<numDisplay;i++){
            System.out.println(_childUrl.get(i));
        }
    }

    public void displayInfoTxt(double score, PrintWriter wrter){
        wrter.println("Score: "+score+"\nTitle: "+_title);
        wrter.println("url: "+_url);
        if(_date>=0)
            wrter.println("Last Update Date: "+new Date(_date)+" ,"+_size+" byte");
        else
            wrter.println("No last updated date"+" ,"+_size+" byte");
        int numDisplay= 0;
        wrter.println("Most Frequent Word: ");
        for(Map.Entry<String,Integer> p:_wordFreq.entrySet()){
            if(numDisplay>=5)
                break;
            numDisplay++;
            wrter.print(p.getKey()+" : "+p.getValue()+" ; ");
        }
        wrter.println();
        wrter.println("Parent Link: ");
        numDisplay= Math.min(_parentUrl.size(), 5);
        for(int i=0;i<numDisplay;i++){
            wrter.println(_parentUrl.toArray()[i]);
        }
        numDisplay= Math.min(_childUrl.size(), 5);
        wrter.println("Child Link: ");
        for(int i=0;i<numDisplay;i++){
            wrter.println(_childUrl.get(i));
        }
    }

}
