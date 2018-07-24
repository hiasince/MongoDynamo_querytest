package test;

import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.*;
import org.json.simple.JSONObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
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
    //MongoDB ���� ����
    static String MongoDB_IP = "00.00.000.000";
    static int MongoDB_PORT  = 00000;
    static String DB_NAME    = "";
    static String DB_USER    = "";
    static String DB_PW      = "";

    //DynamoDB ����
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).build();
    static DynamoDB dynamoDB     = new DynamoDB(client);
    static String tableName      = "";
    Table table                  = dynamoDB.getTable(tableName);

    public static void main( String[] args ) {
      //MongoDB ����
      MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(DB_USER, DB_NAME, DB_PW.toCharArray());
      MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT),Arrays.asList(mongoCredential));
      DB db = mongoClient.getDB(DB_NAME);
      DBCollection collection = db.getCollection("");

      //Scan�� ���� Dynamo���� �ް�, mongoDB�� �ִ� �۾�
      Map<String, AttributeValue> lastKeyEvaluated = null;              //Scan ������
      Map<String, AttributeValue> expressionAttributeValues =
          new HashMap<String, AttributeValue>();
      expressionAttributeValues.put(":val", new AttributeValue().withS("")); //Scan �޾ƿ� ���� ����

      int count = 0;
      do {
        long LoopStartTime = System.nanoTime();

        ScanRequest scanRequest = new ScanRequest()
          .withTableName(tableName)                     //Scan �޾ƿ� table
          .withLimit(4000)                              //Scan �� ���� �޾ƿ� item ��
          .withExclusiveStartKey(lastKeyEvaluated);     //Scan ������ setting (null�� ��� ó������ ��ĵ))
//        .withFilterExpression("")       		//Scan �ε�ȣ ���� �߰�
//        .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = client.scan(scanRequest);

        //Scan 1���� �޾ƿ� �� ��ŭ loop
        for (Map<String, AttributeValue> item : result.getItems()){
            Set<String> keys = item.keySet();   //key�� ����
            Item temp = new Item();
            Map<String, Object> documentMap = new HashMap<String, Object>();
            for(String key : keys) {            //key���� �´� value setting
                String value = item.get(key).toString();
                char type = value.charAt(1);
                value = value.substring(4,value.length()-2);
                if(type == 'N') {
                    int test = Integer.parseInt(value);
                    documentMap.put(key, test);
                }
                else
                    documentMap.put(key, value);
            }
            collection.insert(new BasicDBObject(documentMap));  //insert
        }
        lastKeyEvaluated = result.getLastEvaluatedKey();        //���� Scan�� ������ Key�� ���� Scan�� ���������� ����
        count++;
        long LoopEndTime = System.nanoTime();
        double output = (LoopEndTime - LoopStartTime) / 1000000000.0;
        System.out.println("Scan Count     : " + count);
        System.out.println("Loop Laps Time : " + output + "\n");

      } while (lastKeyEvaluated != null);

    }
}
