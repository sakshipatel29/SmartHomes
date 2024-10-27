package utilities;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import java.util.List;
import java.util.ArrayList;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;
import java.util.Map;

public class MongoDBDataStoreUtilities {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String MONGODB_URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "smart-homes";
    private static final String COLLECTION_NAME = "productreviews";

    static {
        try {
            mongoClient = MongoClients.create(MONGODB_URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertReview(Map<String, Object> review) {
        try {
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            collection.insertOne(new Document(review));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Document> getTopFiveRatedProducts() {
        List<Document> topRatedProducts = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            
            AggregateIterable<Document> result = collection.aggregate(
                List.of(
                    group("$productModelName", 
                          avg("averageRating", "$reviewRating"),
                          first("manufacturerName", "$manufacturerName"),
                          first("productCategory", "$productCategory"),
                          first("productPrice", "$productPrice")),
                    sort(descending("averageRating")),
                    limit(5)
                )
            );

            for (Document doc : result) {
                topRatedProducts.add(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topRatedProducts;
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}