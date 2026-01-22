package main.Searching;
import org.htmlparser.util.ParserException;

import main.Crawler.Crawler;
import main.FrequentCount.*;

import java.io.IOException;
import java.util.*;

import main.HashTableTree.HashTableTree;
import main.StopStem.StopStem;
import main.WebPage.*;

@SuppressWarnings("unchecked")

public class Searching {
    /**
     * find if the 'searchWords' is within 'words'
     * @param words word list
     * @param searchWords the word need to be search
     * @return true if found the word
     */
    public static boolean searchConsecutiveWords(Vector<String> words, Vector<String> searchWords) {
        int windowSize = searchWords.size();
        int size = words.size();
        for (int i = 0; i <= size - windowSize; i++) {
            boolean match = true;
            for (int j = 0; j < windowSize; j++) {
                if (!words.get(i + j).equals(searchWords.get(j))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    /**
     * Decide which URL be return
     * OR is use while no phrase (ALL page contain AT LEAST ONE of the STEMMED query will be returned)
     * AND is use while have phrase(only the page with EXACTLY the same STEMMED phrase will be return)
     *
     * IMPORTANT: If the query have phrase, It only consider the Phrase term, base on AND mentioned above(since all others term are consider optional)
     *
     * @param userInput the query, i.e. the user input, it should only consist one word, or one phrase
     * @return a URL list that contain the query
     */
    public static Vector<String> retainSearchURL(String[] userInput) throws IOException, ParserException {
        StopStem stopStem = new StopStem("stopwords.txt");
        Vector<String> stemmedWords=new Vector<>();
        HashTableTree fd=new HashTableTree("ForwardIndexTree","ht1");
        Set<String> url_search=new HashSet<>();
        boolean anyPhrase=false;
        String p_content="";
        for(String word:userInput){
            if(word==null|| word.isEmpty())
                continue;
            if(word.charAt(0)=='\"'){
                p_content=word;
                stemmedWords.clear();//remove everything
                anyPhrase=true;
                String[] parts = word.split("\"|\\s");
                for (String phaseWord: parts){
                    if (stopStem.isStopWord(phaseWord.toLowerCase().strip())|| Objects.equals(phaseWord, ""))
                        continue;
                    else
                        stemmedWords.add(stopStem.stem(phaseWord.toLowerCase().strip()));
                }
                break;
            }
            if (stopStem.isStopWord(word.toLowerCase().strip()))
                continue;
            else
                stemmedWords.add(stopStem.stem(word.toLowerCase().strip()));
        }
        Vector<String> removeURL=new Vector<>();
        if(!anyPhrase) {
            for (String word : stemmedWords) {
                String fdindex = fd.get(word);
                Map<String, Integer> returnedMap = FrequentCount.parseForwardIndex(fdindex);
                url_search.addAll(returnedMap.keySet());
            }
        }
        else{
            if(!stemmedWords.isEmpty()){
                Set<String>[] url_set = new Set[stemmedWords.size()];
                for(int i=0;i<stemmedWords.size();i++){
                    url_set[i]=new HashSet<>();
                }
                for(int i=0;i<stemmedWords.size();i++){
                    String fdindex = fd.get(stemmedWords.get(i));
                    Map<String, Integer> returnedMap = FrequentCount.parseForwardIndex(fdindex);
                    url_set[i].addAll(returnedMap.keySet());
                }
                url_search = new HashSet<>(url_set[0]);
                for(Set<String> s: url_set){
                    url_search.retainAll(s);
                }
            }
            if(stemmedWords.size()>1) {
                //look for if phrase exist
                for (String url : url_search) {
                    Crawler crwlercrawler = new Crawler(url);
                    Vector<String> words = crwlercrawler.newExtractWords();
                    Vector<String> stemWord=new Vector<>();
                    for (String word : words) {
                        if (stopStem.isStopWord(word.toLowerCase().strip()) || Objects.equals(word, "") || word.isEmpty()) {
                            continue;
                        } else {
                            stemWord.add(stopStem.stem(word.toLowerCase().strip()));
                        }
                    }
                    String[] k=p_content.split(" |\"");
                    Vector<String> wd=getStrings(k);
                    boolean foundText = searchConsecutiveWords(stemWord,wd);
                    if(!foundText){
                        removeURL.add(url);
                    }
                }
            }

        }
        removeURL.forEach(url_search::remove);

        return new Vector<>(url_search);

    }

    /**
     * CALCULATE THE SCORE BASE ON TF-IDF AND COSINE SIMILARITY, AND PageRank
     * @param url_list the urlList of the page need to be returned
     * @param entry the query, i.e. the user input, it should only consist one word, or one phrase
     * @param pageInfo The crawled Page HashMap, key is the URL, value id the WebPage class of the website
     * @return A Sorted URL Map, base on the Score
     * @throws IOException
     */
    public static Map<String,Double> rank(Vector<String> url_list, String[] entry, Map<String,WebPage> pageInfo) throws IOException {
        if(url_list.isEmpty()){
            return new HashMap<>();//no search result
        }
        int totalNumPage=pageInfo.size();
        HashTableTree fd=new HashTableTree("ForwardIndexTree","ht1");
        Vector<String> stemmedEntry = getStrings(entry);

        Map<String,Vector<Double>> url_tfidf=new HashMap<>();
        for(String url:url_list){
            url_tfidf.put(url,new Vector<>());
            for(String word:stemmedEntry){
                String ctnt=fd.get(word);
                Map<String,Integer> url_freq=FrequentCount.parseForwardIndex(ctnt);
                int numWord=url_freq.getOrDefault(url,0);

                double tf= (double) numWord /pageInfo.get(url).getTotalNumWord();
                double idf=Math.log10((double)totalNumPage /Math.max(url_freq.size(),1));// one apply prevent div 0
                double tfidf=tf*idf;
                url_tfidf.get(url).add(tfidf);
            }
        }
        //Now, every word tf-idf score have been calculated for all word in all URL;

        //calculate how many word in the query is in the title
        // use for later give bonus to the URL
        Map<String,Integer> url_QueryOnTopic=new HashMap<>();
        for(String st:url_list){
            String pageTitle=pageInfo.get(st).getTitle();
            String[] splitedTitle=pageTitle.split(" ");
            Vector<String> stemmedTitle=getStrings(splitedTitle);
            int numEntryOnTitle = 0;
            for(String stemword:stemmedEntry){
                if(stemmedTitle.contains(stemword)){
                    numEntryOnTitle++;
                }
            }
            url_QueryOnTopic.put(st,numEntryOnTitle);
        }
        Vector<Double> weightQuery=new Vector<>();
        for(String ignored :stemmedEntry){
            weightQuery.add(((double)1/stemmedEntry.size()));
        }

        Map<String,Double> url_cosine=new HashMap<>();
        for(String url:url_list){
            double cossim=0;
            double cossin_denominator=Math.sqrt(sumDVec(weightQuery)*sumDVec(url_tfidf.get(url)));
            for(int i=0;i<weightQuery.size();i++){
                cossim+=weightQuery.get(i)*url_tfidf.get(url).get(i);
            }
            cossim=cossim/cossin_denominator;
            url_cosine.put(url,cossim);
        }
//cos sim calculated

        //apply weight to prevent dominate by PR score
        double weight=0.015;
        Map<String,Double>url_PR=calculateUrl_PR(pageInfo);
        url_cosine.replaceAll((k, v) -> v + url_PR.get(k)* weight);

        //apply bonus
        for(String url:url_list){
            double raw_score=url_cosine.get(url);
            double new_score=raw_score*(1+0.75*url_QueryOnTopic.get(url));
            url_cosine.put(url,new_score);
        }





//Sort the return list base on score
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(url_cosine.entrySet());
        Comparator<Map.Entry<String, Double>> valueComparator = Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder());
        entryList.sort(valueComparator);
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry2 : entryList) {
            sortedMap.put(entry2.getKey(), entry2.getValue());
        }

        return sortedMap;
    }
    public static double sumDVec(Vector<Double> dbl){
        double sum=0;
        for(double num:dbl){
            sum+=num;
        }
        return sum;
    }

    public static Map<String,Double> calculateUrl_PR(Map<String,WebPage> pageInfo){
        Map<String,Double> url_PagePR=new HashMap<>();
        //damping factor
        double prFactor=0.015;

        //initialize weight to 0.1
        for(Map.Entry<String,WebPage> entry:pageInfo.entrySet()){
            url_PagePR.put(entry.getKey(),0.1);
        }
        //do 2 iterations
        for(int i=0;i<2;i++) {
            for (Map.Entry<String,WebPage> entry:pageInfo.entrySet()) {
                double newPRscore = 0;
                Set<String> parentURL = pageInfo.get(entry.getKey()).getParent();
                for (String s : parentURL) {
                    newPRscore += url_PagePR.get(s) / pageInfo.get(s).getChild().size();
                }
                newPRscore = (prFactor) * newPRscore + 1 - prFactor;
                url_PagePR.put(entry.getKey(), newPRscore);
            }
        }
        return url_PagePR;
    }


    /**
     * parse String base on stopwords.txt
     * @param entry the user entry
     * @return stemmed entry
     */
    public static Vector<String> getStrings(String[] entry) {
        StopStem stopStem = new StopStem("stopwords.txt");
        Vector<String> stemmedEntry=new Vector<>();
        for(String str: entry){

            if (str==null|| str.isEmpty())
                continue;
            if(str.charAt(0)=='\"'){
                String[] parts = str.split("\"|\\s");
                for (String phaseWord: parts){
                    if (stopStem.isStopWord(phaseWord.toLowerCase().strip())|| Objects.equals(phaseWord, ""))
                        continue;
                    else
                        stemmedEntry.add(stopStem.stem(phaseWord.toLowerCase().strip()));
                }
                continue;
            }
            if (stopStem.isStopWord(str.toLowerCase().strip())|| Objects.equals(str, ""))
                continue;
            else
                stemmedEntry.add(stopStem.stem(str.toLowerCase().strip()));
        }
        return stemmedEntry;
    }


}
