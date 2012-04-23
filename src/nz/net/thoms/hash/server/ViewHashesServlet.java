package nz.net.thoms.hash.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class ViewHashesServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<h1>Hashes</h1>");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		displayComments(out, datastore);
	}

	private void displayComments(PrintWriter out, DatastoreService datastore) {
		Query query = new Query("Hash");
		query.addSort("createdAt", SortDirection.DESCENDING);

		out.println("<table>");
		out.println("<tr>");
		out.println("<th>ID</th>");
		out.println("<th>comment</th>");
		out.println("<th>locale</th>");
		out.println("<th>created</th>");
		out.println("<th>updated</th>");
		out.println("</tr>");
		for (Entity comment : datastore.prepare(query).asIterable()) {
			out.println("<tr>");

			out.println("<td>" + comment.getKey() + "</td>");
			out.println("<td>" + comment.getProperty("hash") + "</td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}
}