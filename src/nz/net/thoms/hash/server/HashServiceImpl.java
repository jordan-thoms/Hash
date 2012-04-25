package nz.net.thoms.hash.server;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Mapper;

import nz.net.thoms.hash.client.HashService;
import nz.net.thoms.hash.mapreduce.HashMapper;
import nz.net.thoms.hash.shared.FieldVerifier;
import nz.net.thoms.hash.shared.Util;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.mapreduce.ConfigurationXmlUtil;
import com.google.appengine.tools.mapreduce.DatastoreInputFormat;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HashServiceImpl extends RemoteServiceServlet implements
		HashService {

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	@Override
	public String submitHash(String hash) throws IllegalArgumentException {
		HashEntityGenerator.generatePutEntities(hash, 4);
		Configuration conf = new Configuration(false);
		conf.setClass("mapreduce.map.class", HashMapper.class, Mapper.class);
		conf.setClass("mapreduce.inputformat.class", DatastoreInputFormat.class, InputFormat.class);
		conf.set(DatastoreInputFormat.ENTITY_KIND_KEY, Util.hashIdentifier(hash));
		conf.set("mapreduce.mapper.inputprocessingrate", "1000000000");
		conf.set("mapreduce.mapper.shardcount", "100");
		String xmlConfig = ConfigurationXmlUtil.convertConfigurationToXml(conf);
		
		TaskOptions opts = withUrl("/mapreduce/start")
			    .param("configuration", xmlConfig)
			    .method(TaskOptions.Method.POST)
			    .header("X-Requested-With", "XMLHttpRequest");

		QueueFactory.getQueue("default").add(opts);
		return "";
	}
}
