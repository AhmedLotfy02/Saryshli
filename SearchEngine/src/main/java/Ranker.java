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
    Integer popularity;
    URLWordsAndSentences()
    {
        numOfCompleteSentences = 0;
        words = new HashMap<>();
        this.popularity = 0;
    }
}



public class Ranker {

    DatabaseClass db;
    private RemoveStopWord StopWords;
    private Stemming Stemmer;
    private String sentenceAfterProcessing;

    public HashMap<String, ArrayList<singleURL>> allData;
    public HashMap<String, URLWordsAndSentences> URLS;
    private final int wordCountFactor = 7;
    private final int weightFactor = 1;
    private final int ITFFactor = 10;
    private final int completeSentenceFactor = 4;
    private int page;

    Ranker(String x) {
    }

    public Ranker(String sentB, int page) {
        this.page = page;
        this.sentenceAfterProcessing = sentB.toLowerCase();
        this.db = new DatabaseClass();
        this.allData = new HashMap<>();
        this.Stemmer = new Stemming();
        this.StopWords = new RemoveStopWord(127, "./src/stopWords.txt");
        retrieveDataFromDB();
        filterToGetMostCommonWords();
    }


    public void retrieveDataFromDB() {
        this.db.specifyDB("IndexerDB");

//        for(int i=0;i<sentenceAfterProcessing.size();i++){
        for (String word : this.sentenceAfterProcessing.replaceAll("\"", "").split(" ")) {
            if (StopWords.isNotAStopWord(word) && !word.equals("")) {
                word = Stemmer.getStemmedString(word);
                FindIterable<Document> it = this.db.retreiveDataFromIndexerByWord(word);
                ArrayList<singleURL> wordURLS = new ArrayList<>();
                allData.put(word, wordURLS);
                for (Document doc : it) {
                    if (!doc.isEmpty()) {
                        wordURLS.add(new singleURL(doc.get("_id").toString(), (Integer) doc.get("priority"), (ArrayList<Integer>) doc.get("occurs-at"), (double) doc.get("itf")));
                    }
                }
            }
        }

    }

    ArrayList<String> getCompleteSentences() {
        ArrayList<String> matchingStrings = new ArrayList<>();
        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(sentenceAfterProcessing);
        while (m.find()) {
            matchingStrings.add(m.group(1));
        }

        return matchingStrings;
    }

    void fillQueue(Queue<Integer> q, String firstWord, String url) {
        Integer addedLength = firstWord.length() + 1;
        firstWord = Stemmer.getStemmedString(firstWord);
        if (URLS.get(url).words.get(firstWord) == null)
            return;
        for (Integer x : URLS.get(url).words.get(firstWord).occursAt)
            q.add(x + addedLength);
    }

    Queue<Integer> changeQueue(Queue<Integer> q, String stopWord, String url) {
        Integer addedLength = stopWord.length() + 1;
        Queue<Integer> newQueue = new LinkedList<>();
        while (!q.isEmpty()) {
            int top = q.peek();
            q.remove();
            newQueue.add(top + addedLength);
        }
        return newQueue;
    }

    Queue<Integer> filterQueue(Queue<Integer> q, String word, String url) {
        Integer addedLength = word.length() + 1;
        Queue<Integer> newQueue = new LinkedList<>();
        word = Stemmer.getStemmedString(word);
        if (URLS.get(url).words.get(word) == null)
            return newQueue;
        for (Integer x : URLS.get(url).words.get(word).occursAt) {
            Integer top = q.peek();
            if (top == null) {
                return newQueue;
            }
            if (top.equals(x)) {
                q.remove();
                newQueue.add(top + addedLength);
            }
            if (top.compareTo(x) < 0) // less than
                q.remove();
        }
        return newQueue;
    }

    Integer containsCompleteSentence(String url, String sentence) {
        Queue<Integer> q = new LinkedList<>();
        String[] sentenceWords = sentence.split(" ");
        int i = 0;
        int size = sentenceWords.length;
        while (i < size && !StopWords.isNotAStopWord(sentenceWords[i++])) ;
        if (i == size) return 0;
        fillQueue(q, sentenceWords[i++], url);
        for (; i < sentenceWords.length; i++) {
            if (StopWords.isNotAStopWord(sentenceWords[i])) {
                q = filterQueue(q, sentenceWords[i], url);
            } else
                q = changeQueue(q, sentenceWords[i], url);
        }
        return q.size();
    }

