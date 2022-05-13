import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import jdk.jfr.DataAmount;
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
    MongoDatabase db;
    MongoCollection collection;
   // public DatabaseClass(int dummy){}
    public DatabaseClass(){
        mongoclient= MongoClients.create(uri);
    }

    public void specifyDB(String desiredDatabase){
        this.db=mongoclient.getDatabase(desiredDatabase);
    }
    public void specifyCollection(String collection){
        this.collection=db.getCollection(collection);
    }
    public FindIterable<Document> getDocumentsFromCollection(String Collection){

        return db.getCollection(Collection).find();
    }
    public  FindIterable<Document> retreiveCrawledResult(){
        this.specifyDB("CrawlerDB1");

        this.specifyCollection("CrawlerResult");
        FindIterable<Document> it=collection.find();

    return it;
    }
    public boolean InsertLinks(ArrayList<String> links){
        try {
            if(db.listCollectionNames().equals("CrawlerResult")){
                //delete crawled data to begin from new one
               MongoCollection cRCollection=db.getCollection("CrawlerResult");
               cRCollection.drop();
            }
            else{
                System.out.println("First time to crawl");
            }
            MongoCollection state = db.getCollection("state");
            //delete all before inserting
            state.deleteOne(Filters.eq("_id", "Links"));
            state.deleteOne(Filters.eq("_id", "pages"));
            BasicDBObject doc = new BasicDBObject("_id", "Links").append("data", links);
            state.insertOne(new Document(doc.toMap()));
            doc = new BasicDBObject("_id", "pages").append("count", 0);
            state.insertOne(new Document(doc.toMap()));
        }
        catch (Exception e){
            System.out.println("Error happened in inserting links : "+e);
            return false;
        }
        return true;
    }

    public boolean storeOneCrawlerResult(String title,String CS,String link){
        MongoCollection CrawlerResult=db.getCollection("CrawlerResult");
        try {
            BasicDBObject doc = new BasicDBObject("_id", CS).append("title", title).append("link",link);
            CrawlerResult.insertOne(new Document(doc.toMap()));

        }
        catch (Exception e){
            System.out.println("while storing the crawler result of link : "+ link+" it throws exception :"+e);
            return false;
        }

        return true;
    }
    public boolean updatePageCount(int pageCount){
        MongoCollection state= db.getCollection("state");
        try {
            state.updateOne(Filters.eq("_id", "pages"), new Document("$set", new Document("count", pageCount)));

        }
        catch (Exception e){
            System.out.println("Throws an exception when storing the ne pageCount");
            return false;
        }
        return true;

    }
    public boolean updateLinks(LinkedList<String> links){
        MongoDatabase db1= mongoclient.getDatabase("CrawlerDB1");
        MongoCollection state= db.getCollection("state");
        try {
            state.updateOne(Filters.eq("_id", "Links"), new Document("$set", new Document("data", links)));

        }
        catch (Exception e){
            System.out.println("Throws an exception when storing the new links");
            return false;
        }
//        Document document = new     Document("_id","Links");
//        BasicDBObject doc = new BasicDBObject("_id","Links").append("data", links);
//        state.replaceOne(document,new Document(doc.toMap()));
        System.out.println("Links updated in db");
        return true;
    }
    public boolean CheckForCompactString(String CS){
        MongoCollection crawlerResultCollection=db.getCollection("CrawlerResult");
        FindIterable<Document> links= crawlerResultCollection.find(Filters.eq("_id",CS));
        if(links.first()!=null){
            System.out.println("Link is dup");
             return false;
        }
        //FindIterable<Document> docs=crawlerResultCollection.find();
//        for(Document doc:docs){
//
////            if(doc.getObjectId(CS)!=null){
////                System.out.println("Link is dup");
////                return false; //this link is checked before
////            }
//            String csdb=doc.getString("_id");
//            //System.out.println(csdb);
//            if(csdb==CS){
//                System.out.println("cs found in db");
//                return false;
//            }
//        }
      //  System.out.println("Link is not dup");

        return true;
    }
    public int getPagesNo(){
        int count = 0;
        MongoCollection collection=db.getCollection("state");
        FindIterable<Document> pageNumDocIterable=collection.find(Filters.eq("_id","pages"));
        for(Document doc:pageNumDocIterable){
            count=(Integer)doc.get("count");
        }
        return count;
    }
    public ArrayList<String> getLinks(){
        MongoCollection state = db.getCollection("state");
        FindIterable<Document> docs=state.find(Filters.eq("_id","Links"));
        ArrayList<String> links=new ArrayList<String>();
        for(Document doc:docs){
            links.addAll((ArrayList<String>)doc.get("data"));
          //  System.out.println(links);
        }
        return links;
    }

    public FindIterable<Document> retreiveDataFromIndexerByWord(String word){
        this.specifyCollection(word);
        FindIterable<Document> it=collection.find();
        return it;
    }
    public boolean store(Hashtable<String, Index.Pair_Data> wordTable, String url){
        MongoDatabase db=mongoclient.getDatabase("IndexerDB");

        System.out.println(wordTable.size());

        for(Map.Entry<String, Index.Pair_Data> e:wordTable.entrySet()){
            MongoCollection<Document> collection=db.getCollection(e.getKey());


            int priority=e.getValue().occursAt.get(0);
            e.getValue().occursAt.subList(0,1).clear();
           double itf=e.getValue().itf;
            BasicDBObject doc = new BasicDBObject("_id", url)
                    .append("priority", priority)
                    .append("occurs-at",e.getValue().occursAt)
                    .append("itf",itf);


            try {
                collection.insertOne(new Document(doc.toMap()));

            }
            catch (Exception e1){
                System.out.println("Error when storing in indexer db: "+e1);
            }
//
            //    System.out.println(priority);
            //System.out.println(tf);


        }
//        MongoCollection<Document> collection=db.getCollection("offici");
//        FindIterable<Document> fp=collection.find();
//     for (Document document : fp) {
//        for(Map.Entry<String,Object> e1:document.entrySet()){
//            System.out.println(e1.getKey()+e1.getValue());
//        }
//        }
//
//

        return true;
    }



//    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
//        //String uri="mongodb://ahmed2:bashera2@localhost";
//        //MongoClient mongoclient= MongoClients.create(uri);
//
//        //to test the connection is established or not
//        //MongoIterable<String>dbNames= mongoclient.listDatabaseNames();
////        for(String dbName:dbNames){
////            System.out.println(dbName);
////        }
//
////       MongoDatabase db=mongoclient.getDatabase("database1");
////        MongoCollection<Document> collection=db.getCollection("hi");
////        Hashtable<String, Hashtable<String,Integer>> example=new Hashtable<String,Hashtable<String, Integer>>();
////      //  URL url=new URL("www.google.com");
////        //Sorg.jsoup.nodes.Document doc=new org.jsoup.nodes.Document("www.google.com");
////       // DataStructures d1=new DataStructures(url,"hghg",doc);
////        HashMap<String,DataStructures> h1=new HashMap<String, DataStructures>();
////       // h1.put("ahmed",d1);
//////
////        Document doc1=new Document();
////        doc1.append("website","www.gsad.com");
////        doc1.append("term frequency",101);
////        collection.insertOne(doc1);
////        FindIterable<Document> fp=collection.find();
////     for (Document document : fp) {
////        for(Map.Entry<String,Object> e1:document.entrySet()){
////            System.out.println(e1.getKey()+e1.getValue());
////        }
////        }
//
//
//        //}
//        //Document document=new Document(example);
//
//        //
////        Document document=new Document("name","ahmed");
////        collection.insertOne(document);
//
//
//    }
}
