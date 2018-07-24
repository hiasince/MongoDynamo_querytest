import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.regions.Regions;

public class App
{
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();
    static DynamoDB dynamoDB = new DynamoDB(client);
    static String tableName = "0000";

    public static void main( String[] args ) throws Exception {
      Table table = dynamoDB.getTable(tableName);
      int user         = 0;
      int id           = 0;
      String id        = "0000-00-00 00:00:00";
      String type      = "type";
      String author    = "";
      String body      = "";

      for(int i = 0; i < 100; i++) {
          Item item = new Item()
              .withPrimaryKey("user", user)
              .withNumber("id", id)
              .withString("id", id)
              .withString("type", type)
              .withString("author", author)
              .withString("body", body)
          PutItemOutcome outcome = table.putItem(item);

          id++;
          id++;
          id++;
          id++;
      }
    }
}
