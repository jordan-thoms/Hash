package nz.net.thoms.hash.mapreduce;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import nz.net.thoms.hash.shared.Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.naming.java.javaURLContextFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.mapreduce.AppEngineMapper;

public class HashMapper extends AppEngineMapper<Key, Entity, NullWritable, NullWritable> {
	private static final Logger log = Logger.getLogger(HashMapper.class.getName());

	private MessageDigest hasher;
	
	public HashMapper() {
	}

	@Override
	public void taskSetup(Context context) {
	}

	@Override
	public void taskCleanup(Context context) {
	}

	@Override
	public void setup(Context context) {
		log.warning("Doing per-worker setup");
	}

	@Override
	public void cleanup(Context context) {
		log.warning("Doing per-worker cleanup");
	}

	@Override
	public void map(Key key, Entity value, Context context) {
		if (value.hasProperty("hash")) {
			String hash = (String) value.getProperty("hash");
			String prefix = (String) value.getProperty("prefix");
			long length = (Long) value.getProperty("length");
			log.warning("Mapping key: " + key + " hash: " + hash + " prefix: " + prefix + " length: " + length);
			
			for (String postfix : Util.permute(Util.chars, (int) length - prefix.length())) {
				String candidate = prefix.concat(postfix);
//				log.warning(candidate + " with prefix: " + prefix);
				String hashC = DigestUtils.md5Hex(candidate);
				if (hash.equals(hashC)) {
					log.warning("Password is " + candidate);
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();							
					Entity entry = new Entity("Results");
					entry.setProperty("hash", hash);
					entry.setProperty("password", candidate);
					datastore.put(entry);
				}
			}
		}
	}
}