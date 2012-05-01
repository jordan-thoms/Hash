package nz.net.thoms.hash.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HashServiceAsync {
	void submitHash(String hash, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
