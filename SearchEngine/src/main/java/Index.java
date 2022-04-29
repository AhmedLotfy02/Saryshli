import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Index {
    RemoveStopWord StopWords = new RemoveStopWord(127,"./src/stopWords.txt");
    Stemming Stemmer = new Stemming();
    public String extractStartOfTheLastWord(String sentence)
    {
        sentence = sentence.trim();
        String word = "";
        for(int i = sentence.length()- 1; i > -1 && sentence.charAt(i) != ' ' ;i--) {
            word = sentence.charAt(i) + word;
        }
        return word;
    }
    private void processWord(int start,Hashtable<String,ArrayList<Integer>> databaseWordsFromString,int weight,String word)
    {
        ArrayList<Integer> arrayPointer;
        word = word.trim();
        if(word.length() == 0)
            return;
        word = Stemmer.getStemmedString(word);
        arrayPointer = databaseWordsFromString.get(word);
        if(arrayPointer == null) {
            databaseWordsFromString.put(word,new ArrayList<>(2));
            arrayPointer = databaseWordsFromString.get(word);
            arrayPointer.add(weight);
            arrayPointer.add(start);
            return;
        };
        arrayPointer.add(start);
        arrayPointer.set(0,arrayPointer.get(0)+weight);
//        System.out.println(word+" "+arrayPointer);
    }
    private void getWordsFromString(String sentence,Hashtable<String,ArrayList<Integer>> databaseWordsFromString,int weight)
    {
        String word= extractStartOfTheLastWord(sentence);
        int n = sentence.length();
        int end = sentence.length() - word.length();
        processWord(end,databaseWordsFromString,weight,word);
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
            processWord(start,databaseWordsFromString,weight,word);
        }
    }
    public void indexing(Document doc){
            Hashtable<String, ArrayList<Integer>> databaseWordsFromDocument = new Hashtable<String,ArrayList<Integer>>();
            getWordsFromString("computer computing moaz mostafa",databaseWordsFromDocument,1);
            for(int i = 1;i<7;i++)
                getWordsFromString(doc.select("h"+i).text(),databaseWordsFromDocument,8-i);
            getWordsFromString(doc.select(":not(h1,h2,h3,h4,h5,h6)").text(),
                    databaseWordsFromDocument,1);
            System.out.println(databaseWordsFromDocument);

    }
    public static void main(String[] args) throws IOException {
        try
        {
            Index myIndexer = new Index();
            Connection con = Jsoup.connect("https://www.wikipedia.org");
            Document doc = con.get();
            myIndexer.indexing(doc);
        }
        catch(IOException e)
        {
            System.out.println("hello");
        }
    }
}
