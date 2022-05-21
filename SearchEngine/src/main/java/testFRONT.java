import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class testFRONT {

    public static void main(String[] args){
        String s="how to be footballer";
        String url="https://www.footballflick.com/how-to-become-a-professional-footballer/";
        rankerReturn r1=new rankerReturn(url,18);
        Connection con = Jsoup.connect(r1.url);
        DatabaseClass cs=new DatabaseClass();
        try {
            Document doc = con.get();
            String []type = con.response().contentType().split(";");
            String text = doc.select("*").text();

            int end= r1.plaintTextIndex+300;
            String f="";
            for(int i=r1.plaintTextIndex;i<=end;i++){
                f+=text.charAt(i);
            }


            System.out.println(text);
            System.out.println("-------------------------------");
            System.out.println(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
