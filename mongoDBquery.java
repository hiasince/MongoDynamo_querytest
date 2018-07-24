
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
import com.mongodb.DBCursor;

public class App
{
    static String MongoDB_IP = "00.00.000.000";
    static int MongoDB_PORT  = 00000;
    static String DB_NAME    = "";
    static String DB_USER    = "";
    static String DB_PW      = "";

    public static void main( String[] args ) throws Exception {
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(DB_USER, DB_NAME, DB_PW.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT),Arrays.asList(mongoCredential));
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection collection = db.getCollection("byungiktest");

        BasicDBObject andQuery = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();

        //where 절에 들어갈 field 추가
        obj.add("$explain : 1"dd);
        obj.add(new BasicDBObject("id", 0));
        obj.add(new BasicDBObject("id", "0000-00-00 00:00:00"));
        obj.add(new BasicDBObject("id", 0));
        obj.add(new BasicDBObject("type", "type"));

        //where 절에 조건문 and, or 추가
        andQuery.put("$and", obj);

        //query 시간 측정
        long StartTime = System.nanoTime();
        DBCursor cursor = collection.find(andQuery);    //query문을 돌리고 cursor를 처음으로 맞춘다.
        long EndTime = System.nanoTime();

        System.out.println("Query : " + andQuery.toString());
        while(cursor.hasNext()) {
            System.out.println(cursor.next());
        }
        double output = (EndTime - StartTime) / 1000000000.0;
        System.out.println("\nQuery      : " + andQuery.toString());
        System.out.println("Query Time : " + output);

    }
}
   