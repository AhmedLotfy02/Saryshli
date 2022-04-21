import com.mongodb.client.*;
import org.bson.Document;

public class DatabaseClass {
    public static void main(String[] args){
        String uri="mongodb://ahmed2:bashera2@localhost";
        MongoClient mongoclient= MongoClients.create(uri);

        //to test the connection is established or not
//        MongoIterable<String>dbNames= mongoclient.listDatabaseNames();
//        for(String dbName:dbNames){
//            System.out.println(dbName);
//        }
//        MongoDatabase db=mongoclient.getDatabase("database1");
//        MongoCollection<Document> collection=db.getCollection("inventory");
//
//        Document document=new Document("name","ahmed");
//        collection.insertOne(document);


    }
}
