package main;


import main.Crawler.Crawler;
import main.HashTableTree.HashTableTree;
import main.InvertedIndex.InvertedIndex;
import main.StopStem.StopStem;
import main.TestClass.TestClass;
import main.WebPage.*;
import main.FrequentCount.*;


import java.io.*;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.lang.String;
import org.htmlparser.util.ParserException;

public class Main {
	/**
	 * 
	 * @param url The url for crawling
	 * @param numPage The number of page need to crawl and store in JDBM
	 * @return a Hashmap, key is the URL, Content is the URL Object
	 */

   public static HashMap<String,WebPage> crawling(String url,int numPage){
		Map<String,Long> items=new HashMap<>();
		Deque<String> urlList=new LinkedList<>();
		urlList.add(url);
        int crawledPage=0;
		Crawler crawler;
		HashMap<String,WebPage> webPageHashMap=new HashMap<>();

		/*Delete everything*/
	   File file=new File("ForwardIndexTree.db");
	   file.delete();
	   file=new File("ForwardIndexTree.lg");
	   file.delete();
	   file=new File("printContent.db");
	   file.delete();
	   file=new File("printContent.lg");
	   file.delete();
		try{
			InvertedIndex index = new InvertedIndex("printContent","ht1");
			HashTableTree forward_index = new HashTableTree("ForwardIndexTree","ht1");

			while(true){
				if(crawledPage>= numPage)
					break;
				crawler = new Crawler(urlList.peekFirst());
				if(!items.containsKey(urlList.peekFirst())||items.get(urlList.peekFirst())<crawler.getWebLastModifiedDay()){
					String jbdmindex=crawler.getURL();
					StringBuilder content= new StringBuilder();
					content.append("Page Title: ").append(crawler.getTitle()).append("\n");
					content.append("URL: ").append(crawler.getURL()).append("\n");

					if(crawler.getWebLastModifiedDay()>0){
						Date date=new Date(crawler.getWebLastModifiedDay());
						content.append("Last Modified Day: ").append(date).append("\n");
					}
					
					content.append("Keywords: \n");
					Vector<String> words = crawler.newExtractWords();

					/*Stemming: here*/
					StopStem stopStem = new StopStem("stopwords.txt");
					Vector<String> stemmedWords=new Vector<>();
					
					for(String word:words){
						word.toLowerCase().strip();
						if (stopStem.isStopWord(word.toLowerCase().strip()))
							continue;
						else
							stemmedWords.add(stopStem.stem(word.toLowerCase().strip()));
					}

					FrequentCount f=new FrequentCount();
					Map<String, Integer> wordFrequent=f.countFrequent(stemmedWords);
					int ctr=0;
					for (Map.Entry<String, Integer> entry : wordFrequent.entrySet()) {
						String word = entry.getKey();
						int frequency = entry.getValue();
						if(ctr<10) {
							content.append(word).append(" ").append(frequency).append(" ; ");
							ctr++;
						}
						forward_index.addEntry(word,crawler.getURL(),frequency);
					}
					content.append("\n");

					Vector<String> links = crawler.extractLinks();
					content.append("Children link\n");
					ctr=0;
					for(int i = 0; i < links.size(); i++){	
						if(ctr<10)
							content.append(links.get(i)).append("\n");
						urlList.add(links.get(i));
					}
					content.append("\n");
					content.append("------------------------------------------------------\n");
					WebPage wp=new WebPage(crawler.getURL(),crawler.getTitle(),crawler.extractLinks(),f.countFrequent(stemmedWords)/* Word list*/,crawler.getWebLastModifiedDay(),f.countFrequent(stemmedWords).size(),crawler.getSize());
					webPageHashMap.put(crawler.getURL(),wp);
					items.put(urlList.peekFirst(),crawler.getWebLastModifiedDay());
					crawledPage++;
					if(crawledPage==50)
						System.out.println("50 page have been crawled");
					if(crawledPage==150)
						System.out.println("150 page have been crawled");
					//System.out.println(content);
					index.addEntry(jbdmindex, content.toString());
				}
				urlList.removeFirst();
				if(urlList.isEmpty())
					break;			
			}
			index.finalize();
			forward_index.finalize();
		}
		catch (ParserException e ) {
			System.out.println("ParserException");
		}
		catch (IOException ie) {
			System.out.println("IOException");
		}
		catch (Exception ie)
		{
			System.out.println("other Exception");
		}
		return webPageHashMap;
   }
    public static void main (String[] args) throws ParserException, IOException {
		HashMap<String, WebPage> crawledPage = crawling("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm", 300); // change the URL/nummpage for crawling different website or getting different no. of page
		FrequentCount.addChild(crawledPage);
		System.out.println("Crawling finished");
		//Testing Display
		//WebPage wp=crawledPage.get("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");

		try {
			File file=new File("crawledPage.ser");
			file.delete();
			FileOutputStream fileOut = new FileOutputStream("crawledPage.ser");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(crawledPage);
			objectOut.close();
			fileOut.close();
			System.out.println("Crawled Page saved successfully at crawledPage.ser.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
