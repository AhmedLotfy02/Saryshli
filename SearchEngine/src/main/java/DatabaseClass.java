import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DatabaseClass {
    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        String uri="mongodb://ahmed2:bashera2@localhost";
        MongoClient mongoclient= MongoClients.create(uri);

        //to test the connection is established or not
        MongoIterable<String>dbNames= mongoclient.listDatabaseNames();
//        for(String dbName:dbNames){
//            System.out.println(dbName);
//        }

       MongoDatabase db=mongoclient.getDatabase("database1");
        MongoCollection<Document> collection=db.getCollection("hi");
        Hashtable<String, Hashtable<String,Integer>> example=new Hashtable<String,Hashtable<String, Integer>>();
      //  URL url=new URL("www.google.com");
        //Sorg.jsoup.nodes.Document doc=new org.jsoup.nodes.Document("www.google.com");
       // DataStructures d1=new DataStructures(url,"hghg",doc);
        HashMap<String,DataStructures> h1=new HashMap<String, DataStructures>();
       // h1.put("ahmed",d1);
//
        Document doc1=new Document();
        doc1.append("website","www.gsad.com");
        doc1.append("term frequency",101);
        collection.insertOne(doc1);
        FindIterable<Document> fp=collection.find();
     for (Document document : fp) {
        for(Map.Entry<String,Object> e1:document.entrySet()){
            System.out.println(e1.getKey()+e1.getValue());
        }
        }


        //}
        //Document document=new Document(example);

        //
//        Document document=new Document("name","ahmed");
//        collection.insertOne(document);


    }
}
