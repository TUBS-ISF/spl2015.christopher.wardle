package spl.vocTrainer;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import spl.vocTrainer.VocTrainer;

public class Login {

    public static boolean authenticate(String username, String password) {
	MongoClient client = null;
	try {
	    client = VocTrainer.getDBClientInstance();
	}
	catch (UnknownHostException e) {
	    e.printStackTrace();
	}
	if(client != null){
	    DB database = client.getDB("voctrainer");
	    DBCollection userCollection = database.getCollection("users");
	    
	    DBObject query = new BasicDBObject("username", username).append("password", password);
	    
	    DBObject result = userCollection.findOne(query);
	    
	    if(result != null){
		return true;
	    }
	}
	return false;
    }
}