import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

public class WebCrawlerMain {

    public static void main(String[] args) throws IOException {


        DatabaseClass WebCrawlerDb=new DatabaseClass();
        WebCrawlerDb.specifyDB("CrawlerDB1");
        WebCrawlerDb.specifyCollection("CrawlerResult");
        int pageCount=WebCrawlerDb.getPagesNo();
        ArrayList<String>seedSet;
        if(pageCount>=5000||pageCount==0){
            //means that the last crawling completed sucessfully
            pageCount=0;

            seedSet=new ArrayList<String>();
            seedSet.add("https://www.wikipedia.org/");
            seedSet.add("https://www.youm7.com/");
            seedSet.add("https://www.yallakora.com/");
            seedSet.add ("https://technicalseo.com/");
            seedSet.add("https://www.facebook.com/");
            WebCrawlerDb.InsertLinks(seedSet);
//            ArrayList<String> arr=new ArrayList<>();
//            arr=WebCrawlerDb.getLinks();
//            for(int i=0;i<seedSet.size();i++){
//                System.out.println(arr.get(i));
//            }
        }
        else{
            //not completed so we will get the links where we stopped from the database
            seedSet=WebCrawlerDb.getLinks();
            System.out.println("Continued From last Run");
        }
        LinkedList<String> passedQueue=new LinkedList<String>();

        for(int i=0;i<seedSet.size();i++){
                passedQueue.add(seedSet.get(i));
        }
        WebCrawler webCrawler=new WebCrawler(passedQueue,pageCount,WebCrawlerDb);
        //Scanner sc=new Scanner(System.in);
        //int numThreads=sc.nextInt();
        Thread threads[] = new Thread[8];

        for (int i = 0; i < 8; i++) {
            threads[i] = new Thread(webCrawler);
            threads[i].start();
        }
        for (int i = 0; i < 8; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Thread "+i+" is interrupted");
                throw new RuntimeException(e);
            }
        }






//        String url2= ("https://www.youm7.com/");
//        String url3= ("https://www.yallakora.com/");
//        String url4= ("https://technicalseo.com/");
//        String url5= ("https://www.facebook.com/");
//        //Queue<String> queueLinks = new LinkedList<String>();
     //   queueLinks.add(url1);
//        queueLinks.add(url2);
//        queueLinks.add(url3);
//        queueLinks.add(url4);
//        queueLinks.add(url5);
//        boolean x=WebCrawler.CheckRobot(new URL(url5));
//        System.out.println(x);
      //  WebCrawler crawler = new WebCrawler(queueLinks, 10);
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