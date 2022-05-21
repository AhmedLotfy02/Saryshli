import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import com.mongodb.client.FindIterable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class Index implements Runnable {
    private  DatabaseClass db;
    RemoveStopWord StopWords;
    boolean isSpam;
    private int words_counter;
    Stemming Stemmer ;

    public Index(DatabaseClass db){
        this.db=db;
        StopWords= new RemoveStopWord(127,"./src/stopWords.txt");
        Stemmer= new Stemming();
    }
    public class Pair_Data
    {
        public double itf;
        public ArrayList<Integer> occursAt;
        public Pair_Data()
        {
            occursAt = new ArrayList<>();
        }
    }

    public void run(){



    }
    public String extractStartOfTheLastWord(String sentence)
    {
        sentence = sentence.trim();
        String word = "";
        for(int i = sentence.length()- 1; i > -1 && sentence.charAt(i) != ' ' ;i--) {
            word = sentence.charAt(i) + word;
        }
        return word;
    }
    private void processWord(int start,Hashtable<String, Pair_Data> databaseWordsFromString,int weight,String word,boolean addToDocument)
    {
        word = word.trim();
        word = word.replaceAll("[&\\/#,+()$~%.'\":#?<>{}_@]","");
        word = word.toLowerCase();
        if(word.length() == 0 || !StopWords.isNotAStopWord(word))
            return;
        word = Stemmer.getStemmedString(word);
        Pair_Data pd = databaseWordsFromString.get(word);
        if(addToDocument)
        {
            words_counter++;
            if(pd == null) {
                pd = new Pair_Data();
                databaseWordsFromString.put(word,pd);
                pd.occursAt.add(weight);
                pd.occursAt.add(start);
                return;
            };
            pd.occursAt.add(start);
        }
        pd.occursAt.set(0,pd.occursAt.get(0)+weight);
    }
    private void getWordsFromString(String sentence,Hashtable<String, Pair_Data> databaseWordsFromDocument,int weight,boolean addToDocument)
    {
        String LastWORD = extractStartOfTheLastWord(sentence);
        String word;
        int n = sentence.length();
        int end = sentence.length() - LastWORD.length();
        int start = 0;
        for (int i = 0;i< end;i++){
            start = i;
            word = "";
            char temp = sentence.charAt(i);
            while(temp != ' '){
                word = word + temp;
                i++;
                temp = sentence.charAt(i);
            }
            processWord(start,databaseWordsFromDocument,weight,word,addToDocument);
        }
        processWord(end,databaseWordsFromDocument,weight,LastWORD,addToDocument);
    }
    public void calcITF(Hashtable<String, Pair_Data> databaseWordsFromString)
    {
        databaseWordsFromString.forEach((key, value)-> {
            value.itf = ((double) (value.occursAt.size() - 1)/(double) words_counter)*100;
            if(value.itf >= 20) {
                System.out.println("spam page");
                isSpam = true;
                return;
            }
        });
    }
    public void indexing(Document doc,String url){
        Hashtable<String, Pair_Data> databaseWordsFromDocument = new Hashtable<String,Pair_Data>();

        words_counter = 0;
        isSpam = false;
        getWordsFromString(doc.select("*").text(),
                databaseWordsFromDocument,1,true);
        calcITF(databaseWordsFromDocument);
        if(isSpam)
        {
            System.out.println("spam page\n");
            return;
        }
        for(int i = 1;i<7;i++)
            getWordsFromString(doc.select("h"+i).text(),databaseWordsFromDocument,8-i,false);
        this.db.store(databaseWordsFromDocument,url);
    }
    public static void main(String[] args) throws IOException {
        //try
        //{
        DatabaseClass db=new DatabaseClass();

        Index myIndexer = new Index(db);
        FindIterable<org.bson.Document>it= db.retreiveCrawledResult();
        ArrayList<String> urls=new ArrayList<>();
        for(org.bson.Document doc:it)
            for(Map.Entry<String,Object>e:doc.entrySet()){
                if(e.getKey().equals("link")){
                    try{
//                        urls.add(e.getValue().toString());

                    System.out.println(e.getValue().toString());

                    Connection con = Jsoup.connect(e.getValue().toString());
                    Document doc1 = con.get();
                    myIndexer.indexing(doc1,e.getValue().toString());
                    }
                    catch (Exception e1){
                        System.out.println("error happened while connecting to link : "+e.getKey()+" in indexer");
                    }



                    //System.out.println(e.getValue());
                }
            }

        System.out.println(urls);
    }



}