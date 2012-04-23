package nz.net.thoms.hash.server;

import nz.net.thoms.hash.shared.Util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class HashEntityGenerator {
	private static int APPROX_JOB_SIZE = 1000000;
	private static int CHARSET_SIZE = 100;
	private static int POSTFIX_LENGTH = (int) (Math.log(APPROX_JOB_SIZE) / Math.log(CHARSET_SIZE));
	
	public static void generatePutEntities(String hash, int width) {
		int prefix_length = width - POSTFIX_LENGTH;
		
		if (prefix_length > 0) {
			
		}
	}
}
