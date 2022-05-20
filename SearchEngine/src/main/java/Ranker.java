import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;

class singleURL {
    public String url;
    public int weight;
    double itf;
    ArrayList<Integer> occursAt;
    singleURL(String u , int w , ArrayList<Integer> o , double i)
    {
        url = u;
        weight = w;
        occursAt = o;
        itf = i;
    }
}

class wordInfo{
    ArrayList<Integer> occursAt;
    Integer weight;
    double itf;
    wordInfo(ArrayList<Integer> occurs , int w , double i)
    {
        weight = w;
        occursAt = occurs;
        itf = i;
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

    DatabaseClass db;

    private String sentenceAfterProcessing;

    public HashMap<String, ArrayList<singleURL>> allData;
    public HashMap<String , URLWordsAndSentences> URLS;
    private final int wordCountFactor = 7;
    private final int weightFactor = 1;
    private final int ITFFactor = 10;
    private final int completeSentenceFactor = 4;
    Ranker(String sentB) {
        this.sentenceAfterProcessing=sentB;
        //this.sentencebeforeProcessing=sentB;
        this.db=new DatabaseClass();
        this.allData = new HashMap<>();
        retrieveDataFromDB();
        }


    public void retrieveDataFromDB(){
        this.db.specifyDB("IndexerDB");

//        for(int i=0;i<sentenceAfterProcessing.size();i++){
        for(String word : this.sentenceAfterProcessing.replace("\"" , "").split(" ")){
            FindIterable<Document> it=this.db.retreiveDataFromIndexerByWord(word);
            ArrayList<singleURL> wordURLS = new ArrayList<>();
            allData.put(word , wordURLS);
            for(Document doc:it){
                if(!doc.isEmpty()) {
                    wordURLS.add(new singleURL(doc.get("_id").toString(), (Integer) doc.get("priority"), (ArrayList<Integer>) doc.get("occurs-at") , (double) doc.get("itf")));
                }
            }
        }

    }

    ArrayList<String> getCompleteSentences()
    {
        ArrayList<String> matchingStrings = new ArrayList<>();
        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(sentenceAfterProcessing);
        while(m.find()){
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
            q.add(x + addedLength);
    }
    Queue<Integer> filterQueue(Queue<Integer> q , String word , String url)
    {
        Integer addedLength = word.length() + 1;
        Queue<Integer> newQueue = new LinkedList<>();
        if(URLS.get(url).words.get(word) == null)
            return newQueue;
        for(Integer x : URLS.get(url).words.get(word).occursAt)
        {
            Integer top = q.peek();
            if(top.equals(x)) {
                q.remove();
                newQueue.add(top + addedLength);
            }
            if(top.compareTo(x) < 0) // less than
                q.remove();
        }
        return newQueue;
    }
    Integer containsCompleteSentence(String url , String sentence) {
        Queue<Integer> q = new LinkedList<>();
        String[] sentenceWords = sentence.split(" ");
        fillQueue(q , sentenceWords[0] , url );
        for(int i=1; i < sentenceWords.length ; i++)
        {
            q = filterQueue(q , sentenceWords[i] , url);
        }
        return q.size();
    }
    Consumer<String> completeSentencesConsumer = sentence -> {
        for(String url : URLS.keySet())
        {
            Integer occur = containsCompleteSentence(url , sentence);
            URLS.get(url).numOfCompleteSentences += occur;
        }
    };
    public List<String> getRankedURLS()
    {
        filterToGetMostCommonWords();
        return URLS.entrySet().stream().sorted(sortingComparator).map(s -> s.getKey()).toList();
    }
    void addSingleURLInfo(singleURL urlInfo , String word )
    {
        String url = urlInfo.url;
        if (!URLS.containsKey(url))
            URLS.put(url, new URLWordsAndSentences());
        URLS.get(url).words.put(word, new wordInfo(urlInfo.occursAt , urlInfo.weight , urlInfo.itf));
    }
    void addURLInfo(String word)
    {
        if(!allData.containsKey(word))
            return;
        for (singleURL urlInfo : allData.get(word)) addSingleURLInfo(urlInfo , word);
    }
    void addURLSData()
    {
        for(String word : this.sentenceAfterProcessing.replace("\"" , "").split(" "))
            addURLInfo(word);
    }
    Integer getTotalWeight(Map.Entry<String, URLWordsAndSentences> o)
    {
        HashMap<String , wordInfo> words = o.getValue().words;
        Integer numOfCompleteSentences = o.getValue().numOfCompleteSentences;
        int weight = words.values().stream().map(s -> s.weight).reduce(0 , (a , c) -> a + c);
        double itf = words.values().stream().map(s -> s.itf).reduce(0.0 , (a , c) -> a + c);
        Integer res =  weightFactor * weight + wordCountFactor * words.size() + completeSentenceFactor * numOfCompleteSentences + (int)(ITFFactor * itf);
        return res;
        }
    private Comparator<Map.Entry<String , URLWordsAndSentences>> sortingComparator =
            new Comparator<Map.Entry<String, URLWordsAndSentences>>() {
                @Override
                public int compare(Map.Entry<String, URLWordsAndSentences> o1, Map.Entry<String, URLWordsAndSentences> o2) {
                    return getTotalWeight(o2) - getTotalWeight(o1);
                }
            };
    void filterToGetMostCommonWords() {
        URLS = new HashMap<>();
        addURLSData();
        ArrayList<String> completeSentences = getCompleteSentences();
        completeSentences.forEach(completeSentencesConsumer);
    }
    public static void main(String[] args){
        String s="barcelona";
        Ranker ranker = new Ranker(s);
        List<String> urls =  ranker.getRankedURLS();
        out.println(urls);
    }
}