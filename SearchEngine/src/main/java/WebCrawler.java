import com.mongodb.client.MongoClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.lang.System.exit;

public class WebCrawler implements Runnable {
    DatabaseClass CrawlerDB;
    private ArrayList<String> hyperLinks;
    private static final int MAX_PAGES = 100;
    private static int pagesCount = 0;
    private Thread[] threads;
    HashMap<DataStructures , Integer> popularity = new HashMap<DataStructures, Integer>();
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private ArrayList<String> compactStrings = new ArrayList<>();
     LinkedList<String> links = new LinkedList<String>();
    private int ID;
    private URL url;
     int QueueSize;
     int pageCount=0;
     int crawledPages=0;
    public WebCrawler(LinkedList<String> links , int pageCount,DatabaseClass db) {
        System.out.println("Web Crawler is created");
        CrawlerDB=db;
        this.links = links;
        this.crawledPages = pageCount;
        this.QueueSize=links.size();

    }
    @Override
    public void run() {
        String link;
        while(crawledPages<=5000) {
            System.out.println(crawledPages);
            link = fetchLink();//this function already has synchronization as it access the queue
            boolean checkTheRobot=false;
            try {
                checkTheRobot = this.CheckRobot(new URL(link));
            } catch (MalformedURLException e) {
                System.out.println("Cannot connect to "+link);
                throw new RuntimeException(e);
            }
            if(checkTheRobot){
                //we have permission to crawl
                //lets connect to this url
                ResponseContent responseReturn = request(link);
                if(responseReturn.getConnectionStatus()==true){
                    Elements elements = responseReturn.getDocument().body().select("*");
                    //lets check the compact String so check if we visited this page before or not
                    String cs=computeCompactString(elements);
                    //check if its exist in db or not
                    CrawlerDB.specifyDB("CrawlerDB1");
                    boolean CheckCS=CrawlerDB.CheckForCompactString(cs);
                    if(CheckCS){
                        //new one and store it in CrawlerResult collection
                        Document doc=responseReturn.getDocument();
                        synchronized (this) {
                            this.CrawlerDB.storeOneCrawlerResult(doc.title(), cs, link);
                            DataStructures d1;
                            try {
                                d1=new DataStructures(new URL(link),cs,null);
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                            this.popularity.put(d1, 0);

                            //lets bfs on the hyperlinks
                            Elements hL = doc.select("a[href]");
                            hyperLinks = new ArrayList<>();

                            for (Element hLink : hL) {
                                boolean found=false;
                                String newHyperLink=hLink.absUrl("href");
                                DataStructures d2;
                                int oldvalue=0;
                                for(Map.Entry<DataStructures,Integer> e:this.popularity.entrySet()){
                                    if(e.getKey().url.equals(newHyperLink)){
                                        found=true;
                                        oldvalue=e.getValue();
                                        break;
                                    }
                                }
                                if(found){

                                    int oldValue = oldvalue;
                                    try {
                                        d2=new DataStructures(new URL(newHyperLink),cs,null);
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }

                                    this.popularity.replace(d2, oldValue, ++oldValue);

                                }
                                else{
                                    try {
                                         d2=new DataStructures(new URL(newHyperLink),cs,null);
                                        this.popularity.put(d2, 0);

                                    } catch (MalformedURLException e) {
                                      System.out.println("Cannot connect to hyperlink: "+ link);
                                    }

                                }

                                hyperLinks.add(newHyperLink);
                            }
                        }
                        synchronized (this){
                            if(crawledPages==5000){
                                break;
                            }
                            crawledPages++;
                            CrawlerDB.updatePageCount(crawledPages);
                            for(int i=0;i<hyperLinks.size();i++){
                                links.add(hyperLinks.get(i));
                            }
                            if(links.size()<=5000){
                                CrawlerDB.updateLinks(links);
                            }
                            else{

                                links.subList(5001,links.size()).clear();
                                CrawlerDB.updateLinks(links);
                            }
                            notifyAll();//to notify the waited threads in fetchlink function as we add new links
                        }

                    }
                    else{
                        //visited before

                        synchronized (this){
                            CrawlerDB.updateLinks(links);

                            QueueSize--;
                        }
                    }
                }
                else{
                    System.out.println("cannot connect to  url: "+url);
                    synchronized (this){
                        CrawlerDB.updateLinks(links);

                        QueueSize--;
                    }
                }



            }
            else{
                synchronized (this){
                    CrawlerDB.updateLinks(links);

                    QueueSize--;
                }
                System.out.println("Link : "+link+" blocked our crawler using robot.txt");

            }

        }
        System.out.println("thread : "+Thread.currentThread().getId()+"has finished");




        // TODO Auto-generated method stub
        //CheckRobot(this.url);
//        try {
//            while (links.isEmpty()) {
//                System.out.println("I am waiting");
//            }
//            crawl();
//        } catch (IOException e) {
//            System.out.println(ID);
//            e.printStackTrace();
//        }
    }
    private String fetchLink(){
        String link="";
        synchronized (this) {
            while (link.isEmpty()) {

                if (this.links.size() == 0) {
                    if (QueueSize == 0) {
                        System.out.println("all links blocked crawler with their robot.txt");
                        exit(-1);
                    } else {
                        try {
                            System.out.println("Thread "+Thread.currentThread().getId()+" gone to wait state");
                            wait();
                        } catch (InterruptedException e) {
                            System.out.println("Thread " + Thread.currentThread().getId() + " being interrupted while waiting");
                            throw new RuntimeException(e);
                        }
                    }
                }
                System.out.println("hhh");
                link = links.remove();
                System.out.println(links.size());

            }
        }
        return link;

    }
    public boolean CheckRobot(URL url){
//        System.out.println(url);
        String host=url.getHost();
        String strRobot = "http://" + host + "/robots.txt";
        URL urlRobot;
        try { urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            // Spam Website
            return false;
        }
        //System.out.println(strRobot);
        String strCommands;
        try
        {
            InputStream urlRobotStream = urlRobot.openStream();
            byte b[] = new byte[1000];
            int numRead = urlRobotStream.read(b);
//            System.out.println(numRead);
            if(numRead==-1){
                return true;
            }
                strCommands = new String(b, 0, numRead);
            while (numRead != -1) {
                numRead = urlRobotStream.read(b);
                if (numRead != -1)
                {
                    String newCommands = new String(b, 0, numRead);
                    strCommands += newCommands;
                }
            }
            urlRobotStream.close();
        }
        catch (IOException e)
        {
            return true; // if there is no robots.txt file, it is OK to search
        }
        //System.out.println(strCommands);
        if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
        {
            String[] split = strCommands.split("\n");
            ArrayList<RobotRule> robotRules = new ArrayList<>();
            String mostRecentUserAgent = null;
            boolean useGoogleBotOnly=false;
            for (int i = 0; i < split.length; i++)
            {
                String line = split[i].trim();
                if (line.toLowerCase().startsWith("user-agent"))
                {
                    int start = line.indexOf(":") + 1;
                    int end   = line.length();
                    mostRecentUserAgent = line.substring(start, end).trim();
                   if(useGoogleBotOnly){
                       break;
                   }
                   else if(mostRecentUserAgent.equals("Googlebot") ||mostRecentUserAgent.equals("*")) {
                       useGoogleBotOnly=true;
                   }
                }
                else if (line.startsWith("Disallow")&&useGoogleBotOnly) {
                    if (mostRecentUserAgent != null) {
                        RobotRule r = new RobotRule();
                        r.userAgent = mostRecentUserAgent;
                        int start = line.indexOf(":") + 1;
                        int end   = line.length();
                        r.rule = line.substring(start, end).trim();
                        robotRules.add(r);
                    }
                }
            }
//            System.out.println(robotRules.size());
            for(RobotRule rw:robotRules){
//                System.out.println(rw.rule);
//                System.out.println(rw.userAgent);
//                System.out.println("------------------------------------");
                String path = url.getPath();
              //  if (rw.rule == "/") return false;       // allows nothing if /
                    String recentUrl="http://" + host+rw.rule;
                    if(recentUrl.equals(rw.rule)){
                        return false;
                    }
                //System.out.println("http://" + host+rw.rule);
                if (rw.rule.length() <= path.length())
                {
                    String pathCompare = path.substring(0, rw.rule.length());
                    if (pathCompare.equals(rw.rule)) return false;
                }
            }



        }


        return true;
    }


    private String createCS(Document doc) throws IOException {
        Elements elements = doc.body().select("*");
        String cs = computeCompactString(elements);
        return cs;
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
    // 341
    // 42
    // 6:47

    private ResponseContent request(String url) {

        try {
            url = url.startsWith("http") ? url : "https://" + url;
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() != 200) {
                System.out.println("Error :response code from url : "+ url);
                ResponseContent response=new ResponseContent(null,false);
                return response;
            }
            else{
                String []type = con.response().contentType().split(";");
                if(type[0].compareTo("text/html")!=0)
                {
                    System.out.println("url +"+url+" doesn't have html page");
                    ResponseContent response=new ResponseContent(null,false);
                    return  response;
                }
                ResponseContent response=new ResponseContent(doc,true);
                return response;
            }

        } catch (Exception e) {
            System.out.println("url : "+url+" throws exception in connecting (request func) : "+e);
            ResponseContent response=new ResponseContent(null,false);
            return response;
        }

    }

    @Override
    public void finalize() throws Throwable {
        this.CrawlerDB.updatePopularity(popularity);
    }


    //    public Thread getThread() {
//        return thread;
//    }
}
