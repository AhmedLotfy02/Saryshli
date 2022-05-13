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

    DatabaseClass db;

    private String sentenceAfterProcessing;

    public HashMap<String, ArrayList<singleURL>> allData;
    public HashMap<String , URLWordsAndSentences> URLS;
    private final int wordCountFactor = 7;
    private final int weightFactor = 1;
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
        for(String word : this.sentenceAfterProcessing.split(" ")){
//            String word = this.sentenceAfterProcessing.get(i);
            System.out.println("word: "+word);
            FindIterable<Document> it=this.db.retreiveDataFromIndexerByWord(word);
            ArrayList<singleURL> wordURLS = new ArrayList<>();
            allData.put(word , wordURLS);
            for(Document doc:it){
                if(!doc.isEmpty()) {
                    wordURLS.add(new singleURL(doc.get("_id").toString(), (Integer) doc.get("priority"), (ArrayList<Integer>) doc.get("occurs-at")));
                }
            }
            System.out.println("------------------------------------------------");


        }

        for(Map.Entry<String,ArrayList<singleURL>>e:allData.entrySet()){
                System.out.println(e.getKey());
                for(int i=0;i<e.getValue().size();i++){
                    out.println(e.getValue().get(i).url);
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
//       int i = 0;
//        while(i < sentenceWords.length && !rp.isNotAStopWord(sentenceWords[i++]));

  //      if(i == sentenceWords.length) return 0;
        fillQueue(q , sentenceWords[0] , url );
        out.println(q);
        for(int i=1; i < sentenceWords.length ; i++)
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
    public ArrayList<String> getRankedURLS()
    {
        filterToGetMostCommonWords();
        return (ArrayList<String>)URLS.entrySet().stream().sorted(sortingComparator).map(s -> s.getKey()).toList();
    }
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
    void addURLSData()
    {
        for(String word : this.sentenceAfterProcessing.split(" "))
            addURLInfo(word);
    }
    Integer getTotalWeight(Map.Entry<String, URLWordsAndSentences> o)
    {
        HashMap<String , wordInfo> words = o.getValue().words;
        Integer numOfCompleteSentences = o.getValue().numOfCompleteSentences;
        int weight = words.values().stream().map(s -> s.weight).reduce(0 , (a , c) -> a + c);
        int completeSentenceFactor = 20;
        return weightFactor * weight + wordCountFactor * words.size() + completeSentenceFactor * numOfCompleteSentences ;
    }
    private Comparator<Map.Entry<String , URLWordsAndSentences>> sortingComparator =
            new Comparator<Map.Entry<String, URLWordsAndSentences>>() {
                @Override
                public int compare(Map.Entry<String, URLWordsAndSentences> o1, Map.Entry<String, URLWordsAndSentences> o2) {
                    return getTotalWeight(o1) - getTotalWeight(o2);
                }
            };
    void filterToGetMostCommonWords() {
        URLS = new HashMap<>();
        addURLSData();
        ArrayList<String> completeSentences = getCompleteSentences();
        //searchStr = searchStr.replaceAll("\"[^\"]*\"" , "");
        completeSentences.forEach(completeSentencesConsumer);
    }
    public static void main(String[] args){
        LinkedList<String> l=new LinkedList<>();
        l.add("contributor");
        l.add("Galego");
        l.add("fast");
        String s="How to contributor in corsu fast";
        Ranker ranker=new Ranker(s);



    }
}