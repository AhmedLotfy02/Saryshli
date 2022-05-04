import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException {

        String url1= ("https://www.wikipedia.org/");
        String url2= ("https://www.youm7.com/");
        String url3= ("https://www.yallakora.com/");
        String url4= ("https://technicalseo.com/");
        String url5= ("https://www.facebook.com/");
        Queue<String> queueLinks = new LinkedList<String>();
        queueLinks.add(url1);
        queueLinks.add(url2);
        queueLinks.add(url3);
        queueLinks.add(url4);
        queueLinks.add(url5);
        WebCrawler crawler = new WebCrawler(queueLinks, 10);
//        for(int i=0;i<beginingUrls.size();i++)
//            bots.add(new WebCrawler(beginingUrls.get(i)));
//        ArrayList<DataStructures> SummarizedUrls=new ArrayList<DataStructures>();

//        for(WebCrawler w:bots){
//            try {
//                w.getThread().join();
//            }
//            catch (InterruptedException e){
//                e.printStackTrace();
//            }
//        }

//        String[] beginingUrls={"www.wikipedia.org","www.youm7.com"};

        //ArrayList<WebCrawler> seed=new ArrayList<>();
        // seed.add(new WebCrawler("www.wikipedia.org",3));
//        for(WebCrawler w:seed) {
//            try {
//                w.getThread().join();
//            }
//            catch(InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        Index myIndexer = new Index();
//        Connection con = Jsoup.connect("https://www.wikipedia.org");
//        Document doc = con.get();
//        //System.out.println(doc.baseUri());
//        myIndexer.indexing(doc,doc.baseUri());

    }



}