package nz.net.thoms.hash.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("hash")
public interface HashService extends RemoteService {
	String submitHash(String hash) throws IllegalArgumentException;
}
