package nz.net.thoms.hash.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.net.thoms.hash.mapreduce.HashMapper;
import nz.net.thoms.hash.shared.Util;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class ViewHashesServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(HashMapper.class.getName());

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html><head><meta http-equiv=\"refresh\" content=\"15\"></head><body>");
		out.println("<h1>Hashes</h1>");
		long starta = System.currentTimeMillis();
		int i;
		for (i=0; i< 1000000; i++) {
			i += 2;
			i += (int) Math.sin(i * (i + (int) System.currentTimeMillis()));
		}
		long enda = System.currentTimeMillis();
		out.println("took " + (enda - starta) + " i:" + i);

		try {
			URI uri = new URI(req.getRequestURL().toString());
			String host = uri.getScheme() +"://"+uri.getHost();
			if (uri.getPort() != -1) {
				host += ":" +uri.getPort();
			}
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			Query query = new Query("MapReduceState");
//			query.addFilter("status", Query.FilterOperator.EQUAL, "ACTIVE");
			out.println("Current jobs");
			out.println("<table>");
			out.println("<tr>");
			out.println("<th>ID</th>");
			out.println("<th>Status</th>");
			out.println("<th>shards</th>");
			out.println("<th>hashes/sec</th>");
			out.println("<th>hashes done</th>");
			out.println("</tr>");
			for (Entity job : datastore.prepare(query).asIterable()) {
				out.println("<tr>");
				String id =  job.getKey().getName();
				Task task = getTask(host, id);
				out.println("<td>" + id + "</td>");
				out.println("<td>" + job.getProperty("status") + "</td>");
				Object hasheds;
				int hashed = -1;
				if ((hasheds = ((Map) task.get("counters")).get("Hashes:Checked")) != null) {
					hashed = Integer.valueOf(hasheds.toString());
				}
				long start_timestamp_ms = Long.valueOf(task.get("start_timestamp_ms").toString());
				long updated_timestamp_ms = Long.valueOf(task.get("updated_timestamp_ms").toString());
				
				long s = (updated_timestamp_ms - start_timestamp_ms) / 1000;
				
				long hashedPerSecond = (hashed/s);
				out.println("<td>" + ((List) task.get("shards")).size() + "</td>");
				out.println("<td>" + hashedPerSecond + "</td>");
				out.println("<td>" + hashed  + "</td>");
//				out.println("<td>" + task.toString() + "</td>");
				out.println("</tr>");
			}
			out.println("</table>");
			
			query = new Query("Results");
			out.println("Results");
			out.println("<table>");
			out.println("<tr>");
			out.println("<th>hash</th>");
			out.println("<th>password</th>");
			out.println("</tr>");
			for (Entity result : datastore.prepare(query).asIterable()) {
				out.println("<tr>");
				out.println("<td>" + result.getProperty("hash") + "</td>");
				
				out.println("<td>" + result.getProperty("password") + "</td>");
				out.println("</tr>");
			}
			out.println("</table>");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("</body></html>");
	}

	//		http://localhost:8888/mapreduce/command/get_job_detail?mapreduce_id=job_1335324956390facc96cc828447e1bcda96b5526d0a01_0001
	public static class Task extends GenericJson {
	}

	private static Task getTask(String host, String id) throws IOException {
		HttpRequestFactory requestFactory =
				HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) {
						request.addParser(new JsonHttpParser(JSON_FACTORY));
					}
				});
		GenericUrl url = new GenericUrl(host + "/mapreduce/command/get_job_detail?mapreduce_id=" + id);
		HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().put("X-Requested-With", "XMLHttpRequest");
		HttpResponse response = request.execute();
	    Task task = response.parseAs(Task.class);
	    return task;
	}
}

