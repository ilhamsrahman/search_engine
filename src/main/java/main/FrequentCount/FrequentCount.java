package main.FrequentCount;
import java.util.*;

import main.WebPage.*;

public class FrequentCount {

    /**
     * count Frequency of word in a vector
     * @param content the word vector contain word list
     * @return A map with frequency word, sorted
     */
    public Map<String, Integer> countFrequent(Vector<String> content) {

        HashMap<String, Integer> freqMap = new HashMap<>();
        for(String word:content) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedEntries= new ArrayList<>(freqMap.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String,Integer> returnedMap = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String,Integer> entry:sortedEntries) {
//            if (count >= 10) {
//                break;
//            }
            if(Objects.equals(entry.getKey(), ""))
                continue;
            returnedMap.put(entry.getKey(),entry.getValue());
            count++;
        }
        return returnedMap;
    }

    /**
     * change "abcdefg:::17\nasdfg:::12\nqwert:::6" into
     * ['abcdefg': 17,
     * 'asdfg': 12,
     * 'qwert': 6]
     * @param content the input string
     * @return the map
     */
    public static Map<String, Integer> parseForwardIndex(String content) {
        if (content == null)
                return new HashMap<>();
        Map<String,Integer> returnedMap = new HashMap<>();
        String[] pairs = content.split("\n");
        for (String pair : pairs) {
            String[] parts = pair.split(":::");
            String key = parts[0];
            int value = Integer.parseInt(parts[1]);
            returnedMap.put(key, value);
        }
        return returnedMap;
    }

    /**
     * Add Child list to the page
     * @param hs the crawled Page
     */
    public static void addChild(Map<String,WebPage> hs){
        for(Map.Entry<String,WebPage> pageEntry: hs.entrySet()){
            Vector<String> childList=pageEntry.getValue().getChild();
            for(String childURL:childList){
                hs.get(childURL).addParent(pageEntry.getKey());
            }
        }
    }

}

