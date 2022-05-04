import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class WebCrawler implements Runnable {
    private static final int MAX_PAGES = 100;
    private static int pagesCount = 0;
    private Thread[] threads;
    HashMap<String , Integer> popularity = new HashMap<String, Integer>();
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private ArrayList<String> compactStrings = new ArrayList<>();
    private Queue<String> links = new LinkedList<String>();
    private int ID;
    private URL url;

    public WebCrawler(Queue<String> links , int num) {
        System.out.println("Web Crawler is created");
        this.links = links;
        threads = new Thread[num];
        for(int i = 0; i < num; i++) {
            threads[i] = new Thread(this);
            threads[i].start();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        //CheckRobot(this.url);
        try {
            while (links.isEmpty()) {
                System.out.println("I am waiting");
            }
            crawl();
        } catch (IOException e) {
            System.out.println(ID);
            e.printStackTrace();
        }
    }
    private void deepCrawl(Element link) throws IOException {
        String nextLink = link.absUrl("href");
        synchronized (this){links.add(nextLink);}
        crawl();
    }

    private void extractLinks(Document doc) throws IOException {
        for (Element link : doc.select("a[href]")) {
            deepCrawl(link);
        }
    }
    private void saveDocument(Document doc) throws IOException {
        String cs = createCS(doc);
        if(!existCS(cs)) {
            addCS(cs);
            extractLinks(doc);
        }
    }
    private void crawl() throws IOException {
        if (pagesCount >= MAX_PAGES) return;
        System.out.println(Thread.currentThread().getId());
        String url = "";
        synchronized (this){url = links.remove();}
        Document doc = request(url);
        synchronized (this) {
            Integer val = popularity.get(url);
            if(val != null)
                popularity.put(url, val + 1);
            else popularity.put(url, 1);
        }
        if(doc != null ) saveDocument(doc);
    }

    private String createCS(Document doc) throws IOException {
        Elements elements = doc.body().select("*");
        String cs = computeCompactString(elements);
        return cs;
    }
    private boolean existCS(String cs)
    {
        return compactStrings.contains(cs);
    }
    private void addCS(String cs) {
        synchronized (this) {
            pagesCount++;
            System.out.println("thread id : " + Thread.currentThread().getId() + " page counts: " + pagesCount);
        }
        compactStrings.add(cs);
    }


    private String computeCompactString(Elements elements) {
        Integer numOfElements = elements.size();
        String cs = "";
        int quantity = (int) Math.ceil(elements.size() / 4.0);

        for(int i = 0; i < quantity; i++) {
            String text = elements.get(i).ownText();
            if(text.length() > 0)
                cs += text.charAt(0);
        }

        for(int i = numOfElements - 1; i + quantity >= numOfElements; i--) {
            String text = elements.get(i).ownText();
            if(text.length() > 0)
                cs += text.charAt(0);
        }
        return cs;
    }

    private Document request(String url) {

        try {
            url = url.startsWith("http") ? url : "https://" + url;
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() != 200)
                System.out.println("Error");
            String title = doc.title();
            visitedLinks.add(url);
            return doc;
        } catch (Exception e) {
            System.out.println("soso we74a");
            return null;
        }
    }

//    public Thread getThread() {
//        return thread;
//    }
}
