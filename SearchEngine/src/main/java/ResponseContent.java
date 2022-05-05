import org.jsoup.nodes.Document;

public class ResponseContent {
     private Document doc;
     private boolean connectionStatus;

     ResponseContent(Document doc,boolean connectionStatus){
         this.connectionStatus=connectionStatus;
         this.doc=doc;
     }
     public boolean getConnectionStatus(){
         return this.connectionStatus;
     }
    public Document getDocument(){
        return this.doc;
    }


}
