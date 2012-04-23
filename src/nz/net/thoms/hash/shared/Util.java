package nz.net.thoms.hash.shared;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class Util {
	static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		

	public static String hashIdentifier(String hash) {
		return hash.substring(0, 5);
	}
	
	public static void putEntity(String hash, String prefix, int length) {
		Entity entry = new Entity(Util.hashIdentifier(hash));
		entry.setProperty("hash", hash);
		entry.setProperty("prefix", prefix);
		entry.setProperty("length", length);
	}
}
