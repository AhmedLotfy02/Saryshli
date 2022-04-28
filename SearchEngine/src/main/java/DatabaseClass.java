import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class DatabaseClass {
    MongoClient mongoclient;
    String uri="mongodb://ahmed2:bashera2@localhost";

    public DatabaseClass(){
        mongoclient= MongoClients.create(uri);
    MongoIterable<String>dbNames= mongoclient.listDatabaseNames();
        for(String dbName:dbNames){
            System.out.println(dbName);
        }

    }
    public boolean store(Hashtable<String,ArrayList<Integer>> wordTable,String url){
        MongoDatabase db=mongoclient.getDatabase("database3");

        System.out.println(wordTable);

        for(Map.Entry<String,ArrayList<Integer>> e:wordTable.entrySet()){
            MongoCollection<Document> collection=db.getCollection(e.getKey());
            int priority=e.getValue().get(0);
            int tf=e.getValue().size()-1;
           Document doc=new Document();

           //System.out.println("word : "+e.getKey()+" ,priority: " +priority+"tf: "+tf+"occurs at"+e.getValue());
          doc.append("website",url);
            doc.append("occurs-at",e.getValue());

                     doc.append("priority",priority);
            doc.append("tf",tf);
           collection.insertOne(doc);
//            
            //    System.out.println(priority);
            //System.out.println(tf);


        }
        MongoCollection<Document> collection=db.getCollection("offici");
        FindIterable<Document> fp=collection.find();
     for (Document document : fp) {
        for(Map.Entry<String,Object> e1:document.entrySet()){
            System.out.println(e1.getKey()+e1.getValue());
        }
        }
//
//

        return true;
    }



    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        //String uri="mongodb://ahmed2:bashera2@localhost";
        //MongoClient mongoclient= MongoClients.create(uri);

        //to test the connection is established or not
        //MongoIterable<String>dbNames= mongoclient.listDatabaseNames();
//        for(String dbName:dbNames){
//            System.out.println(dbName);
//        }

//       MongoDatabase db=mongoclient.getDatabase("database1");
//        MongoCollection<Document> collection=db.getCollection("hi");
//        Hashtable<String, Hashtable<String,Integer>> example=new Hashtable<String,Hashtable<String, Integer>>();
//      //  URL url=new URL("www.google.com");
//        //Sorg.jsoup.nodes.Document doc=new org.jsoup.nodes.Document("www.google.com");
//       // DataStructures d1=new DataStructures(url,"hghg",doc);
//        HashMap<String,DataStructures> h1=new HashMap<String, DataStructures>();
//       // h1.put("ahmed",d1);
////
//        Document doc1=new Document();
//        doc1.append("website","www.gsad.com");
//        doc1.append("term frequency",101);
//        collection.insertOne(doc1);
//        FindIterable<Document> fp=collection.find();
//     for (Document document : fp) {
//        for(Map.Entry<String,Object> e1:document.entrySet()){
//            System.out.println(e1.getKey()+e1.getValue());
//        }
//        }


        //}
        //Document document=new Document(example);

        //
//        Document document=new Document("name","ahmed");
//        collection.insertOne(document);


    }
}
