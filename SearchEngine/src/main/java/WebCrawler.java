import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebCrawler implements Runnable {
    private static final int MAX_PAGES = 5000;
    private static int pagesCount = 0;
    private Thread thread;
    private String firstLink;
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private ArrayList<String> compactStrings = new ArrayList<>();

    private int ID;
    private URL url;

    public WebCrawler(URL url) {
        System.out.println("Web Crawler is created");
        firstLink = url.getHost();
//        Connection con=Jsoup.connect(url.getHost());
//        if(con.response().statusCode()!=200){
//            return; //couldn't connect to the url
//        }
        System.out.println(firstLink);
        this.url=url;

//        boolean x=CheckRobot(url);
//        System.out.println(x);
//        if(!x)
//            return;

        //ID = num;
        //thread = new Thread(this);
        //thread.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        //CheckRobot(this.url);
        //crawl(firstLink);
    }

    private void deepCrawl(Element link) throws IOException {
        String nextLink = link.absUrl("href");
        URL url = new URL(nextLink);
        if (GetCompactString(url)) {
            synchronized (this) {
                pagesCount++;
            }
            crawl(nextLink);
        }
    }

    private void extractLinks(Document doc) throws IOException {
        if (doc == null) return;
        for (Element link : doc.select("a[href]")) deepCrawl(link);
    }

    private void crawl(String url) throws IOException {
        if (pagesCount > MAX_PAGES) return;
        Document doc = request(url);
        extractLinks(doc);
    }

    public boolean CheckRobot(URL url){
        System.out.println(url);
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
            System.out.println(numRead);
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
            System.out.println(robotRules.size());
            for(RobotRule rw:robotRules){
                System.out.println(rw.rule);
                System.out.println(rw.userAgent);
                System.out.println("------------------------------------");
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

    public boolean GetCompactString(URL url) throws IOException {

        Document doc = Jsoup.connect(url.toString()).get();
        Elements elements = doc.body().select("*");
        String cs = computeCompactString(elements);
        if(compactStrings.contains(cs)) return false;
        else {
            compactStrings.add(cs);
            return true;
        }
    }

    /**
     * Computing the compact string.
     * @param elements
     * @return String
     * */
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
            System.out.println("\n" + url);
            Connection con = Jsoup.connect("https://www.wikipedia.org");
            Document doc = con.get();
            if (con.response().statusCode() != 200)
                return null;
            System.out.println("\n bot ID : " + ID + " Received Webpage at " + url);
            String title = doc.title();
            System.out.println(title);
            visitedLinks.add(url);
            return doc;
        } catch (IOException e) {
            return null;
        }
    }

    public Thread getThread() {
        return thread;
    }
}
