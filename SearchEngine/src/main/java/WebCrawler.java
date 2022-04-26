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
import java.util.Hashtable;

public class WebCrawler implements Runnable {
    private static final int MAX_DEPTH = 3;
    private Thread thread;
    private String firstLink;
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private Hashtable<String, Boolean> compactStrings = new Hashtable<>();

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
        crawl(1, firstLink);
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

    public boolean GetCompactString(URL url) {
        try {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements elements = doc.body().select("*");
            String cs = computeCompactString(elements);
            if(compactStrings.containsKey(cs)) return false;
            else {
                compactStrings.put(cs, true);
                return true;
            }
        }catch(IOException e) {
            System.out.println(e);
        }
        return false;
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

        for(int i = 0; i < quantity; i++)
            cs += elements.get(i).ownText().charAt(0);
        for(int i = numOfElements - 1; i + quantity >= numOfElements; i--)
            cs += elements.get(i).ownText().charAt(0);

        return cs;
    }

    private void crawl(int level, String url) {
        if (level <= MAX_DEPTH) {
            Document doc = request(url);
            if (doc != null) {
                for (Element link : doc.select("a[href]")) {
                    String nextLink = link.absUrl("href");
                    if (visitedLinks.contains(nextLink) == false) {
                        crawl(level++, nextLink);
                    }
                }
            }
        }
    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() == 200) {
                System.out.println("\n bot ID : " + ID + " Received Webpage at " + url);
                String title = doc.title();
                System.out.println(title);
                visitedLinks.add(url);
                return doc;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Thread getThread() {
        return thread;
    }
}
