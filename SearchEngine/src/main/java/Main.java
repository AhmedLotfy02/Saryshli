


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws MalformedURLException {

        URL url1= new URL("https://www.wikipedia.org/");
        URL url2= new URL("https://www.youm7.com/");
        URL url3= new URL("https://www.yallakora.com/");
        URL url4= new URL("https://technicalseo.com/");
        URL url5= new URL("https://www.facebook.com/");
        ArrayList<URL> beginingUrls=new ArrayList<URL>();
       // beginingUrls.add(url1);
        beginingUrls.add(url5);
        //beginingUrls.add(url3);
        ArrayList<WebCrawler> bots=new ArrayList<WebCrawler>();
        for(int i=0;i<beginingUrls.size();i++)
            bots.add(new WebCrawler(beginingUrls.get(i)));
        ArrayList<DataStructures> SummarizedUrls=new ArrayList<DataStructures>();
        
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
    }

}