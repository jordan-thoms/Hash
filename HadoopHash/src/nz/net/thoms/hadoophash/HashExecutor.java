package nz.net.thoms.hadoophash;

public interface HashExecutor {
	public HashResult executeHashes(String hash, String prefix, int length);
}
