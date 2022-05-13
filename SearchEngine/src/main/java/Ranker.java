import java.util.*;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

class singleURL {
    public String url;
    public int weight;
    ArrayList<Integer> occursAt;
    singleURL(String u , int w , ArrayList<Integer> o)
    {
        url = u;
        weight = w;
        occursAt = o;
    }
}

class wordInfo{
    ArrayList<Integer> occursAt;
    Integer weight;
    wordInfo(ArrayList<Integer> occurs)
    {
        weight = 0;
        occursAt = occurs;
    }
}
class URLWordsAndSentences
{
    HashMap<String , wordInfo> words;
    Integer numOfCompleteSentences;
    URLWordsAndSentences()
    {
        numOfCompleteSentences = 0;
        words = new HashMap<>();
    }
}



public class Ranker {

    public HashMap<String, ArrayList<singleURL>> allData;
    public HashMap<String, wordInfo> mostURLS;
    public HashMap<String , URLWordsAndSentences> URLS;
    private final int completeSentenceFactor = 20;
    private final int wordCountFactor = 7;
    private final int weightFactor = 1;
    Ranker() {
        String searchStr = "hello \"Iam Omar\" love you all \"new user\" this is good \"great\" fareed".toLowerCase(Locale.ROOT);
        mostURLS = new HashMap<>();
        allData = new HashMap<>();
//        sortBy();
        ArrayList<Integer> arr1 = new ArrayList<>() , arr2 = new ArrayList<>() , arr3= new ArrayList<>() , arr4= new ArrayList<>();
        arr1.add(10);
        arr1.add(30);
        arr1.add(100);
        arr2.add(10);
        arr2.add(14);
        arr2.add(50);
        allData.put("iam" , new ArrayList<>());
        allData.put("omar" , new ArrayList<>() );
        allData.get("omar").add(new singleURL("https://omar.com" , 20, arr2));
        allData.get("iam").add(new singleURL("https://omar.com" , 30 , arr1));
        filterToGetMostCommonWords(searchStr);
    }

    ArrayList<String> getCompleteSentences(String searchStr)
    {
        out.println(searchStr);
        ArrayList<String> matchingStrings = new ArrayList<>();
        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(searchStr);
        while(m.find()){
            String res = m.group(1);
            out.println(res);
            matchingStrings.add(m.group(1));
        }
        return matchingStrings;
    }

    void fillQueue(Queue<Integer> q , String firstWord , String url )
    {
        Integer addedLength = firstWord.length() + 1;
        if(URLS.get(url).words.get(firstWord) == null)
            return;
        for(Integer x : URLS.get(url).words.get(firstWord).occursAt)
//            q.addLast(x + addedLength);
            q.add(x + addedLength);
        out.println(firstWord);
        out.println(q);
    }
    Queue<Integer> filterQueue(Queue<Integer> q , String word , String url)
    {
        Integer addedLength = word.length() + 1;
        Queue<Integer> newQueue = new LinkedList<>();
        if(URLS.get(url).words.get(word) == null)
            return newQueue;
        out.println(URLS.get(url).words.get(word).occursAt);
        for(Integer x : URLS.get(url).words.get(word).occursAt)
        {
            Integer top = q.peek();
            if(top == x) {
                q.remove();
                newQueue.add(top + addedLength);
            }
            if(top < x)
                q.remove();
        }
        out.println(newQueue);
        return newQueue;
    }
    Integer containsCompleteSentence(String url , String sentence) {
        Queue<Integer> q = new LinkedList<>();
        String[] sentenceWords = sentence.split(" ");
        fillQueue(q , sentenceWords[0] , url );
        out.println(q);
        for(int i = 1; i < sentenceWords.length ; i++)
        {
            q = filterQueue(q , sentenceWords[i] , url);
        }
        return q.size();
    }
    Consumer<String> completeSentencesConsumer = sentence -> {
        for(String url : URLS.keySet())
        {
            out.println(url);
            Integer occur = containsCompleteSentence(url , sentence);
            out.println(occur);
            // add the weight here;
        }
    };
    void addSingleURLInfo(singleURL urlInfo , String word )
    {
        String url = urlInfo.url;
        if (!URLS.containsKey(url))
            URLS.put(url, new URLWordsAndSentences());
        URLS.get(url).words.put(word, new wordInfo(urlInfo.occursAt));
    }
    void addURLInfo(String word)
    {
        if(!allData.containsKey(word))
            return;
        for (singleURL urlInfo : allData.get(word)) addSingleURLInfo(urlInfo , word);
    }
    void addURLSData(String searchStr)
    {
        for(String word : searchStr.split(" "))
            addURLInfo(word);
    }
    Integer getTotalWeight(Map.Entry<String, URLWordsAndSentences> o)
    {
        HashMap<String , wordInfo> words = o.getValue().words;
        Integer numOfCompleteSentences = o.getValue().numOfCompleteSentences;
        int weight = words.values().stream().map(s -> s.weight).reduce(0 , (a , c) -> a + c);
        return weightFactor * weight + wordCountFactor * words.size() + completeSentenceFactor * numOfCompleteSentences ;
    }
    private Comparator<Map.Entry<String , URLWordsAndSentences>> sortingComparator =
            new Comparator<Map.Entry<String, URLWordsAndSentences>>() {
                @Override
                public int compare(Map.Entry<String, URLWordsAndSentences> o1, Map.Entry<String, URLWordsAndSentences> o2) {
                    return getTotalWeight(o1) - getTotalWeight(o2);
                }
            };
    void filterToGetMostCommonWords(String searchStr) {
        URLS = new HashMap<>();
        addURLSData(searchStr.replaceAll("\"" , ""));
        ArrayList<String> completeSentences = getCompleteSentences(searchStr);
        searchStr = searchStr.replaceAll("\"[^\"]*\"" , "");
        completeSentences.forEach(completeSentencesConsumer);
    }
}