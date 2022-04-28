
import org.jsoup.nodes.Document;

import java.net.URL;

public class DataStructures {
    private URL url;
    private String CompactString;
    private Document doc;

    DataStructures(URL url,String CS,Document doc){
        this.url=url;
        this.CompactString=CS;
        this.doc=doc;
    }
}
