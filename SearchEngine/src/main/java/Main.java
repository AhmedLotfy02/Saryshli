


import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ArrayList<WebCrawler> bots=new ArrayList<>();
        bots.add(new WebCrawler("www.wikipedia.org",3));
        for(WebCrawler w:bots) {
            try {
                w.getThread().join();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}