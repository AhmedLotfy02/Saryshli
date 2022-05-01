import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class WebCrawler implements Runnable {
    private static final int MAX_PAGES = 100;
    private static int pagesCount = 0;
    private Thread thread;
    private String firstLink;
    private static ArrayList<String> visitedLinks = new ArrayList<String>();
    private static ArrayList<String> compactStrings = new ArrayList<>();
    HashMap<String , Integer> popularity;
    private int ID;
    private URL url;

    public WebCrawler(URL url , int num) {
        System.out.println("Web Crawler is created");
        firstLink = url.getHost();
        System.out.println(firstLink);
        this.url=url;
        ID = num;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        //CheckRobot(this.url);
        try {
            System.out.println("ID : " + ID);
            crawl(firstLink);
        } catch (IOException e) {
            System.out.println(ID);
            e.printStackTrace();
        }
    }
    private void deepCrawl(Element link) throws IOException {
        String nextLink = link.absUrl("href");
        crawl(nextLink);
    }

    private void extractLinks(Document doc) throws IOException {
        for (Element link : doc.select("a[href]")) {
            deepCrawl(link);
        };
    }
    private void saveDocument(Document doc) throws IOException {
        String cs = createCS(doc);
        if(!existCS(cs)) {
            addCS(cs);
            extractLinks(doc);
        }
    }
    private void crawl(String url) throws IOException {
        if (pagesCount >= MAX_PAGES) return;
        Document doc = request(url);
        synchronized (this) {
            popularity.put(url, popularity.get(url) + 1);
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

    public Thread getThread() {
        return thread;
    }
}
//    public boolean CheckRobot(URL url){
////        System.out.println(url);
//        String host=url.getHost();
//        String strRobot = "http://" + host + "/robots.txt";
//        URL urlRobot;
//        try { urlRobot = new URL(strRobot);
//        } catch (MalformedURLException e) {
//            // Spam Website
//            return false;
//        }
//        //System.out.println(strRobot);
//        String strCommands;
//        try
//        {
//            InputStream urlRobotStream = urlRobot.openStream();
//            byte b[] = new byte[1000];
//            int numRead = urlRobotStream.read(b);
////            System.out.println(numRead);
//            if(numRead==-1){
//                return true;
//            }
//            strCommands = new String(b, 0, numRead);
//            while (numRead != -1) {
//                numRead = urlRobotStream.read(b);
//                if (numRead != -1)
//                {
//                    String newCommands = new String(b, 0, numRead);
//                    strCommands += newCommands;
//                }
//            }
//            urlRobotStream.close();
//        }
//        catch (IOException e)
//        {
//            return true; // if there is no robots.txt file, it is OK to search
//        }
//        //System.out.println(strCommands);
//        if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
//        {
//            String[] split = strCommands.split("\n");
//            ArrayList<RobotRule> robotRules = new ArrayList<>();
//            String mostRecentUserAgent = null;
//            boolean useGoogleBotOnly=false;
//            for (int i = 0; i < split.length; i++)
//            {
//                String line = split[i].trim();
//                if (line.toLowerCase().startsWith("user-agent"))
//                {
//                    int start = line.indexOf(":") + 1;
//                    int end   = line.length();
//                    mostRecentUserAgent = line.substring(start, end).trim();
//                   if(useGoogleBotOnly){
//                       break;
//                   }
//                   else if(mostRecentUserAgent.equals("Googlebot") ||mostRecentUserAgent.equals("*")) {
//                       useGoogleBotOnly=true;
//                   }
//                }
//                else if (line.startsWith("Disallow")&&useGoogleBotOnly) {
//                    if (mostRecentUserAgent != null) {
//                        RobotRule r = new RobotRule();
//                        r.userAgent = mostRecentUserAgent;
//                        int start = line.indexOf(":") + 1;
//                        int end   = line.length();
//                        r.rule = line.substring(start, end).trim();
//                        robotRules.add(r);
//                    }
//                }
//            }
////            System.out.println(robotRules.size());
//            for(RobotRule rw:robotRules){
////                System.out.println(rw.rule);
////                System.out.println(rw.userAgent);
////                System.out.println("------------------------------------");
//                String path = url.getPath();
//              //  if (rw.rule == "/") return false;       // allows nothing if /
//                    String recentUrl="http://" + host+rw.rule;
//                    if(recentUrl.equals(rw.rule)){
//                        return false;
//                    }
//                //System.out.println("http://" + host+rw.rule);
//                if (rw.rule.length() <= path.length())
//                {
//                    String pathCompare = path.substring(0, rw.rule.length());
//                    if (pathCompare.equals(rw.rule)) return false;
//                }
//            }
//
//
//
//        }
//
//
//        return true;
//    }