    Consumer<String> completeSentencesConsumer = sentence -> {
        for (String url : URLS.keySet()) {
            Integer occur = containsCompleteSentence(url, sentence);
            URLS.get(url).numOfCompleteSentences += occur;
            out.println(occur);
        }
    };

    int getBestBlainTextIndex(String url) {

        if (URLS.get(url) == null || URLS.get(url).words == null || URLS.get(url).words.size() == 0)
            return 0;
//        out.println("C " + URLS.get(url).words.get(0).occursAt);
        HashMap<String, wordInfo> words = URLS.get(url).words;
        return words.values().stream().toList().get(0).occursAt.get(0);
    }

    private int getFromPage() {
        int startPage = (this.page - 1) * 10;
        return startPage >= URLS.size() ? -1 : startPage;
    }

    private int getToPage() {
        int endPage = (this.page * 10);
        return Math.min(endPage, URLS.size());
    }

    public List<rankerReturn> getURLS() {
        int start = getFromPage();
        if (start == -1)
            return new ArrayList<>();
        int end = getToPage();
        out.println(start + " " + end);
        return URLS.entrySet().stream().sorted(sortingComparator)
                .toList().subList(start, end).stream()
                .map(s -> s.getKey()).map(url -> new rankerReturn(url, getBestBlainTextIndex(url))).toList();
    }

    public paginationRanker getRankedURLS()
    {
        return new paginationRanker(getURLS() , URLS.size());
    }
    void addSingleURLInfo(singleURL urlInfo , String word )
    {
        String url = urlInfo.url;
        if (!URLS.containsKey(url))
            URLS.put(url, new URLWordsAndSentences());
        out.println("adding....................");
        wordInfo lword = new wordInfo(urlInfo.occursAt , urlInfo.weight, urlInfo.itf);
        if(lword == null)
            out.println("NULL");

        URLS.get(url).words.put(word, lword);

    }
    void addURLInfo(String word)
    {
        if(!allData.containsKey(word))
            return;
        out.println("adding.....");
        for (singleURL urlInfo : allData.get(word)) addSingleURLInfo(urlInfo , word);
    }
    void addURLSData()
    {
        for(String word : this.sentenceAfterProcessing.replaceAll("\"" , "").split(" ")) {
            if(StopWords.isNotAStopWord(word)) addURLInfo(Stemmer.getStemmedString(word));
        }
//        db.f();
        FindIterable<Document> f =  db.getUrlsPopularity(URLS.keySet().stream().toList());
        for(Document doc : f)
        {
            URLWordsAndSentences u = URLS.get(doc.get("_id"));
            String p = (String)doc.get("popularity");
            if(p != null)
                u.popularity = Integer.parseInt(p);
        }
    }
    Integer getTotalWeight(Map.Entry<String, URLWordsAndSentences> o)
    {
        int popularityFactor = 10;
        HashMap<String , wordInfo> words = o.getValue().words;
        Integer numOfCompleteSentences = o.getValue().numOfCompleteSentences;
        Integer popularity = o.getValue().popularity;
        int weight = words.values().stream().map(s -> s.weight).reduce(0 , (a , c) -> a + c);
        double itf = words.values().stream().map(s -> s.itf).reduce(0.0 , (a , c) -> a + c);
        Integer res =  weightFactor * weight + wordCountFactor * words.size() + completeSentenceFactor * numOfCompleteSentences + (int)(ITFFactor * itf) + popularity * popularityFactor;
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
        out.println(allData);
        addURLSData();
        out.println(URLS);
        ArrayList<String> completeSentences = getCompleteSentences();
        completeSentences.forEach(completeSentencesConsumer);
    }


    public static void main(String[] args){
        String s="question";
        Ranker ranker = new Ranker(s , 1);
        paginationRanker pR =  ranker.getRankedURLS();
        List<rankerReturn> urls = pR.urls;
        out.println(urls);;
//        out.println(urls.subList(0 , 1));
//        out.println(urls.get(0).plaintTextIndex);
//        out.println(urls);

    }
}