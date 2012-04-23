package nz.net.thoms.hash.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.net.thoms.hash.mapreduce.HashMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.ConfigurationXmlUtil;
import com.google.appengine.tools.mapreduce.DatastoreInputFormat;

public class HashServlet extends HttpServlet {

	public HashServlet() {
	}

	private String generateHtml(String configXml) {
		return "<html>"
				+ "<body>"
				+ "<form action=\"/mapreduce/start\" method=\"POST\">"
				+ "<textarea name=\"configuration\" rows=20 cols=80>"
				+ configXml
				+ "</textarea>"
				+ "<input type=\"submit\" value=\"Start\">"
				+ "</form>";
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		// Generate the configuration programmatically
		Configuration conf = new Configuration(false);
		try {
			conf.setClass("mapreduce.map.class", HashMapper.class, Mapper.class);
			conf.setClass("mapreduce.inputformat.class", DatastoreInputFormat.class, InputFormat.class);
			conf.set(DatastoreInputFormat.ENTITY_KIND_KEY, "Hash");
			conf.set("mapreduce.mapper.inputprocessingrate", "1000000");
			conf.set("mapreduce.mapper.shardcount", "20");

			// Render it as an HTML form so that the user can edit it.
			String html = generateHtml(
					ConfigurationXmlUtil.convertConfigurationToXml(conf));
			PrintWriter pw = new PrintWriter(resp.getOutputStream());
			pw.println(html);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
